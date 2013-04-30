package org.cruft4j.calculator.db

import groovy.sql.Sql


abstract class DAOTest  extends GroovyTestCase {

  Sql sql = Sql.newInstance("jdbc:h2:cruft4junittest", "sa", "sa", "org.h2.Driver")
}
