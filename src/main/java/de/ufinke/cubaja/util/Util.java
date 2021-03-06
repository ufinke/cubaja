// Copyright (c) 2007 - 2013, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.util;

import java.io.File;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Miscellaneous utility methods.
 * @author Uwe Finke
 */
public class Util {

  static private Text text = Text.getPackageInstance(Util.class);
  
  static private double P_FACTOR[] = new double[] {
    1.0,
    10.0,
    100.0,
    1000.0,
    10000.0,
    100000.0,
    1000000.0,
    10000000.0,
    100000000.0,
    1000000000.0,
    10000000000.0,
    100000000000.0,
    1000000000000.0,
    10000000000000.0,
    100000000000000.0,
    1000000000000000.0,
    10000000000000000.0,
    100000000000000000.0
  };
  static private double MAX_DOUBLE = Long.MAX_VALUE;
  static private MathContext MATH_CONTEXT = new MathContext(64, RoundingMode.HALF_UP);
  
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
   * Returns the maximum value.
   * A <code>null</code> value isn't compared.
   * If the paramter list is empty, the result is <code>null</code>.
   * @param <D> data type
   * @param comparables values
   * @return the maximum value
   */
  static public <D extends Comparable<? super D>> D max(D... comparables) {
    
    D max = null;
    
    for (int i = 0; i < comparables.length; i++) {
      D element = comparables[i];
      if (element != null) {
        if (max == null || max.compareTo(element) < 0) {
          max = element;
        }
      }
    }
    
    return max;
  }
  
