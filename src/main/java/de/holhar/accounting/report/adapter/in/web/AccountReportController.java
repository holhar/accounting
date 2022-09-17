package de.holhar.accounting.report.adapter.in.web;

import de.holhar.accounting.common.WebAdapter;
import de.holhar.accounting.report.application.port.in.DownloadMonthlyReportsCsvUseCase;
import de.holhar.accounting.report.application.port.out.LoadReportsPort;
import de.holhar.accounting.report.domain.MonthlyReport;
import de.holhar.accounting.report.application.service.AccountReportService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@WebAdapter
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class AccountReportController {

  private final DownloadMonthlyReportsCsvUseCase downloadMonthlyReportsCsvUseCase;
  private final AccountReportService reportService;
  private final LoadReportsPort loadReportsPort;

  // TODO: Take shortcuts consciously...
  @GetMapping
  public List<MonthlyReport> getMonthlyReports() {
    return loadReportsPort.loadAllMonthlyReports();
  }

  // TODO: Implement endpoint for downloading csv file
  @GetMapping("/monthly")
  public ResponseEntity<String> createMonthlyReports() {
    reportService.createReports();
    return ResponseEntity.ok("Report creation successful");
  }

  @PostMapping
  public ResponseEntity<String> createFullCsvReport() throws IOException {
    // TODO: Continue with use case definition for controllers
    String csvReport = downloadMonthlyReportsCsvUseCase.createCsvReport();
    return ResponseEntity.ok(csvReport);
  }
}
