package de.holhar.accounting.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.holhar.accounting.report.domain.MonthlyReport;
import java.time.LocalDate;
import java.time.Month;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

class MonthlyReportTest {

  @Test
  void winAndSavingRateCalculation() {
    MonthlyReport monthlyReport = new MonthlyReport(
        "2021_11_CHECKING_ACCOUNT_STATEMENT",
        LocalDate.of(2021, Month.NOVEMBER, 1)
    );

    monthlyReport.setIncome(Money.of(4321.23, "EUR"));
    monthlyReport.setExpenditure(Money.of(-1834.34, "EUR"));
    monthlyReport.calculateWinAndSavingRate();

    assertEquals(Money.of(2486.89, "EUR"), monthlyReport.getWin());
    assertEquals(Money.of(57.55, "EUR"), monthlyReport.getSavingRate());
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
}
