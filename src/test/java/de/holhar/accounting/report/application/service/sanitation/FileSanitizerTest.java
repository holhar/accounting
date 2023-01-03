package de.holhar.accounting.report.application.service.sanitation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.Test;

class FileSanitizerTest {

  private final Sanitizer fileSanitizer = new FileSanitizer();

  @Test
  void cleanUp_accountingStatement_Succeeds() throws IOException {
    // Given
    Path unprocessedFile = Paths.get("src/test/resources/accounting/unprocessed/acc_202001.csv");

    // When
    List<String> resultLines = fileSanitizer.sanitize(unprocessedFile);

    // Then
    Path processedFile = Paths.get("src/test/resources/accounting/sanitized/acc_202001.csv");
    List<String> sanitizedLines = Files.readAllLines(processedFile);
    assertEquals(sanitizedLines, resultLines);
  }

  @Test
  void cleanUp_creditCardStatement_Succeeds() throws IOException {
    // Given
    Path unprocessedFile = Paths.get("src/test/resources/accounting/unprocessed/cre_202001.csv");

    // When
    List<String> resultLines = fileSanitizer.sanitize(unprocessedFile);

    // Then
    Path processedFile = Paths.get("src/test/resources/accounting/sanitized/cre_202001.csv");
    List<String> sanitizedLines = Files.readAllLines(processedFile);
    assertEquals(sanitizedLines, resultLines);
  }

}
