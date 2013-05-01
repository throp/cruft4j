package org.cruftj4.maven;

import static org.cruft4j.calculator.calc.ScoreCalculator.printConfig;
import static org.cruft4j.calculator.calc.ScoreCalculator.printProjectStats;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.cruft4j.calculator.calc.ScoreCalculator;
import org.cruft4j.calculator.model.ProjectStats;
import org.cruft4j.calculator.model.RunConfig;

/**
 * Calculate the Cruft4J score, and fail the build if it's above the configured
 * threshold.
 * 
 * @goal calculate-cruft
 */
public class Cruft4JMojo extends AbstractMojo {

  /**
   * @parameter expression="${project.artifactId}"
   * @required
   */
  private String artifact;

  /**
   * @parameter expression="${project.build.sourceDirectory}"
   * @required
   */
  private File sourceDirectory;

  /**
   * @parameter expression="${project.build.directory}"
   * @required
   */
  private File buildDirectory;

  /**
   * @parameter default-value=""
   */
  private String outputDirectory;

  /**
   * @parameter default-value=""
   */
  private String projectName;

  /**
   * @parameter default-value="100"
   */
  private Integer rawScoreThreshold;

  /**
   * @parameter default-value="20"
   */
  private Integer scoreThreshold;

  public void execute() throws MojoExecutionException, MojoFailureException {

    String sourceDirectoryPath = sourceDirectory.getAbsolutePath();
    String buildDirectoryPath = buildDirectory.getAbsolutePath();

    // Initialize the RunConfig object, given the config from the pom.xml
    RunConfig runConfig = new RunConfig();
    if (outputDirectory == null || "".equals(outputDirectory)) {
      runConfig.setOutputDir(buildDirectoryPath);
      runConfig.setProjectName("cruft4j");
    } else {
      runConfig.setOutputDir(outputDirectory);
      runConfig.setProjectName(!"".equals(projectName) ? projectName : artifact);
    }
    runConfig.setSourceDir(sourceDirectoryPath);
    runConfig.setLogToConsole(false);

    // Run Cruft4J
    ProjectStats project = new ScoreCalculator().calculate(runConfig);

    // Print to the console
    String out = printConfig(runConfig) + "\n" + printProjectStats(runConfig, project);
    getLog().info(out);

    // If the scores are above either of the thresholds, then fail the build!
    if (project.getRawScore() >= rawScoreThreshold) {
      throw new MojoFailureException("Cruft4J raw score (" + project.getRawScore()
          + ") is greater than threshold (" + rawScoreThreshold + ").");
    }

    if (Math.round(project.getScaledScore()) >= scoreThreshold) {
      throw new MojoFailureException("Cruft4J score (" + project.getFormattedScaledScore()
          + ") is greater than threshold (" + scoreThreshold + ")");
    }
  }
}