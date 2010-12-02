// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

public class XMLWriterConfig {

  private String encoding;
  private String lineSeparator;
  private boolean pretty;
  private String indent;

  public XMLWriterConfig() {

    encoding = "UTF-8";
    lineSeparator = System.getProperty("line.separator");
    pretty = true;
    indent = "  ";
  }

  public String getEncoding() {

    return encoding;
  }

  public void setEncoding(String encoding) {

    this.encoding = encoding;
  }

  public String getLineSeparator() {

    return lineSeparator;
  }

  public void setLineSeparator(String lineSeparator) {

    this.lineSeparator = lineSeparator;
  }

  public boolean isPretty() {

    return pretty;
  }

  public void setPretty(boolean pretty) {

    this.pretty = pretty;
  }

  public String getIndent() {

    return indent;
  }

  public void setIndent(String indent) {

    this.indent = indent;
  }
}
