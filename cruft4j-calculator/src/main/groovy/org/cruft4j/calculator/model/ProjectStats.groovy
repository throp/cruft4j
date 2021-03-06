package org.cruft4j.calculator.model

import groovy.transform.ToString

import java.text.DecimalFormat
import java.text.SimpleDateFormat


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

  def String getFormattedRunDate() {
    return new SimpleDateFormat("MM/dd/yyyy hh:mm a").format(runDate)
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
    return Integer.parseInt(getScaledComplexityScore()) + Integer.parseInt(getScaledCopypasteScore())
  }

  def String getScaledComplexityScore() {
    if(ncss == 0) {
      return "0"
    }
    return new DecimalFormat("#").format((complexityScore / ncss) * 1000)
  }

  def String getScaledCopypasteScore() {
    if(ncss == 0) {
      return "0"
    }
    return new DecimalFormat("#").format((copypasteScore / ncss) * 1000)
  }

  def String getPreScaledComplexityScore() {
    if(ncss == 0) {
      return "0"
    }
    return new DecimalFormat("#.###").format(complexityScore / ncss)
  }

  def String getPreScaledCopypasteScore() {
    if(ncss == 0) {
      return "0"
    }
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