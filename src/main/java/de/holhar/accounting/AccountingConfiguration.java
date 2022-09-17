package de.holhar.accounting;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({AccountingConfigurationProperties.class})
public class AccountingConfiguration {

}
