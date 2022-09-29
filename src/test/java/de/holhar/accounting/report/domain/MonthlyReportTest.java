package de.holhar.accounting.report.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.holhar.accounting.TestUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import javax.money.Monetary;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

class MonthlyReportTest {

  @Test
  void winAndSavingRateCalculation_winIsNotNull() {
    MonthlyReport monthlyReport = new MonthlyReport(
        "2021_11_CHECKING_ACCOUNT_STATEMENT",
        LocalDate.of(2021, Month.NOVEMBER, 1)
    );
    monthlyReport.setIncome(Money.of(4321.23, "EUR"));
    monthlyReport.setExpenditure(Money.of(1834.34, "EUR"));

    monthlyReport.calculateWinAndSavingRate();

    assertEquals(Money.of(2486.89, "EUR"), monthlyReport.getWin());
    assertEquals(new BigDecimal("57.55").setScale(2, RoundingMode.HALF_UP), monthlyReport.getSavingRate());
  }

  @Test
  void winAndSavingRateCalculation_winIsNull() {
    var monthlyReport = new MonthlyReport("2021_11_CHECKING_ACCOUNT_STATEMENT",
        LocalDate.of(2021, Month.NOVEMBER, 1)
    );
    monthlyReport.setIncome(Money.of(0, "EUR"));
    monthlyReport.setExpenditure(Money.of(0, "EUR"));

    monthlyReport.calculateWinAndSavingRate();

    assertEquals(BigDecimal.ZERO, monthlyReport.getSavingRate());
  }

  @Test
  void testAddToInvestment() {
    MonthlyReport monthlyReport = new MonthlyReport(
        "2021_11_CHECKING_ACCOUNT_STATEMENT",
        LocalDate.of(2021, Month.OCTOBER, 1)
    );
    monthlyReport.setInvestment(Money.of(111.11, "EUR"));

    monthlyReport.addToInvestment(Money.of(68.99, "EUR"));

    assertEquals(Money.of(180.10, "EUR"), monthlyReport.getInvestment());
  }

  @Test
  void testWhenNegativeInvestmentIsAddedThenAddPositiveValue() {
    MonthlyReport monthlyReport = new MonthlyReport(
        "2021_11_CHECKING_ACCOUNT_STATEMENT",
        LocalDate.of(2021, Month.OCTOBER, 1)
    );
    monthlyReport.setInvestment(Money.of(111.11, "EUR"));

    monthlyReport.addToInvestment(Money.of(-20.89, "EUR"));

    assertEquals(Money.of(132.00, "EUR"), monthlyReport.getInvestment());
  }

  @Test
  void calculateExpenditure_monthlyReportWithCostCentresGiven_shouldSumUpExpenditures() {
    MonthlyReport monthlyReport = new MonthlyReport(
        "2021_11_CHECKING_ACCOUNT_STATEMENT",
        LocalDate.of(2021, Month.NOVEMBER, 1)
    );
    monthlyReport.setIncome(Money.of(4321.23, "EUR"));
    monthlyReport.setExpenditure(Money.of(1834.34, "EUR"));

    CostCentre costCentre1 = new CostCentre(EntryType.FOOD_AND_DRUGSTORE);
    costCentre1.addAmount(Money.of(-100.82, "EUR"));
    monthlyReport.getCostCentres().add(costCentre1);
    CostCentre costCentre2 = new CostCentre(EntryType.ACCOMMODATION_AND_COMMUNICATION);
    costCentre2.addAmount(Money.of(-1084.21, "EUR"));
    monthlyReport.getCostCentres().add(costCentre2);

    monthlyReport.calculateExpenditure();

    assertEquals(Money.of(1185.03, "EUR"), monthlyReport.getExpenditure());
  }

  @Test
  void getProfit_positiveEntries_shouldAddPositiveEntries() {
    List<Entry> entries = Arrays.asList(
        TestUtils.getCheckingAccountEntryAmountOnly(10_045L),
        TestUtils.getCheckingAccountEntryAmountOnly(2_398L),
        TestUtils.getCheckingAccountEntryAmountOnly(235_954)
    );

    MonthlyReport monthlyReport = new MonthlyReport(
        "2021_11_CHECKING_ACCOUNT_STATEMENT",
        LocalDate.of(2021, Month.NOVEMBER, 1)
    );

    monthlyReport.addToIncome(entries.get(0));
    monthlyReport.addToIncome(entries.get(1));
    monthlyReport.addToIncome(entries.get(2));

    assertEquals(Money.of(2483.97, "EUR"), monthlyReport.getIncome());
  }

  @Test
  void getProfit_negativeEntry_shouldThrowException() {
    CheckingAccountEntry entry = TestUtils.getCheckingAccountEntryAmountOnly(-10_045L);

    MonthlyReport monthlyReport = new MonthlyReport(
        "2021_11_CHECKING_ACCOUNT_STATEMENT",
        LocalDate.of(2021, Month.NOVEMBER, 1)
    );

    IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
        () -> monthlyReport.addToIncome(entry));

    assertEquals("Given entry amount must be above zero (a profit), but was EUR -100.45",
        e.getMessage());
  }

  @Test
  void addToCostCentres() {
    MonthlyReport monthlyReport = new MonthlyReport(
        "2021_11_CHECKING_ACCOUNT_STATEMENT",
        LocalDate.of(2021, Month.NOVEMBER, 1)
    );

    Entry entry1 = TestUtils.getCheckingAccountEntryAmountAndClientOnly(-6_999L,
        "XX sportsEquipment YY", EntryType.LEISURE_ACTIVITIES_AND_PURCHASES);
    Entry entry2 = TestUtils.getCheckingAccountEntryAmountAndClientOnly(-11_283L,
        "XX sportsEquipment ZZ", EntryType.LEISURE_ACTIVITIES_AND_PURCHASES);

    monthlyReport.addToCostCentres(entry1);
    monthlyReport.addToCostCentres(entry2);

    assertEquals(1, monthlyReport.getCostCentres().size());
    Money actualAmount = monthlyReport.getCostCentres().stream()
        .filter(c -> c.getEntryType().equals(EntryType.LEISURE_ACTIVITIES_AND_PURCHASES))
        .map(CostCentre::getAmount)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(
            "Should contain LEISURE_ACTIVITIES_AND_PURCHASES cost centre"));
    Money expectedAmount = Money.ofMinor(Monetary.getCurrency("EUR"), 18_282L);
    assertEquals(expectedAmount, actualAmount);
  }
}
