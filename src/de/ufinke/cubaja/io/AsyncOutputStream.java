// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AsyncOutputStream extends FilterOutputStream {

  static enum Action {
    WRITE,
    FLUSH,
    CLOSE
  }
  
  static class Task {
    
    private byte[] buffer;
    private int offset;
    private int length;
    private Action action;
    
    Task(byte[] buffer, int offset, int length, Action action) {
      
      this.buffer = buffer;
      this.offset = offset;
      this.length = length;
      this.action = action;
    }
    
    byte[] getBuffer() {
      
      return buffer;
    }
    
    int getOffset() {
      
      return offset;
    }
    
    int getLength() {
      
      return length;
    }

    Action getAction() {
      
      return action;
    }
  }
  
  static private final int DEFAULT_BUFFER_SIZE = 8192;
  
  private boolean closed;
  
  private int bufferSize;
  private volatile byte[] buffer;
  private byte[] secondBuffer;
  private int bufferPosition;
  
  private BlockingQueue<Task> queue;
  private ExecutorService executorService;
  private ExecutorCompletionService<Object> executorCompletionService;
  
  public AsyncOutputStream(OutputStream out) throws IOException {
    
    this(out, DEFAULT_BUFFER_SIZE);
  }
  
  public AsyncOutputStream(OutputStream out, int bufferSize) throws IOException {
    
    super(out);
    
    this.bufferSize = bufferSize;
    
    queue = new ArrayBlockingQueue<Task>(1);
    
    executorService = Executors.newSingleThreadExecutor();
    executorCompletionService = new ExecutorCompletionService<Object>(executorService);
    executorCompletionService.submit(new Callable<Object>() {
      public Object call() throws Exception {
        return work();
      }
    });
    
    buffer = new byte[bufferSize];
    secondBuffer = new byte[bufferSize];
  }
  
  Object work() throws Exception {
    
    boolean loop = true;
    
    while (loop) {
      
      Task task = queue.take();

      int length = task.getLength();
      if (length > 0) {        
        out.write(task.getBuffer(), task.getOffset(), length);
      }
      
      switch (task.getAction()) {
        case FLUSH:
          out.flush();
          break;
        case CLOSE:
          out.close();
          loop = false;
          break;
      } 
    }
    
    return null;
  }
  
  private void throwIOException(Throwable t) throws IOException {
    
    IOException ex = new IOException(t.getLocalizedMessage());
    ex.initCause(t);
    throw ex;
  }
  
  private void switchBuffer(Action action) throws IOException {
    
    writeBuffer(buffer, 0, bufferPosition, action);
    
    byte[] temp = buffer;
    buffer = secondBuffer;
    secondBuffer = temp;
    
    bufferPosition = 0;
  }
  
  private void writeBuffer(byte[] buf, int offset, int length, Action action) throws IOException {
    
    pollFuture();
    
    Task task = new Task(buf, offset, length, action);
    
    try {      
      queue.put(task);
    } catch (Exception e) {
      throwIOException(e);
    }
  }
  
  private void pollFuture() throws IOException {
    
    Future<Object> result = executorCompletionService.poll();
    if (result != null) {
      checkFuture(result);
    }
  }
  
  private void takeFuture() throws IOException {
    
    try {      
      checkFuture(executorCompletionService.take());
    } catch (Exception e) {
      throwIOException(e);
    }
  }
  
  private void checkFuture(Future<Object> result) throws IOException {
    
    try {
      result.get();
    } catch (ExecutionException ee) {
      Throwable t = ee.getCause();
      if (t instanceof IOException) {
        throw (IOException) t;
      }
      throwIOException(ee);
    } catch (Exception e) {
      throwIOException(e);
    }
  }
  
  public void write(int b) throws IOException {
    
    if (closed) {
      throw new IOException("stream closed");
    }
    
    buffer[bufferPosition++] = (byte) b;
    
    if (bufferPosition == bufferSize) {
      switchBuffer(Action.WRITE);
    }
  }
  
  public void write(byte[] buf, int offset, int length) throws IOException {

    if (closed) {
      throw new IOException("stream closed");
    }

    while (length > 0) {
      int chunkLength = Math.min(length, bufferSize - bufferPosition);
      System.arraycopy(buf, offset, buffer, bufferPosition, chunkLength);
      bufferPosition += chunkLength;
      if (bufferPosition == bufferSize) {
        switchBuffer(Action.WRITE);
      }
      offset += chunkLength;
      length -= chunkLength;
    }        
  }
  
  public void flush() throws IOException {
    
    if (closed) {
      throw new IOException("stream closed");
    }

    switchBuffer(Action.FLUSH);
  }
  
  public void close() throws IOException {
    
    if (closed) {
      return;
    }

    closed = true;
    
    switchBuffer(Action.CLOSE);
    takeFuture();
    executorService.shutdown();
    
    out = null;
    buffer = null;
    secondBuffer = null;
  }
}
