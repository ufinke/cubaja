// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import de.ufinke.cubaja.io.RandomAccessBuffer;

class IOManager {

  private SortManager info;
  private SortConfig config;
  
  private File file;
  RandomAccessFile raf;
  private RandomAccessBuffer buffer;
    
  private ExecutorService executor;
  
  private SynchronousQueue<SortArray> writeQueue;
  private Callable<SortArray> writeRunCallable;
  private Future<SortArray> writeRunFuture;
  
  private BlockingQueue<Run> readQueue;
  private Future<Object> readBlockFuture;
  private Callable<Object> readBlockCallable;
  
  private List<Long> runPositions;
  
  public IOManager(SortManager info) throws Exception {
    
    this.info = info;
    config = info.getConfig();
    
    open();
    buffer = new RandomAccessBuffer(Info.SortManager, 1000);
    
    executor = Executors.newSingleThreadExecutor();
    
    runPositions = new ArrayList<Long>();
    
    writeQueue = new SynchronousQueue<SortArray>();
    writeRunCallable = new Callable<SortArray>() {
      public SortArray call() throws Exception {
        synchronized (raf) {
          return backgroundWriteRun();
        }
      }
    };
    
    readQueue = new LinkedBlockingQueue<Run>();
    readBlockCallable = new Callable<Object>() {
      public Object call() throws Exception {
        synchronized (raf) {
          return readBlock();
        }
      }
    };
  }
  
  protected void finalize() {
    
    try {
      close();
    } catch (Exception e) {        
    }
  }
  
  public void close() throws Exception {
    
    info.sync();
    
    //readBlockFuture.get();
    
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
    file.deleteOnExit();
  }
  
  public SortArray writeRun(SortArray array) throws Exception {
    
    /*
    info.sync();
    
    SortArray result = (writeRunFuture == null) ? new SortArray(info.getRunSize()) : writeRunFuture.get();
    
    writeRunFuture = executor.submit(writeRunCallable);
    
    writeQueue.put(array);
    
    info.sync();
    
    return result;
    */
    
    runPositions.add(raf.getFilePointer());
    
    int size = array.getSize();
    int maxSize = info.getBlockSize();
    int startIndex = 0;
    while (startIndex < size) {
      int endIndex = Math.min(startIndex + maxSize, size);
      writeBlock(array, startIndex, endIndex);
      startIndex = endIndex;
    }
    
    raf.writeInt(0);
    
    array.clear();
    
    return array;
  }
  
  SortArray backgroundWriteRun() throws Exception {
    
    info.sync();
    
    SortArray array = writeQueue.take();
    
    runPositions.add(raf.getFilePointer());
    
    int size = array.getSize();
    int maxSize = info.getBlockSize();
    int startIndex = 0;
    while (startIndex < size) {
      int endIndex = Math.min(startIndex + maxSize, size);
      writeBlock(array, startIndex, endIndex);
      startIndex = endIndex;
    }
    
    raf.writeInt(0);
    
    array.clear();
    
    info.sync();
    
    return array;
  }
  
  private void writeBlock(SortArray array, int start, int end) throws Exception {

    buffer.setPosition(4);
    
    OutputStream stream = buffer.getOutputStream();
    if (config.isCompress()) {
      stream = new BufferedOutputStream(new DeflaterOutputStream(stream));
    }
    ObjectOutputStream out = new ObjectOutputStream(stream);
    
    out.writeInt(end - start);
    Object[] object = array.getArray();
    for (int i = start; i < end; i++) {
      out.writeObject(object[i]);
      object[i] = null;
    }
    
    out.close();
    
    buffer.setPosition(0);
    buffer.writeInt(buffer.size());
    
    buffer.drainTo(raf);
  }
  
  public void finishWrite() throws Exception {

    /*
    info.sync();
    
    writeRunFuture.get();
    
    info.sync();
    */
  }
  
  public int getRunCount() {
    
    return runPositions.size();
  }
  
  public long getFileSize() throws Exception {
    
    return raf.getFilePointer();
  }
  
  public List<Run> getRuns() throws Exception {

    info.sync();
    
    List<Run> runList = new ArrayList<Run>(runPositions.size());
    for (Long position : runPositions) {
      Run run = new Run(info, this, position + 4, getBlockLength(position));
      runList.add(run);
    }
    
    info.sync();
    
    return runList;
  }
  
  private int getBlockLength(long position) throws Exception {
    
    raf.seek(position);
    return raf.readInt();
  }
  
  Object readBlock() throws Exception {
    
    info.sync();
    
    Run run = readQueue.take();
    
    long blockPosition = run.getBlockPosition();
    int blockLength = run.getBlockLength();
    long nextPosition = blockPosition + blockLength;
    
    raf.seek(blockPosition);
    buffer.reset();
    buffer.transferFrom(raf, blockLength);
    
    int blockEnd = blockLength - 4;
    buffer.setPosition(blockEnd);
    int nextLength = buffer.readInt();
    buffer.cut(0, blockEnd);
    
    buffer.setPosition(0);
    InputStream stream = buffer.getInputStream();
    if (config.isCompress()) {
      stream = new BufferedInputStream(new InflaterInputStream(stream));
    }
    ObjectInputStream in = new ObjectInputStream(stream);

    int size = in.readInt();
    Object[] array = run.getNextArray();
    for (int i = 0; i < size; i++) {
      array[i] = in.readObject();
    }
    run.setNextSize(size);
    run.setBlockPosition(nextPosition);
    run.setBlockLength(nextLength);
    
    info.sync();
    
    run.releaseLatch();
    
    return new Object();
  }
  
  public void requestNextBlock(Run run) throws Exception {

    /*
    info.sync();

    if (readBlockFuture != null) {
      readBlockFuture.get();
    }
    
    readBlockFuture = executor.submit(readBlockCallable);
    readQueue.put(run);
    
    info.sync();
    */
    
    long blockPosition = run.getBlockPosition();
    int blockLength = run.getBlockLength();
    long nextPosition = blockPosition + blockLength;
    
    raf.seek(blockPosition);
    buffer.reset();
    buffer.transferFrom(raf, blockLength);
    
    int blockEnd = blockLength - 4;
    buffer.setPosition(blockEnd);
    int nextLength = buffer.readInt();
    buffer.cut(0, blockEnd);
    
    buffer.setPosition(0);
    InputStream stream = buffer.getInputStream();
    if (config.isCompress()) {
      stream = new BufferedInputStream(new InflaterInputStream(stream));
    }
    ObjectInputStream in = new ObjectInputStream(stream);

    int size = in.readInt();
    Object[] array = run.getNextArray();
    for (int i = 0; i < size; i++) {
      array[i] = in.readObject();
    }
    run.setNextSize(size);
    run.setBlockPosition(nextPosition);
    run.setBlockLength(nextLength);
    
    info.sync();
    
    run.releaseLatch();
  }
}
