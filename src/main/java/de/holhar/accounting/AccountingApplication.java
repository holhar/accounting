package de.holhar.accounting;

import de.holhar.accounting.adapter.CliAdapter;
import de.holhar.accounting.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.Optional;

@SpringBootApplication
@EnableConfigurationProperties({AppProperties.class})
public class AccountingApplication {

    private final Optional<CliAdapter> cliAdapter;

    public AccountingApplication(Optional<CliAdapter> cliAdapter) {
        this.cliAdapter = cliAdapter;
    }

    public static void main(String[] args) {
        SpringApplication.run(AccountingApplication.class, args);
    }

    @PostConstruct
    private void init() {
        cliAdapter.ifPresent(CliAdapter::startApplication);
    }
}
