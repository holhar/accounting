package de.holhar.accounting.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AccountStatement {

    private final String id;
    private final Type type;
    private final LocalDate from;
    private final LocalDate to;
    private final Balance balance;
    private final List<Entry> entries;

    public enum Type {
        CHECKING_ACCOUNT, CREDIT_CARD
    }

    public static class Entry {

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

        public Entry(LocalDate bookingDate, LocalDate valueDate, String bookingText, String client, String intendedUse,
                     String accountId, String bankCode, BigDecimal amount, String creditorId, String clientReference,
                     String customerReference) {
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
    }


    public AccountStatement(String id, Type type, LocalDate from, LocalDate to, Balance balance, List<Entry> entries) {
        this.id = id;
        this.type = type;
        this.from = from;
        this.to = to;
        this.balance = balance;
        this.entries = entries;
    }

    public String getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }

    public Balance getBalance() {
        return balance;
    }

    public List<Entry> getEntries() {
        return new ArrayList<>(entries);
    }
}
