package de.holhar.accounting.report.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.javamoney.moneta.Money;

@Entity
@Data
@NoArgsConstructor
public class CreditCardEntry implements Entry {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonIgnore
  protected Long id;

  /*
   * Revenue billed and not included in the balance
   */
  private boolean billedAndNotIncluded;

  @Column
  private LocalDate valueDate;

  @Column
  private LocalDate receiptDate;
  private String description;

  @Columns(columns = {@Column(name = "amount_currency"), @Column(name = "amount_in_minor_unit")})
  @Type(type = "org.jadira.usertype.moneyandcurrency.moneta.PersistentMoneyMinorAmountAndCurrency")
  private Money amount;

  @Enumerated(EnumType.STRING)
  private EntryType type;

  public CreditCardEntry(boolean billedAndNotIncluded, LocalDate valueDate,
      LocalDate receiptDate, String description, Money amount, EntryType type) {
    this.billedAndNotIncluded = billedAndNotIncluded;
    this.valueDate = valueDate;
    this.receiptDate = receiptDate;
    this.description = description;
    this.amount = amount;
    this.type = type;
  }
}
