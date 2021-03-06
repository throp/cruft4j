package org.cruft4j.calculator.report

import org.cruft4j.calculator.model.Bucket
import org.cruft4j.calculator.model.Method
import org.cruft4j.calculator.model.ProjectStats
import org.cruft4j.calculator.model.Severity
import org.cruft4j.calculator.model.ViolationType

/**
 * 
 * @author Ben Northrop
 */
class ReportGeneratorTest extends GroovyTestCase {

  void testGenerateComplexity() {
    println "here"
    ProjectStats ps = new ProjectStats()
    Method m1 = new Method(
        name: "foo",
        ncss: 50,
        ccn: 7,
        javadocs: 1,
        bucket: new Bucket(0, 1, Severity.Orange, ViolationType.Complexity))
    ps.methods.add(m1)

    ReportGenerator rg = new ReportGenerator(ps, ps)
    assert rg.generateComplexityHtml(ps.methods).contains("html")
    assert rg.generateComplexityHtml(ps.methods).contains("7")
  }
}
