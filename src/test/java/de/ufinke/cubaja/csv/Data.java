package de.ufinke.cubaja.csv;

import java.math.*;

public class Data {

  private BigDecimal bigDecimalField;
  private BigInteger bigIntegerField;

  public Data() {

  }

  public BigDecimal getBigDecimalField() {

    return bigDecimalField;
  }

  public void setBigDecimalField(BigDecimal bigDecimalField) {

    this.bigDecimalField = bigDecimalField;
  }

  public BigInteger getBigIntegerField() {

    return bigIntegerField;
  }

  public void setBigIntegerField(BigInteger bigIntegerField) {

    this.bigIntegerField = bigIntegerField;
  }
}
