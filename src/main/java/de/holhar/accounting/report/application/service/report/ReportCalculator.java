package de.holhar.accounting.report.application.service.report;

import de.holhar.accounting.report.domain.CostCentre;
import de.holhar.accounting.report.domain.Entry;
import de.holhar.accounting.report.domain.MonthlyReport;
import org.javamoney.moneta.Money;

public class ReportCalculator {

  public Money getExpenditure(MonthlyReport monthlyReport) {
    return monthlyReport.getCostCentres().stream()
        .map(CostCentre::getAmount)
        .reduce(Money.of(0, "EUR"), Money::add);
  }

  public Money getProfit(MonthlyReport monthlyReport, Entry entry) {
    if (entry.getAmount().isNegative()) {
      throw new IllegalArgumentException(
          "Given entry amount must be above zero (a profit), but was " + entry.getAmount());
    }
    return monthlyReport.getIncome().add(entry.getAmount());
  }

  public void addToCostCentres(MonthlyReport monthlyReport, Entry entry) {
    CostCentre costCentre = new CostCentre(entry.getType());
    costCentre.addAmount(entry.getAmount());
    if (monthlyReport.getCostCentres().contains(costCentre)) {
      monthlyReport.getCostCentres().stream()
          .filter(c -> c.getEntryType().equals(costCentre.getEntryType()))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException(
              "Could not match given cost centre type " + costCentre.getEntryType()))
          .addAmount(costCentre.getAmount());
    } else {
      monthlyReport.getCostCentres().add(costCentre);
    }
  }
}
