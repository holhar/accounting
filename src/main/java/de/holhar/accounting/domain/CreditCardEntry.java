package de.holhar.accounting.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
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
  private BigDecimal amount;
  private BigDecimal originalAmount;

  @Enumerated(EnumType.STRING)
  private EntryType type;

  public CreditCardEntry(boolean billedAndNotIncluded, LocalDate valueDate,
      LocalDate receiptDate, String description, BigDecimal amount,
      BigDecimal originalAmount, EntryType type) {
    this.billedAndNotIncluded = billedAndNotIncluded;
    this.valueDate = valueDate;
    this.receiptDate = receiptDate;
    this.description = description;
    this.amount = amount;
    this.originalAmount = originalAmount;
    this.type = type;
  }
}
