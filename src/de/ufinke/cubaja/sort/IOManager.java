// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.zip.DeflaterOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.ufinke.cubaja.io.RandomAccessBuffer;
import de.ufinke.cubaja.util.Stopwatch;
import de.ufinke.cubaja.util.Text;

class IOManager {

  static private Log logger = LogFactory.getLog(IOManager.class);
  static private Text text = new Text(IOManager.class);
  
  private Info info;
  private SortConfig config;
  
  private File file;
  private RandomAccessFile raf;
    
  private ExecutorService executor;
  
  private SynchronousQueue<SortArray> writeQueue;
  private Callable<SortArray> writeRunCallable;
  private Future<SortArray> writeRunFuture;
  private RandomAccessBuffer outBuffer;
  
  private List<Long> runList;
  
  public IOManager(Info info) throws Exception {
    
    this.info = info;
    config = info.getConfig();
    
    open();
    
    executor = Executors.newSingleThreadExecutor();
    
    runList = new ArrayList<Long>();
    writeQueue = new SynchronousQueue<SortArray>();
    writeRunCallable = new Callable<SortArray>() {
      public SortArray call() throws Exception {
        return backgroundWriteRun();
      }
    };
    outBuffer = new RandomAccessBuffer(Info.BYTES_PER_BLOCK, 1000);
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
    
    if (raf != null) {
      raf.close();
      file.delete();
    }    
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
    
    SortArray result = (writeRunFuture == null) ? new SortArray(info.getRunSize()) : writeRunFuture.get();
    
    writeRunFuture = executor.submit(writeRunCallable);
    
    writeQueue.put(array);
    
    return result;
  }
  
  SortArray backgroundWriteRun() throws Exception {
    
    SortArray array = writeQueue.take();
    
    Stopwatch watch = new Stopwatch();

    runList.add(raf.getFilePointer());
    
    int startIndex = 0;
    while (startIndex < array.getSize()) {
      int endIndex = Math.min(startIndex + info.getBlockSize(), array.getSize());
      writeBlock(array, startIndex, endIndex);
      startIndex = endIndex;
    }
    
    raf.writeInt(0);
    
    if (config.isLog()) {
      long elapsed = watch.elapsedMillis();
      logger.trace(text.get("runWritten", info.id(), array.getSize(), elapsed, runList.size()));
    }
    
    array.clear();    
    return array;
  }
  
  private void writeBlock(SortArray array, int start, int end) throws Exception {

    outBuffer.setPosition(4);
    
    OutputStream stream = outBuffer.getOutputStream();
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
    
    outBuffer.setPosition(0);
    outBuffer.writeInt(outBuffer.size());
    
    outBuffer.drainTo(raf);
  }
  
  public List<Run> getRuns() throws Exception {
    
    writeRunFuture.get();
    
    //TODO
    return null;
  }
}
