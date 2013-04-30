package org.cruft4j.calculator.calc

import org.cruft4j.calculator.model.RunConfig

class ScoreCalculatorTest extends GroovyTestCase {

  void testDeriveProjectOutputDir() {
    def RunConfig runConfig = new RunConfig()


    def cruft4jHome = System.getenv("CRUFT4J_HOME")
    if(cruft4jHome == null) {
      assert ScoreCalculator.deriveAndCreateProjectOutputDir(runConfig) == ".\\cruft4j\\"
    }
    else {
      assert ScoreCalculator.deriveAndCreateProjectOutputDir(runConfig) == cruft4jHome + "\\output\\"
    }

    runConfig.setOutputDir("C:\\Temp")

    assert ScoreCalculator.deriveAndCreateProjectOutputDir(runConfig) == "C:\\Temp\\"

    runConfig.setProjectName("Foo")

    assert ScoreCalculator.deriveAndCreateProjectOutputDir(runConfig) == "C:\\Temp\\Foo\\"

    runConfig.setOutputDir("")

    if(cruft4jHome == null) {
      assert ScoreCalculator.deriveAndCreateProjectOutputDir(runConfig) == ".\\output\\Foo\\"
    }
    else {
      assert ScoreCalculator.deriveAndCreateProjectOutputDir(runConfig) == cruft4jHome + "\\output\\Foo\\"
    }
  }

  void testInitRunConfig() {
    assert 1 == 1

    def runConfig = ScoreCalculator.initRunConfig("-sourceDir", "C:\\Sandbox\\foo")

    assert runConfig.sourceDir == "C:\\Sandbox\\foo"
    assert runConfig.outputDir == ""

    runConfig = ScoreCalculator.initRunConfig(
        "-sourceDir", "C:\\Sandbox\\foo",
        "-outputDir", "C:\\Sandbox\\fooout")

    assert runConfig.sourceDir == "C:\\Sandbox\\foo"
    assert runConfig.outputDir == "C:\\Sandbox\\fooout"

    runConfig = ScoreCalculator.initRunConfig(
        "-sourceDir", "C:\\Sandbox\\foo",
        "-console",
        "-outputDir", "C:\\Sandbox\\fooout",
        "-fresh")

    assert runConfig.sourceDir == "C:\\Sandbox\\foo"
    assert runConfig.outputDir == "C:\\Sandbox\\fooout"
    assert runConfig.runFresh == true
    assert runConfig.logToConsole == true
  }
}
