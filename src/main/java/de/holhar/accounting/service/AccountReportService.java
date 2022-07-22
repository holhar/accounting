package de.holhar.accounting.service;

import de.holhar.accounting.config.AppProperties;
import de.holhar.accounting.domain.CheckingAccountEntry;
import de.holhar.accounting.domain.CreditCardEntry;
import de.holhar.accounting.domain.MonthlyReport;
import de.holhar.accounting.repository.CheckingAccountEntryRepository;
import de.holhar.accounting.repository.CreditCardEntryRepository;
import de.holhar.accounting.repository.MonthlyReportRepository;
import de.holhar.accounting.service.report.AccountReportManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AccountReportService {

  private static final Logger logger = LoggerFactory.getLogger(AccountReportService.class);

  private final CreditCardEntryRepository creditCardEntryRepository;
  private final CheckingAccountEntryRepository checkingAccountEntryRepository;
  private final MonthlyReportRepository monthlyReportRepository;

  private final AccountReportManager accountReportManager;

  private final LocalDate reportStartDate;

  public AccountReportService(
      CreditCardEntryRepository creditCardEntryRepository,
      CheckingAccountEntryRepository checkingAccountEntryRepository,
      MonthlyReportRepository monthlyReportRepository,
      AccountReportManager accountReportManager,
      AppProperties appProperties
  ) {
    this.creditCardEntryRepository = creditCardEntryRepository;
    this.checkingAccountEntryRepository = checkingAccountEntryRepository;
    this.monthlyReportRepository = monthlyReportRepository;

    this.accountReportManager = accountReportManager;

    this.reportStartDate = LocalDate.parse(appProperties.getReportStartDate(),
        DateTimeFormatter.ofPattern("yyyy-MM-dd"));
  }

  public void createReports() {
    LocalDate nextMonth = LocalDate.now().plus(1L, ChronoUnit.MONTHS);
    LocalDate monthIterator = reportStartDate;

    while (monthIterator.isBefore(nextMonth)) {
      LocalDate start = monthIterator.withDayOfMonth(1);
      LocalDate end = monthIterator.withDayOfMonth(monthIterator.getMonth().length(monthIterator.isLeapYear()));
      List<CheckingAccountEntry> checkingAccountEntries = checkingAccountEntryRepository.findByBookingDateAfterAndBookingDateBefore(start, end);
      List<CreditCardEntry> creditCardEntries = creditCardEntryRepository.findByValueDateAfterAndValueDateBefore(start, end);

      if (monthlyReportIsPresent(monthIterator) || checkingAccountEntries.isEmpty() || creditCardEntries.isEmpty()) {
        logger.info("There are no entries present for statement span '{}' -> continue", monthIterator);
        continue;
      }

      MonthlyReport monthlyReport = accountReportManager.createMonthlyReport(monthIterator,
          checkingAccountEntries, creditCardEntries);
      monthlyReportRepository.save(monthlyReport);
      monthIterator = monthIterator.plus(1L, ChronoUnit.MONTHS);
    }
  }

  private boolean monthlyReportIsPresent(LocalDate monthIterator) {
    return monthlyReportRepository.findByYearAndMonth(monthIterator.getYear(),
        monthIterator.getMonth()).isPresent();
  }

}
