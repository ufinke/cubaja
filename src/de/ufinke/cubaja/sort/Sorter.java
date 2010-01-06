// Copyright (c) 2008 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import de.ufinke.cubaja.util.IteratorException;
import de.ufinke.cubaja.util.Stopwatch;
import de.ufinke.cubaja.util.Text;

public class Sorter<D extends Serializable> implements Iterable<D> {

  static private Text text = new Text(Sorter.class);
  
  Info info;
  private Stopwatch stopwatch;
  
  private Comparator<? super D> comparator;
  private SortAlgorithm algorithm;
  
  private boolean isCalculated;
  private boolean isRetrieveState;
  
  private SortArray array;
  private IOManager ioManager;
  
  long putCount;
  long getCount;
  private Timer timer;
  
  public Sorter(Comparator<? super D> comparator) {
    
    this(comparator, new SortConfig());
  }
  
  public Sorter(Comparator<? super D> comparator, SortConfig config) {
  
    this.comparator = comparator;

    info = new Info();
    info.setConfig(config);
    info.setComparator(comparator);
    
    if (info.isDebug()) {
      stopwatch = new Stopwatch();
      info.debug("sortOpen");
      if (info.isTrace()) {
        createTimer(createPutTimerTask());
      }
    }
    
    algorithm = config.getAlgorithm();
    algorithm.setComparator(comparator);
    
    array = new SortArray(Info.PROBE_SIZE);
  }
  
  public void add(D element) throws Exception {
  
    if (isRetrieveState) {
      throw new IllegalStateException(text.get("illegalAdd"));
    }
    
    if (array.isFull()) {
      if (isCalculated) {
        writeRun();
      } else {
        calculateSizes();
        if (array.isFull()) {
          writeRun();
        }
      }
    }
    
    array.add(element);
    putCount++;
  }
  
  private void writeRun() throws Exception {
    
    if (array.getSize() == 0) {
      return;
    }
    
    sortArray();
    
    if (ioManager == null) {
      ioManager = new IOManager(info);
    }
    
    array = ioManager.writeRun(array);
  }
  
  private void sortArray() {
    
    algorithm.sort(array.getArray(), array.getSize());    
  }
  
  private void calculateSizes() throws Exception {

    ByteArrayOutputStream bos = new ByteArrayOutputStream(Info.BYTES_PER_BLOCK);
    ObjectOutputStream oos = new ObjectOutputStream(bos);
    for (Object object : array.getArray()) {
      oos.writeObject(object);
    }
    oos.close();
    
    info.calculateSizes(bos.size());
    
    if (info.getRunSize() > Info.PROBE_SIZE) {
      array.enlarge(info.getRunSize());
    }
    
    isCalculated = true;
  }
  
  public Iterator<D> iterator() {

    isRetrieveState = true;

    if (info.isTrace()) {
      timer.cancel();
    }
    
    try {
      return createIterator(); 
    } catch (Exception e) {
      throw new IteratorException(e);
    }
  }
  
  @SuppressWarnings({"unchecked"})
  private Iterator<D> createIterator() throws Exception {
    
    final Iterator<Object> source = (ioManager == null) ? getSimpleIterator() : getMergeIterator();
    
    return new Iterator<D>() {

      public boolean hasNext() {

        boolean result = source.hasNext();
        if (! result) {
          close();
        }
        return result;
      }

      public D next() {

        getCount++;
        return (D) source.next();
      }

      public void remove() {

        throw new UnsupportedOperationException();
      }      
    };
  }
  
  private Iterator<Object> getSimpleIterator() {
    
    sortArray();
    
    if (info.isDebug()) {
      info.debug("sortSwitch", putCount, 0 , 0);
      if (info.isTrace()) {
        createTimer(createGetTimerTask());
      }
    }
    
    return array;
  }
  
  @SuppressWarnings({"unchecked", "rawtypes"})
  private Iterator<Object> getMergeIterator() throws Exception {
    
    writeRun();
    ioManager.finishWrite();
    
    if (info.isDebug()) {
      info.debug("sortSwitch", putCount, ioManager.getRunCount(), ioManager.getFileSize());
      if (info.isTrace()) {
        createTimer(createGetTimerTask());
      }
    }
    
    return new Merger(comparator, ioManager.getRuns()).iterator();
  }
  
  void close() throws IteratorException {

    if (ioManager != null) {
      try {
        ioManager.close();
      } catch (Exception e) {
        throw new IteratorException(e);
      }
      ioManager = null;
    }
    
    if (info.isDebug()) {
      if (info.isTrace()) {
        timer.cancel();
      }
      long time = stopwatch.elapsedMillis();
      info.debug("sortClose", getCount, stopwatch.format(time));
    }
  }
  
  private TimerTask createPutTimerTask() {
    
    return new TimerTask() {
      public void run() {
        info.trace("sortPut", putCount);
      }
    };
  }
  
  private TimerTask createGetTimerTask() {
    
    return new TimerTask() {
      public void run() {
        info.trace("sortGet", getCount);
      }
    };
  }

  private void createTimer(TimerTask task) {
    
    long millis = info.getConfig().getLogInterval() * 1000;
    timer = new Timer();
    timer.schedule(task, millis, millis);
  }
}
