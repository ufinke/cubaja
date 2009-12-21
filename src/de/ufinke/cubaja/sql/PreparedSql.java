// Copyright (c) 2006 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.ufinke.cubaja.util.Text;

/**
 * Wrapper for a <code>PreparedStatement</code>.
 * Superclass of <code>Query</code> and <code>Update</code>
 * which are created by <code>Database</code> methods.
 * @author Uwe Finke
 */
public class PreparedSql {

  static private final Text text = new Text(PreparedSql.class);
  
  /**
   * PreparedStatement.
   */
  protected PreparedStatement statement;
  /**
   * Sql.
   */
  protected Sql sql;
  /**
   * Configuration.
   */
  protected DatabaseConfig config;
  
  private List<String> variableList;
  private Map<String, Integer> variableMap;
  private boolean changed;
  
  private Class<?> dataClass;
  private VariableSetter variableSetter;
  private VariableSetterGenerator generator;
  
  /**
   * Constructor.
   * @param statement
   * @param sql
   * @param config
   */
  protected PreparedSql(PreparedStatement statement, Sql sql, DatabaseConfig config) {
  
    this.statement = statement;
    this.sql = sql;
    this.config = config;
    
    variableList = sql.getVariables();
  }
  
  /**
   * Retrieves the position of a named variable.
   * @param name
   * @return position of the variable, beginning with <code>1</code>
   * @throws SQLException
   */
  public int getVariablePosition(String name) throws SQLException {
  
    if (variableMap == null) {
      variableMap = new HashMap<String, Integer>(variableList.size() << 1);
      for (int i = 1; i < variableList.size(); i++) {
        variableMap.put(variableList.get(i), i);
      }
    }
    
    Integer position = variableMap.get(name);
    if (position == null) {
      throw new SQLException(text.get("variableNotFound", name));
    }
    return position;
  }
  
  /**
   * Signals whether variables have been set.
   * @return flag
   */
  protected boolean isChanged() {
    
    return changed;
  }
  
  /**
   * Sets a <code>boolean</code> variable identified by name.
   * @param name
   * @param value
   * @throws SQLException
   */
  public void setBoolean(String name, boolean value) throws SQLException {
    
    setBoolean(getVariablePosition(name), value);
  }
  
  /**
   * Sets a <code>boolean</code> variable identified by position.
   * @param position
   * @param value
   * @throws SQLException
   */
  public void setBoolean(int position, boolean value) throws SQLException {
    
    changed = true;
    statement.setBoolean(position, value);
  }

  /**
   * Sets a <code>Boolean</code> variable identified by name.
   * @param name
   * @param value
   * @throws SQLException
   */
  public void setBoolean(String name, Boolean value) throws SQLException {
    
    setBoolean(getVariablePosition(name), value);
  }
  
  /**
   * Sets a <code>Boolean</code> variable identified by position.
   * @param position
   * @param value
   * @throws SQLException
   */
  public void setBoolean(int position, Boolean value) throws SQLException {
    
    changed = true;
    if (value == null) {
      statement.setNull(position, Types.BOOLEAN);
    } else {
      statement.setBoolean(position, value);
    }
  }

  /**
   * Sets a <code>byte</code> variable identified by name.
   * @param name
   * @param value
   * @throws SQLException
   */
  public void setByte(String name, byte value) throws SQLException {
    
    setByte(getVariablePosition(name), value);
  }
  
  /**
   * Sets a <code>byte</code> variable identified by position.
   * @param position
   * @param value
   * @throws SQLException
   */
  public void setByte(int position, byte value) throws SQLException {
    
    changed = true;
    statement.setByte(position, value);
  }

  /**
   * Sets a <code>Byte</code> variable identified by name.
   * @param name
   * @param value
   * @throws SQLException
   */
  public void setByte(String name, Byte value) throws SQLException {
    
    setByte(getVariablePosition(name), value);
  }
  
