package de.holhar.accounting.report.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.util.Set;
import java.util.TreeSet;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.javamoney.moneta.Money;

@NoArgsConstructor
@Entity
@Data
public class MonthlyReport implements Comparable<MonthlyReport> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonIgnore
  protected Long id;

  @ElementCollection
  protected final Set<CostCentre> costCentres = new TreeSet<>();

  protected String friendlyName;
  protected int year;
  private Month month;

  @Columns(columns = {@Column(name = "income_currency"), @Column(name = "income_in_minor_unit")})
  @Type(type = "org.jadira.usertype.moneyandcurrency.moneta.PersistentMoneyMinorAmountAndCurrency")
  protected Money income;

  @Columns(columns = {@Column(name = "expenditure_currency"), @Column(name = "expenditure_in_minor_unit")})
  @Type(type = "org.jadira.usertype.moneyandcurrency.moneta.PersistentMoneyMinorAmountAndCurrency")
  protected Money expenditure;

  @Columns(columns = {@Column(name = "win_currency"), @Column(name = "win_in_minor_unit")})
  @Type(type = "org.jadira.usertype.moneyandcurrency.moneta.PersistentMoneyMinorAmountAndCurrency")
  protected Money win;

  @Columns(columns = {@Column(name = "investment_currency"), @Column(name = "investment_in_minor_unit")})
  @Type(type = "org.jadira.usertype.moneyandcurrency.moneta.PersistentMoneyMinorAmountAndCurrency")
  protected Money investment;

  protected BigDecimal savingRate;

  public MonthlyReport(String friendlyName, LocalDate date) {
    this.friendlyName = friendlyName;
    this.month = date.getMonth();
    this.year = date.getYear();
    this.income = Money.of(0, "EUR");
    this.investment = Money.of(0, "EUR");
    this.expenditure = Money.of(0, "EUR");
    this.win = Money.of(0, "EUR");
    this.savingRate = new BigDecimal("0");
  }

  // FIXME: Saving rate calculation
  public void calculateWinAndSavingRate() {
    win = income.subtract(expenditure);
    if (income.isNegative() || income.isPositive()) {
      savingRate = new BigDecimal("100.000000")
          .divide(income.getNumberStripped(), RoundingMode.DOWN)
          .multiply(win.getNumberStripped())
          .setScale(2, RoundingMode.HALF_UP);
    } else {
      savingRate = BigDecimal.ZERO;
    }
  }

  // Incorporate transfer fees as well?
  public void addToInvestment(Money investment) {
    if (investment.isNegative()) {
      investment = investment.multiply(-1);
    }
    this.investment = this.investment.add(investment);
  }

  public void calculateExpenditure() {
    Money expenditure = this.getCostCentres().stream()
        .map(CostCentre::getAmount)
        .reduce(Money.of(0, "EUR"), Money::add);
    this.setExpenditure(expenditure);
  }

  public void addToIncome(Entry entry) {
    if (entry.getAmount().isNegative()) {
      throw new IllegalArgumentException(
          "Given entry amount must be above zero (a profit), but was " + entry.getAmount());
    }
    this.setIncome(this.getIncome().add(entry.getAmount()));
  }

  public void addToCostCentres(Entry entry) {
    CostCentre costCentre = new CostCentre(entry.getType());
    costCentre.addAmount(entry.getAmount());
    if (this.getCostCentres().contains(costCentre)) {
      this.getCostCentres().stream()
          .filter(c -> c.getEntryType().equals(costCentre.getEntryType()))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException(
              "Could not match given cost centre type " + costCentre.getEntryType()))
          .addAmount(costCentre.getAmount());
    } else {
      this.getCostCentres().add(costCentre);
    }
  }

  @Override
  public int compareTo(MonthlyReport other) {
    return this.month.getValue() - other.getMonth().getValue();
  }
}
