package de.holhar.accounting.domain;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Value
@AllArgsConstructor
public class AccountStatement {

    String id;
    Type type;
    LocalDate from;
    LocalDate to;
    Balance balance;
    List<Entry> entries;

    public enum Type {
        CHECKING_ACCOUNT, CREDIT_CARD
    }

    public String getFriendlyName() {
        String month = "" + from.getMonthValue();
        month = month.length() < 2 ? "0" + month : month;
        return from.getYear() + "_" + month + "_" + type.name();
    }
}
