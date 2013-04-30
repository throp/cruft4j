package org.cruft4j.calculator.calc

import static groovyx.net.http.ContentType.*
import groovy.sql.Sql
import groovyx.net.http.*
import javancss.Javancss
import net.sourceforge.pmd.PMD
import net.sourceforge.pmd.cpd.CPD
import net.sourceforge.pmd.cpd.CPDConfiguration

import org.cruft4j.calculator.db.CopyPasteDAO
import org.cruft4j.calculator.db.MethodDAO
import org.cruft4j.calculator.db.ProjectDAO
import org.cruft4j.calculator.db.TableGenerator
import org.cruft4j.calculator.model.Bucket
import org.cruft4j.calculator.model.CopyPaste
import org.cruft4j.calculator.model.CopyPasteFile
import org.cruft4j.calculator.model.Method
import org.cruft4j.calculator.model.ProjectStats
import org.cruft4j.calculator.model.RunConfig
import org.cruft4j.calculator.model.Severity
import org.cruft4j.calculator.model.ViolationType
import org.cruft4j.calculator.report.ReportGenerator
import org.cruft4j.calculator.report.ReportGenerator.ReportType



/**
 * ScoreCalculator.groovy 
 *
 * @author Ben Northrop
 */
class ScoreCalculator {

  final static Bucket COMPLEXITY_WHITE = new Bucket(Integer.MIN_VALUE, 5, Severity.Green, ViolationType.Complexity)
  final static Bucket COMPLEXITY_YELLOW = new Bucket(6, 10, Severity.Yellow, ViolationType.Complexity)
  final static Bucket COMPLEXITY_ORANGE = new Bucket(11, 20, Severity.Orange, ViolationType.Complexity)
  final static Bucket COMPLEXITY_RED = new Bucket(21, Integer.MAX_VALUE, Severity.Red, ViolationType.Complexity)

  final static Bucket COPYPASTE_WHITE = new Bucket(Integer.MIN_VALUE, 50, Severity.Green, ViolationType.CopyPaste)
  final static Bucket COPYPASTE_YELLOW = new Bucket(51, 100, Severity.Yellow, ViolationType.CopyPaste)
  final static Bucket COPYPASTE_ORANGE = new Bucket(101, 200, Severity.Orange, ViolationType.CopyPaste)
  final static Bucket COPYPASTE_RED = new Bucket(201, Integer.MAX_VALUE, Severity.Red, ViolationType.CopyPaste)

  final static Bucket[] COMPLEXITY_BUCKETS = [
    COMPLEXITY_WHITE,
    COMPLEXITY_YELLOW,
    COMPLEXITY_ORANGE,
    COMPLEXITY_RED
  ]

  final static Bucket[] COPYPASTE_BUCKETS = [
    COPYPASTE_WHITE,
    COPYPASTE_YELLOW,
    COPYPASTE_ORANGE,
    COPYPASTE_RED
  ]

  /**
   * Main method which makes the calculator available via the command line
   */
  static void main(args) {
    def runConfig = initRunConfig(args)
    def project = new ScoreCalculator().calculate(runConfig)
  }

  /**
   * Read command line options and from them initialize a RunConfig object which defines how cruft4j should run.
   */
  static RunConfig initRunConfig(String [] args) {
    def runConfig = new RunConfig()
    def option = ""

    // Read in the command line arguments, which are preceded by a option switch
    args.each{
      if(option != "") {
        if(option == "-sourceDir") runConfig.sourceDir = it
        else if(option == "-outputDir") runConfig.outputDir = it
        else if(option == "-projectName") runConfig.projectName = it
        else if(option == "-projectUrl") runConfig.projectUrl = it
        else if(option == "-repositoryUrl") runConfig.repositoryUrl = it
        option = ""
      }
      else {
        if(it.substring(0, 1) == "-") {
          if(it == "-useCache") runConfig.runFresh = false
          else if(it == "-console") runConfig.logToConsole = true
          else if(it == "-archive") runConfig.archive = true
          else option = it
        }
      }
    }

    if(runConfig.sourceDir == "") {
      throw new RuntimeException("No source directory passed in.  Please use \"-sourceDir <path to source>\".")
    }


    return runConfig
  }

  /**
   * Overloaded method, in the case that the projectOutputDir is not passed in.
   */
  static String printConfig(RunConfig runConfig) {
    printConfig(runConfig, deriveAndCreateProjectOutputDir(runConfig));
  }

