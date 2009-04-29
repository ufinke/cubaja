// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import java.io.*;
import java.util.concurrent.*;

public class AsynchronousOutputStream extends FilterOutputStream {

  static class Task {
    
    private byte[] buffer;
    private int bufferPosition;
    private boolean close;
    
    Task(byte[] buffer, int bufferPosition, boolean close) {
      
      this.buffer = buffer;
      this.bufferPosition = bufferPosition;
      this.close = close;
    }
    
    byte[] getBuffer() {
      
      return buffer;
    }
    
    int getBufferPosition() {
      
      return bufferPosition;
    }
    
    boolean isClose() {
      
      return close;
    }
  }
  
  private final int bufferSize;
  private int bufferPosition;
  private byte[] buffer;
  
  private BlockingQueue<Task> queue;
  private ExecutorService executorService;
  private ExecutorCompletionService<Object> executorCompletionService;
  
  public AsynchronousOutputStream(OutputStream out, int bufferSize) throws IOException {
    
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
    bufferPosition = 0;
  }
  
  Object work() throws Exception {
    
    boolean close = false;
    
    do {
      
      Task task = queue.take();
      
      close = task.isClose();
      
    } while (! close);
    
    out.close();
    
    return null;
  }
  
  private void throwIOException(Throwable t) throws IOException {
    
    IOException ex = new IOException("problem");
    ex.initCause(t);
    throw ex;
  }
  
  private void switchBuffer() throws IOException {
    
    writeBuffer(false);
    //TODO switch buffer array
  }
  
  private void writeBuffer(boolean lastWrite) throws IOException {
    
    pollFuture();
    
    Task task = new Task(buffer, bufferPosition, lastWrite);
    
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
    
    buffer[bufferPosition++] = (byte) b;
    
    if (bufferPosition == bufferSize) {
      switchBuffer();
    }
  }
  
  public void write(byte[] b, int off, int len) throws IOException {
    
    //TODO
  }
  
  public void flush() throws IOException {
    
    switchBuffer();
  }
  
  public void close() throws IOException {
    
    writeBuffer(true);
    takeFuture();
    executorService.shutdown();
  }
}
