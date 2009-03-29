package de.ufinke.cubaja.io;

/**
 * Writes and reads objects to <code>BinaryOutputStream</code> and from <code>BinaryInputStream</code>.
 * @author Uwe Finke
 * @param <T> object type
 */
public interface BinaryStreamer<T> {

  /**
   * Writes object to <code>BinaryOutputStream</code>.
   * @param stream output stream
   * @param object object to write
   * @throws Exception
   */
  public void write(BinaryOutputStream stream, T object) throws Exception;
  
  /**
   * Reads object from <code>BinaryInputStream</code>.
   * @param stream input stream
   * @return object
   * @throws Exception
   */
  public T read(BinaryInputStream stream) throws Exception;
}
