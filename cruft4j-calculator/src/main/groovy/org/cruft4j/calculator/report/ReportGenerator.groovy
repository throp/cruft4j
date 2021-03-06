package org.cruft4j.calculator.report

import org.cruft4j.calculator.model.ProjectStats
import org.cruft4j.calculator.model.Severity


/**
 * 
 * @author Ben Northrop
 */
class ReportGenerator {

  static enum ReportType {
    Summary("Summary", "cruft4j-summary.html"),

    Complexity("Complexity", "cruft4j-complexity.html"),

    CopyPaste("Copy-Paste", "cruft4j-copypaste.html"),

    Trend("Trend", "cruft4j-trend.html")

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
   * Generate the HTML file for the trend report.
   */
  def generateTrendHtml(ProjectStats [] projects) {
    def out = generateHeader(ReportType.Trend)

    out += """
      <table class="copypaste violation" align="center">
                <tr>
                  <th>Run Date</th>
                  <th>Complexity Score</th>
                  <th>Copy-Paste Score</th>
                  <th>Overall Score</th>
                </tr>
      """

    projects.each {  out += """
      <tr>
        <td>${it.formattedRunDate}</td>
        <td>${it.scaledComplexityScore}</td>
        <td>${it.scaledCopypasteScore}</td>
        <td class="totalScore ${it.scoreSeverity}"><a class="${projectStats.scoreSeverity}" href="http://www.bennorthrop.com/cruft4j/compare.php?score=${projectStats.formattedScaledScore}&bucket=${projectStats.scoreSeverity}">${projectStats.formattedScaledScore}</a></td>
      </tr>
"""  }

    out += "</table>"
    out += generateFooter()

    return out
  }

  /**
   * Generate the HTML file for the copy-paste results.
   */
  def generateCopyPasteHtml(copyPastes) {
    def out = generateHeader(ReportType.CopyPaste)

    if(copyPastes.grep({ it.isNew }).size() == copyPastes.size()) {
      out = generateCopypasteTable(out, copyPastes)
    }
    else if(copyPastes.grep({ it.isNew }).size() > 0) {
      out += """<div class="subTitle">New instances since last run...</div>"""
      out = generateCopypasteTable(out, copyPastes.grep({ it.isNew }))
      out += """<br/>"""
      out += """<div class="subTitle">Instances existing since last run...</div>"""
      out = generateCopypasteTable(out, copyPastes.grep({ !it.isNew }))
    }
    else {
      out = generateCopypasteTable(out, copyPastes)
    }

    out += generateFooter()

    return out
  }

  /**
   * Return the table of copy paste details.
   */
  def String generateCopypasteTable(out, copyPastes) {
    if(copyPastes.isEmpty()) {
      out += "<p>none</p>"
      return out
    }

    out += """
        <table class="copypaste violation" align="center">
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
          <a class="fileLink" href="file:///${it.fileName}">${it.fileName}</a>
          (${it.lineNumber
          })<br>
         """ }

      out += """\
            </td>
          <td>${it.tokens}</td>
          <td>${
            it.lines
          }</td>
        </tr>
          """
    }
    out += "</table>"

