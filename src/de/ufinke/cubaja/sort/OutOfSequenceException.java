package de.ufinke.cubaja.sort;

/**
 * Signals a sort sequence violation.
 * @author Uwe Finke
 */
public class OutOfSequenceException extends RuntimeException {

  /**
   * Constructor.
   * @param msg a message text
   */
  public OutOfSequenceException(String msg) {
    
    super(msg);
  }
}
