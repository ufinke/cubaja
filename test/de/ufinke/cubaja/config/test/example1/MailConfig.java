package de.ufinke.cubaja.config.test.example1;

import java.util.ArrayList;
import java.util.List;
import de.ufinke.cubaja.config.*;

public class MailConfig extends ConfigNode {

  private String server;
  private int port;
  private String subject;
  private String from;
  private String bounceTo;
  private String content;
  private List<MailAddressConfig> addressList;

  public MailConfig() {

    port = 25;
    addressList = new ArrayList<MailAddressConfig>();
  }

  public String getServer() {

    return server;
  }

  @Mandatory
  public void setServer(String server) {

    this.server = server;
  }

  public int getPort() {

    return port;
  }

  public void setPort(int port) {

    this.port = port;
  }

  public String getSubject() {

    return subject;
  }

  @Mandatory
  public void setSubject(String subject) {

    this.subject = subject;
  }

  public String getFrom() {

    return from;
  }

  @Mandatory
  public void setFrom(String from) {

    this.from = from;
  }

  public String getBounceTo() {

    if (bounceTo == null) {
      bounceTo = from;
    }
    return bounceTo;
  }

  public void setBounceTo(String bounceTo) {

    this.bounceTo = bounceTo;
  }
  
  public String getContent() {
  
    return content;
  }
  
  @Mandatory
  public void setContent(String content) {
  
    this.content = content;
  }
  
  public void addTo(MailAddressConfig address) {
    
    addressList.add(address);
  }
  
  public List<MailAddressConfig> getAddressList() {
    
    return addressList;
  }
}
