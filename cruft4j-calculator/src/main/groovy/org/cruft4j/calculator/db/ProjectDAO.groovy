package org.cruft4j.calculator.db

import groovy.sql.Sql

import org.cruft4j.calculator.model.ProjectStats

/**
 * 
 * @author Ben Northrop
 */
class ProjectDAO extends DAO {

  ProjectDAO(Sql sql) {
    this.sql = sql
    createTable()
  }

  def ProjectStats [] findAllProjects(String projectPath) {
    def projects = []
    sql.eachRow( """
        SELECT 
          name,
          path,
          run_id,
          run_date,
          ncss,
          complexity_score,
          copypaste_score,
          raw_score,
          scaled_score,
          num_complexity_yellow,
          num_complexity_orange,
          num_complexity_red,
          num_copypaste_yellow,
          num_copypaste_orange,
          num_copypaste_red
        FROM project_stats 
        ORDER BY run_id DESC
""" ) {
          def projectStats = new ProjectStats(
              name : it.name,
              path : it.path,
              runDate : it.run_date,
              ncss : it.ncss,
              complexityScore : it.complexity_score,
              copypasteScore : it.copypaste_score,
              rawScore : it.raw_score,
              scaledScore : it.scaled_score,
              numComplexityYellow : it.num_complexity_yellow,
              numComplexityOrange : it.num_complexity_orange,
              numComplexityRed : it.num_complexity_red,
              numCopypasteYellow : it.num_copypaste_yellow,
              numCopypasteOrange : it.num_copypaste_orange,
              numCopypasteRed : it.num_copypaste_red)
          projects << projectStats
        }
    return projects
  }

  def ProjectStats fetchMostRecentProjectStats(String projectPath) {
    def row = sql.firstRow("""\
        SELECT 
          name,
          path,
          run_id,
          run_date,
          ncss,
          complexity_score,
          copypaste_score,
          raw_score,
          scaled_score,
          num_complexity_yellow,
          num_complexity_orange,
          num_complexity_red,
          num_copypaste_yellow,
          num_copypaste_orange,
          num_copypaste_red
        FROM project_stats
        WHERE path = ${projectPath}
        ORDER BY run_id DESC
       """)
    if(row == null || row.isEmpty()) {
      return new ProjectStats()
    }
    return new ProjectStats(
    name : row.name,
    path : row.path,
    runDate : row.run_date,
    ncss : row.ncss,
    complexityScore : row.complexity_score,
    copypasteScore : row.copypaste_score,
    rawScore : row.raw_score,
    scaledScore : row.scaled_score,
    numComplexityYellow : row.num_complexity_yellow,
    numComplexityOrange : row.num_complexity_orange,
    numComplexityRed : row.num_complexity_red,
    numCopypasteYellow : row.num_copypaste_yellow,
    numCopypasteOrange : row.num_copypaste_orange,
    numCopypasteRed : row.num_copypaste_red
    )
  }

  /**
   * Insert a row in for one run of cruft4j on the project.  
   */
  public def insert(ProjectStats projectStats) {
    def success = sql.execute("""\
        INSERT INTO project_stats ( 
          name,
          path,
          run_id,
          run_date,
          ncss,
          complexity_score,
          copypaste_score,
          raw_score,
          scaled_score,
          num_complexity_yellow,
          num_complexity_orange,
          num_complexity_red,
          num_copypaste_yellow,
          num_copypaste_orange,
          num_copypaste_red
        )
        VALUES (
          ${projectStats.name},
          ${projectStats.path},
          ${projectStats.runId},
          ${projectStats.runDate},
          ${projectStats.ncss},
          ${projectStats.complexityScore},
          ${projectStats.copypasteScore},
          ${projectStats.rawScore},
          ${projectStats.scaledScore},
          ${projectStats.numComplexityYellow},
          ${projectStats.numComplexityOrange},
          ${projectStats.numComplexityRed},
          ${projectStats.numCopypasteYellow},
          ${projectStats.numCopypasteOrange},
          ${projectStats.numCopypasteRed}
        )
      """)
  }

  /**
   * Called prior to every query, in case the table does not already exist.  
   */
  private def createTable() {
    def success = sql.execute("""\
        CREATE TABLE IF NOT EXISTS project_stats (
          name VARCHAR(50),
          path VARCHAR(1000),
          run_id BIGINT,
          run_date TIMESTAMP,
          ncss INT,
          complexity_score INT,
          copypaste_score INT,
          raw_score INT,
          scaled_score DECIMAL,
          num_complexity_yellow INT,
          num_complexity_orange INT,
          num_complexity_red INT,
          num_copypaste_yellow INT,
          num_copypaste_orange INT,
          num_copypaste_red INT
        )
      """)
  }
}
