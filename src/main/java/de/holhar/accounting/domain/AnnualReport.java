package de.holhar.accounting.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

public class AnnualReport extends MonthlyReport {

    public AnnualReport(String friendlyName, int year) {
        super(friendlyName, LocalDate.of(year, Month.JANUARY, 1));
        this.income = new BigDecimal("0");
        this.expenditure = new BigDecimal("0");
        this.investment = new BigDecimal("0");
    }

    public void addProfitAndExpenses(BigDecimal profit, BigDecimal expenditure, BigDecimal investment) {
        this.income = this.income.add(profit);
        this.expenditure = this.expenditure.add(expenditure);
        this.investment = this.investment.add(investment);
        calcWinAndSavingRate();
    }

    @Override
    public String toString() {
        return "Report{" +
                "friendlyName=" + friendlyName +
                ", year=" + year +
                ", income=" + income +
                ", expenditure=" + expenditure +
                ", win=" + win +
                ", savingRate=" + savingRate +
                '}';
    }
}
