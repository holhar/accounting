package de.holhar.accounting.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value = "application.properties")
@ConfigurationProperties(prefix = "app")
public class AppProperties {

  private String csvPath;
  private boolean skipImport;
  private String checkingAccountIdentifier;
  private String creditCardIdentifier;
  private List<String> intendedUseIdentifiers;

  public String getCsvPath() {
    return csvPath;
  }

  public void setCsvPath(String csvPath) {
    this.csvPath = csvPath;
  }

  public boolean isSkipImport() {
    return skipImport;
  }

  public void setSkipImport(boolean skipImport) {
    this.skipImport = skipImport;
  }

  public String getCheckingAccountIdentifier() {
    return checkingAccountIdentifier;
  }

  public void setCheckingAccountIdentifier(String checkingAccountIdentifier) {
    this.checkingAccountIdentifier = checkingAccountIdentifier;
  }

  public String getCreditCardIdentifier() {
    return creditCardIdentifier;
  }

  public void setCreditCardIdentifier(String creditCardIdentifier) {
    this.creditCardIdentifier = creditCardIdentifier;
  }

  public List<String> getIntendedUseIdentifiers() {
    return intendedUseIdentifiers;
  }

  public void setIntendedUseIdentifiers(List<String> intendedUseIdentifiers) {
    this.intendedUseIdentifiers = intendedUseIdentifiers;
  }
}
