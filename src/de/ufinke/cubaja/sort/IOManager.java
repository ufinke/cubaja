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
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
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
  private RandomAccessBuffer buffer;
    
  private ExecutorService executor;
  
  private SynchronousQueue<SortArray> writeQueue;
  private Callable<SortArray> writeRunCallable;
  private Future<SortArray> writeRunFuture;
  private List<Long> runPositions;
  private BlockingQueue<SortArray> readQueue;
  private Future<Object> readRunFuture;
  
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

    runPositions.add(raf.getFilePointer());
    
    int startIndex = 0;
    while (startIndex < array.getSize()) {
      int endIndex = Math.min(startIndex + info.getBlockSize(), array.getSize());
      writeBlock(array, startIndex, endIndex);
      startIndex = endIndex;
    }
    
    raf.writeInt(0);
    
    if (config.isLog()) {
      long elapsed = watch.elapsedMillis();
      logger.trace(text.get("runWritten", info.id(), array.getSize(), elapsed, runPositions.size()));
    }
    
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
  
  public List<SortArray> getRuns() throws Exception {
    
    writeRunFuture.get();

    int runs = runPositions.size();
    int capacity = runs + runs / 2;
    readQueue = new ArrayBlockingQueue<SortArray>(capacity);
    
    Callable<Object> readCallable = new Callable<Object>() {
      public Object call() throws Exception {
        return backgroundRead();
      }
    };
    readRunFuture = executor.submit(readCallable);
    
    List<SortArray> runList = new ArrayList<SortArray>(runs);
    for (int i = 0; i < runs; i++) {
      SortArray run = new SortArray(readQueue);
      runList.add(run);
    }
    
    return runList;
  }
  
  @SuppressWarnings("rawtypes")
  Object backgroundRead() throws Exception {
    
    final Comparator comparator = info.getComparator();
    
    Comparator<Run> runComparator = new Comparator<Run>() {
    
      @SuppressWarnings("unchecked")
      public int compare(Run a, Run b) {
        
        if (a.isFirstBlock()) {
          return -1;
        }
        if (b.isFirstBlock()) {
          return 1;
        }
        return comparator.compare(a.getLastObject(), b.getLastObject());
      }
    };
    
    List<Iterable<Run>> runList = new ArrayList<Iterable<Run>>(runPositions.size());
    for (Long position : runPositions) {
      Run run = new Run();
      run.setNextBlockPosition(position + 4);
      run.setNextBlockLength(getBlockLength(position));
      runList.add(run);
    }
    Merger<Run> merger = new Merger<Run>(runComparator, runList);
    
    for (Run selectedRun : merger) {
      readBlock(selectedRun);
    }
    
    return new Object();
  }
  
  private int getBlockLength(long position) throws Exception {
    
    raf.seek(position);
    return raf.readInt();
  }
  
  private void readBlock(Run run) throws Exception {
    
    long blockPosition = run.getNextBlockPosition();
    int blockLength = run.getNextBlockLength();
    
    buffer.reset();
    raf.seek(blockPosition);
    buffer.transferFrom(raf, blockLength);
    
    int blockEnd = blockLength - 4;
    buffer.setPosition(blockEnd);
    run.setNextBlockLength(buffer.readInt());
    run.setNextBlockPosition(blockPosition + blockLength);
    
    buffer.cut(0, blockEnd);
    InputStream stream = buffer.getInputStream();
    if (config.isCompress()) {
      stream = new BufferedInputStream(new InflaterInputStream(stream));
    }
    ObjectInputStream in = new ObjectInputStream(stream);

    int size = in.readInt();
    SortArray array = new SortArray(in.readInt());
    for (int i = 0; i < size; i++) {
      array.add(in.readObject());
    }
    readQueue.put(array);
  }
}
