package de.ufinke.cubaja.config.test.example1;

import de.ufinke.cubaja.config.*;

public class DatabaseConfig extends ConfigNode {

  private String driver;
  private String url;
  private String user;
  private String password;

  public DatabaseConfig() {

  }

  public String getDriver() {

    return driver;
  }

  @Mandatory
  public void setDriver(String driver) {

    this.driver = driver;
  }

  public String getUrl() {

    return url;
  }

  @Mandatory
  public void setUrl(String url) {

    this.url = url;
  }

  public String getUser() {

    return user;
  }

  public void setUser(String user) {

    this.user = user;
  }

  public String getPassword() {

    return password;
  }

  public void setPassword(String password) {

    this.password = password;
  }
}
