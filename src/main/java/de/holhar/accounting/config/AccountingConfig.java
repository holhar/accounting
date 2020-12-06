package de.holhar.accounting.config;

import de.holhar.accounting.service.AccountingService;
import de.holhar.accounting.service.sanitation.FileSanitationService;
import de.holhar.accounting.service.sanitation.SanitationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class AccountingConfig {

    @Bean
    public AccountingService accountingService() throws IOException {
        return new AccountingService();
    }

    @Bean
    public SanitationService fileSanitationService() {
        return new FileSanitationService();
    }
}
