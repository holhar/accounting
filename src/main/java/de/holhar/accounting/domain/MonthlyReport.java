package de.holhar.accounting.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.util.Set;
import java.util.TreeSet;

public class MonthlyReport {

    protected final String friendlyName;
    protected final int year;
    private final Month month;

    protected BigDecimal income;
    protected BigDecimal expenditure;
    protected BigDecimal win;
    protected BigDecimal savingRate;

    protected final Set<CostCentre> costCentres = new TreeSet<>();

    public MonthlyReport(String friendlyName, LocalDate date, BigDecimal income, BigDecimal expenditure) {
        this.friendlyName = friendlyName;
        this.month = date.getMonth();
        this.year = date.getYear();
        this.income = income;
        this.expenditure = expenditure;
        calcWin();
        calcSavingRate();
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public int getYear() {
        return year;
    }

    public Month getMonth() {
        return month;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public BigDecimal getExpenditure() {
        return expenditure;
    }

    public BigDecimal getWin() {
        return income.add(expenditure);
    }

    public BigDecimal getSavingRate() {
        return savingRate;
    }

    public void calcWin() {
        win = income.add(expenditure);
    }

    public void calcSavingRate() {
        if (income.compareTo(new BigDecimal("0.00")) != 0) {
            savingRate = new BigDecimal("100.0000").divide(income, RoundingMode.DOWN).multiply(win);
        }
    }

    // FIXME: Violates immutability principle
    public Set<CostCentre> getCostCentres() {
        return costCentres;
    }

    @Override
    public String toString() {
        return "Report{" +
                "friendlyName=" + friendlyName +
                ", year=" + year +
                ", month=" + month +
                ", income=" + income +
                ", expenditure=" + expenditure +
                ", win=" + win +
                ", savingRate=" + savingRate +
                '}';
    }
}
