package de.holhar.accounting.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

public class AnnualReport extends MonthlyReport {

    public AnnualReport(String friendlyName, int year, BigDecimal profit, BigDecimal expenditure) {
        super(friendlyName, LocalDate.of(year, Month.JANUARY, 1));
        this.income = profit;
        this.expenditure = expenditure;
    }

    public void addProfitAndExpenses(BigDecimal profit, BigDecimal expenditure) {
        this.income = this.income.add(profit);
        this.expenditure = this.expenditure.add(expenditure);
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
