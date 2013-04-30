package org.cruft4j.calculator.report

import org.cruft4j.calculator.model.ProjectStats


/**
 * 
 * @author Ben Northrop
 */
class ReportGenerator {

  static enum ReportType {
    Summary("Summary", "cruft4j-summary.html"),

    ComplexityAll("ComplexityAll", "cruft4j-complexity-all.html"),

    ComplexityNew("ComplexityAll", "cruft4j-complexity-new.html"),

    CopyPasteAll("Copy-Paste", "cruft4j-copypaste-all.html"),

    CopyPasteNew("Copy-Paste", "cruft4j-copypaste-new.html")

    String name

    String url

    ReportType(String name, String url) {
      this.name = name
      this.url = url
    }
  }

  ProjectStats projectStats

  ProjectStats lastProjectStats

  ReportGenerator(ProjectStats projectStats, ProjectStats lastProjectStats) {
    this.projectStats = projectStats
    this.lastProjectStats = lastProjectStats
  }



  /**
   * Generate the HTML file for the copy-paste results.
   */
  def generateCopyPasteHtml(copyPastes, reportType) {
    def out = generateHeader(reportType)

    if(copyPastes.isEmpty()) {
      out += "<p>none</p>"
    }
    else {

      out += """
        <table class="copypaste violation">
          <tr>
            <th>Files</th>
            <th>Tokens</th>
            <th>Lines</th>
          </tr>
      """

      copyPastes.each {
        out += """\
        <tr class="${it.bucket.severity.name}">
          <td>""";

        it.files.each { out += """\
          <a href="${it.fileName}">${it.fileName}</a>
          (${it.lineNumber
          })<br>
         """}

        out += """\
            </td>
          <td>${it.tokens}</td>
          <td>${
            it.lines
          }</td>
        </tr>
          """
      }
    }
    out += generateFooter()
    return out
  }

  /**
   * Generate the HTML file for the complexity results.
   */
  def generateComplexityHtml(methods, reportType) {
    def out = generateHeader(reportType)

    if(methods.isEmpty()) {
      out += "<p>none</p>"
    }
    else {
      out += """
          <table class="complexity violation">
          <tr>
          <th>Method</th>
            <th>Complexity</th>
          <th>NCSS</th>
            <th>Javadoc</th>
          </tr>
      """


      methods.each { out += """\
				<tr class="${it.bucket.severity.name}">
					<td>
            <a href="${it.getFileName()}">${it.getNameTrunc()}</a><br/></td>
          <td>${
            it.ccn
          }</td>
					<td>${it.ncss}</td>
          <td>${
            it.javadocs ? "Yes" : "No"
          }</td>
				</tr>
          """ }
    }
    out += generateFooter()
    return out
  }

