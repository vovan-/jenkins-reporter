package org.zeroturnaround.jenkins.reporter;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.jenkins.reporter.model.JenkinsView;
import org.zeroturnaround.jenkins.reporter.util.URLParamEncoder;

/**
 * Main entry point
 */
public class Main {
  private static final String VIEW_URL_PATTERN_PROPERTY = "jenkins.pattern";

  private static final String JENKINS_URL_PROPERTY = "jenkins.url";

  private static final Logger log = LoggerFactory.getLogger(Main.class); // NOSONAR

  /**
   * The HTTP url of your Jenkins instances. For example http://jenkins/
   */
  private static final String JENKINS_URL = System.getProperty(JENKINS_URL_PROPERTY);

  /**
   * The URL pattern for Jenkins. For example "%s/view/SomeView/view/%s". This will get expanded
   * to http://jenkins/view/SomeView/view/viewname. If you have some sort of view plugin installed
   * then this approach is required.
   */
  private static final String VIEW_URL_PATTERN = System.getProperty(VIEW_URL_PATTERN_PROPERTY);

  private static final String OUTPUT_FILE_NAME = System.getProperty("output.file");

  public static final String JOB_NAME_PREFIX = System.getProperty("jobName.prefix");

  public static final void main(String[] args) throws Exception {
    if (args.length == 0) {
      System.err.println("Please give the name of Jenkins view as parameter to this script.");
      System.exit(-1);
    }

    if (!validateArguments()) {
      System.exit(1);
    }

    // lets support URL's ending with a slash and also without one
    String jenkinsUrl = JENKINS_URL;
    if (JENKINS_URL.endsWith("/"))
      jenkinsUrl = JENKINS_URL.substring(0, JENKINS_URL.length() - 1);

    // Lets generate a report for all the view specified
    for (final String jenkinsViewName : args) {
      final URI viewUrl = new URI(String.format(VIEW_URL_PATTERN, jenkinsUrl, URLParamEncoder.encode(jenkinsViewName)));

      // lets generate a output filename if none provided
      String outputFileName = OUTPUT_FILE_NAME;
      if (outputFileName == null) {
        final SimpleDateFormat sdf = new SimpleDateFormat("'jenkins-report-'yyyy-MM-dd_HH.mm.ss");
        outputFileName = jenkinsViewName + "-" + sdf.format(new Date()) + "-";
      }
      log.debug("Using view URL {} and generating output to {}", viewUrl, outputFileName);

      final File outputFile = File.createTempFile(outputFileName, ".html");
      final PrintWriter out = new PrintWriter(new FileWriter(outputFile));

      // ViewData viewData =
      JenkinsHelper jHelper = (new JenkinsHelperBuilder()).createDefault();
      JenkinsView viewData = jHelper.getViewData(viewUrl);

      final JenkinsReportGenerator app = (new JenkinsReportGeneratorBuilder()).buildDefaultGenerator();
      app.generateReport(viewData, out);

      log.info("Generated report to: " + outputFile);
    }
  }

  private static boolean validateArguments() {
    if (VIEW_URL_PATTERN == null) {
      System.out.println(String.format("Please provide your Jenkins view url pattern with -D%s", VIEW_URL_PATTERN_PROPERTY));
      return false;
    }

    if (JENKINS_URL == null) {
      System.out.println(String.format("Please provide your jenkins URL via JVM property -D%s", JENKINS_URL_PROPERTY));
      return false;
    }
    return true;
  }
}