  /**
   * Print the config info to the console.
   */
  static String printConfig(RunConfig runConfig, String projectOutputDir) {

    String str = """\

    ----------------------------------
    - Cruft4J
    ----------------------------------
    CONFIG
      Source: ${runConfig.sourceDir}
      Ouput: ${projectOutputDir}
    """

    if(!"".equals(runConfig.projectName) && !"cruft4j".equals(runConfig.projectName)) {
      str += "\n  Project: ${runConfig.projectName}"
    }
    if(!"".equals(runConfig.repositoryUrl)) {
      str += "\n  Repository URL: ${runConfig.repositoryUrl}"
    }
    if(!"".equals(runConfig.projectUrl)) {
      str += "\n  Project URL: ${runConfig.projectUrl}"
    }
    if(!runConfig.runFresh) {
      str += "\n  Run Fresh: ${runConfig.runFresh}"
    }

    if(runConfig.logToConsole) {
      println(str)
    }

    return str
  }

  /**
   * Print the final calculated statistics to the console.
   */
  static String printProjectStats(RunConfig runConfig, ProjectStats project) {

    String str = """\
    PROJECT STATS
      Total Methods    : ${project.methods.size()}
      Methods Javadoc'd: ${project.numJavadocs}
      NCSS             : ${project.ncss}
    COPY PASTE VIOLATIONS
      Yellow: ${project.numCopypasteYellow}
      Orange: ${project.numCopypasteOrange}
      Red   : ${project.numCopypasteRed}
      Score : ${project.copypasteScore}
    COMPLEXITY VIOLATIONS
      Yellow: ${project.numComplexityYellow}
      Orange: ${project.numComplexityOrange}
      Red   : ${project.numComplexityRed}
      Score : ${project.complexityScore}
    OVERALL SCORE
      Raw   : ${project.rawScore}
      Scaled: ${project.getFormattedScaledScore()}
    """

    if(runConfig.logToConsole) { println str}

    return str
  }

  /**
   * The main method...for...
   */
  def ProjectStats calculate(RunConfig runConfig) {

    def projectOutputDir = deriveAndCreateProjectOutputDir(runConfig)
    def cpdFileNameTmp = projectOutputDir  + "cpd-out-tmp.xml"
    def cpdFileName = projectOutputDir + "cpd-out.xml"
    def ncssFileName = projectOutputDir + "ncss-out.xml"

    def sql = Sql.newInstance("jdbc:h2:${projectOutputDir}cruft4j", "sa", "sa", "org.h2.Driver")
    def tableGenerator = new TableGenerator(sql)
    def projectDAO = new ProjectDAO(sql)
    def copyPasteDAO = new CopyPasteDAO(sql)
    def methodDAO = new MethodDAO(sql)

    printConfig(runConfig, projectOutputDir)

    def project = new ProjectStats()
    project.setName(runConfig.projectName)
    project.setPath(runConfig.sourceDir)
    project.setUrl(runConfig.projectUrl)
    project.setRepositoryUrl(runConfig.repositoryUrl)
    project.setRunDate(new Date())

    //For testing purposes, allow for short-cut running the tools
    if(runConfig.runFresh) {
      runCpd(runConfig.sourceDir, cpdFileNameTmp)
      runJavaNcss(runConfig.sourceDir, ncssFileName)
      fixCpd(cpdFileNameTmp, cpdFileName)
    }

    parseCpd(project, cpdFileName)
    parseJavaNcss(project, ncssFileName)

    project.numCopypasteYellow = project.copyPastes.findAll{ it.bucket.severity == Severity.Yellow}.size()
    project.numCopypasteOrange = project.copyPastes.findAll{ it.bucket.severity == Severity.Orange}.size()
    project.numCopypasteRed = project.copyPastes.findAll{ it.bucket.severity == Severity.Red}.size()
    project.numComplexityYellow = project.methods.findAll{ it.bucket.severity == Severity.Yellow}.size()
    project.numComplexityOrange = project.methods.findAll{ it.bucket.severity == Severity.Orange}.size()
    project.numComplexityRed = project.methods.findAll{ it.bucket.severity == Severity.Red}.size()
    project.numJavadocs = project.methods.findAll{ it.javadocs == 1} .size()
    project.copypasteScore = calculateScore(project.numCopypasteYellow, project.numCopypasteOrange, project.numCopypasteRed)
    project.complexityScore = calculateScore(project.numComplexityYellow, project.numComplexityOrange, project.numComplexityRed)
    project.rawScore = project.copypasteScore + project.complexityScore
    project.scaledScore = (project.rawScore / project.ncss) * 1000

    project.methods.sort{-it.ccn}
    project.copyPastes.sort{-it.tokens}

    printProjectStats(runConfig, project)

    def previousStats = projectDAO.fetchMostRecentProjectStats(project.path)

    projectDAO.insert(project)
    project.methods.each() { it.setIsNew(methodDAO.checkIfNew(it)) }
    project.methods.each() { methodDAO.insert(it) }
    project.copyPastes.each() { it.setIsNew(copyPasteDAO.checkIfNew(it)) }
    project.copyPastes.each() {  copyPasteDAO.insert(it) }


    ReportGenerator reportGenerator = new ReportGenerator(project, previousStats)

    File fileComplexityAll = new File(projectOutputDir + ReportType.ComplexityAll.url)
    File fileComplexityNew = new File(projectOutputDir + ReportType.ComplexityNew.url)
    File fileCopyPasteAll = new File(projectOutputDir + ReportType.CopyPasteAll.url)
    File fileCopyPasteNew = new File(projectOutputDir + ReportType.CopyPasteNew.url)
    File fileOverall = new File(projectOutputDir + ReportType.Summary.url)
    fileComplexityAll.write(reportGenerator.generateComplexityHtml(project.methods, ReportType.ComplexityAll))
    fileComplexityNew.write(reportGenerator.generateComplexityHtml(project.methods.grep({it.isNew}), ReportType.ComplexityNew))
    fileCopyPasteAll.write(reportGenerator.generateCopyPasteHtml(project.copyPastes, ReportType.CopyPasteAll))
    fileCopyPasteNew.write(reportGenerator.generateCopyPasteHtml(project.copyPastes.grep({it.isNew}), ReportType.CopyPasteNew))
    fileOverall.write(reportGenerator.generateOverallHtml())

    if(runConfig.archive) {
      archive(runConfig, project)
    }

    return project
  }

