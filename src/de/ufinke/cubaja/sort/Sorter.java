// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.ufinke.cubaja.util.Text;

public class Sorter<D extends Serializable> implements Iterable<D> {

  static private Text text = new Text(Sorter.class);

  private Log logger;
  private SortConfig config;
  private Comparator<? super D> comparator;
  private SortAlgorithm algorithm;
  
  private boolean isRetrieveState;
  
  private SortArray array;
  
  public Sorter(Comparator<? super D> comparator) {
    
    this(comparator, new SortConfig());
  }
  
  public Sorter(Comparator<? super D> comparator, SortConfig config) {
  
    this.comparator = comparator;
    this.config = config;
    
    if (config.isLog()) {
      logger = LogFactory.getLog(Sorter.class);
    }
    
    algorithm = config.getAlgorithm();
    algorithm.setComparator(comparator);
    
    array = new SortArray(config.isCalculated() ? config.getRecordsPerRun() : 1000);
  }
  
  public void add(D element) throws Exception {
  
    if (isRetrieveState) {
      throw new IllegalStateException(text.get("illegalAdd"));
    }
    
    array.add(element);
    
    if (array.isFull()) {
      if (! config.isCalculated()) {
        calculateSizes();
        if (array.isFull()) {
          writeArray();
        }
      } else {
        writeArray();
      }
    }
  }
  
  private void writeArray() throws Exception {
    
  }
  
  private void calculateSizes() throws Exception {

    ByteArrayOutputStream bos = new ByteArrayOutputStream(100000);
    ObjectOutputStream oos = new ObjectOutputStream(bos);
    for (Object object : array.getArray()) {
      oos.writeObject(object);
    }
    oos.close();
    
    long bytesPerRecord = bos.size() / 1000;
    long memory = Runtime.getRuntime().maxMemory() / 4;
    
    long recordsPerBlock = 1024 * 256 / bytesPerRecord;
    if (config.getRecordsPerBlock() == 0) {
      config.setRecordsPerBlock((int) recordsPerBlock);
    }
    
    long recordsPerRun = memory / bytesPerRecord;
    if (recordsPerRun > Integer.MAX_VALUE) {
      recordsPerRun = Integer.MAX_VALUE;
    }
    if (config.getRecordsPerRun() == 0) {
      config.setRecordsPerRun((int) recordsPerRun);
      if (config.getRecordsPerRun() > 1000) {
        array.enlarge(config.getRecordsPerRun());
      }
    }
    
    if (config.isLog()) {
      logger.debug(text.get("calcSizes", recordsPerRun, recordsPerBlock));
    }
  }
  
  public Iterator<D> iterator() {

    isRetrieveState = true;

    return null;
  }
  
}
