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
import java.util.Iterator;
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
import de.ufinke.cubaja.util.IteratorException;
import de.ufinke.cubaja.util.Stopwatch;
import de.ufinke.cubaja.util.Text;

class IOManager {

  static private Log logger = LogFactory.getLog(IOManager.class);
  static private Text text = new Text(IOManager.class);
  
  static private class Run implements Iterator<RunCompareInfo>, Iterable<RunCompareInfo> {

    private IOManager ioManager;
    private long nextBlockPosition;
    private int nextBlockLength;
    private Object lastObject;
    private boolean firstBlock;

    Run(IOManager ioManager) {

      this.ioManager = ioManager;
      firstBlock = true;
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

    public boolean hasNext() {

      return nextBlockLength > 0;
    }

    public RunCompareInfo next() {

      try {
        ioManager.readBlock(this);
      } catch (Exception e) {
        throw new IteratorException(e);
      } 
      return new RunCompareInfo(lastObject, firstBlock);
    }

    public void remove() {

      throw new UnsupportedOperationException();
    }
    
    public Iterator<RunCompareInfo> iterator() {
      
      return this;
    }
  }
  
  static private class RunCompareInfo {

    private Object object;
    private boolean firstBlock;
    
    public RunCompareInfo(Object object, boolean firstBlock) {
    
      this.object = object;
      this.firstBlock = firstBlock;
    }
    
    public Object getObject() {
    
      return object;
    }

    public boolean isFirstBlock() {
    
      return firstBlock;
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
  private int blockCount;
  
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

    if (config.isLog()) {
      logger.debug(text.get("sortSwitch", info.id()));
    }
    
    int runs = runPositions.size();
    int capacity = runs / 2 + 2;
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
    
    Comparator<RunCompareInfo> runComparator = new Comparator<RunCompareInfo>() {
    
      @SuppressWarnings("unchecked")
      public int compare(RunCompareInfo a, RunCompareInfo b) {
        
        if (a.isFirstBlock()) {
          return -1;
        }
        if (b.isFirstBlock()) {
          return 1;
        }
        return comparator.compare(a.getObject(), b.getObject());
      }
    };
    
    List<Iterable<RunCompareInfo>> runList = new ArrayList<Iterable<RunCompareInfo>>(runPositions.size());
    for (Long position : runPositions) {
      Run run = new Run(this);
      run.setNextBlockPosition(position + 4);
      run.setNextBlockLength(getBlockLength(position));
      runList.add(run);
    }
    Merger<RunCompareInfo> merger = new Merger<RunCompareInfo>(runComparator, runList);

    Iterator<RunCompareInfo> iterator = merger.iterator();
    while (iterator.hasNext()) {      
      iterator.next();
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
    if (config.isLog()) {
      blockCount++;
      logger.trace("reading block " + blockCount + " at position " + blockPosition + " with length " + blockLength);
    }
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
    SortArray array = new SortArray(size);
    for (int i = 0; i < size; i++) {
      array.add(in.readObject());
    }
    array.setFollowUp(nextLength > 0);
    array.setInfo(info);
    array.setBlockCount(blockCount);
    readQueue.put(array);
    
    run.setNextBlockPosition(nextPosition);
    run.setNextBlockLength(nextLength);
    run.setLastObject(array.getLastEntry());
  }
}
