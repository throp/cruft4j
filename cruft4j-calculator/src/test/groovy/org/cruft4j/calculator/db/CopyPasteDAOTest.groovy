package org.cruft4j.calculator.db

import org.cruft4j.calculator.model.Bucket
import org.cruft4j.calculator.model.CopyPaste
import org.cruft4j.calculator.model.ProjectStats
import org.cruft4j.calculator.model.Severity
import org.cruft4j.calculator.model.ViolationType

class CopyPasteDAOTest extends DAOTest {

  CopyPasteDAO dao = new CopyPasteDAO(sql)

  void testInsert() {
    def project = new ProjectStats(runDate: new Date())
    def copyPaste = new CopyPaste(
        lines: 157,
        tokens: 500,
        bucket: new Bucket(6, 10, Severity.Yellow, ViolationType.CopyPaste),
        projectStats: project)


    dao.insert(copyPaste)
  }
}
