package org.cruft4j.calculator.db

import org.cruft4j.calculator.model.Bucket
import org.cruft4j.calculator.model.Method
import org.cruft4j.calculator.model.ProjectStats
import org.cruft4j.calculator.model.Severity
import org.cruft4j.calculator.model.ViolationType


class MethodDAOTest extends DAOTest {

  MethodDAO dao = new MethodDAO(sql)

  void testInsert() {
    def project = new ProjectStats(runDate: new Date())
    def method = new Method(
        name: "foo()",
        ncss: 234,
        ccn: 6,
        javadocs: true,
        bucket: new Bucket(6, 10, Severity.Yellow, ViolationType.Complexity),
        projectStats: project)


    dao.insert(method)
  }
}
