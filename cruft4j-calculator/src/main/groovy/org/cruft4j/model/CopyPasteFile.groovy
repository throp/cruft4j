package org.cruft4j.calculator.model

/**
 * @author Ben Northrop
 */
class CopyPasteFile {

  String fileName

  int lineNumber


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
    result = prime * result + lineNumber;
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
    CopyPasteFile other = (CopyPasteFile) obj;
    if (fileName == null) {
      if (other.fileName != null)
        return false;
    } else if (!fileName.equals(other.fileName))
      return false;
    if (lineNumber != other.lineNumber)
      return false;
    return true;
  }
}
