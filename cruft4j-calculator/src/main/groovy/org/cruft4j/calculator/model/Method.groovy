package org.cruft4j.calculator.model


/**
 * @author Ben Northrop
 */
class Method implements Serializable {

  String name

  int ncss

  int ccn

  boolean javadocs

  Bucket bucket

  ProjectStats projectStats

  boolean isNew


  def boolean isDefault() {
    return name.contains("equals(") || name.contains("hashCode(") || name.contains("toString(")
  }

  def String getNameTrunc() {
    return name.size() > 125 ? name[0..124] + "..." : name
  }

  /**
   * Note - this doesn't totally work!  Missing the directory between the sourceDir and the package!
   */
  def String getFileName() {
    if(name == null || "".equals(name)) {
      return ""
    }
    int index = name.lastIndexOf(".")
    String fileName = index == -1 ? name : name.substring(0, index)
    fileName = fileName.replace(".", File.separator + "")
    String projectPath = projectStats?.path ?: ""
    return projectPath + File.separator + fileName
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((bucket == null) ? 0 : bucket.hashCode());
    result = prime * result + ccn;
    result = prime * result + (javadocs ? 1 : 0);
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ncss;
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
    Method other = (Method) obj;
    if (bucket == null) {
      if (other.bucket != null)
        return false;
    } else if (!bucket.equals(other.bucket))
      return false;
    if (ccn != other.ccn)
      return false;
    if (javadocs != other.javadocs)
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (ncss != other.ncss)
      return false;
    return true;
  }
}
