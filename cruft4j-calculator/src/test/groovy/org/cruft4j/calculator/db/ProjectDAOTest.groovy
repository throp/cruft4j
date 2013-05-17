package org.cruft4j.calculator.db

import org.cruft4j.calculator.model.ProjectStats



class ProjectDAOTest extends DAOTest {

  def projectDAO = new ProjectDAO(sql)

  void testInsertAndFetch() {
    ProjectStats ps = new ProjectStats(
        name : "Test",
        path: "test/foo/bar",
        url: "http://foo.bar.com",
        runDate : new Date(),
        ncss : 1234,
        complexityScore : 234,
        copypasteScore : 39,
        rawScore: 23432,
        scaledScore: 5.23f,
        numComplexityYellow: 1,
        numComplexityOrange: 5,
        numComplexityRed: 2,
        numCopypasteYellow: 2,
        numCopypasteOrange: 5,
        numCopypasteRed: 10,
        numJavadocs: 50
        )
    projectDAO.insert(ps)

    def ps2 = projectDAO.fetchMostRecentProjectStats()
  }

  void testFindAll() {
    println projectDAO.findAllProjects()
  }
}
