package de.holhar.accounting.report.adapter.in.web;

import de.holhar.accounting.common.WebAdapter;
import de.holhar.accounting.report.application.service.AccountStatementService;
import de.holhar.accounting.report.application.service.CsvDeSanitizationService;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@WebAdapter
@RestController
@RequestMapping("/statements")
@RequiredArgsConstructor
public class AccountStatementController {

  private static final Logger logger = LoggerFactory.getLogger(AccountStatementController.class);

  private final AccountStatementService accountStatementService;
  private final CsvDeSanitizationService csvDeSanitizationService;
  private final FileHandler fileHandler;

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
}
