package de.holhar.accounting.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class Balance {

  BigDecimal value;
  LocalDate date;
}
