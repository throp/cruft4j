package org.cruft4j.calculator.db

import groovy.sql.Sql

import org.cruft4j.calculator.model.Method

/**
 *
 * @author Ben Northrop
 */
class MethodDAO extends DAO {

  MethodDAO(Sql sql) {
    this.sql = sql
    createTable()
  }

  /**
   * Find if a method violation is new, by checking to see if a method with the same hashCode exists.
   */
  public boolean checkIfNew(Method method) {
    def isNew = true
    sql.eachRow("SELECT * FROM method WHERE hash_code = ${method.hashCode()}") { isNew = false }
    return isNew
  }

  /**
   * Insert a row, checking first whether the table exists.
   */
  public def insert(Method method) {

    sql.execute("""\
        INSERT INTO method ( 
          name,
          ncss,
          ccn,
          javadoc,
          is_new, 
          bucket,
          hash_code, 
          run_id
        )
        VALUES (
          ${method.name},
          ${method.ncss},
          ${method.ccn},
          ${method.javadocs},
          ${method.isNew},
          ${method.bucket.severity.code},
          ${method.hashCode()},
          ${method.projectStats.runId}
        )
      """)
  }

  /**
   * Called prior to every query, in case the table does not already exist.
   */
  private def createTable() {
    sql.execute("""\
        CREATE TABLE IF NOT EXISTS method (
          name VARCHAR(1000),
          ncss INT,
          ccn INT,
          javadoc BOOLEAN,
          is_new BOOLEAN, 
          bucket VARCHAR(1),
          hash_code BIGINT,
          run_id BIGINT
        )
      """)
  }
}
