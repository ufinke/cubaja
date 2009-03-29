// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.Comparator;
import de.ufinke.cubaja.util.Util;

/**
 * A <code>Comparator</code> which compares <code>Comparable</code>s.
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
   * Returns the compare value as defined by <code>Comparator</code>.
   * A <code>null</code> value is less than any other value.
   * @return <code>&lt;=-1</code> (a &lt; b), <code>0</code> (a = b), or <code>&gt;=1</code> (a &gt; b)  
   */
  public int compare(D a, D b) {
    
    return Util.compare(a, b);
  }
}
