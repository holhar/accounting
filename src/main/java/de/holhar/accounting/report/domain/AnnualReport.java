package de.holhar.accounting.report.domain;

import de.holhar.accounting.common.MoneyUtils;
import java.time.LocalDate;
import java.time.Month;
import org.javamoney.moneta.Money;

public class AnnualReport extends MonthlyReport {

  public AnnualReport(String friendlyName, int year) {
    super(friendlyName, LocalDate.of(year, Month.JANUARY, 1));
    this.income = MoneyUtils.ZERO;
    this.expenditure = MoneyUtils.ZERO;
    this.investment = MoneyUtils.ZERO;
  }

  public void addProfitAndExpenses(Money profit, Money expenditure, Money investment) {
    this.income = this.income.add(profit);
    this.expenditure = this.expenditure.add(expenditure);
    this.investment = this.investment.add(investment);
    calculateWinAndSavingRate();
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
