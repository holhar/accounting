package de.holhar.accounting.service.deserialization;

import de.holhar.accounting.config.AppProperties;
import de.holhar.accounting.domain.Entry;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value = "deserializer")
public class DeserializerStrategy implements Deserializer {

  private final String checkingAccountIdentifier;
  private final String creditCardIdentifier;
  private final CheckingAccountEntryDeserializer checkingAccountEntryDeserializer;
  private final CreditCardEntryDeserializer creditCardEntryDeserializer;

  @Autowired
  public DeserializerStrategy(AppProperties properties,
      CheckingAccountEntryDeserializer checkingAccountEntryDeserializer,
      CreditCardEntryDeserializer creditCardEntryDeserializer) {
    this.checkingAccountIdentifier = properties.getCheckingAccountIdentifier();
    this.creditCardIdentifier = properties.getCreditCardIdentifier();
    this.checkingAccountEntryDeserializer = checkingAccountEntryDeserializer;
    this.creditCardEntryDeserializer = creditCardEntryDeserializer;
  }

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
