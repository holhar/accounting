package de.holhar.accounting;

import de.holhar.accounting.report.adapter.in.web.FileHandler;
import de.holhar.accounting.report.application.port.out.LoadStatementsPort;
import de.holhar.accounting.report.application.service.deserialization.CheckingAccountEntryDeserializer;
import de.holhar.accounting.report.application.service.deserialization.CreditCardEntryDeserializer;
import de.holhar.accounting.report.application.service.deserialization.Deserializer;
import de.holhar.accounting.report.application.service.deserialization.DeserializerStrategy;
import de.holhar.accounting.report.application.service.report.AccountReportManager;
import de.holhar.accounting.report.application.service.report.ReportCalculator;
import de.holhar.accounting.report.application.service.sanitation.FileSanitizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({AccountingConfigurationProperties.class})
public class AccountingConfiguration {

  @Bean
  public FileHandler fileHandler() {
    return new FileHandler();
  }

  @Bean
  public FileSanitizer fileSanitizer() {
    return new FileSanitizer();
  }

  @Bean
  public AccountReportManager accountReportManager(
      ReportCalculator reportCalculator,
      LoadStatementsPort loadStatementsPort
  ) {
    return new AccountReportManager(reportCalculator, loadStatementsPort);
  }

  @Bean
  public ReportCalculator reportCalculator() {
    return new ReportCalculator();
  }

  @Bean
  public CheckingAccountEntryDeserializer accountStatementDeserializer() {
    return new CheckingAccountEntryDeserializer();
  }

  @Bean
  public CreditCardEntryDeserializer creditCardStatementDeserializer() {
    return new CreditCardEntryDeserializer();
  }

  @Bean
  public Deserializer deserializer(
      AccountingConfigurationProperties properties,
      CheckingAccountEntryDeserializer accountStatementDeserializer,
      CreditCardEntryDeserializer creditCardStatementDeserializer
  ) {
    return new DeserializerStrategy(
        properties.getCheckingAccountIdentifier(),
        properties.getCreditCardIdentifier(),
        accountStatementDeserializer,
        creditCardStatementDeserializer
    );
  }
}
