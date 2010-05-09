// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import de.ufinke.cubaja.io.RandomAccessBuffer;

final class FileTask implements Runnable {

  private final SortManager manager;
  private final File file;
  private final RandomAccessFile raf;
  private final RandomAccessBuffer buffer;
  private final List<Run> runList;
  
  private boolean loop;

  private int blockCount;
  private int objectCount;
  private ObjectOutputStream out;

  public FileTask(SortManager manager) throws Exception {

    this.manager = manager;

    SortConfig config = manager.getConfig();
    
    File dir = new File(config.getWorkDirectory());
    dir.mkdirs();
    
    SimpleDateFormat sdf = new SimpleDateFormat("_yyyyMMdd_HHmmssSSS");
    File file = null;
    boolean exists = true;
    while (exists) {
      StringBuilder sb = new StringBuilder(50);
      sb.append(config.getFilePrefix());
      sb.append(sdf.format(new Date()));
      sb.append(".tmp");
      file = new File(dir, sb.toString());
      exists = file.exists();
    }

    file.deleteOnExit();
    this.file = file;
    raf = new RandomAccessFile(file, "rw");
    
    buffer = new RandomAccessBuffer(manager.getBlockSize() + 1024, 1024);
    runList = new ArrayList<Run>();
  }
  
  protected void finalize() {
    
    try {
      close();
    } catch (Exception e) {        
    }
  }
  
  public void run() {
    
    try {
      work();
    } catch (Throwable t) {
      manager.setError(t);
    }
  }
  
  private void work() throws Exception {

    final BlockingQueue<Request> queue = manager.getFileQueue();
    loop = true;
    
    while (loop) {
      final Request request = queue.poll(1, TimeUnit.SECONDS);
      if (manager.hasError()) {
        loop = false;
      } else if (request != null) {
        handleRequest(request);
      }
    }
  }
  
  private void handleRequest(final Request request) throws Exception {
    
    switch (request.getType()) {

      case BEGIN_RUN:
        beginRun();
        break;
        
      case WRITE_BLOCKS:
        writeBlocks((SortArray) request.getData());
        break;
        
      case END_RUN:
        endRun();
        break;
        
      case SWITCH_STATE:
        switchState();
        break;
        
      case READ_BLOCK:
        readBlock((Run) request.getData());
        break;
        
      case CLOSE:
        close();
        break;
    }
  }
  
  private void beginRun() throws Exception {

    blockCount = 0;
    initBlock();
  }
  
  private void endRun() throws Exception {
    
    finishBlock(true);
  }
  
  private void writeBlocks(SortArray sortArray) throws Exception {

    final Object[] array = sortArray.getArray();
    final int size = sortArray.getSize();    
    final int limit = manager.getBlockSize();
    
    int position = 0;
    while (position < size) {
      if (buffer.size() >= limit) {
        finishBlock(false);
        initBlock();
      }
      out.writeObject(array[position++]);
      objectCount++;
    }
  }
  
  private void initBlock() throws Exception {
    
    objectCount = 0;
    buffer.setPosition(8);
    out = new ObjectOutputStream(buffer.getOutputStream());
  }
  
  private void finishBlock(boolean lastBlock) throws Exception {
    
    out.close();
    
    final RandomAccessBuffer buffer = this.buffer;
    
    int len = buffer.size();
    
    if (blockCount == 0) {
      runList.add(new Run(manager, raf.getFilePointer() + 4, len));
    }
    
    buffer.setPosition(0);
    buffer.writeInt(len);
    buffer.writeInt(objectCount);
    
    if (lastBlock) {
      buffer.setPosition(buffer.size());
      buffer.writeInt(0);
    }
    
    buffer.drainTo(raf);
    
    blockCount++;
  }
  
  private void switchState() throws Exception {
    
    if (manager.isDebug()) {
      manager.debug("sortFile", runList.size(), raf.getFilePointer());
    }
    
    final BlockingQueue<Request> queue = manager.getSortQueue();
    final Request request = new Request(RequestType.INIT_RUN_MERGE, runList);
    
    boolean written = false;
    while ((! written) && loop) {
      written = queue.offer(request, 1, TimeUnit.SECONDS);
      if (manager.hasError()) {
        loop = false;
      }
    }    
  }
  
  private void readBlock(Run run) throws Exception {
    
    final long blockPosition = run.getBlockPosition();
    final int blockLength = run.getBlockLength();
    
    run.setBlockPosition(blockPosition + blockLength);

    final RandomAccessBuffer buffer = this.buffer;
    
    raf.seek(blockPosition);
    buffer.reset();
    buffer.transferFullyFrom(raf, blockLength);
    
    int blockEnd = blockLength - 4;
    buffer.setPosition(blockEnd);
    run.setBlockLength(buffer.readInt());
    buffer.cut(0, blockEnd);
    buffer.setPosition(0);
    
    int size = buffer.readInt();
    Object[] array = new Object[size];
    
    ObjectInputStream in = new ObjectInputStream(buffer.getInputStream());
    for (int i = 0; i < size; i++) {
      array[i] = in.readObject();
    }
    in.close();
    
    run.setNextArray(new SortArray(array, size));
    run.releaseLatch();
  }
  
  private void close() throws Exception {
    
    raf.close();
    file.delete();
    loop = false;
  }
  
}
