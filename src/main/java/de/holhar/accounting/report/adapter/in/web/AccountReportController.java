package de.holhar.accounting.report.adapter.in.web;

import de.holhar.accounting.common.WebAdapter;
import de.holhar.accounting.report.application.port.in.CreateMonthlyReportsUseCase;
import de.holhar.accounting.report.application.port.in.DownloadMonthlyReportsCsvUseCase;
import de.holhar.accounting.report.application.port.out.LoadReportsPort;
import de.holhar.accounting.report.domain.MonthlyReport;
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
  private final CreateMonthlyReportsUseCase createMonthlyReportsUseCase;
  private final LoadReportsPort loadReportsPort;

  // Taking shortcuts consciously...
  @GetMapping
  public List<MonthlyReport> getMonthlyReports() {
    return loadReportsPort.loadAllMonthlyReports();
  }

  @GetMapping(produces = "text/csv")
  public ResponseEntity<String> downloadMonthlyReports() throws IOException {
    String csvReport = downloadMonthlyReportsCsvUseCase.downloadCsvReport();
    return ResponseEntity.ok(csvReport);
  }

  @PostMapping
  public ResponseEntity<String> createMonthlyReports() {
    createMonthlyReportsUseCase.createReports();
    return ResponseEntity.ok("Report creation successful");
  }
}
