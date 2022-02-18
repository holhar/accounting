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
    private final EntryType type;

    public CreditCardEntry(boolean billedAndNotIncluded, LocalDate valueDate, LocalDate receiptDate,
                           String description, BigDecimal amount, BigDecimal originalAmount, EntryType type) {
        this.billedAndNotIncluded = billedAndNotIncluded;
        this.valueDate = valueDate;
        this.receiptDate = receiptDate;
        this.description = description;
        this.amount = amount;
        this.originalAmount = originalAmount;
        this.type = type;
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

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    @Override
    public EntryType getType() {
        return type;
    }
}
