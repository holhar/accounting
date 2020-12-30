package de.holhar.accounting.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Balance {

    private final BigDecimal value;
    private final LocalDate date;

    public Balance(BigDecimal value, LocalDate date) {
        this.value = value;
        this.date = date;
    }

    public BigDecimal getValue() {
        return value;
    }

    public LocalDate getDate() {
        return date;
    }
}
