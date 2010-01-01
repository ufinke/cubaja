// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.ufinke.cubaja.util.IteratorException;
import de.ufinke.cubaja.util.Stopwatch;
import de.ufinke.cubaja.util.Text;

public class Sorter<D extends Serializable> implements Iterable<D> {

  static private Text text = new Text(Sorter.class);
  static private Log logger = LogFactory.getLog(Sorter.class);

  private Info info;
  private SortConfig config;
  private Stopwatch stopwatch;
  
  private Comparator<? super D> comparator;
  private SortAlgorithm algorithm;
  
  private boolean isCalculated;
  private boolean isRetrieveState;
  
  private SortArray array;
  private long count;
  private int writtenRuns;
  private IOManager ioManager;
  
  public Sorter(Comparator<? super D> comparator) {
    
    this(comparator, new SortConfig());
  }
  
  public Sorter(Comparator<? super D> comparator, SortConfig config) {
  
    this.comparator = comparator;
    this.config = config;

    info = new Info();
    info.setConfig(config);
    
    if (config.isLog()) {
      stopwatch = new Stopwatch();
      logger.debug(text.get("sortOpen", info.id()));
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
  }
  
  private void writeRun() throws Exception {
    
    if (array.getSize() == 0) {
      return;
    }
    
    sortArray();
    
    if (writtenRuns == 0) {
      ioManager = new IOManager(info);
    }
    
    array = ioManager.writeRun(array);
    
    writtenRuns++;
  }
  
  private void sortArray() {
    
    Stopwatch watch = new Stopwatch();
    
    algorithm.sort(array);    
    count += array.getSize();
    
    if (config.isLog()) {
      long elapsed = watch.elapsedMillis();
      logger.trace(text.get("arraySorted", info.id(), array.getSize(), elapsed));
    }
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
    
    try {
      startRetrieve();
      if (writtenRuns == 0) {
        return createSimpleIterator();
      } else {
        return createMergeIterator(); 
      }
    } catch (Exception e) {
      throw new IteratorException(e);
    }
  }
  
  private void startRetrieve() throws Exception {
        
    if (writtenRuns > 0) {
      if (config.isLog()) {
        stopwatch.elapsedMillis();
      }
    }
  }

  private Iterator<D> createSimpleIterator() {
    
    sortArray();
    
    final SortArray localArray = array;
    
    return new Iterator<D>() {

      public boolean hasNext() {

        boolean result = localArray.hasNext();
        if (! result) {
          close();
        }
        return result;
      }

      @SuppressWarnings("unchecked")
      public D next() {

        return (D) localArray.next();
      }

      public void remove() {

        throw new UnsupportedOperationException();
      }
    };
  }
  
  @SuppressWarnings({"unchecked", "rawtypes"})
  private Iterator<D> createMergeIterator() throws Exception {
    
    writeRun();

    Merger merger = new Merger(comparator, ioManager.getRuns());
    final Iterator<Object> mergeIterator = merger.iterator();
    
    return new Iterator<D>() {

      public boolean hasNext() {

        boolean result = mergeIterator.hasNext();
        if (! result) {
          close();
        }
        return result;
      }

      public D next() {

        return (D) mergeIterator.next();
      }

      public void remove() {

        throw new UnsupportedOperationException();
      }      
    };
  }
  
  void close() {
    
    if (config.isLog()) {
      long time = stopwatch.elapsedMillis();
      logger.debug(text.get("sortClose", info.id(), count, stopwatch.format(time)));
    }
  }
}
