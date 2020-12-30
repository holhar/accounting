package de.holhar.accounting.config;

import de.holhar.accounting.service.AccountingService;
import de.holhar.accounting.service.deserialization.Deserializer;
import de.holhar.accounting.service.deserialization.AccountStatementDeserializer;
import de.holhar.accounting.service.sanitation.FileSanitationService;
import de.holhar.accounting.service.sanitation.SanitationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class AccountingConfig {

    @Bean
    public AccountingService accountingService(SanitationService fileSanitationService,
                                               Deserializer accountStatementDeserializer) throws IOException {
        return new AccountingService(fileSanitationService, accountStatementDeserializer);
    }

    @Bean
    public SanitationService fileSanitationService() {
        return new FileSanitationService();
    }

    @Bean
    public Deserializer accountStatementDeserializer() {
        return new AccountStatementDeserializer();
    }
}
