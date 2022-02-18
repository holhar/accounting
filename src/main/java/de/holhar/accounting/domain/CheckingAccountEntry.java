package de.holhar.accounting.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CheckingAccountEntry implements Entry {

    private final LocalDate bookingDate;
    private final LocalDate valueDate;
    private final String bookingText;
    private final String client;
    private final String intendedUse;
    private final String accountId;
    private final String bankCode;
    private final BigDecimal amount;
    private final String creditorId;
    private final String clientReference;
    private final String customerReference;
    private final EntryType type;

    public CheckingAccountEntry(LocalDate bookingDate, LocalDate valueDate, String bookingText, String client, String intendedUse,
                                String accountId, String bankCode, BigDecimal amount, String creditorId, String clientReference,
                                String customerReference, EntryType type) {
        this.bookingDate = bookingDate;
        this.valueDate = valueDate;
        this.bookingText = bookingText;
        this.client = client;
        this.intendedUse = intendedUse;
        this.accountId = accountId;
        this.bankCode = bankCode;
        this.amount = amount;
        this.creditorId = creditorId;
        this.clientReference = clientReference;
        this.customerReference = customerReference;
        this.type = type;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    public String getBookingText() {
        return bookingText;
    }

    public String getClient() {
        return client;
    }

    public String getIntendedUse() {
        return intendedUse;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getBankCode() {
        return bankCode;
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    public String getCreditorId() {
        return creditorId;
    }

    public String getClientReference() {
        return clientReference;
    }

    public String getCustomerReference() {
        return customerReference;
    }

    @Override
    public EntryType getType() {
        return type;
    }
}
