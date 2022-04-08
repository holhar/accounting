package de.holhar.accounting.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class CreditCardEntry implements Entry {

  /*
   * Revenue billed and not included in the balance
   */
  boolean billedAndNotIncluded;
  LocalDate valueDate;
  LocalDate receiptDate;
  String description;
  BigDecimal amount;
  BigDecimal originalAmount;
  EntryType type;
}
