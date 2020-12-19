package de.holhar.accounting.domain;

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
