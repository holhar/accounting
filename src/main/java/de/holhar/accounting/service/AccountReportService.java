package de.holhar.accounting.service;

import de.holhar.accounting.config.AppProperties;
import de.holhar.accounting.domain.MonthlyReport;
import de.holhar.accounting.repository.MonthlyReportRepository;
import de.holhar.accounting.service.report.AccountReportManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AccountReportService {

  private static final Logger logger = LoggerFactory.getLogger(AccountReportService.class);

  private final MonthlyReportRepository monthlyReportRepository;

  private final AccountReportManager accountReportManager;

  private final LocalDate reportStartDate;

  public AccountReportService(
      MonthlyReportRepository monthlyReportRepository,
      AccountReportManager accountReportManager,
      AppProperties appProperties
  ) {
    this.monthlyReportRepository = monthlyReportRepository;

    this.accountReportManager = accountReportManager;

    this.reportStartDate = LocalDate.parse(appProperties.getReportStartDate(),
        DateTimeFormatter.ofPattern("yyyy-MM-dd"));
  }

  public void createReports() {
    LocalDate monthIterator = reportStartDate;
    AccountReportManager.ReportEntry reportData = accountReportManager.getReportDataSetEntry(monthIterator);

    while (!reportData.getCheckingAccountEntries().isEmpty()
        && !reportData.getCreditCardEntries().isEmpty()) {

      if (monthlyReportIsPresent(monthIterator)) {
        logger.info("Report is present for date '{}' -> continue", reportStartDate);
        monthIterator = monthIterator.plus(1L, ChronoUnit.MONTHS);
        reportData = accountReportManager.getReportDataSetEntry(monthIterator);
        continue;
      }

      MonthlyReport monthlyReport = accountReportManager.createMonthlyReport(monthIterator,
          reportData.getCheckingAccountEntries(), reportData.getCreditCardEntries());
      monthlyReportRepository.save(monthlyReport);

      monthIterator = monthIterator.plus(1L, ChronoUnit.MONTHS);
      reportData = accountReportManager.getReportDataSetEntry(monthIterator);
    }
  }

  private boolean monthlyReportIsPresent(LocalDate monthIterator) {
    return monthlyReportRepository.findByYearAndMonth(monthIterator.getYear(),
        monthIterator.getMonth()).isPresent();
  }

}