  /**
   * Send the project stats to the server so they can be considered within the overall statistics.  Only for Ben to call.
   */
  def void archive(RunConfig runConfig, ProjectStats project) {

    // Maybe not the best place to put a username and password, but better than in the code!
    def username = System.getenv().get("CRUFT4J_USERNAME")
    def password = System.getenv().get("CRUFT4J_PASSWORD")

    def http = new HTTPBuilder( 'http://www.bennorthrop.com' )
    http.auth.basic(username, password)
    def payload = [
      projectName: project.getName(),
      projectPath: project.getUrl(),
      projectUrl: project.getUrl(),
      repositoryUrl: project.getRepositoryUrl(),
      ncss: project.getNcss(),
      complexityScore: project.getComplexityScore(),
      copypasteScore: project.getCopypasteScore(),
      rawScore: project.getRawScore(),
      scaledScore: project.getScaledScore(),
      numComplexityYellow: project.getNumComplexityYellow(),
      numComplexityOrange: project.getNumComplexityOrange(),
      numComplexityRed: project.getNumComplexityRed(),
      numCopypasteYellow: project.getNumCopypasteYellow(),
      numCopypasteOrange: project.getNumCopypasteOrange(),
      numCopypasteRed: project.getNumCopypasteRed(),
      numJavadocs: project.getNumJavadocs()
    ]

    http.get( path : '/Cruft4J/archive/insert_project.php', contentType : TEXT, query : payload) { resp, reader ->
      println "response status: ${resp.statusLine}"
    }
  }

  /**
   * Define the project output directory, based on the runconfig and CRUFT4J_HOME environemnt variable.
   */
  def static String deriveAndCreateProjectOutputDir(RunConfig runConfig) {

    def projectOutputDir = ""
    def cruft4jHome = System.getenv("CRUFT4J_HOME")

    if(runConfig.outputDir == "") {
      if(cruft4jHome == null) {
        projectOutputDir = "." + File.separator + "output"
      } else {
        projectOutputDir = cruft4jHome + File.separator + "output"
        verifyDirectoryExists(projectOutputDir)
      }
    } else {
      projectOutputDir = runConfig.outputDir
      verifyDirectoryExists(projectOutputDir)
    }

    // If there's a project name, then create a subdirectory
    if(runConfig.projectName != "") {
      projectOutputDir += File.separator + runConfig.projectName
    }

    projectOutputDir += File.separator

    // If the directory doesn't exist, create it
    def dir = new File(projectOutputDir)
    if(!dir.exists()) dir.mkdir()

    return projectOutputDir
  }