  /**
   * Sets a <code>Byte</code> variable identified by position.
   * @param position
   * @param value
   * @throws SQLException
   */
  public void setByte(int position, Byte value) throws SQLException {
    
    changed = true;
    if (value == null) {
      statement.setNull(position, Types.TINYINT);
    } else {
      statement.setByte(position, value);
    }
  }
  
  /**
   * Sets a <code>char</code> variable identified by name.
   * @param name
   * @param value
   * @throws SQLException
   */
  public void setCharacter(String name, char value) throws SQLException {
    
    setCharacter(getVariablePosition(name), value);
  }
  
  /**
   * Sets a <code>char</code> variable identified by position.
   * @param position
   * @param value
   * @throws SQLException
   */
  public void setCharacter(int position, char value) throws SQLException {
    
    setString(position, String.valueOf(value));
  }
  
  /**
   * Sets a <code>Character</code> variable identified by name.
   * @param name
   * @param value
   * @throws SQLException
   */
  public void setCharacter(String name, Character value) throws SQLException {
    
    setCharacter(getVariablePosition(name), value);
  }
  
  /**
   * Sets a <code>Character</code> variable identified by position.
   * @param position
   * @param value
   * @throws SQLException
   */
  public void setCharacter(int position, Character value) throws SQLException {
    
    changed = true;
    if (value == null) {
      statement.setNull(position, Types.CHAR);
    } else {
      statement.setString(position, String.valueOf(value));
    }
  }

  /**
   * Sets a <code>short</code> variable identified by name.
   * @param name
   * @param value
   * @throws SQLException
   */
  public void setShort(String name, short value) throws SQLException {
    
    setShort(getVariablePosition(name), value);
  }
  
  /**
   * Sets a <code>short</code> variable identified by position.
   * @param position
   * @param value
   * @throws SQLException
   */
  public void setShort(int position, short value) throws SQLException {
    
    changed = true;
    statement.setShort(position, value);
  }

  /**
   * Sets a <code>Short</code> variable identified by name.
   * @param name
   * @param value
   * @throws SQLException
   */
  public void setShort(String name, Short value) throws SQLException {
    
    setShort(getVariablePosition(name), value);
  }
  
  /**
   * Sets a <code>Short</code> variable identified by position.
   * @param position
   * @param value
   * @throws SQLException
   */
  public void setShort(int position, Short value) throws SQLException {
    
    changed = true;
    if (value == null) {
      statement.setNull(position, Types.SMALLINT);
    } else {
      statement.setShort(position, value);
    }
  }

  /**
   * Sets an <code>int</code> variable identified by name.
   * @param name
   * @param value
   * @throws SQLException
   */
  public void setInt(String name, int value) throws SQLException {
    
    setInt(getVariablePosition(name), value);
  }
  
  /**
   * Sets an <code>int</code> variable identified by position.
   * @param position
   * @param value
   * @throws SQLException
   */
  public void setInt(int position, int value) throws SQLException {
    
    changed = true;
    statement.setInt(position, value);
  }

  /**
   * Sets an <code>Integer</code> variable identified by name.
   * @param name
   * @param value
   * @throws SQLException
   */
  public void setInt(String name, Integer value) throws SQLException {
    
    setInt(getVariablePosition(name), value);
  }
  
  /**
   * Sets an <code>Integer</code> variable identified by position.
   * @param position
   * @param value
   * @throws SQLException
   */
  public void setInt(int position, Integer value) throws SQLException {
    
    changed = true;
    if (value == null) {
      statement.setNull(position, Types.INTEGER);
    } else {
      statement.setInt(position, value);
    }
  }

  /**
   * Sets a <code>long</code> variable identified by name.
   * @param name
   * @param value
   * @throws SQLException
   */
  public void setLong(String name, long value) throws SQLException {
    
    setLong(getVariablePosition(name), value);
  }
  
