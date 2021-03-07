package de.holhar.accounting.config;

import de.holhar.accounting.service.AccountingService;
import de.holhar.accounting.service.deserialization.CreditCardStatementDeserializer;
import de.holhar.accounting.service.deserialization.Deserializer;
import de.holhar.accounting.service.deserialization.AccountStatementDeserializer;
import de.holhar.accounting.service.deserialization.DeserializerStrategy;
import de.holhar.accounting.service.sanitation.FileSanitationService;
import de.holhar.accounting.service.sanitation.SanitationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class AccountingConfig {

    @Bean
    public AccountingService accountingService(SanitationService fileSanitationService,
                                               Deserializer deserializer) throws IOException {
        return new AccountingService(fileSanitationService, deserializer);
    }

    @Bean
    public SanitationService fileSanitationService() {
        return new FileSanitationService();
    }

    @Bean
    public Deserializer accountStatementDeserializer() {
        return new AccountStatementDeserializer();
    }

    @Bean
    public Deserializer creditCardStatementDeserializer() {
        return new CreditCardStatementDeserializer();
    }

    @Bean
    public Deserializer deserializer(Deserializer accountStatementDeserializer, Deserializer creditCardStatementDeserializer) {
        return new DeserializerStrategy(accountStatementDeserializer, creditCardStatementDeserializer);
    }
}
