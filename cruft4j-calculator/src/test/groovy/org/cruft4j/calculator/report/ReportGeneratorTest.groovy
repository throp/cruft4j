package org.cruft4j.calculator

import org.cruft4j.calculator.model.Bucket
import org.cruft4j.calculator.model.Method
import org.cruft4j.calculator.model.ProjectStats
import org.cruft4j.calculator.model.Severity
import org.cruft4j.calculator.model.ViolationType
import org.cruft4j.calculator.report.ReportGenerator
import org.cruft4j.calculator.report.ReportGenerator.ReportType

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
    assert rg.generateComplexityHtml(ps.methods, ReportType.ComplexityAll).contains("html")
    assert rg.generateComplexityHtml(ps.methods, ReportType.ComplexityAll).contains("50")
    assert rg.generateComplexityHtml(ps.methods, ReportType.ComplexityAll).contains("foo")
    assert rg.generateComplexityHtml(ps.methods, ReportType.ComplexityAll).contains("7")
  }
}
