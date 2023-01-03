package de.holhar.accounting.report.application.service.report;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.holhar.accounting.TestUtils;
import de.holhar.accounting.common.MoneyUtils;
import de.holhar.accounting.report.domain.CheckingAccountEntry;
import de.holhar.accounting.report.domain.CreditCardEntry;
import de.holhar.accounting.report.domain.EntryType;
import de.holhar.accounting.report.domain.MonthlyReport;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountReportManagerTest {

  @InjectMocks
  private AccountReportManager manager;

  @Test
  void createMonthlyReport() {
    // Given
    Money expectedExpenditure = MoneyUtils.ofMinor(95436L);
    Money expectedIncome = MoneyUtils.ofMinor(260015L);
    Money expectedInvestment = MoneyUtils.ofMinor(30101L);
    List<CheckingAccountEntry> checkingAccountEntries = getCheckingAccountEntries();
    List<CreditCardEntry> creditCardEntries = getCreditCardEntries();

    // When
    MonthlyReport monthlyReport = manager.createMonthlyReport(
        LocalDate.of(2021, Month.NOVEMBER, 1),
        checkingAccountEntries,
        creditCardEntries
    );

    // Then
    assertEquals("2021_11_REPORT", monthlyReport.getFriendlyName());
    assertEquals(Month.NOVEMBER, monthlyReport.getMonth());
    assertEquals(2021, monthlyReport.getYear());
    assertEquals(expectedExpenditure, monthlyReport.getExpenditure());
    assertEquals(expectedIncome, monthlyReport.getIncome());
    assertEquals(expectedInvestment, monthlyReport.getInvestment());
  }

  private List<CreditCardEntry> getCreditCardEntries() {
    List<CreditCardEntry> creditCardEntries = new ArrayList<>();
    creditCardEntries.add(TestUtils.getCreditCardEntry(20000L, EntryType.INCOME));
    creditCardEntries.add(TestUtils.getCreditCardEntry(-1000L, EntryType.LEISURE_ACTIVITIES_AND_PURCHASES));
    creditCardEntries.add(TestUtils.getCreditCardEntry(-2596L, EntryType.LEISURE_ACTIVITIES_AND_PURCHASES));
    creditCardEntries.add(TestUtils.getCreditCardEntry(-999L, EntryType.LEISURE_ACTIVITIES_AND_PURCHASES));
    return creditCardEntries;
  }

  private List<CheckingAccountEntry> getCheckingAccountEntries() {
    List<CheckingAccountEntry> entries = new ArrayList<>();
    entries.add(TestUtils.getCheckAccEntry(240015L, "employer", EntryType.INCOME));
    entries.add(TestUtils.getCheckAccEntry(-50045L, "landlord", EntryType.ACCOMMODATION_AND_COMMUNICATION));
    entries.add(TestUtils.getCheckAccEntry(-20796L, "groceries", EntryType.FOOD_AND_DRUGSTORE));
    entries.add(TestUtils.getCheckAccEntry(-20000L, "a friend", EntryType.MISCELLANEOUS));
    entries.add(TestUtils.getCheckAccEntry(-15445L, "depot transfer 1", EntryType.DEPOT_TRANSFER));
    entries.add(TestUtils.getCheckAccEntry(-14656L, "depot transfer 2", EntryType.DEPOT_TRANSFER));
    return entries;
  }
}
