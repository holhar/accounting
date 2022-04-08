package de.holhar.accounting.service.deserialization;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public abstract class AbstractStatementDeserializer {

  protected static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

  protected LocalDate getDate(String dateLine, String s) {
    List<String> fromLineList = Arrays.asList(dateLine.split(";"));
    String fromStringValue = Optional.ofNullable(fromLineList.get(1))
        .orElseThrow(() -> new IllegalArgumentException(s));
    return LocalDate.parse(fromStringValue, formatter);
  }
}
