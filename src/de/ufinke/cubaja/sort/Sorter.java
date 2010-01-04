// Copyright (c) 2008 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
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
  private IOManager ioManager;
  
  public Sorter(Comparator<? super D> comparator) {
    
    this(comparator, new SortConfig());
  }
  
  public Sorter(Comparator<? super D> comparator, SortConfig config) {
  
    this.comparator = comparator;
    this.config = config;

    info = new Info();
    info.setConfig(config);
    info.setComparator(comparator);
    
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
    
    if (ioManager == null) {
      ioManager = new IOManager(info);
    }
    
    array = ioManager.writeRun(array);
  }
  
  private void sortArray() {
    
    Stopwatch watch = new Stopwatch();
    
    algorithm.sort(array.getArray(), array.getSize());    
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

        return (D) source.next();
      }

      public void remove() {

        throw new UnsupportedOperationException();
      }      
    };
  }
  
  private Iterator<Object> getSimpleIterator() {
    
    sortArray();
    return array;
  }
  
  @SuppressWarnings({"unchecked", "rawtypes"})
  private Iterator<Object> getMergeIterator() throws Exception {
    
    writeRun();
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
    
    if (config.isLog()) {
      long time = stopwatch.elapsedMillis();
      logger.debug(text.get("sortClose", info.id(), count, stopwatch.format(time)));
    }
  }
}