  /**
   * Sets a <code>long</code> variable identified by position.
   * @param position
   * @param value
   * @throws SQLException
   */
  public void setLong(int position, long value) throws SQLException {
    
    changed = true;
    statement.setLong(position, value);
  }

  /**
   * Sets a <code>Long</code> variable identified by name.
   * @param name
   * @param value
   * @throws SQLException
   */
  public void setLong(String name, Long value) throws SQLException {
    
    setLong(getVariablePosition(name), value);
  }
  
  /**
   * Sets a <code>Long</code> variable identified by position.
   * @param position
   * @param value
   * @throws SQLException
   */
  public void setLong(int position, Long value) throws SQLException {
    
    changed = true;
    if (value == null) {
      statement.setNull(position, Types.BIGINT);
    } else {
      statement.setLong(position, value);
    }
  }

  /**
   * Sets a <code>float</code> variable identified by name.
   * @param name
   * @param value
   * @throws SQLException
   */
  public void setFloat(String name, float value) throws SQLException {
    
    setFloat(getVariablePosition(name), value);
  }
  
  /**
   * Sets a <code>float</code> variable identified by position.
   * @param position
   * @param value
   * @throws SQLException
   */
  public void setFloat(int position, float value) throws SQLException {
    
    changed = true;
    statement.setFloat(position, value);
  }

  /**
   * Sets a <code>Float</code> variable identified by name.
   * @param name
   * @param value
   * @throws SQLException
   */
  public void setFloat(String name, Float value) throws SQLException {
    
    setFloat(getVariablePosition(name), value);
  }
  
  /**
   * Sets a <code>Float</code> variable identified by position.
   * @param position
   * @param value
   * @throws SQLException
   */
  public void setFloat(int position, Float value) throws SQLException {
    
    changed = true;
    if (value == null) {
      statement.setNull(position, Types.REAL);
    } else {
      statement.setFloat(position, value);
    }
  }

  /**
   * Sets a <code>double</code> variable identified by name.
   * @param name
   * @param value
   * @throws SQLException
   */
  public void setDouble(String name, double value) throws SQLException {
    
    setDouble(getVariablePosition(name), value);
  }
  
  /**
   * Sets a <code>double</code> variable identified by position.
   * @param position
   * @param value
   * @throws SQLException
   */
  public void setDouble(int position, double value) throws SQLException {
    
    changed = true;
    statement.setDouble(position, value);
  }

  /**
   * Sets a <code>Double</code> variable identified by name.
   * @param name
   * @param value
   * @throws SQLException
   */
  public void setDouble(String name, Double value) throws SQLException {
    
    setDouble(getVariablePosition(name), value);
  }
  
  /**
   * Sets a <code>Double</code> variable identified by position.
   * @param position
   * @param value
   * @throws SQLException
   */
  public void setDouble(int position, Double value) throws SQLException {
    
    changed = true;
    if (value == null) {
      statement.setNull(position, Types.DOUBLE);
    } else {
      statement.setDouble(position, value);
    }
  }

  /**
   * Sets a <code>BigDecimal</code> variable identified by name.
   * @param name
   * @param value
   * @throws SQLException
   */
  public void setBigDecimal(String name, BigDecimal value) throws SQLException {
    
    setBigDecimal(getVariablePosition(name), value);
  }
  
  /**
   * Sets a <code>BigDecimal</code> variable identified by position.
   * @param position
   * @param value
   * @throws SQLException
   */
  public void setBigDecimal(int position, BigDecimal value) throws SQLException {
    
    changed = true;
    statement.setBigDecimal(position, value);
  }

  /**
   * Sets a <code>BigInteger</code> variable identified by name.
   * The value is presented to the driver as <code>BigDecimal</code>.
   * @param name
   * @param value
   * @throws SQLException
   */
  public void setBigInteger(String name, BigInteger value) throws SQLException {
    
    setBigInteger(getVariablePosition(name), value);
  }
  
