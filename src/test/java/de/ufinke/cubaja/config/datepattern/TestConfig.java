package de.ufinke.cubaja.config.datepattern;

import java.util.*;
import de.ufinke.cubaja.config.*;

public class TestConfig {

  private Date dateA;
  private Date dateB;
  private Date dateC;

  public TestConfig() {

  }

  public Date getDateA() {

    return dateA;
  }

  public void setDateA(Date dateA) {

    this.dateA = dateA;
  }

  public Date getDateB() {

    return dateB;
  }

  @Pattern("dd.MM.yyyy")
  public void setDateB(Date dateB) {

    this.dateB = dateB;
  }

  public Date getDateC() {

    return dateC;
  }

  @Pattern("dd.MM.yyyy")
  public void setDateC(Date dateC) {

    this.dateC = dateC;
  }

}
