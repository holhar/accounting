package de.holhar.accounting.service.report;

import de.holhar.accounting.domain.CheckingAccountEntry;
import de.holhar.accounting.domain.CreditCardEntry;
import de.holhar.accounting.domain.Entry;
import de.holhar.accounting.domain.EntryType;
import de.holhar.accounting.domain.MonthlyReport;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AccountReportManager implements ReportManager {

  private final ReportCalculator reportCalculator;

  public AccountReportManager(ReportCalculator reportCalculator) {
    this.reportCalculator = reportCalculator;
  }

  public MonthlyReport createMonthlyReport(final LocalDate statementDate,
      List<CheckingAccountEntry> checkingAccountEntries, List<CreditCardEntry> creditCardEntries) {
    // TODO: Validate input params
    MonthlyReport monthlyReport = new MonthlyReport(getFriendlyName(statementDate), statementDate);

    calculateCosts(monthlyReport, checkingAccountEntries, creditCardEntries);
    calculateProfit(monthlyReport, checkingAccountEntries, creditCardEntries);
    calculateInvestments(monthlyReport, checkingAccountEntries);
    monthlyReport.setExpenditure(reportCalculator.getExpenditure(monthlyReport));
    monthlyReport.calcWinAndSavingRate();

    return monthlyReport;
  }

  private String getFriendlyName(LocalDate statementDate) {
    String month = "" + statementDate.getMonthValue();
    month = month.length() < 2 ? "0" + month : month;
    return statementDate.getYear() + "_" + month + "_REPORT";
  }

  private void calculateCosts(MonthlyReport monthlyReport,
      List<CheckingAccountEntry> checkingAccountEntries, List<CreditCardEntry> creditCardEntries) {
    checkingAccountEntries.stream()
        .filter(Entry::isExpenditure)
        .forEach(entry -> reportCalculator.addToCostCentres(monthlyReport, entry));

    creditCardEntries.stream()
        .filter(Entry::isExpenditure)
        .forEach(entry -> reportCalculator.addToCostCentres(monthlyReport, entry));
  }

  private void calculateProfit(MonthlyReport monthlyReport,
      List<CheckingAccountEntry> checkingAccountEntries, List<CreditCardEntry> creditCardEntries) {
    checkingAccountEntries.stream()
        .filter(entry -> entry.getType().equals(EntryType.INCOME))
        .forEach(
            entry -> monthlyReport.setIncome(reportCalculator.getProfit(monthlyReport, entry)));

    creditCardEntries.stream()
        .filter(entry -> entry.getType().equals(EntryType.INCOME))
        .forEach(
            entry -> monthlyReport.setIncome(reportCalculator.getProfit(monthlyReport, entry)));
  }

  private void calculateInvestments(MonthlyReport monthlyReport, List<CheckingAccountEntry> checkingAccountEntries) {
    checkingAccountEntries.stream()
        .filter(entry -> entry.getType().equals(EntryType.DEPOT_TRANSFER))
        .forEach(entry -> monthlyReport.addToInvestment(entry.getAmount()));
  }
}