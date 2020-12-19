package de.holhar.accounting.domain;

import java.time.LocalDate;

public class Balance {

    private final float value;
    private final LocalDate date;

    public Balance(float value, LocalDate date) {
        this.value = value;
        this.date = date;
    }

    public float getValue() {
        return value;
    }

    public LocalDate getDate() {
        return date;
    }
}
