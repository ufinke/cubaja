// Copyright (c) 2011, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controls <code>exectuteBatch</code> for a group of <code>Update</code> instances.
 * <p>
 * Any number of <code>Update</code> instances may be added to a
 * <code>BatchGroup</code>.
 * A call to the <code>executeBatch</code> method of this group or
 * one of the <code>Update</code> instances which belong to this group
 * calls <code>executeBatch</code> of all group members in the sequence
 * they where added to this group.
 * Note that an <code>executeBatch</code> of an <code>Update</code> may
 * be forced automatically when the internal buffer 
 * (controlled by the <code>batchSize</code> attribute in 
 * <code>DatabaseConfig</code>) is full.
 * <p>
 * This class may be useful for high volume DML when a <code>delete</code> has to
 * precede an <code>insert</code> statement
 * (e.g. as alternate strategy for 'upsert' problems)
 * or in any other case where the execution sequence of batch DML statements matters.
 * @author Uwe Finke
 */
public class BatchGroup {

  private List<Update> updateList;
  private boolean autoCommit;

  public BatchGroup() {

    updateList = new ArrayList<Update>();
  }

  /**
   * Adds an <code>Update</code> to this group.
   * 
   * @param update preparted DML statement
   */
  public void addUpdate(Update update) {

    updateList.add(update);
  }

  /**
   * Sets automatic commit after <code>executeBatch</code>. An <code>executeBatch</code>
   * may be triggered automatically by one of the <code>Update</code> instances.
   * Default is <code>false</code>.
   * 
   * @param autoCommit true or false
   */
  public void setAutoCommit(boolean autoCommit) {

    this.autoCommit = autoCommit;
  }

  /**
   * Calls <code>executeBatch</code> of all <code>Update</code> instances.
   * If this group's <code>autoCommit</code> flag is set,
   * a <code>commit</code> is send to the database connection. 
   * @throws SQLException when an exception occurs during SQL processing
   */
  public void executeBatch() throws SQLException {

    for (Update update : updateList) {
      update.doExecuteBatch();
    }

    if (autoCommit) {
      if (updateList.size() > 0) {
        updateList.get(0).statement.getConnection().commit();
      }
    }
  }
}
