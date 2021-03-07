package de.holhar.accounting.domain;

import java.math.BigDecimal;

public class MonthlyReport {

    private final String friendlyName;
    private final BigDecimal profit;
    private final BigDecimal expenditure;

    public MonthlyReport(String friendlyName, BigDecimal profit, BigDecimal expenditure) {
        this.friendlyName = friendlyName;
        this.profit = profit;
        this.expenditure = expenditure;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public BigDecimal getExpenditure() {
        return expenditure;
    }

    public BigDecimal getWin() {
        return profit.add(expenditure);
    }

    @Override
    public String toString() {
        return "MonthlyReport{" +
                "profit=" + profit +
                ", expenditure=" + expenditure +
                ", win=" + profit.add(expenditure) +
                '}';
    }
}
