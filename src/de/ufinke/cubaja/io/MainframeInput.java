package de.ufinke.cubaja.io;

public interface MainframeInput {

  public boolean nextRecord(RandomAccessBuffer buffer) throws Exception;
  
  public void close() throws Exception;
}
