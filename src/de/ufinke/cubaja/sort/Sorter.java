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
import de.ufinke.cubaja.util.IteratorException;
import de.ufinke.cubaja.util.Text;

public class Sorter<D extends Serializable> implements Iterable<D> {

  static private Text text = new Text(Sorter.class);
  
  static final int BLOCK_SIZE = 256 * 1024;
  static private final int CALC_RECORD_COUNT = 1000;

  private Log logger;
  private SortConfig config;
  private Comparator<? super D> comparator;
  private SortAlgorithm algorithm;
  
  private boolean isRetrieveState;
  
  private SortArray array;
  private int writtenRuns;
  private IOManager ioManager;
  
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
    
    array = new SortArray(config.isCalculated() ? config.getRecordsPerRun() : CALC_RECORD_COUNT);
  }
  
  public void add(D element) throws Exception {
  
    if (isRetrieveState) {
      throw new IllegalStateException(text.get("illegalAdd"));
    }
    
    if (array.isFull()) {
      if (config.isCalculated()) {
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
    
    algorithm.sort(array);
    
    if (writtenRuns == 0) {
      ioManager = new IOManager(config);
    }
    
    array = ioManager.writeRun(array);
    
    writtenRuns++;
  }
  
  private void calculateSizes() throws Exception {

    ByteArrayOutputStream bos = new ByteArrayOutputStream(CALC_RECORD_COUNT * 100);
    ObjectOutputStream oos = new ObjectOutputStream(bos);
    for (Object object : array.getArray()) {
      oos.writeObject(object);
    }
    oos.close();
    
    int bytesPerRecord = bos.size() / CALC_RECORD_COUNT;
    long memory = Runtime.getRuntime().maxMemory() / 4;
    
    if (config.getRecordsPerBlock() == 0) {
      long recordsPerBlock = BLOCK_SIZE / bytesPerRecord;
      config.setRecordsPerBlock((int) recordsPerBlock);
    }
    
    if (config.getRecordsPerRun() == 0) {
      long recordsPerRun = memory / bytesPerRecord;
      if (recordsPerRun > Integer.MAX_VALUE) {
        recordsPerRun = Integer.MAX_VALUE;
      }
      config.setRecordsPerRun((int) recordsPerRun);
    } else { // check blocks per run
      config.setRecordsPerRun(config.getRecordsPerRun());
    }
    
    if (config.getRecordsPerRun() > CALC_RECORD_COUNT) {
      array.enlarge(config.getRecordsPerRun());
    }
    
    if (config.isLog()) {
      logger.debug(text.get("calcSizes", config.getRecordsPerRun(), config.getRecordsPerBlock()));
    }
  }
  
  public Iterator<D> iterator() {

    try {
      startRetrieve();
    } catch (Exception e) {
      throw new IteratorException(e);
    }
    
    return null;
  }
  
  private void startRetrieve() throws Exception {
    
    isRetrieveState = true;
        
    if (writtenRuns > 0) {
      writeRun();
      ioManager.finishWriteRuns();
    }
  }
  
}
