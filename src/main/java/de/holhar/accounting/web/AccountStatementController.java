package de.holhar.accounting.web;

import de.holhar.accounting.service.AccountStatementService;
import de.holhar.accounting.service.CsvDeSanitizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statements")
public class AccountStatementController {

  private static final Logger logger = LoggerFactory.getLogger(AccountStatementController.class);

  private final AccountStatementService accountStatementService;
  private final CsvDeSanitizationService csvDeSanitizationService;

  public AccountStatementController(
      AccountStatementService accountStatementService,
      CsvDeSanitizationService csvDeSanitizationService
  ) {
    this.accountStatementService = accountStatementService;
    this.csvDeSanitizationService = csvDeSanitizationService;
  }

  @PostMapping("/import")
  public ResponseEntity<String> batchImport() {
    try {
      accountStatementService.importStatements();
    } catch (Exception e) {
      logger.error("Reports import failed", e);
      return ResponseEntity.internalServerError().body("Batch import failed: " + e.getMessage());
    }
    return ResponseEntity.ok("Batch import successful");
  }

  @PostMapping("/de-sanitize")
  public ResponseEntity<String> migrateType() {
    try {
      csvDeSanitizationService.deSanitize();
    } catch (Exception e) {
      logger.error("Type migration failed", e);
      return ResponseEntity.internalServerError().body("Type migration failed: " + e.getMessage());
    }
    return ResponseEntity.ok("Type migration successful");
  }
}
