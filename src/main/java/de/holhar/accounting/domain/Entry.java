package de.holhar.accounting.domain;

import java.time.LocalDate;

public class Entry {

    private final LocalDate bookingDate;
    private final LocalDate valueDate;
    private final String bookingText;
    private final String client;
    private final String intendedUse;
    private final String accoundId;
    private final String bankCode;
    private final float amount;
    private final String creditorId;
    private final String clientReference;
    private final String customerReference;

    public Entry(LocalDate bookingDate, LocalDate valueDate, String bookingText, String client, String intendedUse,
                 String accoundId, String bankCode, float amount, String creditorId, String clientReference,
                 String customerReference) {
        this.bookingDate = bookingDate;
        this.valueDate = valueDate;
        this.bookingText = bookingText;
        this.client = client;
        this.intendedUse = intendedUse;
        this.accoundId = accoundId;
        this.bankCode = bankCode;
        this.amount = amount;
        this.creditorId = creditorId;
        this.clientReference = clientReference;
        this.customerReference = customerReference;
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

    public String getAccoundId() {
        return accoundId;
    }

    public String getBankCode() {
        return bankCode;
    }

    public float getAmount() {
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
}
