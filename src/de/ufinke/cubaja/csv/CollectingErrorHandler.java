// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.util.*;

/**
 * <code>ErrorHandler</code> which collects exceptions.
 * @author Uwe Finke
 */
public class CollectingErrorHandler implements ErrorHandler {

  private List<CsvException> errorList;
  
  public CollectingErrorHandler() {
  
    errorList = new ArrayList<CsvException>();
  }
  
  /**
   * Adds the passed exception to a list.
   */
  public void handleError(CsvException error) throws CsvException {

    errorList.add(error);
  }
  
  /**
   * Returns the error list.
   * @return list
   */
  public List<CsvException> getErrorList() {
    
    return errorList;
  }
  
  /**
   * Returns whether the list contains errors.
   * @return flag
   */
  public boolean hasErrors() {
    
    return errorList.size() > 0;
  }
  
  /**
   * Clears the error list.
   */
  public void reset() {
    
    errorList.clear();
  }
  
}