  /**
   * Generate the overall report, showing the score for all the source.
   */
  def generateOverallHtml() {
    def out = generateHeader(ReportType.Summary)

    out += """\

          <table align="center" class="stat">
          <tr>
          <th colspan="2">Overall Score</th>
        </tr>
          <tr>
          <td class="label">Raw Score:</td>
          <td class="value">${projectStats.rawScore}</td>
          <td class="value">${
            lastProjectStats.rawScore
          }</td> 
        </tr>
          <tr>
          <td class="label">Scaled Score:</td>
          <td class="value">${projectStats.scaledScore}</td>
          </tr>
      </table>

          <table align="center" class="stat">
          <tr>
          <th colspan="2">Project Stats</th>
        </tr>
          <tr>
          <td class="label">Total Methods:</td>
          <td class="value">${projectStats.methods.size()}</td>
          </tr>
        <tr>
          <td class="label">Methods with Javadoc:</td>
          <td class="value">${
            projectStats.numJavadocs
          }</td>
        </tr>
          <tr>
          <td class="label">Total Lines of Code (NCSS):</td>
          <td class="value">${projectStats.ncss}</td>
          </tr>
      </table>

          <table align="center" class="stat">
          <tr>
          <th colspan="2">Copy-Paste Violations</th>
        </tr>
          <tr>
          <td class="label red">Red:</td>
          <td class="value red">${projectStats.numCopypasteRed}</td>
          </tr>
        <tr>
          <td class="label orange">Orange:</td>
          <td class="value orange">${
            projectStats.numCopypasteOrange
          }</td>
        </tr>
          <tr>
          <td class="label yellow">Yellow:</td>
          <td class="value yellow">${projectStats.numCopypasteYellow}</td>
          </tr>
        <tr>
          <td class="label">Score:</td>
          <td class="value">${
            projectStats.copypasteScore
          }</td>
        </tr>
          </table>

      <table align="center" class="stat">
        <tr>
          <th colspan="2">Complexity Violations</th>
          </tr>
        <tr>
          <td class="label red">Red:</td>
          <td class="value red">${
            projectStats.numComplexityRed
          }</td>
        </tr>
          <tr>
          <td class="label orange">Orange:</td>
          <td class="value orange">${projectStats.numComplexityOrange}</td>
          </tr>
        <tr>
          <td class="label yellow">Yellow:</td>
          <td class="value yellow">${
            projectStats.numComplexityYellow
          }</td>
        </tr>
          <tr>
          <td class="label">Score:</td>
          <td class="value">${projectStats.complexityScore}</td>
          </tr>
      </table>
          """

    out += generateFooter()
    return out
  }

  /**
   * Generate the HTML for the report header (with nav bar, style, etc.).
   */
  def generateHeader(ReportType report) {
    return  """\
      <html>
          <head>
          <title>Cruft4j - ${
            report.name
          }</title>
        <!-- Embedding style so that each report is self-contained (e.g. could be emailed 
             to someone) -->
        <style>
          h1 { 
            text-align: left; 
            margin: 10px;
          }
          div.navbar { 
            margin: 10px;
          }
          table.violation { 
            border-spacing: 0px; 
            border-collapse: collapse; 
            padding: 20px;
            margin: 10px;
          }
          table.violation th { 
            border: 1px solid #333333; 
            background-color: #666666; 
            text-align: left; 
            font-size: 18px;
            color: white;
            padding: 5px; 
          }
          table.violation tr.yellow { background-color: yellow; }
          table.violation tr.orange { background-color: orange; }
          table.violation tr.red { background-color: red; }
          table.violation td { border: 1px solid #333333; padding: 5px; }
          
          table.stat { 
            border-spacing: 0px; 
            border-collapse: collapse; 
            padding: 20px;
            margin: 10px;
          }
           
          table.stat th { 
            border: 1px solid #333333; 
            background-color: #666666; 
            text-align: left; 
            font-size: 18px;
            color: white;
            padding: 5px; 
          }
          
          table.stat td { 
            border: 1px solid #333333; 
            padding: 5px; 
          }
          table.stat td.label { width: 120px; }
          table.stat td.value { width: 120px; }
          table.stat td.label { font-weight: 500; }
          table.stat td.yellow { background-color: yellow; } 
          table.stat td.orange { background-color: orange; } 
          table.stat td.red { background-color: red; } 

        </style>
          </head>
      <body align="center">
        <h1>Cruft4J > ${report.name}</h1>
          <div class="navbar">
          <a href="${ReportType.Summary.url}">${
            ReportType.Summary.name
          }</a>
          &nbsp;&nbsp;|&nbsp;&nbsp;
          Complexity 
          (<a href="${ReportType.ComplexityAll.url}">all</a>, <a href="${ReportType.ComplexityNew.url}">new</a>)
          &nbsp;&nbsp;|&nbsp;&nbsp;
          Copy-Paste
          (<a href="${ReportType.CopyPasteAll.url}">all</a>, <a href="${ReportType.CopyPasteNew.url}">new</a>)
        </div>
                """
  }

  def generateFooter() {
    return "</table></body></html>"
  }
}