  /**
   * Verify that a given output directory exists.
   */
  def static void verifyDirectoryExists(String dir) {
    if(!new File(dir).exists()) {
      throw new Cruft4jException("Directory does not exist: " + dir)
    }
  }

  /**
   * Find the bucket that corresponds the number (e.g. complexity, copy-paste tokens, etc.).
   */
  def Bucket deriveBucket(Bucket [] buckets, int number) {
    return buckets.find { it.isIn(number) }
  }

  /**
   * Caluclate the raw Cruft score.
   */
  def calculateScore(int yellow, int orange, int red) {
    return (yellow * Severity.Yellow.points) + (orange * Severity.Orange.points) + (red * Severity.Red.points)
  }



  /**
   * Fix the XML file so it can be read back in by cruft4j (for some reason the first line is messed up by CPD).  
   */
  def fixCpd(String tmpFileName, String fileName) {
    def tmpFile = new File(tmpFileName)
    def newFile = new File(fileName)
    def i = 0
    tmpFile.eachLine { line ->
      if(i == 0) newFile.write("<?xml version=\"1.0\"?>")
      else newFile.append(line)
      i++
    }
  }

  /**
   * Parse the XML file generated from JavaNCSS, and populate into a cruft4j data structure.
   */
  void parseJavaNcss(ProjectStats project, String fileName) {
    def javancss = new XmlParser().parse(fileName)
    javancss.functions.function.each{
      Bucket bucket = deriveBucket(COMPLEXITY_BUCKETS, it.ccn.text().toInteger())
      if(bucket.severity != Severity.Green) {
        def method = new Method(
            name: it.name.text(),
            ncss: it.ncss.text().toInteger(),
            ccn: it.ccn.text().toInteger(),
            javadocs: it.javadocs.text().toInteger() == 1 ? true : false,
            bucket: bucket,
            projectStats: project
            )
        project.methods.add(method)
      } else {
        project.numComplexityGreen++
      }
    }
    project.ncss = javancss.functions.ncss[0].text().replaceAll(",", "").toFloat()
  }

  /**
   * Parse the CPD output (XML) and put into the Cruft4J data model.
   */
  void parseCpd(ProjectStats project, String fileName) {
    def pmdcpd = new XmlParser().parse(fileName)
    pmdcpd.duplication.each{
      def copyPaste = new CopyPaste(
          lines: it.attributes().get("lines").toInteger(),
          tokens: it.attributes().get("tokens").toInteger(),
          bucket: deriveBucket(COPYPASTE_BUCKETS, it.attributes().get("tokens").toInteger()),
          projectStats: project
          )

      it.file.each{
        copyPaste.files.add(new CopyPasteFile(
            fileName: it.attributes().get("path"),
            lineNumber: it.attributes().get("line").toInteger()))
      }
      project.copyPastes.add(copyPaste)
    }
  }

  /**
   * Run the Java NCSS program to calculate complexity.
   */
  void runJavaNcss(String sourceDir, String fileName) {
    String [] params = [
      "-function",
      "-recursive",
      "-xml",
      "-out",
      fileName,
      sourceDir
    ]
    Javancss pJavancss = new Javancss(params)
  }

  /**
   * Run PMD - not used...but could get complexity from this route as well.
   */
  void runPmd(String sourceDir, String fileName) {
    String[] params =  [
      sourceDir,
      "xml",
      "code-cruft.xml",
      "-reportfile",
      fileName
    ]

    PMD.run(params)
  }


  /**
   * Run PMD CPD to calculate the copy-paste instances. 
   */
  void runCpd(String sourceDir, String fileName) {
    File file = new File(fileName)

    String [] args = [
      "--minimum-tokens",
      "51",
      "--files",
      sourceDir,
      "--format",
      "net.sourceforge.pmd.cpd.XMLRenderer"
    ]
    CPDConfiguration config = new CPDConfiguration(args)

    CPD cpd = new CPD(config)
    boolean missingFiles = true
    for (int position = 0; position < args.length; position++) {
      if (args[position].equals("--files")) {
        cpd.addRecursively(args[position + 1])
        if ( missingFiles ) {
          missingFiles = false
        }
      }
    }
    if ( missingFiles ) {
      println("No " + "--files" + " value passed in")
    }

    cpd.go()
    if (cpd.getMatches().hasNext()) {
      file.write(config.renderer().render(cpd.getMatches()))
    }
  }
}