package de.holhar.accounting.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value = "application.properties")
@ConfigurationProperties(prefix = "app")
public class AppProperties {

  private String importPath;
  private String deSanitizationPath;

  private String checkingAccountIdentifier;
  private String creditCardIdentifier;
  private List<String> intendedUseIdentifiers;

  public String getImportPath() {
    return importPath;
  }

  public void setImportPath(String importPath) {
    this.importPath = importPath;
  }

  public String getDeSanitizationPath() {
    return deSanitizationPath;
  }

  public void setDeSanitizationPath(String deSanitizationPath) {
    this.deSanitizationPath = deSanitizationPath;
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
