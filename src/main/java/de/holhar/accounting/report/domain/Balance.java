package de.holhar.accounting.report.domain;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.javamoney.moneta.Money;

@Value
@AllArgsConstructor
public class Balance {

  Money amount;
  LocalDate date;
}
