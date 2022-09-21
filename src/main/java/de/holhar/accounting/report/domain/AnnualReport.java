package de.holhar.accounting.report.domain;

import java.time.LocalDate;
import java.time.Month;
import org.javamoney.moneta.Money;

public class AnnualReport extends MonthlyReport {

  public AnnualReport(String friendlyName, int year) {
    super(friendlyName, LocalDate.of(year, Month.JANUARY, 1));
    this.income = Money.of(0, "EUR");
    this.expenditure = Money.of(0, "EUR");
    this.investment = Money.of(0, "EUR");
  }

  public void addProfitAndExpenses(Money profit, Money expenditure, Money investment) {
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
