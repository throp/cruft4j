package org.cruft4j.calculator.model

import groovy.transform.ToString

import java.text.DecimalFormat


/**
 * ProjectStats.groovy 
 * 
 * @author Ben Northrop
 */
@ToString
class ProjectStats implements Serializable {

  String name

  String path

  String url

  String repositoryUrl

  Date runDate

  long ncss

  int complexityScore

  int copypasteScore

  int rawScore

  float scaledScore

  int numComplexityGreen

  int numComplexityYellow

  int numComplexityOrange

  int numComplexityRed

  int numCopypasteYellow

  int numCopypasteOrange

  int numCopypasteRed

  int numJavadocs

  def methods = []

  def copyPastes = []

  def long getRunId() {
    runDate.getTime()
  }

  def String getFormattedCopypasteScore() {
    return new DecimalFormat("#,###").format(copypasteScore)
  }

  def String getFormattedComplexityScore() {
    return new DecimalFormat("#,###").format(complexityScore)
  }

  def String getFormattedNcss() {
    return new DecimalFormat("#,###").format(ncss)
  }

  def String getFormattedScaledScore() {
    return new DecimalFormat("#").format(scaledScore)
  }

  def String getScaledComplexityScore() {
    return new DecimalFormat("#").format((complexityScore / ncss) * 1000)
  }

  def String getScaledCopypasteScore() {
    return new DecimalFormat("#").format((copypasteScore / ncss) * 1000)
  }

  def String getPreScaledComplexityScore() {
    return new DecimalFormat("#.###").format(complexityScore / ncss)
  }

  def String getPreScaledCopypasteScore() {
    return new DecimalFormat("#.###").format(copypasteScore / ncss)
  }

  def String getScoreSeverity() {
    if(scaledScore <=30) {
      return Severity.Green.name
    }
    else if ( scaledScore > 30 && scaledScore <= 60) {
      return Severity.Yellow.name
    }
    else if (scaledScore > 60 && scaledScore <= 90) {
      return Severity.Orange.name
    }
    else if(scaledScore > 90) {
      return Severity.Red.name
    }
  }
}