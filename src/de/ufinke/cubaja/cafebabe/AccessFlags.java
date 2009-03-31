// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.cafebabe;

public interface AccessFlags {

  static public final int ACC_PUBLIC       = 0x0001;
  static public final int ACC_PRIVATE      = 0x0002;
  static public final int ACC_PROTECTED    = 0x0004;
  static public final int ACC_STATIC       = 0x0008;
  static public final int ACC_FINAL        = 0x0010;
  static public final int ACC_SUPER        = 0x0020;
  static public final int ACC_SYNCHRONIZED = 0x0020;
  static public final int ACC_VOLATILE     = 0x0040;
  static public final int ACC_BRIDGE       = 0x0040;
  static public final int ACC_TRANSIENT    = 0x0080;
  static public final int ACC_VARARGS      = 0x0080;
  static public final int ACC_NATIVE       = 0x0100;
  static public final int ACC_INTERFACE    = 0x0200;
  static public final int ACC_ABSTRACT     = 0x0400;
  static public final int ACC_STRICT       = 0x0800;
  static public final int ACC_SYNTHETIC    = 0x1000;
  static public final int ACC_ANNOTATION   = 0x2000;
  static public final int ACC_ENUM         = 0x4000;
  
}
