package de.holhar.accounting.report.application.service.report;

import de.holhar.accounting.report.application.port.out.LoadStatementsPort;
import de.holhar.accounting.report.domain.CheckingAccountEntry;
import de.holhar.accounting.report.domain.CreditCardEntry;
import de.holhar.accounting.report.domain.Entry;
import de.holhar.accounting.report.domain.EntryType;
import de.holhar.accounting.report.domain.MonthlyReport;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccountReportManager implements ReportManager {

  public static class ReportEntry {

    private final List<CheckingAccountEntry> checkingAccountEntries;
    private final List<CreditCardEntry> creditCardEntries;

    public ReportEntry(
        List<CheckingAccountEntry> checkingAccountEntries,
        List<CreditCardEntry> creditCardEntries) {
      this.checkingAccountEntries = checkingAccountEntries;
      this.creditCardEntries = creditCardEntries;
    }

    public List<CheckingAccountEntry> getCheckingAccountEntries() {
      return checkingAccountEntries;
    }

    public List<CreditCardEntry> getCreditCardEntries() {
      return creditCardEntries;
    }
  }

  private final LoadStatementsPort loadStatementsPort;

  public ReportEntry getReportDataSetEntry(LocalDate monthIterator) {
    LocalDate startDate = monthIterator.withDayOfMonth(1);
    LocalDate endDate = monthIterator.withDayOfMonth(monthIterator.getMonth().length(monthIterator.isLeapYear()));
    List<CheckingAccountEntry> checkingAccountEntries = loadStatementsPort.loadCheckingAccountStatementsByValueDateBetween(startDate, endDate);
    List<CreditCardEntry> creditCardEntries = loadStatementsPort.loadCreditCardStatementsByReceiptDateBetween(startDate, endDate);
    return new ReportEntry(checkingAccountEntries, creditCardEntries);
  }

  public MonthlyReport createMonthlyReport(
      final LocalDate statementDate,
      List<CheckingAccountEntry> checkingAccountEntries,
      List<CreditCardEntry> creditCardEntries
  ) {
    // TODO: Validate input params
    MonthlyReport monthlyReport = new MonthlyReport(getFriendlyName(statementDate), statementDate);

    calculateCosts(monthlyReport, checkingAccountEntries, creditCardEntries);
    calculateProfit(monthlyReport, checkingAccountEntries, creditCardEntries);
    calculateInvestments(monthlyReport, checkingAccountEntries);
    monthlyReport.calculateExpenditure();
    monthlyReport.calculateWinAndSavingRate();

    return monthlyReport;
  }

  private String getFriendlyName(LocalDate statementDate) {
    String month = "" + statementDate.getMonthValue();
    month = month.length() < 2 ? "0" + month : month;
    return statementDate.getYear() + "_" + month + "_REPORT";
  }

  private void calculateCosts(
      MonthlyReport monthlyReport,
      List<CheckingAccountEntry> checkingAccountEntries,
      List<CreditCardEntry> creditCardEntries
  ) {
    checkingAccountEntries.stream()
        .filter(Entry::isExpenditure)
        .forEach(monthlyReport::addToCostCentres);

    creditCardEntries.stream()
        .filter(Entry::isExpenditure)
        .forEach(monthlyReport::addToCostCentres);
  }

  private void calculateProfit(
      MonthlyReport monthlyReport,
      List<CheckingAccountEntry> checkingAccountEntries,
      List<CreditCardEntry> creditCardEntries
  ) {
    checkingAccountEntries.stream()
        .filter(entry -> entry.getType().equals(EntryType.INCOME))
        .forEach(monthlyReport::addToIncome);

    creditCardEntries.stream()
        .filter(entry -> entry.getType().equals(EntryType.INCOME))
        .forEach(monthlyReport::addToIncome);
  }

  private void calculateInvestments(
      MonthlyReport monthlyReport,
      List<CheckingAccountEntry> checkingAccountEntries
  ) {
    checkingAccountEntries.stream()
        .filter(entry -> entry.getType().equals(EntryType.DEPOT_TRANSFER))
        .forEach(entry -> monthlyReport.addToInvestment(entry.getAmount()));
  }
}
