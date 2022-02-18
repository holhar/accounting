package de.holhar.accounting.service.deserialization;

import de.holhar.accounting.config.AppProperties;
import de.holhar.accounting.domain.AccountStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component(value = "deserializer")
public class DeserializerStrategy implements Deserializer {

    private final String checkingAccountIdentifier;
    private final String creditCardIdentifier;
    private final AccountStatementDeserializer accountStatementDeserializer;
    private final CreditCardStatementDeserializer creditCardStatementDeserializer;

    @Autowired
    public DeserializerStrategy(AppProperties properties,
                                AccountStatementDeserializer accountStatementDeserializer,
                                CreditCardStatementDeserializer creditCardStatementDeserializer) {
        this.checkingAccountIdentifier = properties.getCheckingAccountIdentifier();
        this.creditCardIdentifier = properties.getCreditCardIdentifier();
        this.accountStatementDeserializer = accountStatementDeserializer;
        this.creditCardStatementDeserializer = creditCardStatementDeserializer;
    }

    @Override
    public AccountStatement readStatement(List<String> lines) {
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("Invalid lines given, size must be greater than zero");
        }
        if (lines.get(0).startsWith(checkingAccountIdentifier)) {
            return accountStatementDeserializer.readStatement(lines);
        } else if (lines.get(0).startsWith(creditCardIdentifier)) {
            return creditCardStatementDeserializer.readStatement(lines);
        } else {
            throw new IllegalArgumentException("Can not deserialize given lines");
        }
    }
}
