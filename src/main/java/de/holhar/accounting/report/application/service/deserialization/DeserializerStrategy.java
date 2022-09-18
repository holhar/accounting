package de.holhar.accounting.report.application.service.deserialization;

import de.holhar.accounting.report.domain.Entry;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeserializerStrategy implements Deserializer {

  private final String checkingAccountIdentifier;
  private final String creditCardIdentifier;
  private final CheckingAccountEntryDeserializer checkingAccountEntryDeserializer;
  private final CreditCardEntryDeserializer creditCardEntryDeserializer;

  @Override
  public Stream<Entry> readStatement(List<String> lines) {
    if (lines.isEmpty()) {
      throw new IllegalArgumentException("Invalid lines given, size must be greater than zero");
    }
    if (lines.get(0).startsWith(checkingAccountIdentifier)) {
      return checkingAccountEntryDeserializer.readStatement(lines);
    } else if (lines.get(0).startsWith(creditCardIdentifier)) {
      return creditCardEntryDeserializer.readStatement(lines);
    } else {
      throw new IllegalArgumentException("Can not deserialize given lines");
    }
  }
}
