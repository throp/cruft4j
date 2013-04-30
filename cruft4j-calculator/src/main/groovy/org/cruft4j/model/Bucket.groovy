package org.cruft4j.calculator.model

/**
 * Defines a severity "bucket" for either the complexity or copy-paste metric (e.g. "yellow", "orange", "red").
 *  
 * @author Ben Northrop
 */
class Bucket {

  int min

  int max

  Severity severity

  ViolationType violationType

  Bucket(int min, int max, Severity severity, ViolationType violationType) {
    this.min = min
    this.max = max
    this.severity =  severity
    this.violationType = violationType
  }

  def boolean isIn(int num) {
    return num >= min && num <= max
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((severity == null) ? 0 : severity.points);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Bucket other = (Bucket) obj;
    if (severity != other.severity)
      return false;
    return true;
  }
}
