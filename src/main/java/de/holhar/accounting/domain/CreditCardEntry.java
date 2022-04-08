package de.holhar.accounting.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

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
