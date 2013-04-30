package org.cruft4j.calculator.db

import groovy.sql.Sql

class TableGenerator {

  private Sql sql

  TableGenerator(Sql sql) {
    this.sql = sql
  }

  def createTables() {
    //createMethod()
    //createCopyPaste()
    //createCopyPasteFile()
  }



  private def createMethod() {
    sql.execute("""\
        CREATE TABLE IF NOT EXISTS method (
          method_id INT,
          name VARCHAR(1000),
          path VARCHAR(1000),
          ncss INT,
          ccn INT,
          javadoc BOOLEAN,
          hash_code BIGINT,
          run_id BIGINT,
          bucket VARCHAR(1),
          is_different BOOLEAN
        )
      """)
  }

  private def createCopyPaste() {
    sql.execute("""\
        CREATE TABLE IF NOT EXISTS copy_paste (
          copy_paste_id INT,
          lines INT,
          tokens INT,
          hash_code BIGINT,
          run_id BIGINT,
          bucket VARCHAR(1),
          is_different BOOLEAN
        )
      """)
  }

  private def createCopyPasteFile() {
    sql.execute("""\
        CREATE TABLE IF NOT EXISTS copy_paste_file (
          copy_paste_id INT,
          file_name VARCHAR(1000),
          line_num INT,
          run_id BIGINT
        )
      """)
  }
}
