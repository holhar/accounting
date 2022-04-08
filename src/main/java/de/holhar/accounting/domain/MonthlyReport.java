package de.holhar.accounting.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.util.Set;
import java.util.TreeSet;

@Data
public class MonthlyReport implements Comparable<MonthlyReport> {

    protected final String friendlyName;
    protected final int year;
    private final Month month;

    protected BigDecimal income;
    protected BigDecimal expenditure;
    protected BigDecimal win;
    protected BigDecimal savingRate;
    protected BigDecimal investment;

    protected final Set<CostCentre> costCentres = new TreeSet<>();

    public MonthlyReport(String friendlyName, LocalDate date) {
        this.friendlyName = friendlyName;
        this.month = date.getMonth();
        this.year = date.getYear();
        this.income = new BigDecimal("0");
        this.investment = new BigDecimal("0");
        this.expenditure = new BigDecimal("0");
        this.win = new BigDecimal("0");
        this.savingRate = new BigDecimal("0");
    }

    public void calcWinAndSavingRate() {
        win = income.add(expenditure);
        if (income.compareTo(new BigDecimal("0")) != 0) {
            savingRate = new BigDecimal("100.000000")
                    .divide(income, RoundingMode.DOWN)
                    .multiply(win)
                    .setScale(2, RoundingMode.HALF_UP);
        } else {
            savingRate = new BigDecimal("0");
        }
    }

    // Incorporate fees as well?
    public void addToInvestment(BigDecimal investment) {
        if (investment.compareTo(new BigDecimal("0")) < 0) {
            investment = investment.multiply(new BigDecimal("-1"));
        }
        this.investment = this.investment.add(investment);
    }

    @Override
    public int compareTo(MonthlyReport other) {
        return this.month.getValue() - other.getMonth().getValue();
    }
}
