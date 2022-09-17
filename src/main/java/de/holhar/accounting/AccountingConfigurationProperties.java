package de.holhar.accounting;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "accounting")
public class AccountingConfigurationProperties {

  private String importPath;
  private String deSanitizationPath;

  private String checkingAccountIdentifier;
  private String creditCardIdentifier;
  private List<String> intendedUseIdentifiers;

  private String reportStartDate;
}