  /**
   * Returns the minimum value.
   * A <code>null</code> value isn't compared.
   * If the paramter list is empty, the result is <code>null</code>.
   * @param <D> data type
   * @param comparables values
   * @return the minimum value
   */
  static public <D extends Comparable<? super D>> D min(D... comparables) {
    
    D min = null;
    
    for (int i = 0; i < comparables.length; i++) {
      D element = comparables[i];
      if (element != null) {
        if (min == null || min.compareTo(element) > 0) {
          min = element;
        }
      }
    }
    
    return min;
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
   * Tests if two objects which may be <code>null</code> are equal.
   * If both parameters are <code>null</code> the result is <code>true</code>.
   * @param <D> data type
   * @param a first value
   * @param b second value
   * @return <code>true</code> or <code>false</code>
   */
  static public <D extends Object> boolean isEqual(D a, D b) {
    
    if (a == null) {
      return b == null;
    }
    
    if (b == null) {
      return false;
    }
    
    return a.equals(b);
  }
  
  /**
   * <p>
   * Returns a method name derived from a string.
   * </p><p>
   * The result consists of the prefix, followed by
   * the modified input string.
   * All underline and hyphen characters are eliminated.
   * The first character of the input string and all characters
   * which follow the eliminated characters are returned in upper case.
   * </p>
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
   * Creates an instance of a class.
   * If the list of constructor argument parameters is not empty,
   * this method uses the reflection API to instantiate an object.
   * @param <D> class type
   * @param clazz class
   * @param constructorArgs argument types
   * @return object
   * @throws Exception when instance could not be created
   */
  static public <D> D createInstance(Class<D> clazz, Object... constructorArgs) throws Exception {
    
    if (constructorArgs.length == 0) {
      return clazz.newInstance();
    }
    
    Class<?>[] argClasses = new Class<?>[constructorArgs.length];
    for (int i = 0; i < constructorArgs.length; i++) {
      argClasses[i] = constructorArgs[i].getClass();
    }
    Constructor<D> constructor = clazz.getConstructor(argClasses);
    return constructor.newInstance(constructorArgs);
  }
  
  /**
   * Returns the enum constant of the specified enum type with the specified name.
   * If name is <code>null</code> or has a length of 0, then the result is <code>null</code>.
   * If name does not match an enum constant, there will be a second try with
   * the uppercase value of name.  
   * @param <E> the enum type
   * @param enumType the enum type
   * @param name the enum name
   * @return an enum constant
   * @throws NoSuchEnumException when Enum has no appropriate constant
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
   * @throws NoSuchEnumException when Enum has no appropriate constant
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
   * @param enumConstant enum constant
   * @return enum ordinal
   */
  static public int getOrdinal(Enum<?> enumConstant) {

    return (enumConstant == null) ? -1 : enumConstant.ordinal();
  }
  
  /**
   * Returns the file path.
   * Returns the canonical path, or - in case of failure - the absolute path.
   * @param file file
   * @return path
   */
  static public String getPath(File file) {
    
    try {
      return file.getCanonicalPath();
    } catch (Exception e) {
      return file.getAbsolutePath();
    }
  }
  
  /**
   * Formats a date.
   * Returns an empty string if date is <code>null</code>.
   * @param date date value
   * @param pattern formatter pattern
   * @return formatted date
   */
  static public String format(Date date, String pattern) {
    
    if (date == null) {
      return "";
    }
    
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    return sdf.format(date);
  }
  
  /**
   * Formats a <code>BigDecimal</code> to plain text.
   * @param value numeric value
   * @param scale number of fraction digits
   * @param decimalChar the character representing the decimal point
   * @param trim if <code>true</code>, trailing zeroes in the fractional part are removed
   * @return formatted string
   */
  static public String format(BigDecimal value, int scale, char decimalChar, boolean trim) {
    
    if (value == null) {
      return "";
    }
    
    if (value.scale() != scale) {
      value = value.setScale(scale, RoundingMode.HALF_UP);
    }
    
    String sValue = value.toPlainString();
    
    if (value.scale() <= 0) {
      return sValue;
    }
    
    if (trim) {
      int length = sValue.length();
      while (sValue.charAt(length - 1) == '0') {
        length--;
      }
      if (sValue.charAt(length - 1) == '.') {
        return sValue.substring(0, length - 1);
      }
      sValue = sValue.substring(0, length);
    }
    
    if (decimalChar != '.') {
      sValue = sValue.replace('.', decimalChar);
    }
    
    return sValue;
  }
  
  /**
   * Formats a <code>double</code> value with a limited number of fraction digits.
   * @param value numeric value
   * @param scale number of fraction digits
   * @param decimalChar the character representing the decimal point
   * @param trim if <code>true</code>, trailing zeroes in the fractional part are removed
   * @return formatted string
   */
  static public String format(double value, int scale, char decimalChar, boolean trim) {
    
    if (value == 0.0) {
      return "0";
    }
    
    if (Double.isInfinite(value)) {
      return "";
    }
    if (Double.isNaN(value)) {
      return "";
    }
    if (scale < 0) {
      throw new IllegalArgumentException(text.get("doubleScaleMin", scale));
    }
    if (scale > 17) {
      throw new IllegalArgumentException(text.get("doubleScaleMax", scale));
    }

    boolean isNegative = value < 0;
    double rValue = (isNegative ? value * -1 : value) * P_FACTOR[scale] + 0.5; 
    
    if (rValue > MAX_DOUBLE) {
      return format(new BigDecimal(value, MATH_CONTEXT), scale, decimalChar, trim);
    }
        
    String s = Long.toString((long) rValue);
    int sLen = s.length();
    int sPos = 0;
    
    StringBuilder sb = new StringBuilder(24);
    
    if (isNegative) {
      sb.append('-');
    }
    
    if (scale == 0) {
      sb.append(s);
      return sb.toString();
    }
    
    int integerLength = sLen - scale;
    
    if (integerLength <= 0) {
      sb.append('0');
    } else {
      while (sPos < integerLength) {
        sb.append(s.charAt(sPos++));
      }
    }
    
    sb.append(decimalChar);
    
    while (integerLength < 0) {
      sb.append('0');
      integerLength++;
    }
    
    while (sPos < sLen) {
      sb.append(s.charAt(sPos++));
    }
    
    if (! trim) {
      return sb.toString();
    }
    
    int end = sb.length() - 1;
    while (sb.charAt(end) == '0') {
      end--;
    }
    if (sb.charAt(end) == decimalChar) {
      end--;
    }
    return sb.substring(0, end + 1);
  }
  
  /**
   * Formats whitespace within a text.
   * All consecutive 
   * {@link java.lang.Character#isWhitespace(char) whitespace}  
   * characters in a text are replaced by one single space character.
   * Leading and trailing whitespaces are trimmed. 
   * Returns an empty string when parameter is null.
   * @param text input string
   * @return normalized text
   */
  static public String normalize(String text) {
    
    if (text == null) {
      return "";
    }
    
    StringBuilder sb = new StringBuilder(text.length());
    boolean whitespacePending = false;
    
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      if (Character.isWhitespace(c)) {
        whitespacePending = true;
      } else {
        if (whitespacePending) {
          sb.append(' ');
          whitespacePending = false;
        }
        sb.append(c);
      }
    }
    
    return sb.toString();
  }
  
  /**
   * Tests whether a <code>String</code> doesn't have content.
   * The result is <code>true</code> if the input is <code>null</code>
   * or the length of the trimmed string is <code>0</code>.
   * @param string string to test
   * @return flag
   */
  static public boolean isEmpty(String string) {
    
    return string == null || string.trim().length() == 0;
  }
  
  /**
   * Tests whether a <code>String</code> has content.
   * The result is <code>true</code> if the input is not <code>null</code>
   * and the length of the trimmed string is greater than <code>0</code>.
   * @param string string to test
   * @return flag
   */
  static public boolean hasContent(String string) {
    
    return string != null && string.trim().length() > 0;
  }

}
