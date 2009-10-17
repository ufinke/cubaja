// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

class TypeCombination {

  private int sqlType;
  private Class<?> setterClass;
  private int hashCode;
  
  TypeCombination(int sqlType, Class<?> setterClass) {

    this.sqlType = sqlType;
    this.setterClass = setterClass;
    
    hashCode = sqlType << 16 + setterClass.hashCode();
  }
  
  public boolean equals(Object o) {
    
    TypeCombination other = (TypeCombination) o;
    return sqlType == other.sqlType && setterClass == other.setterClass;
  }
  
  public int hashCode() {
    
    return hashCode;
  }
}
