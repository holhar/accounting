package de.holhar.accounting.report.domain;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.javamoney.moneta.Money;

@Embeddable
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class CostCentre implements Comparable<CostCentre> {

  @Enumerated(EnumType.STRING)
  private EntryType entryType;
  private Money amount = Money.of(0, "EUR");

  public CostCentre(EntryType entryType) {
    this.entryType = entryType;
  }

  public void addAmount(Money newAmount) {
    this.amount = this.amount.add(newAmount);
  }

  @Override
  public int compareTo(CostCentre c) {
    return this.entryType.toString().compareTo(c.entryType.toString());
  }
}
