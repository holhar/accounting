package de.holhar.accounting.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.holhar.accounting.report.domain.MonthlyReport;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.Test;

class MonthlyReportTest {

  @Test
  void winAndSavingRateCalculation() {
    MonthlyReport monthlyReport = new MonthlyReport(
        "2021_11_CHECKING_ACCOUNT_STATEMENT",
        LocalDate.of(2021, Month.NOVEMBER, 1)
    );

    monthlyReport.setIncome(new BigDecimal("4321.23"));
    monthlyReport.setExpenditure(new BigDecimal("-1834.34"));
    monthlyReport.calcWinAndSavingRate();

    assertEquals(new BigDecimal("2486.89"), monthlyReport.getWin());
    assertEquals(new BigDecimal("57.55"), monthlyReport.getSavingRate());
  }

  @Test
  void testAddToInvestment() {
    MonthlyReport monthlyReport = new MonthlyReport(
        "2021_11_CHECKING_ACCOUNT_STATEMENT",
        LocalDate.of(2021, Month.OCTOBER, 1)
    );
    monthlyReport.setInvestment(new BigDecimal("111.11"));

    monthlyReport.addToInvestment(new BigDecimal("68.99"));

    assertEquals(new BigDecimal("180.10"), monthlyReport.getInvestment());
  }

  @Test
  void testWhenNegativeInvestmentIsAddedThenAddPositiveValue() {
    MonthlyReport monthlyReport = new MonthlyReport(
        "2021_11_CHECKING_ACCOUNT_STATEMENT",
        LocalDate.of(2021, Month.OCTOBER, 1)
    );
    monthlyReport.setInvestment(new BigDecimal("111.11"));

    monthlyReport.addToInvestment(new BigDecimal("-20.89"));

    assertEquals(new BigDecimal("132.00"), monthlyReport.getInvestment());
  }
}
