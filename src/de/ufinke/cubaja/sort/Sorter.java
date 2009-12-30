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
      stopwatch = new Stopwatch(text.get("stopSort", info.id()));
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
    
    Stopwatch watch = new Stopwatch();
    algorithm.sort(array);
    if (config.isLog()) {
      long elapsed = watch.elapsedMillis();
      logger.trace(text.get("arraySorted", info.id(), array.getSize(), elapsed));
    }
    
    if (writtenRuns == 0) {
      ioManager = new IOManager(info);
    }
    
    array = ioManager.writeRun(array);
    
    writtenRuns++;
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
      if (config.isLog()) {
        stopwatch.elapsedMillis();
      }
    }
  }
  
}
