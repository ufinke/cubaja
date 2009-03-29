package de.ufinke.cubaja.config.test.example2;

public class ParmOneConfig extends ParmConfig {

  private String value;
  private SubElementOneConfig subElement;

  public ParmOneConfig() {

  }

  public String getValue() {

    return value;
  }

  public void setValue(String value) {

    this.value = value;
  }

  public SubElementOneConfig getSubElement() {

    return subElement;
  }

  public void setSubElementOne(SubElementOneConfig subElement) {

    this.subElement = subElement;
  }

}
