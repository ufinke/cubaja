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
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import de.ufinke.cubaja.io.RandomAccessBuffer;
import de.ufinke.cubaja.util.*;

class IOManager {

  static private class Run implements Comparable<Run> {

    @SuppressWarnings("rawtypes")
    private Comparator comparator;
    private long nextBlockPosition;
    private int nextBlockLength;
    private boolean firstBlock;
    Object lastObject;
    int id;

    @SuppressWarnings("rawtypes")
    Run(int id, Comparator comparator) {

      this.id = id;
      this.comparator = comparator;
      firstBlock = true;
    }
    
    @SuppressWarnings("unchecked")
    public int compareTo(Run other) {
      
      if (firstBlock) {
        if (other.firstBlock) {
          return Util.compare(id, other.id);
        }
        return -1;
      } 
      
      if (other.firstBlock) {
        return 1;
      }
      
      int result = comparator.compare(lastObject, other.lastObject);
      if (result == 0) {
        return Util.compare(id, other.id);
      }
      return result;
    }

    public void setLastObject(Object lastObject) {

      this.lastObject = lastObject;
      firstBlock = false;
    }
    
    public long getNextBlockPosition() {

      return nextBlockPosition;
    }

    public void setNextBlockPosition(long nextBlockPosition) {

      this.nextBlockPosition = nextBlockPosition;
    }

    public int getNextBlockLength() {

      return nextBlockLength;
    }

    public void setNextBlockLength(int nextBlockLength) {

      this.nextBlockLength = nextBlockLength;
    }
  }
  
  private Info info;
  private SortConfig config;
  
  private File file;
  private RandomAccessFile raf;
  private RandomAccessBuffer buffer;
    
  private ExecutorService executor;
  
  private SynchronousQueue<SortArray> writeQueue;
  private Callable<SortArray> writeRunCallable;
  private Future<SortArray> writeRunFuture;
  private List<Long> runPositions;
  private BlockingQueue<SortArray> readQueue;
  private Future<Object> readRunFuture;
  private PriorityQueue<Run> runQueue;
  
  public IOManager(Info info) throws Exception {
    
    this.info = info;
    config = info.getConfig();
    
    open();
    buffer = new RandomAccessBuffer(Info.BYTES_PER_BLOCK, 1000);
    
    executor = Executors.newSingleThreadExecutor();
    
    runPositions = new ArrayList<Long>();
    writeQueue = new SynchronousQueue<SortArray>();
    writeRunCallable = new Callable<SortArray>() {
      public SortArray call() throws Exception {
        return backgroundWriteRun();
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
    
    readRunFuture.get();
    
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
    
    SortArray result = (writeRunFuture == null) ? new SortArray(info.getRunSize()) : writeRunFuture.get();
    
    writeRunFuture = executor.submit(writeRunCallable);
    
    writeQueue.put(array);
    
    return result;
  }
  
  SortArray backgroundWriteRun() throws Exception {
    
    SortArray array = writeQueue.take();
    
    runPositions.add(raf.getFilePointer());
    
    int startIndex = 0;
    while (startIndex < array.getSize()) {
      int endIndex = Math.min(startIndex + info.getBlockSize(), array.getSize());
      writeBlock(array, startIndex, endIndex);
      startIndex = endIndex;
    }
    
    raf.writeInt(0);
    
    array.clear();    
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
    
    writeRunFuture.get();
  }
  
  public int getRunCount() {
    
    return runPositions.size();
  }
  
  public long getFileSize() throws Exception {
    
    return raf.getFilePointer();
  }
  
  public List<Iterable<Object>> getRuns() throws Exception {

    int runs = runPositions.size();
    int capacity = Math.max(runs / 2, 2);
    readQueue = new ArrayBlockingQueue<SortArray>(capacity);
    
    Callable<Object> readCallable = new Callable<Object>() {
      public Object call() throws Exception {
        return backgroundRead();
      }
    };
    readRunFuture = executor.submit(readCallable);
    
    List<Iterable<Object>> runList = new ArrayList<Iterable<Object>>(runs);
    for (int i = 0; i < runs; i++) {
      SortArray run = new SortArray(readQueue);
      runList.add(run);
    }
    
    return runList;
  }
  
  Object backgroundRead() throws Exception {

    runQueue = new PriorityQueue<Run>(runPositions.size());
    int runId = 0;    
    
    for (Long position : runPositions) {
      Run run = new Run(++runId, info.getComparator());
      run.setNextBlockPosition(position + 4);
      run.setNextBlockLength(getBlockLength(position));
      runQueue.offer(run);
    }
    
    Run currentRun = runQueue.poll();
    while (currentRun != null) {
      readBlock(currentRun);
      currentRun = runQueue.poll();
    }
    
    return new Object();
  }
  
  private int getBlockLength(long position) throws Exception {
    
    raf.seek(position);
    return raf.readInt();
  }
  
  void readBlock(Run run) throws Exception {
    
    long blockPosition = run.getNextBlockPosition();
    int blockLength = run.getNextBlockLength();
    long nextPosition = blockPosition + blockLength;
    System.out.println("block " + run.id + ": " + run.lastObject);
    
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
    SortArray array = new SortArray(size);
    for (int i = 0; i < size; i++) {
      array.add(in.readObject());
    }
    array.setFollowUp(nextLength > 0);
    Object lastObject = array.getLastEntry();
    readQueue.put(array);
    
    if (nextLength == 0) {
      return;
    }
    
    run.setNextBlockPosition(nextPosition);
    run.setNextBlockLength(nextLength);
    run.setLastObject(lastObject);
    runQueue.offer(run);
  }
}
