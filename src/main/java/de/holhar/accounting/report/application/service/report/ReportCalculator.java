package de.holhar.accounting.report.application.service.report;

import de.holhar.accounting.report.domain.CostCentre;
import de.holhar.accounting.report.domain.Entry;
import de.holhar.accounting.report.domain.MonthlyReport;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class ReportCalculator {

  public BigDecimal getExpenditure(MonthlyReport monthlyReport) {
    return monthlyReport.getCostCentres().stream()
        .map(CostCentre::getAmount)
        .reduce(new BigDecimal("0"), BigDecimal::add);
  }

  public BigDecimal getProfit(MonthlyReport monthlyReport, Entry entry) {
    if (entry.getAmount().compareTo(new BigDecimal("0")) < 0) {
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
