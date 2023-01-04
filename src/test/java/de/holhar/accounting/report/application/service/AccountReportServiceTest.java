package de.holhar.accounting.report.application.service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.holhar.accounting.AccountingConfigProperties;
import de.holhar.accounting.TestUtils;
import de.holhar.accounting.report.application.port.out.LoadReportsPort;
import de.holhar.accounting.report.application.port.out.SaveReportsPort;
import de.holhar.accounting.report.application.service.report.AccountReportManager;
import de.holhar.accounting.report.application.service.report.AccountReportManager.ReportEntry;
import de.holhar.accounting.report.domain.CheckingAccountEntry;
import de.holhar.accounting.report.domain.CreditCardEntry;
import de.holhar.accounting.report.domain.EntryType;
import de.holhar.accounting.report.domain.MonthlyReport;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AccountReportServiceTest {

  private AccountReportService cut;

  private LoadReportsPort loadReportsPort;
  private SaveReportsPort saveReportsPort;
  private AccountReportManager accountReportManager;
  private AccountingConfigProperties accountingConfigProperties;

  @BeforeEach
  public void setup() {
    loadReportsPort = Mockito.mock(LoadReportsPort.class);
    saveReportsPort = Mockito.mock(SaveReportsPort.class);
    accountReportManager = Mockito.mock(AccountReportManager.class);
    accountingConfigProperties = Mockito.mock(AccountingConfigProperties.class);

    when(accountingConfigProperties.getReportStartDate()).thenReturn("2020-01-01");
    when(accountingConfigProperties.getImportPath()).thenReturn("/tmp");

    cut = new AccountReportService(
        loadReportsPort, saveReportsPort, accountReportManager, accountingConfigProperties
    );
  }

  @Test
  void createReports_monthlyReportNotPresentYet() {
    // Given
    ReportEntry reportEntryWithData = getReportEntryWithData();
    LocalDate monthIterator = LocalDate.of(2020, 1, 1);
    when(accountReportManager.getReportDataSetEntry(monthIterator))
        .thenReturn(reportEntryWithData);

    // 2nd iteration
    ReportEntry reportEntryWithoutData = getReportEntryWithoutData();
    LocalDate monthIteratorPlusOneMonth = monthIterator.plus(1L, ChronoUnit.MONTHS);
    when(accountReportManager.getReportDataSetEntry(monthIteratorPlusOneMonth))
        .thenReturn(reportEntryWithoutData);

    when(loadReportsPort.loadMonthlyReportByYearAndMonth(2020, Month.JANUARY))
        .thenReturn(Optional.empty());
    MonthlyReport monthlyReport = new MonthlyReport("REPORT_2020_01", monthIterator);
    when(accountReportManager.createMonthlyReport(
        monthIterator,
        reportEntryWithData.getCheckingAccountEntries(),
        reportEntryWithData.getCreditCardEntries())
    ).thenReturn(monthlyReport);

    // When
    cut.createReports();

    // Then
    verify(saveReportsPort).saveMonthlyReport(monthlyReport);
  }

  private ReportEntry getReportEntryWithData() {
    List<CheckingAccountEntry> checkingAccountEntries = Collections.singletonList(
        TestUtils.getCheckAccEntry(10000L, "client", EntryType.MISCELLANEOUS)
    );
    List<CreditCardEntry> creditCardEntries = Collections.singletonList(
        TestUtils.getCreditCardEntry(5000L, EntryType.FOOD_AND_DRUGSTORE)
    );
    return new ReportEntry(checkingAccountEntries, creditCardEntries);
  }

  private ReportEntry getReportEntryWithoutData() {
    List<CheckingAccountEntry> checkingAccountEntries = Collections.emptyList();
    List<CreditCardEntry> creditCardEntries = Collections.emptyList();
    return new ReportEntry(checkingAccountEntries, creditCardEntries);
  }
}
