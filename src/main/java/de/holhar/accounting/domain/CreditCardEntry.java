package de.holhar.accounting.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreditCardEntry implements Entry {

    /*
     * Revenue billed and not included in the balance
     */
    private final boolean billedAndNotIncluded;
    private final LocalDate valueDate;
    private final LocalDate receiptDate;
    private final String description;
    private final BigDecimal amount;
    private final BigDecimal originalAmount;

    public CreditCardEntry(boolean billedAndNotIncluded, LocalDate valueDate, LocalDate receiptDate,
                           String description, BigDecimal amount, BigDecimal originalAmount) {
        this.billedAndNotIncluded = billedAndNotIncluded;
        this.valueDate = valueDate;
        this.receiptDate = receiptDate;
        this.description = description;
        this.amount = amount;
        this.originalAmount = originalAmount;
    }

    public boolean isBilledAndNotIncluded() {
        return billedAndNotIncluded;
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    public LocalDate getReceiptDate() {
        return receiptDate;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }
}
