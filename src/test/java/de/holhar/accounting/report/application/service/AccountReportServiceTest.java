package de.holhar.accounting.report.application.service;

import static de.holhar.accounting.TestUtils.getCheckAccEntry;
import static de.holhar.accounting.TestUtils.getCreditCardEntry;
import static de.holhar.accounting.report.domain.EntryType.ACCOMMODATION_AND_COMMUNICATION;
import static de.holhar.accounting.report.domain.EntryType.FOOD_AND_DRUGSTORE;
import static de.holhar.accounting.report.domain.EntryType.HEALTH_AND_FITNESS;
import static de.holhar.accounting.report.domain.EntryType.INCOME;
import static de.holhar.accounting.report.domain.EntryType.LEISURE_ACTIVITIES_AND_PURCHASES;
import static de.holhar.accounting.report.domain.EntryType.MISCELLANEOUS;
import static de.holhar.accounting.report.domain.EntryType.TRANSPORTATION_AND_TRAVELLING;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.holhar.accounting.AccountingConfigProperties;
import de.holhar.accounting.common.MoneyUtils;
import de.holhar.accounting.report.application.port.out.LoadReportsPort;
import de.holhar.accounting.report.application.port.out.SaveReportsPort;
import de.holhar.accounting.report.application.service.report.AccountReportManager;
import de.holhar.accounting.report.application.service.report.AccountReportManager.ReportEntry;
import de.holhar.accounting.report.domain.CheckingAccountEntry;
import de.holhar.accounting.report.domain.CreditCardEntry;
import de.holhar.accounting.report.domain.MonthlyReport;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
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

  @BeforeEach
  public void setup() throws IOException {
    loadReportsPort = Mockito.mock(LoadReportsPort.class);
    saveReportsPort = Mockito.mock(SaveReportsPort.class);
    accountReportManager = Mockito.mock(AccountReportManager.class);

    AccountingConfigProperties accountingConfigProperties =
        Mockito.mock(AccountingConfigProperties.class);
    Files.createDirectories(Paths.get("/tmp/testDownloadFile"));
    when(accountingConfigProperties.getReportStartDate()).thenReturn("2020-01-01");
    when(accountingConfigProperties.getImportPath()).thenReturn("/tmp/testDownloadFile");

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
        getCheckAccEntry(10000L, "client", MISCELLANEOUS)
    );
    List<CreditCardEntry> creditCardEntries = Collections.singletonList(
        getCreditCardEntry(5000L, FOOD_AND_DRUGSTORE)
    );
    return new ReportEntry(checkingAccountEntries, creditCardEntries);
  }

  private ReportEntry getReportEntryWithoutData() {
    List<CheckingAccountEntry> checkingAccountEntries = Collections.emptyList();
    List<CreditCardEntry> creditCardEntries = Collections.emptyList();
    return new ReportEntry(checkingAccountEntries, creditCardEntries);
  }
  
  @Test
  void downloadCsvReport() throws IOException {
    // Given
    String expected = "ID,Year,Month,Income,Expenditure,Win,SavingRate,Invest,\"####\",Accommodation,Food,Health,Transportation,Leisure,Misc\n"
        + "2020_01_REPORTS,2020,JANUARY,1000.00,882.00,118.00,\"11,80\",25.00,----,500.00,12.00,2.00,30.00,333.00,5.00\n"
        + "2020_02_REPORTS,2020,FEBRUARY,1000.00,882.00,118.00,\"11,80\",0.00,----,500.00,12.00,2.00,30.00,333.00,5.00\n"
        + "\n"
        + "2020_ANNUAL_REPORT,2020,----,2000.00,1764.00,236.00,\"11,80\",25.00";

    when(loadReportsPort.loadAllMonthlyReports()).thenReturn(getMonthlyReports());

    // When
    String actual = cut.downloadCsvReport();

    // Then
    assertThat(actual).isEqualTo(expected);
  }
  
  private List<MonthlyReport> getMonthlyReports() {
    var entry1 = getCheckAccEntry(100000L, "employer", INCOME);
    var entry2 = getCheckAccEntry(50000L, "accomodation", ACCOMMODATION_AND_COMMUNICATION);
    var entry3 = getCheckAccEntry(1200L, "food", FOOD_AND_DRUGSTORE);
    var entry4 = getCheckAccEntry(200L, "health", HEALTH_AND_FITNESS);
    var entry5 = getCheckAccEntry(3000L, "transportation", TRANSPORTATION_AND_TRAVELLING);
    var entry6 = getCheckAccEntry(33300L, "leisure", LEISURE_ACTIVITIES_AND_PURCHASES);
    var entry7 = getCheckAccEntry(500L, "misc", MISCELLANEOUS);

    MonthlyReport report1 = new MonthlyReport("2020_01_REPORTS", LocalDate.of(2020, 1, 1));
    report1.addToCostCentres(entry2);
    report1.addToCostCentres(entry3);
    report1.addToCostCentres(entry4);
    report1.addToCostCentres(entry5);
    report1.addToCostCentres(entry6);
    report1.addToCostCentres(entry7);
    report1.addToIncome(entry1);
    report1.addToInvestment(MoneyUtils.ofMinor(2500L));
    report1.calculateExpenditure();
    report1.calculateWinAndSavingRate();

    MonthlyReport report2 = new MonthlyReport("2020_02_REPORTS", LocalDate.of(2020, 2, 1));
    report2.addToCostCentres(entry2);
    report2.addToCostCentres(entry3);
    report2.addToCostCentres(entry4);
    report2.addToCostCentres(entry5);
    report2.addToCostCentres(entry6);
    report2.addToCostCentres(entry7);
    report2.addToIncome(entry1);
    report2.calculateExpenditure();
    report2.calculateWinAndSavingRate();

    return Arrays.asList(report1, report2);
  }
}
