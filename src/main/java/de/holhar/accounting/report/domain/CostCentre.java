package de.holhar.accounting.report.domain;

import de.holhar.accounting.common.MoneyUtils;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.javamoney.moneta.Money;

@Embeddable
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class CostCentre implements Comparable<CostCentre> {

  @Enumerated(EnumType.STRING)
  private EntryType entryType;

  @Columns(columns = {@Column(name = "amount_currency"), @Column(name = "amount_in_minor_unit")})
  @Type(type = "org.jadira.usertype.moneyandcurrency.moneta.PersistentMoneyMinorAmountAndCurrency")
  private Money amount = MoneyUtils.ZERO;

  public CostCentre(EntryType entryType) {
    this.entryType = entryType;
  }

  public void addAmount(Money amount) {
    if (amount.isNegative()) {
      amount = amount.multiply(-1);
    }
    this.setAmount(this.amount.add(amount));
  }

  @Override
  public int compareTo(CostCentre c) {
    return this.entryType.toString().compareTo(c.entryType.toString());
  }
}
