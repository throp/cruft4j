package org.cruft4j.calculator.db

import groovy.sql.Sql

import org.cruft4j.calculator.model.CopyPaste

class CopyPasteDAO extends DAO {

  CopyPasteDAO(Sql sql) {
    this.sql = sql
    createTable()
  }

  /**
   * Find if a method violation is new, by checking to see if a method with the same hashCode exists.
   */
  public boolean checkIfNew(CopyPaste copyPaste) {
    def isNew = true
    sql.eachRow("SELECT * FROM copy_paste WHERE hash_code = ${copyPaste.hashCode()}") { isNew = false }
    return isNew
  }

  /**
   * 
   */
  private def insert(CopyPaste copyPaste) {
    sql.execute("""\
        INSERT INTO copy_paste ( 
          lines,
          tokens,
          bucket,
          is_new, 
          hash_code,
          run_id
        )
        VALUES (
          ${copyPaste.lines},
          ${copyPaste.tokens},
          ${copyPaste.bucket.severity.code},
          ${copyPaste.isNew},
          ${copyPaste.hashCode()},
          ${copyPaste.projectStats.runId}
        )
      """)
  }

  /**
   * Called prior to every query, in case the table does not already exist.  
   */
  private def createTable() {
    def success = sql.execute("""\
        CREATE TABLE IF NOT EXISTS copy_paste (
          lines INT,
          tokens INT,
          bucket VARCHAR(1),
          is_new BOOLEAN,
          hash_code BIGINT,
          run_id BIGINT
        )
      """)
  }
}