  /**
   * Sets a <code>BigInteger</code> variable identified by position.
   * The value is presented to the driver as <code>BigDecimal</code>.
   * @param position
   * @param value
   * @throws SQLException
   */
  public void setBigInteger(int position, BigInteger value) throws SQLException {
    
    changed = true;
    statement.setBigDecimal(position, new BigDecimal(value));
  }

  /**
   * Sets a <code>String</code> variable identified by name.
   * @param name
   * @param value
   * @throws SQLException
   */
  public void setString(String name, String value) throws SQLException {
    
    setString(getVariablePosition(name), value);
  }
  
  /**
   * Sets a <code>String</code> variable identified by position.
   * @param position
   * @param value
   * @throws SQLException
   */
  public void setString(int position, String value) throws SQLException {
    
    changed = true;
    statement.setString(position, value);
  }

  /**
   * Sets a <code>Date</code> variable identified by name.
   * @param name
   * @param value
   * @throws SQLException
   */
  public void setDate(String name, java.util.Date value) throws SQLException {
    
    setDate(getVariablePosition(name), value);
  }
  
  /**
   * Sets a <code>Date</code> variable identified by position to a <code>java.util.Date</code> value.
   * @param position
   * @param value
   * @throws SQLException
   */
  public void setDate(int position, java.util.Date value) throws SQLException {
    
    setDate(position, new java.sql.Date(value.getTime()));
  }

  /**
   * Sets a <code>Timestamp</code> variable identified by name to a <code>java.util.Date</code> value.
   * @param name
   * @param value
   * @throws SQLException
   */
  public void setTimestamp(String name, java.util.Date value) throws SQLException {
    
    setTimestamp(getVariablePosition(name), value);
  }
  
  /**
   * Sets a <code>Timestamp</code> variable identified by position to a <code>java.util.Date</code> value.
   * @param position
   * @param value
   * @throws SQLException
   */
  public void setTimestamp(int position, java.util.Date value) throws SQLException {
    
    setTimestamp(position, new Timestamp(value.getTime()));
  }

  /**
   * Sets a <code>Time</code> variable identified by name to a <code>java.util.Date</code> value.
   * @param name
   * @param value
   * @throws SQLException
   */
  public void setTime(String name, java.util.Date value) throws SQLException {
    
    setTime(getVariablePosition(name), value);
  }
  
  /**
   * Sets a <code>Time</code> variable identified by position to a <code>java.util.Date</code> value.
   * @param position
   * @param value
   * @throws SQLException
   */
  public void setTime(int position, java.util.Date value) throws SQLException {
    
    setTime(position, new Time(value.getTime()));
  }

  /**
   * Sets a <code>Date</code> variable identified by name.
   * @param name
   * @param value
   * @throws SQLException
   */
  public void setDate(String name, java.sql.Date value) throws SQLException {
    
    setDate(getVariablePosition(name), value);
  }
  
  /**
   * Sets a <code>Date</code> variable identified by position.
   * @param position
   * @param value
   * @throws SQLException
   */
  public void setDate(int position, java.sql.Date value) throws SQLException {
    
    changed = true;
    statement.setDate(position, value);
  }

  /**
   * Sets a <code>Timestamp</code> variable identified by name.
   * @param name
   * @param value
   * @throws SQLException
   */
  public void setTimestamp(String name, Timestamp value) throws SQLException {
    
    setTimestamp(getVariablePosition(name), value);
  }
  
  /**
   * Sets a <code>Timestamp</code> variable identified by position.
   * @param position
   * @param value
   * @throws SQLException
   */
  public void setTimestamp(int position, Timestamp value) throws SQLException {
    
    changed = true;
    statement.setTimestamp(position, value);
  }

  /**
   * Sets a <code>Time</code> variable identified by name.
   * @param name
   * @param value
   * @throws SQLException
   */
  public void setTime(String name, Time value) throws SQLException {
    
    setTime(getVariablePosition(name), value);
  }
  