    return out
  }

  /**
   * Generate the HTML file for the complexity results.
   */
  def generateComplexityHtml(methods) {
    def out = generateHeader(ReportType.Complexity)

    if(methods.grep({ it.isNew }).size() == methods.size()) {
      out = generateComplexityTable(out, methods)
    }
    else if(methods.grep({ it.isNew }).size() > 0) {
      out += """<div class="subTitle">New instances since last run...</div>"""
      out = generateComplexityTable(out, methods.grep({ it.isNew }))
      out += """<br/>"""
      out += """<div class="subTitle">Instances existing since last run...</div>"""
      out = generateComplexityTable(out, methods.grep({ !it.isNew }))
    }
    else {
      out = generateComplexityTable(out, methods)
    }

    out += generateFooter()
    return out
  }

  /**
   * Generate the table display of the complexity violations.
   */
  def String generateComplexityTable(out, methods) {

    if(methods.isEmpty()) {
      out += "<p>none</p>"
      return out
    }

    out += """
          <table class="complexity violation"  align="center">
          <tr>
            <th>Method</th>
            <th>Complexity</th>
          </tr>
      """


    methods.each { out += """\
        <tr class="${it.bucket.severity.name}">
          <td>
            ${it.getNameTrunc()}<br/>
            </td>
          <td>${
            it.ccn
          }</td>
        </tr>
          """ }
    out += "</table>"
    return out
  }

  /**
   * Generate the overall report, showing the score for all the source.
   */
  def generateSummaryReport() {
    def out = generateHeader(ReportType.Summary)

    out += """\
<table align="center">
        <tr>
          <td colspan="6" class="scoreTypeTitle">Complexity</td>
          <td width="50"></td>
          <td colspan="6" class="scoreTypeTitle">Copy-Paste</td>
        </tr>
        <tr>
          <td></td>
          <td>Instances</td>
          <td></td>
          <td>Points</td>
          <td></td>
          <td>Total</td>
          <td></td>
          <td></td>
          <td>Instances</td>
          <td></td>
          <td>Points</td>
          <td></td>
          <td>Total</td>
        </tr>
        <tr>
          <td>Yellow</td>
          <td class="numberCell">${projectStats.numComplexityYellow}</td>
          <td>&nbsp;x</td>
          <td class="numberCell">${Severity.Yellow.points}</td>
          <td>=</td>
          <td class="numberCell">${projectStats.numComplexityYellow * Severity.Yellow.points}</td>
          <td></td>
          <td>Yellow</td>
          <td class="numberCell">${projectStats.numCopypasteYellow}</td>
          <td>&nbsp;x</td>
          <td class="numberCell">${Severity.Yellow.points}</td>
          <td>=</td>
          <td class="numberCell">${projectStats.numCopypasteYellow * Severity.Yellow.points}</td>
        </tr>
        <tr>
          <td>Orange</td>
          <td class="numberCell">${projectStats.numComplexityOrange}</td>
          <td>&nbsp;x</td>
          <td class="numberCell">${Severity.Orange.points}</td>
          <td>=</td>
          <td class="numberCell">${projectStats.numComplexityOrange * Severity.Orange.points}</td>
          <td></td>
          <td>Orange</td>
          <td class="numberCell">${projectStats.numCopypasteOrange}</td>
          <td>&nbsp;x</td>
          <td class="numberCell">${Severity.Orange.points}</td>
          <td>=</td>
          <td class="numberCell">${projectStats.numCopypasteOrange * Severity.Orange.points}</td>
        </tr>
        <tr>
          <td>Red</td>
          <td class="numberCell">${projectStats.numComplexityRed}</td>
          <td>&nbsp;x</td>
          <td class="numberCell">${Severity.Red.points}</td>
          <td>=</td>
          <td class="numberCell">${projectStats.numComplexityRed * Severity.Orange.points}</td>
          <td></td>
          <td>Red</td>
          <td class="numberCell">${projectStats.numCopypasteRed}</td>
          <td>&nbsp;x</td>
          <td class="numberCell">${Severity.Red.points}</td>
          <td>=</td>
          <td class="numberCell">${projectStats.numCopypasteRed * Severity.Red.points}</td>
        </tr>
        <!-- Sum points -->
        <tr height="1">
          <td colspan="4"></td>
          <td colspan="2" class="lineCell"><div></div></td>
          <td colspan="5"></td>    
          <td colspan="2" class="lineCell"><div></div></td>
        </tr>
        <tr>
          <td></td>
          <td></td>
          <td></td>
          <td></td>
          <td></td>
          <td class="numberCell">${projectStats.formattedComplexityScore}</td>
          <td></td>
          <td></td>
          <td></td>
          <td></td>
          <td></td>
          <td></td>
          <td class="numberCell">${projectStats.formattedCopypasteScore}</td>
        </tr>
        <!-- LOC -->
        <tr>
          <td></td>
          <td></td>
          <td></td>
          <td></td>
          <td>/</td>
          <td class="numberCell">${projectStats.formattedNcss}</td>
          <td>LOC</td>
          <td></td>
          <td></td>
          <td></td>
          <td></td>
          <td>/</td>
          <td class="numberCell">${projectStats.formattedNcss}</td>
          <td>LOC</td>
        </tr>
        <tr height="1">
          <td colspan="4"></td>
          <td colspan="2" class="lineCell"><div></div></td>
          <td colspan="5"></td>    
          <td colspan="2" class="lineCell"><div></div></td>
        </tr>  
        <tr>
          <td></td>
          <td></td>
          <td></td>
          <td></td>
          <td></td>
          <td class="numberCell">${projectStats.preScaledComplexityScore}</td>
          <td></td>
          <td></td>
          <td></td>
          <td></td>
          <td></td>
          <td></td>
          <td class="numberCell">${projectStats.preScaledCopypasteScore}</td>
        </tr>  
        <tr>
          <td></td>
          <td></td>
          <td></td>
          <td></td>
          <td>x</td>
          <td class="numberCell">1,000</td>
          <td></td>
          <td></td>
          <td></td>
          <td></td>
          <td></td>
          <td>x</td>
          <td class="numberCell">1,000</td>
        </tr>  
        <!-- Total -->
        <tr height="1">
          <td colspan="4"></td>
          <td colspan="2" class="lineCell"><div></div></td>
          <td colspan="5"></td>    
          <td colspan="2" class="lineCell"><div></div></td>
        </tr>
        <tr>
          <td></td>
          <td></td>
          <td></td>
          <td></td>
          <td>=</td>
          <td class="numberCell">${projectStats.scaledComplexityScore}</td>
          <td></td>
          <td></td>
          <td></td>
          <td></td>
          <td></td>
          <td>=</td>
          <td class="numberCell">${projectStats.scaledCopypasteScore}</td>
        </tr>    
        <tr>
          <td colspan="4"></td>
          <td colspan="9" class="lineCell"><div></div></td>
        </tr>
        <tr>
          <td colspan="8"></td>
          <td colspan="4" class="scoreTypeTitle" style="text-align: right">Cruft Score:&nbsp;&nbsp;</td>
          <td colspan="1" class="totalScore ${projectStats.scoreSeverity}"><a class="${projectStats.scoreSeverity}" href="http://www.bennorthrop.com/cruft4j/compare.php?score=${projectStats.formattedScaledScore}&bucket=${projectStats.scoreSeverity}" >${projectStats.formattedScaledScore}</a></td>
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
                <title>Cruft4J &#187; ${report.name}</title>
                <style type="text/css">
              
                body { font-family: Garamond; font-size: 12pt;}
                a { text-decoration: none; }
                a:hover { text-decoration: underline; }
                td.numberCell { text-align: right; }
                td.lineCell { border-color: #333333; border-width: 0px 0px 1px 0px; border-style: solid; padding: 0px; margin: 0px; }
                td.heading { font-weight: bold; }
                td.title { text-align: center; }
                .fileLink { color: #333333; }
                .green { background-color: green; color: white; }
                .yellow { background-color: yellow; color: #333333; }
                .orange { background-color: orange; color: white; }
                .red { background-color: red; color: white; }
                .titleBar { font-size: 24pt; font-weight: bold; padding: 2px; border-width: 0px 0px 2px 0px; border-style: solid; margin: 0px; border-color: #333333; color: #333333;}
                .navBar { padding: 0px; margin: 3px; text-align: right; font-size: 12pt; width:1000px;}
                .totalScore { font-size: 24pt; padding: 3px; text-align: center; font-weight: bold; }
                .projectBar { background-color: gainsboro; font-size: 12pt; width: 800;}
                .projectLabel { font-weight: bold; width: 110px; }
                .projectValue { text-align: left; text-align: left;}
                .cruft4jTitle a { font-color: #333333;  font-style: none; display: inline; }
                .scoreTypeTitle { font-weight: bold; font-size: 14pt; }
                .subTitle { font-weight: bold; font-size: 14pt; } 
                table.violation { border-spacing: 0px; border-collapse: collapse; padding: 20px; margin: 10px;  }
                table.violation th { border: 1px solid #333333; background-color: #666666; text-align: left; font-size: 18px;color: white;padding: 5px;  }
                table.violation tr.yellow { background-color: yellow; }
                table.violation tr.orange { background-color: orange; }
                table.violation tr.red { background-color: red; }
                table.violation td.green { background-color: green; color: white; }
                table.violation td.yellow { background-color: yellow; color: #333333; }
                table.violation td.orange { background-color: orange; color: white; }
                table.violation td.red { background-color: red; color: white; }
                table.violation td { border: 1px solid #333333; padding: 5px; color: #333333; }
                table.calculation th { border: 1px 0px 1px 0px solid #333333; background-color: #666666; text-align: left; font-size: 18px; color: white; padding: 5px; }
                table.calculation { border-spacing: 0px; border-collapse: collapse; padding: 20px; margin: 10px;  }
                </style>
              </head>
              <body >
              <table border="0" cellspacing="0" cellpadding="0" width="1000" align="center" >
                <tr>
                  <td width="1000" align="center">  
                    <table class="titleBar">
                      <tr>
                        <td width="960"><a href="http://www.bennorthrop.com/cruft4j/index.php" ><font color="black">Cruft4J</font></a> &#187; ${report.name} Report</td>
                        <td width="40" class="totalScore ${projectStats.scoreSeverity}"><a class="${projectStats.scoreSeverity}" href="http://www.bennorthrop.com/cruft4j/compare.php?score=${projectStats.formattedScaledScore}&bucket=${projectStats.scoreSeverity}">${projectStats.formattedScaledScore}</a></td>
                      </tr>
                    </table>
                    <div class="navBar">
                        <b>Reports:</b> &nbsp;
                        <a href="${ReportType.Summary.url}">Summary</a> &nbsp;|&nbsp;
                        <a href="${ReportType.Trend.url}">Trend</a> &nbsp;|&nbsp;
                        <a href="${ReportType.Complexity.url}">Complexity</a> &nbsp;|&nbsp; 
                        <a href="${ReportType.CopyPaste.url}">Copy Paste</a>
                    </div>
                    <br/>
                    <table class="projectBar" align="center"> 
                      <tr>
                        <td class="projectLabel" >Project Name:</td>
                        <td class="projectValue">${projectStats.name}</td>
                      </tr>
                      <tr>
                        <td class="projectLabel">Run Date:</td>
                        <td class="projectValue">${projectStats.runDate}</td>
                      </tr>
                    </table>
                    <br/>
                    <br/>
                """
  }

  def generateFooter() {
    return "</table></body></html>"
  }
}
