package de.ufinke.cubaja.sql;

import org.junit.*;
import static org.junit.Assert.*;
import de.ufinke.cubaja.*;
import de.ufinke.cubaja.config.*;
import java.util.*;

public class SqlTest {

  static private TestEnvironment environment;
  
  @BeforeClass
  static public void environment() throws Exception {
    
    environment = new TestEnvironment("sql");
  }
    
  private Database database;
  private Date date;
  private Date timestamp;
  
  @Test
  public void basicTest() throws Exception {
    
    connect();
    createTable();
    insertSomeRows();
    selectOneRow();
    selectCursor();
    disconnect();
  }

  private void connect() throws Exception {
    
    Configurator configurator = new Configurator();
    configurator.setBaseName(environment.getBaseName("config"));
    configurator.addPropertyProvider(environment.getProperties());
    SqlTestConfig config = configurator.configure(new SqlTestConfig());
    database = new Database(config.getDatabase());
    
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
  
  private void disconnect() throws Exception {
    
    database.close();
  }
  
  private void createTable() throws Exception {

    database.execute("drop table basic_data", 1051);
    database.execute(new Sql(getClass(), "create_table"));
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

    SqlTestData data = null;
    
    data = new SqlTestData();
    data.setIntField(1);
    data.setDecimalField(1.23);
    data.setCharField('x');
    data.setStringField("hello");
    data.setDateField(date);
    data.setTimestampField(timestamp);
    insert.setVariables(data);
    insert.addBatch();
    
    data = new SqlTestData();
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
    
    SqlTestData data = database.select("select * from basic_data where int_field = 1", SqlTestData.class);
    assertEquals(1, data.getIntField());
    assertEquals(1.23, data.getDecimalField(), 0.0001);
    assertEquals('x', data.getCharField());
    assertEquals("hello", data.getStringField());
    assertEquals(date, data.getDateField());
    assertEquals(timestamp, data.getTimestampField());
  }
  
  private void selectCursor() throws Exception {

    Sql sql = new Sql(getClass(), "select");
    sql.resolve("fromConstant", "1");
    sql.resolve("toConstant", "100");
    
    int sum = 0;
    Query query = database.createQuery(sql);
    for (SqlTestData data : query.cursor(SqlTestData.class)) {
      sum += data.getIntField();
    }
    query.close();
    
    assertEquals(3, sum);
  }
  
}