  /**
   * Sets a <code>Time</code> variable identified by position.
   * @param position
   * @param value
   * @throws SQLException
   */
  public void setTime(int position, Time value) throws SQLException {
    
    changed = true;
    statement.setTime(position, value);
  }
  
  /**
   * Sets an <code>Array</code> variable identified by name.
   * @param name
   * @param value
   * @throws SQLException
   */
  public void setArray(String name, Array value) throws SQLException {
    
    setArray(getVariablePosition(name), value);
  }
  
  /**
   * Sets an <code>Array</code> variable identified by position.
   * @param position
   * @param value
   * @throws SQLException
   */
  public void setArray(int position, Array value) throws SQLException {
    
    changed = true;
    statement.setArray(position, value);
  }
  
  /**
   * Sets a <code>Blob</code> variable identified by name.
   * @param name
   * @param value
   * @throws SQLException
   */
  public void setBlob(String name, Blob value) throws SQLException {
    
    setBlob(getVariablePosition(name), value);
  }
  
  /**
   * Sets a <code>Blob</code> variable identified by position.
   * @param position
   * @param value
   * @throws SQLException
   */
  public void setBlob(int position, Blob value) throws SQLException {
    
    changed = true;
    statement.setBlob(position, value);
  }
  
  /**
   * Sets a <code>Clob</code> variable identified by name.
   * @param name
   * @param value
   * @throws SQLException
   */
  public void setClob(String name, Clob value) throws SQLException {
    
    setClob(getVariablePosition(name), value);
  }
  
  /**
   * Sets a <code>Clob</code> variable identified by position.
   * @param position
   * @param value
   * @throws SQLException
   */
  public void setClob(int position, Clob value) throws SQLException {
    
    changed = true;
    statement.setClob(position, value);
  }
  
  /**
   * Sets a <code>Object</code> variable identified by name.
   * @param name
   * @param value
   * @throws SQLException
   */
  public void setObject(String name, Object value) throws SQLException {
    
    setObject(getVariablePosition(name), value);
  }
  
  /**
   * Sets a <code>Object</code> variable identified by position.
   * @param position
   * @param value
   * @throws SQLException
   */
  public void setObject(int position, Object value) throws SQLException {
    
    changed = true;
    statement.setObject(position, value);
  }
  
  /**
   * Sets a <code>Ref</code> variable identified by name.
   * @param name
   * @param value
   * @throws SQLException
   */
  public void setRef(String name, Ref value) throws SQLException {
    
    setRef(getVariablePosition(name), value);
  }
  
  /**
   * Sets a <code>Ref</code> variable identified by position.
   * @param position
   * @param value
   * @throws SQLException
   */
  public void setRef(int position, Ref value) throws SQLException {
    
    changed = true;
    statement.setRef(position, value);
  }
  
  /**
   * Sets a <code>URL</code> variable identified by name.
   * @param name
   * @param value
   * @throws SQLException
   */
  public void setURL(String name, URL value) throws SQLException {
    
    setURL(getVariablePosition(name), value);
  }
  
  /**
   * Sets a <code>URL</code> variable identified by position.
   * @param position
   * @param value
   * @throws SQLException
   */
  public void setURL(int position, URL value) throws SQLException {
    
    changed = true;
    statement.setURL(position, value);
  }

  /**
   * Sets variables to values provided in a data object.
   * The data object's class must have getter methods 
   * with names matching to variable names.
   * @param dataObject
   * @throws Exception
   */
  public void setVariables(Object dataObject) throws Exception {
    
    Class<?> clazz = dataObject.getClass();
    
    if (dataClass != clazz) {
      dataClass = clazz;
      if (generator == null) {
        generator = new VariableSetterGenerator(variableList);
      }
      variableSetter = generator.getSetter(clazz);
    }
    
    variableSetter.setVariables(this, dataObject);
  }

}
