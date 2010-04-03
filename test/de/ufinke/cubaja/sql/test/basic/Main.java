package de.ufinke.cubaja.sql.test.basic;

import org.junit.*;
import static org.junit.Assert.*;
import de.ufinke.cubaja.*;
import de.ufinke.cubaja.config.*;
import de.ufinke.cubaja.sql.*;
import java.util.*;

public class Main extends TestClass {

  private Database database;
  private Date date;
  private Date timestamp;
  
  @Test
  public void testBasic() {
    
    try {
      createDates();
      connect();
      createTable();
      insertSomeRows();
      selectOneRow();
      disconnect();
    } catch (Throwable t) {
      t.printStackTrace();
      fail(t.getMessage());
    }
  }
  
  private void connect() throws Exception {
    
    Configurator configurator = new Configurator();
    configurator.setBaseName(getResourceName("config"));
    Config config = configurator.configure(new Config());
    database = new Database(config.getDatabase());
  }
  
  private void disconnect() throws Exception {
    
    database.close();
  }
  
  private void createTable() throws Exception {

    database.execute("drop table basic_data", 1051);
    database.execute(new Sql(getClass(), "create_table"));
  }
  
  private void createDates() {
    
    Calendar cal = Calendar.getInstance();
    
    cal.clear();
    cal.set(Calendar.YEAR, 2010);
    cal.set(Calendar.MONTH, 0);
    cal.set(Calendar.DAY_OF_MONTH, 31);
    date = cal.getTime();
    
    cal.clear();
    cal.set(Calendar.YEAR, 2010);
    cal.set(Calendar.MONTH, 11);
    cal.set(Calendar.DAY_OF_MONTH, 6);
    cal.set(Calendar.HOUR_OF_DAY, 20);
    cal.set(Calendar.MINUTE, 21);
    cal.set(Calendar.SECOND, 22);
    timestamp = cal.getTime();
  }
  
  private void insertSomeRows() throws Exception {
    
    Sql sql = new Sql().
      append("insert into basic_data").
      appendInsert("int_field"
                 , "decimal_field"
                 , "char_field"
                 , "string_field"
                 , "date_field"
                 , "timestamp_field"
                  );
    
    Update insert = database.createUpdate(sql);

    Data data = null;
    
    data = new Data();
    data.setIntField(1);
    data.setDecimalField(1.23);
    data.setCharField('x');
    data.setStringField("hello");
    data.setDateField(date);
    data.setTimestampField(timestamp);
    insert.setVariables(data);
    insert.addBatch();
    
    data = new Data();
    data.setIntField(2);
    data.setDecimalField(2.23);
    data.setCharField('y');
    data.setStringField("world");
    data.setDateField(new java.util.Date());
    data.setTimestampField(new java.util.Date());
    insert.setVariables(data);
    insert.addBatch();
    
    insert.executeBatch();
    insert.close();
    
    database.commit();
  }
  
  private void selectOneRow() throws Exception {
    
    Data data = database.select("select * from basic_data where int_field = 1", Data.class);
    assertEquals(1, data.getIntField());
    assertEquals(1.23, data.getDecimalField(), 0.0001);
    assertEquals('x', data.getCharField());
    assertEquals("hello", data.getStringField());
    assertEquals(date, data.getDateField());
    assertEquals(timestamp, data.getTimestampField());
  }
  
}
