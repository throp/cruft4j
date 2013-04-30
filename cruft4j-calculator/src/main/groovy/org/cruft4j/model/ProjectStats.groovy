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

  def String getFormattedScaledScore() {
    return new DecimalFormat("#").format(scaledScore)
  }
}