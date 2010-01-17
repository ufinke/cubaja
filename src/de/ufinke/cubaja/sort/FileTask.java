// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.zip.DeflaterOutputStream;
import de.ufinke.cubaja.io.RandomAccessBuffer;

final class FileTask implements Runnable {

  private final SortManager manager;
  private final File file;
  private final RandomAccessFile raf;
  private final RandomAccessBuffer buffer;
  private final List<Run> runList;
  
  private boolean loop;

  private int blockCount;
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
        
      case END_RUN:
        endRun();
        break;
        
      case WRITE_BLOCKS:
        writeBlocks((SortArray) request.getData());
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
    }
  }
  
  private void finishBlock(boolean lastBlock) throws Exception {
    
    out.close();
    
    int len = buffer.size();
    
    if (blockCount == 0) {
      runList.add(new Run(manager, raf.getFilePointer() + 4, len));
    }
    
    buffer.setPosition(0);
    buffer.writeInt(len);
    
    if (lastBlock) {
      buffer.writeInt(0);
    }
    
    buffer.drainTo(raf);
    
    blockCount++;
  }
  
  private void initBlock() throws Exception {
    
    buffer.setPosition(4);
    out = new ObjectOutputStream(buffer.getOutputStream());
  }
  
}
