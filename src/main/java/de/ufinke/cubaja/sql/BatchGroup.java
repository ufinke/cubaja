// Copyright (c) 2011, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controls <tt>exectuteBatch</tt> for a group of <tt>Update</tt> instances.
 * <p>
 * Any number of <tt>Update</tt> instances may be added to a
 * <tt>BatchGroup</tt>.
 * A call to the <tt>executeBatch</tt> method of this group or
 * one of the <tt>Update</tt> instances which belong to this group
 * calls <tt>executeBatch</tt> of all group members in the sequence
 * they where added to this group.
 * Note that an <tt>executeBatch</tt> of an <tt>Update</tt> may
 * be forced automatically when the internal buffer 
 * (controlled by the <tt>batchSize</tt> attribute in 
 * <tt>DatabaseConfig</tt>) is full.
 * <p>
 * This class may be useful for high volume DML when a <tt>delete</tt> has to
 * precede an <tt>insert</tt> statement
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
   * Adds an <tt>Update</tt> to this group.
   * 
   * @param update
   */
  public void addUpdate(Update update) {

    updateList.add(update);
  }

  /**
   * Sets automatic commit after <tt>executeBatch</tt>. An <tt>executeBatch</tt>
   * may be triggered automatically by one of the <tt>Update</tt> instances.
   * Default is <tt>false</tt>.
   * 
   * @param autoCommit
   */
  public void setAutoCommit(boolean autoCommit) {

    this.autoCommit = autoCommit;
  }

  /**
   * Calls <tt>executeBatch</tt> of all <tt>Update</tt> instances.
   * If this group's <tt>autoCommit</tt> flag is set,
   * a <tt>commit</tt> is send to the database connection. 
   * @throws SQLException
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
