package de.holhar.accounting.web;

import de.holhar.accounting.domain.MonthlyReport;
import de.holhar.accounting.repository.MonthlyReportRepository;
import de.holhar.accounting.service.AccountReportService;
import java.io.IOException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
public class AccountReportController {

  private final AccountReportService reportService;
  private final MonthlyReportRepository repository;

  public AccountReportController(AccountReportService reportService, MonthlyReportRepository repository) {
    this.reportService = reportService;
    this.repository = repository;
  }

  @GetMapping
  public List<MonthlyReport> getMonthlyReports() {
    return repository.findAll();
  }

  // TODO: Implement endpoint for downloading csv file
  @GetMapping("/monthly")
  public ResponseEntity<String> createMonthlyReports() {
    reportService.createReports();
    return ResponseEntity.ok("Report creation successful");
  }

  @PostMapping
  public ResponseEntity<String> createFullCsvReport() throws IOException {
    reportService.createCsvReport();
    return ResponseEntity.ok("Successfully created CSV report");
  }
}
