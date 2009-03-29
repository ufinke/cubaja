package de.ufinke.cubaja.config.test.example1;

import de.ufinke.cubaja.config.*;

public class MailAddressConfig {

  private String address;
  private String title;

  public MailAddressConfig() {

    title = "Dear customer";
  }

  public String getAddress() {

    return address;
  }

  @Mandatory
  public void setAddress(String address) {

    this.address = address;
  }

  public String getTitle() {

    return title;
  }

  public void setTitle(String title) {

    this.title = title;
  }
}
