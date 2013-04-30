package org.cruft4j.calculator.model


/**
 * @author Ben Northrop
 */
class CopyPaste implements Serializable {

  int lines

  int tokens

  def files = []

  Bucket bucket

  ProjectStats projectStats

  boolean isNew

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((bucket == null) ? 0 : bucket.hashCode());
    result = prime * result + ((files == null) ? 0 : Arrays.deepHashCode(files.hashCode()));
    result = prime * result + lines;
    result = prime * result + tokens;
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
    CopyPaste other = (CopyPaste) obj;
    if (bucket == null) {
      if (other.bucket != null)
        return false;
    } else if (!bucket.equals(other.bucket))
      return false;
    if (files == null) {
      if (other.files != null)
        return false;
    } else if (!files.equals(other.files))
      return false;
    if (lines != other.lines)
      return false;
    if (tokens != other.tokens)
      return false;
    return true;
  }
}
