package de.holhar.accounting.web;

import de.holhar.accounting.service.AccountStatementService;
import de.holhar.accounting.service.CsvDeSanitizationService;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/statements")
public class AccountStatementController {

  private static final Logger logger = LoggerFactory.getLogger(AccountStatementController.class);

  private final AccountStatementService accountStatementService;
  private final CsvDeSanitizationService csvDeSanitizationService;
  private final FileHandler fileHandler;

  public AccountStatementController(
      AccountStatementService accountStatementService,
      CsvDeSanitizationService csvDeSanitizationService,
      FileHandler fileHandler
  ) {
    this.accountStatementService = accountStatementService;
    this.csvDeSanitizationService = csvDeSanitizationService;
    this.fileHandler = fileHandler;
  }

  @PostMapping("/import")
  public ResponseEntity<String> batchImport(@RequestParam("file") MultipartFile zipFile)
      throws IOException {
    List<Path> files = fileHandler.unpackZipFile(zipFile);
    try {
      accountStatementService.importStatements(files);
    } catch (Exception e) {
      logger.error("Reports import failed", e);
      return ResponseEntity.internalServerError().body("Batch import failed: " + e.getMessage());
    }
    return ResponseEntity.ok("Batch import successful");
  }

  // TODO: Adjust endpoint to return file for download with de-sanitized files
  @PostMapping("/de-sanitize")
  public ResponseEntity<String> migrateType(@RequestParam("file") MultipartFile zipFile)
      throws IOException {
    List<Path> files = fileHandler.unpackZipFile(zipFile);
    try {
      csvDeSanitizationService.deSanitize(files);
    } catch (Exception e) {
      logger.error("Type migration failed", e);
      return ResponseEntity.internalServerError().body("Type migration failed: " + e.getMessage());
    }
    return ResponseEntity.ok("Type migration successful");
  }
}
