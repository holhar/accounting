package de.holhar.accounting.report.application.service;

import de.holhar.accounting.AccountingConfigurationProperties;
import de.holhar.accounting.common.UseCase;
import de.holhar.accounting.report.application.service.sanitation.DeSanitizer;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Create beans via AccountingConfiguration
@UseCase
public class CsvDeSanitizationService {

  private static final Logger logger = LoggerFactory.getLogger(CsvDeSanitizationService.class);

  private final DeSanitizer deSanitizer;
  private final Path resultPath;

  public CsvDeSanitizationService(DeSanitizer deSanitizer, AccountingConfigurationProperties accountingConfigurationProperties) {
    this.deSanitizer = deSanitizer;

    Path deSanitizationPath = ServiceUtils.getValidPath(accountingConfigurationProperties.getDeSanitizationPath());
    this.resultPath = deSanitizationPath.resolve("result");
  }

  public void deSanitize(List<Path> files) {
      files.forEach(this::deSanitize);
  }

  private void deSanitize(Path sourceFilePath) {
    logger.debug("Start type migration for {}", sourceFilePath.getFileName());
    List<String> lines;
    try {
      lines = deSanitizer.deSanitize(sourceFilePath);
      writeToFile(sourceFilePath.getFileName(), lines);
    } catch (Exception e) {
      var errorMsg = String.format("Failed to migrate types for file '%s'", sourceFilePath.getFileName());
      logger.warn(errorMsg);
      logger.info("Skip migration for {}", sourceFilePath.getFileName());
      return;
    }
    logger.debug("Finished type migration for {}", sourceFilePath.getFileName());
  }

  private void writeToFile(Path fileName, List<String> lines) {
    Path resultFilePath = resultPath.resolve(fileName);
    try(BufferedWriter writer = Files.newBufferedWriter(resultFilePath, StandardCharsets.ISO_8859_1)) {
      lines.stream().sequential().forEach(line -> writeLine(writer, line));
    } catch (IOException e) {
      var errorMsg = String.format("Failed to write migrate types to file '%s'", resultFilePath.getFileName().toString());
      throw new IllegalArgumentException(errorMsg, e);
    }
  }

  private void writeLine(BufferedWriter writer, String line) {
    try {
      writer.write(line + "\n");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
