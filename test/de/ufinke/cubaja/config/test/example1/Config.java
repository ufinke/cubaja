package de.ufinke.cubaja.config.test.example1;

import java.util.HashMap;
import java.util.Map;
import de.ufinke.cubaja.config.*;

public class Config {

  private DatabaseConfig dbone;
  private DatabaseConfig dbtwo;
  private Map<String, FileConfig> fileMap;
  private MailConfig mail;
  private ValuesConfig someValues;

  public Config() {

    fileMap = new HashMap<String, FileConfig>();
  }

  public DatabaseConfig getDbone() {

    return dbone;
  }

  @Mandatory
  public void setDbone(DatabaseConfig dbone) {

    this.dbone = dbone;
  }

  public DatabaseConfig getDbtwo() {

    return dbtwo;
  }

  @Mandatory
  public void setDbtwo(DatabaseConfig dbtwo) {

    this.dbtwo = dbtwo;
  }

  public MailConfig getMail() {

    return mail;
  }

  @Mandatory
  public void setMail(MailConfig mail) {

    this.mail = mail;
  }

  public ValuesConfig getSomeValues() {

    return someValues;
  }

  @Mandatory
  public void setSomeValues(ValuesConfig someValues) {

    this.someValues = someValues;
  }

  public void addFile(FileConfig file) {
    
    fileMap.put(file.getKey(), file);
  }
  
  public String getFileName(String key) {
    
    return fileMap.get(key).getName();
  }
}
