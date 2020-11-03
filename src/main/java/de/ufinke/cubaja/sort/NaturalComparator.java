// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.Comparator;
import de.ufinke.cubaja.util.Util;

/**
 * A <tt>Comparator</tt> which compares <tt>Comparable</tt>s.
 * @author Uwe Finke
 * @param <D> data type
 */
public class NaturalComparator<D extends Comparable<? super D>> implements Comparator<D> {

  /**
   * Constructor.
   */
  public NaturalComparator() {
    
  }
  
  /**
   * Returns the comparison result as defined by <tt>Comparator</tt>.
   * A <tt>null</tt> value is less than any other value.
   * @return <tt>&lt;=-1</tt> (a &lt; b), <tt>0</tt> (a = b), or <tt>&gt;=1</tt> (a &gt; b)  
   */
  public int compare(D a, D b) {
    
    return Util.compare(a, b);
  }
}
