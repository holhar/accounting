package de.holhar.accounting.service.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.holhar.accounting.TestUtils;
import de.holhar.accounting.report.application.service.report.ReportCalculator;
import de.holhar.accounting.report.domain.CheckingAccountEntry;
import de.holhar.accounting.report.domain.CostCentre;
import de.holhar.accounting.report.domain.Entry;
import de.holhar.accounting.report.domain.EntryType;
import de.holhar.accounting.report.domain.MonthlyReport;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportCalculatorTest {

  private ReportCalculator reportCalculator;

  @BeforeEach
  public void setup() {
    reportCalculator = new ReportCalculator();
  }

  @Test
  void getExpenditure_monthlyReportWithCostCentresGiven_shouldSumUpExpenditures() {
    MonthlyReport monthlyReport = new MonthlyReport(
        "2021_11_CHECKING_ACCOUNT_STATEMENT",
        LocalDate.of(2021, Month.NOVEMBER, 1)
    );
    monthlyReport.setIncome(new BigDecimal("4321.23"));
    monthlyReport.setExpenditure(new BigDecimal("-1834.34"));

    CostCentre costCentre1 = new CostCentre(EntryType.FOOD_AND_DRUGSTORE);
    costCentre1.addAmount(new BigDecimal("-100.82"));
    monthlyReport.getCostCentres().add(costCentre1);
    CostCentre costCentre2 = new CostCentre(EntryType.ACCOMMODATION_AND_COMMUNICATION);
    costCentre2.addAmount(new BigDecimal("-1084.21"));
    monthlyReport.getCostCentres().add(costCentre2);

    BigDecimal actual = reportCalculator.getExpenditure(monthlyReport);

    assertEquals(new BigDecimal("-1185.03"), actual);
  }

  @Test
  void getProfit_positiveEntries_shoulAddPositiveEntries() {
    List<Entry> entries = Arrays.asList(
        TestUtils.getCheckingAccountEntryAmountOnly("100.45"),
        TestUtils.getCheckingAccountEntryAmountOnly("23.98"),
        TestUtils.getCheckingAccountEntryAmountOnly("2359.54")
    );

    MonthlyReport monthlyReport = new MonthlyReport(
        "2021_11_CHECKING_ACCOUNT_STATEMENT",
        LocalDate.of(2021, Month.NOVEMBER, 1)
    );

    monthlyReport.setIncome(reportCalculator.getProfit(monthlyReport, entries.get(0)));
    monthlyReport.setIncome(reportCalculator.getProfit(monthlyReport, entries.get(1)));
    monthlyReport.setIncome(reportCalculator.getProfit(monthlyReport, entries.get(2)));

    assertEquals(new BigDecimal("2483.97"), monthlyReport.getIncome());
  }

  @Test
  void getProfit_negativeEntry_shouldThrowException() {
    CheckingAccountEntry entry = TestUtils.getCheckingAccountEntryAmountOnly("-100.45");

    MonthlyReport monthlyReport = new MonthlyReport(
        "2021_11_CHECKING_ACCOUNT_STATEMENT",
        LocalDate.of(2021, Month.NOVEMBER, 1)
    );

    IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
        () -> reportCalculator.getProfit(monthlyReport, entry));

    assertEquals("Given entry amount must be above zero (a profit), but was -100.45",
        e.getMessage());
  }

  @Test
  void addToCostCentres() {
    MonthlyReport monthlyReport = new MonthlyReport(
        "2021_11_CHECKING_ACCOUNT_STATEMENT",
        LocalDate.of(2021, Month.NOVEMBER, 1)
    );

    Entry entry1 = TestUtils.getCheckingAccountEntryAmountAndClientOnly("-69.99",
        "XX sportsEquipment YY", EntryType.LEISURE_ACTIVITIES_AND_PURCHASES);
    Entry entry2 = TestUtils.getCheckingAccountEntryAmountAndClientOnly("-112.83",
        "XX sportsEquipment ZZ", EntryType.LEISURE_ACTIVITIES_AND_PURCHASES);

    reportCalculator.addToCostCentres(monthlyReport, entry1);
    reportCalculator.addToCostCentres(monthlyReport, entry2);

    assertEquals(1, monthlyReport.getCostCentres().size());
    BigDecimal amount = monthlyReport.getCostCentres().stream()
        .filter(c -> c.getEntryType().equals(EntryType.LEISURE_ACTIVITIES_AND_PURCHASES))
        .map(CostCentre::getAmount)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(
            "Should contain LEISURE_ACTIVITIES_AND_PURCHASES cost centre"));
    assertEquals(0, new BigDecimal("-182.82").compareTo(amount));
  }
}
