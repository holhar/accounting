package de.holhar.accounting.report.domain;

import java.math.BigDecimal;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class CostCentre implements Comparable<CostCentre> {

  @Enumerated(EnumType.STRING)
  private EntryType entryType;
  private BigDecimal amount = new BigDecimal("0");

  public CostCentre(EntryType entryType) {
    this.entryType = entryType;
  }

  public void addAmount(BigDecimal newAmount) {
    this.amount = this.amount.add(newAmount);
  }

  @Override
  public int compareTo(CostCentre c) {
    return this.entryType.toString().compareTo(c.entryType.toString());
  }
}
