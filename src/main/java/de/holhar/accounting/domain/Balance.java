package de.holhar.accounting.domain;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

@Value
@AllArgsConstructor
public class Balance {

    BigDecimal value;
    LocalDate date;
}
