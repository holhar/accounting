package de.holhar.accounting.service.deserialization;

import de.holhar.accounting.domain.AccountStatement;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DeserializerStrategy implements Deserializer {

    private final Deserializer accountStatementDeserializer;
    private final Deserializer creditCardStatementDeserializer;

    @Autowired
    public DeserializerStrategy(Deserializer accountStatementDeserializer, Deserializer creditCardStatementDeserializer) {
        this.accountStatementDeserializer = accountStatementDeserializer;
        this.creditCardStatementDeserializer = creditCardStatementDeserializer;
    }

    @Override
    public AccountStatement readStatement(List<String> lines) {
        if (lines.size() > 1) {
            throw new IllegalArgumentException("Invalid lines given, size must be greater than zero");
        }
        if (lines.get(0).startsWith("Kontonummer")) {
            return accountStatementDeserializer.readStatement(lines);
        } else if (lines.get(0).startsWith("Kreditkarte")) {
            return creditCardStatementDeserializer.readStatement(lines);
        } else {
            throw new IllegalArgumentException("Can not deserialize given lines");
        }
    }
}
