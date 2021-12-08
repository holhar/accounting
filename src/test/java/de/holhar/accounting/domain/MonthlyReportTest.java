package de.holhar.accounting.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}