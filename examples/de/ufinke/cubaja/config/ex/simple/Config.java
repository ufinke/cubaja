package de.ufinke.cubaja.config.ex.simple;

import java.util.Date;
import de.ufinke.cubaja.io.FileConfig;
import de.ufinke.cubaja.sql.DatabaseConfig;
import de.ufinke.cubaja.config.*;

public class Config {

  private Date dateFrom;
  private Date dateTo;
  private DatabaseConfig database;
  private FileConfig output;

  public Config() {

  }

  public Date getDateFrom() {

    return dateFrom;
  }

  public void setDateFrom(Date dateFrom) {

    this.dateFrom = dateFrom;
  }

  public Date getDateTo() {

    return dateTo;
  }

  public void setDateTo(Date dateTo) {

    this.dateTo = dateTo;
  }

  public DatabaseConfig getDatabase() {

    return database;
  }

  @Mandatory
  public void setDatabase(DatabaseConfig database) {

    this.database = database;
  }

  public FileConfig getOutput() {

    return output;
  }

  public void setOutput(FileConfig output) {

    this.output = output;
  }
}
