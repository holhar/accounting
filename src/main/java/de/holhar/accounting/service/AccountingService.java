package de.holhar.accounting.service;

import de.holhar.accounting.domain.AccountStatement;
import de.holhar.accounting.domain.MonthlyReport;
import de.holhar.accounting.domain.MonthlyReportRepository;
import de.holhar.accounting.service.deserialization.Deserializer;
import de.holhar.accounting.service.report.ReportManager;
import de.holhar.accounting.service.sanitation.SanitationService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// TODO Refactor AccountService with AccountStatementReportManager and ReportCalculator!
@Service
public class AccountingService {

  private final SanitationService sanitationService;
  private final Deserializer deserializer;
  private final ReportManager reportManager;
  private final MonthlyReportRepository monthlyReportRepository;

  @Autowired
  public AccountingService(SanitationService sanitationService, Deserializer deserializer,
      ReportManager reportManager, MonthlyReportRepository monthlyReportRepository) {
    this.sanitationService = sanitationService;
    this.deserializer = deserializer;
    this.reportManager = reportManager;
    this.monthlyReportRepository = monthlyReportRepository;
  }

  public Map<Integer, List<MonthlyReport>> createReport(Path csvPath) throws IOException {
    Map<LocalDate, Set<AccountStatement>> accountingStatementsPerMonthMap;
    try (Stream<Path> pathStream = Files.list(csvPath)) {
      accountingStatementsPerMonthMap = pathStream.parallel()
          .map(sanitationService::cleanUp)
          .map(deserializer::readStatement)
          .collect(Collectors.groupingBy(AccountStatement::getFrom, TreeMap::new, Collectors.toSet()));
    }
    TreeMap<Integer, List<MonthlyReport>> monthlyReportPerYear = accountingStatementsPerMonthMap.entrySet()
        .stream()
        .map(this::getMonthlyReport)
        .collect(Collectors.groupingBy(MonthlyReport::getYear, TreeMap::new, Collectors.toList()));

    for (List<MonthlyReport> reportList : monthlyReportPerYear.values()) {
      Collections.sort(reportList);
    }
    return monthlyReportPerYear;
  }

  private MonthlyReport getMonthlyReport(Entry<LocalDate, Set<AccountStatement>> e) {
    MonthlyReport monthlyReport = reportManager.createMonthlyReport(e.getKey(), e.getValue());

    // persist collected data => TODO refactoring
    monthlyReportRepository.save(monthlyReport);

    return monthlyReport;
  }
}
