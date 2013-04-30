package org.cruft4j.calculator.model;


public class CopyPasteTest extends GroovyTestCase {

  void testHashCode() {

    def project = new ProjectStats(runDate: new Date())

    def cp1 = new CopyPaste(
        lines: 157,
        tokens: 500,
        bucket: new Bucket(6, 10, Severity.Yellow, ViolationType.CopyPaste),
        projectStats: project)

    def cp2 = new CopyPaste(
        lines: 158,
        tokens: 500,
        bucket: new Bucket(6, 10, Severity.Yellow, ViolationType.CopyPaste),
        projectStats: project)

    def cp3 = new CopyPaste(
        lines: 157,
        tokens: 500,
        bucket: new Bucket(6, 10, Severity.Yellow, ViolationType.CopyPaste),
        projectStats: project)

    assert cp1.hashCode() != cp2.hashCode()
    assert cp1.hashCode() == cp3.hashCode()
  }
}
