// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.util;

/**
 * Miscellaneous utility methods.
 * @author Uwe Finke
 */
public class Util {

  static private Text text = new Text(Util.class);
  
  private Util() {
    
  }
  
  /**
   * Compares two <code>Comparable</code>s which may be <code>null</code>.
   * A <code>null</code> value is less than any other value.
   * @param <D> data type
   * @param a first object to compare
   * @param b second object to compare
   * @return <code>&lt;=-1</code> (a &lt; b), <code>0</code> (a = b), or <code>&gt;=1</code> (a &gt; b)  
   */
  static public <D extends Comparable<? super D>> int compare(D a, D b) {
    
    if (a == null) {
      if (b == null) {
        return 0;
      }
      return -1;
    }
    if (b == null) {
      return 1;
    }
    return a.compareTo(b);
  }
  
  /**
   * Compares two <code>int</code> values.
   * @param a first value
   * @param b second value
   * @return <code>-1</code>, <code>0</code>, or <code>1</code>, as defined by <code>Comparable</code>
   */
  static public int compare(int a, int b) {
    
    if (a < b) {
      return -1;
    }
    if (a > b) {
      return 1;
    }
    return 0;
  }
  
  /**
   * Compares two <code>long</code> values.
   * @param a first value
   * @param b second value
   * @return <code>-1</code>, <code>0</code>, or <code>1</code>, as defined by <code>Comparable</code>
   */
  static public int compare(long a, long b) {
    
    if (a < b) {
      return -1;
    }
    if (a > b) {
      return 1;
    }
    return 0;
  }
  
  /**
   * Compares two <code>double</code> values.
   * @param a first value
   * @param b second value
   * @return <code>-1</code>, <code>0</code>, or <code>1</code>, as defined by <code>Comparable</code>
   */
  static public int compare(double a, double b) {
    
    if (a < b) {
      return -1;
    }
    if (a > b) {
      return 1;
    }
    return 0;
  }
  
  /**
   * Returns a method name derived from a string.
   * <p/>
   * The result consists of the prefix, followed by
   * the modified input string.
   * All underline and hyphen characters are eliminated.
   * The first character of the input string and all characters
   * which follow the eliminated characters are returned in upper case.
   * @param input an input string
   * @param prefix a method name prefix, may be <code>null</code>
   * @return a formally valid Java method name
   */
  static public String createMethodName(String input, String prefix) {
    
    StringBuilder sb = new StringBuilder(100);

    if (prefix != null) {
      sb.append(prefix);
    }
    
    boolean upper = true;
    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);
      if (c == '_') {
        upper = true;
      } else if (c == '-') {
        upper = true;
      } else if (upper) {
        sb.append(Character.toUpperCase(c));
        upper = false;
      } else {
        sb.append(c);
      }
    }
    
    return sb.toString();
  }
  
  /**
   * Returns the enum constant of the specified enum type with the specified name.
   * If name is <code>null</code> or has a length of 0, then the result is <code>null</code>.
   * If name doesn't match an enum constant, there will be a second try with
   * the uppercase value of name.  
   * @param <E> the enum type
   * @param enumType the enum type
   * @param name the enum name
   * @return an enum constant
   * @throws NoSuchEnumException
   */
  static public <E extends Enum<E>> E getEnum(Class<E> enumType, String name) throws NoSuchEnumException {
    
    if (name == null || name.length() == 0) {
      return null;
    }
    
    try {
      try {
        return Enum.valueOf(enumType, name);
      } catch (Exception e) {
        return Enum.valueOf(enumType, name.toUpperCase());
      }
    } catch (Exception e) {
      throw new NoSuchEnumException(text.get("enumName", enumType, name));
    }
  }
  
  /**
   * Returns the enum constant of the specified enum type with the specified ordinal number.
   * If ordinal is <code>-1</code>, then the result is <code>null</code>.
   * @param <E> the enum type
   * @param enumType the enum type
   * @param ordinal the ordinal number
   * @return an enum constant
   * @throws NoSuchEnumException
   */
  static public <E extends Enum<E>> E getEnum(Class<E> enumType, int ordinal) throws NoSuchEnumException {
    
    if (ordinal == -1) {
      return null;
    }
    
    try {
      return enumType.getEnumConstants()[ordinal];
    } catch (Exception e) {
      throw new NoSuchEnumException(text.get("enumOrdinal", enumType, ordinal));
    }
  }
  
  /**
   * Returns the enum ordinal number or <code>-1</code> if the argument is <code>null</code>.
   * @param enumConstant
   * @return enum ordinal
   */
  static public int getOrdinal(Enum<?> enumConstant) {

    return (enumConstant == null) ? -1 : enumConstant.ordinal();
  }
  
}
