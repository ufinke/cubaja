// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.zip.DeflaterOutputStream;
import de.ufinke.cubaja.io.OutputBuffer;

class IOManager {

  private SortConfig config;
  
  private File file;
  private RandomAccessFile raf;
    
  private ExecutorService executor;
  
  private SynchronousQueue<SortArray> writeQueue;
  private Callable<SortArray> writeRunCallable;
  private Future<SortArray> writeRunFuture;
  private OutputBuffer outBuffer;
  
  public IOManager(SortConfig config) throws Exception {
    
    this.config = config;
    
    open();
    
    executor = Executors.newSingleThreadExecutor();
    
    writeQueue = new SynchronousQueue<SortArray>();
    writeRunCallable = new Callable<SortArray>() {
      public SortArray call() throws Exception {
        return backgroundWriteRun();
      }
    };
    outBuffer = new OutputBuffer(Sorter.BLOCK_SIZE);
  }
  
  protected void finalize() {
    
    try {
      close();
    } catch (Exception e) {        
    }
  }
  
  public void close() throws Exception {
    
    if (executor != null) {
      executor.shutdown();
    }
    
    if (raf == null) {
      return;
    }
    
    raf.close();
    //file.delete();
    
    raf = null; 
    file = null;
  }
  
  private void open() throws Exception {

    File dir = new File(config.getWorkDirectory());
    dir.mkdirs();
    
    SimpleDateFormat sdf = new SimpleDateFormat("_yyyyMMdd_HHmmssSSS");
    boolean exists = true;
    while (exists) {
      StringBuilder sb = new StringBuilder(50);
      sb.append(config.getFilePrefix());
      sb.append(sdf.format(new Date()));
      sb.append(".tmp");
      file = new File(dir, sb.toString());
      exists = file.exists();
    }

    raf = new RandomAccessFile(file, "rw");
  }
  
  public SortArray writeRun(SortArray array) throws Exception {
    
    SortArray result = (writeRunFuture == null) ? new SortArray(config.getRecordsPerRun()) : writeRunFuture.get();
    
    writeRunFuture = executor.submit(writeRunCallable);
    
    writeQueue.put(array);
    
    return result;
  }
  
  SortArray backgroundWriteRun() throws Exception {
    
    SortArray array = writeQueue.take();
    
    int startIndex = 0;
    while (startIndex < array.getSize()) {
      int endIndex = Math.min(startIndex + config.getRecordsPerBlock(), array.getSize());
      writeBlock(array, startIndex, endIndex);
      startIndex = endIndex;
    }
    
    array.clear();    
    return array;
  }
  
  private void writeBlock(SortArray array, int start, int end) throws Exception {
    
    OutputStream stream = outBuffer;
    if (config.isCompress()) {
      stream = new BufferedOutputStream(new DeflaterOutputStream(stream));
    }
    ObjectOutputStream out = new ObjectOutputStream(stream);
    
    Object[] object = array.getArray();
    for (int i = start; i < end; i++) {
      out.writeObject(object[i]);
      object[i] = null;
    }
    
    out.close();
    outBuffer.writeTo(raf);
    
    outBuffer.reset();
  }
  
  public void finishWriteRuns() throws Exception {
    
    close();
  }
}
