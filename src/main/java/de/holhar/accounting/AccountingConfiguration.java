package de.holhar.accounting;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.holhar.accounting.report.adapter.in.web.FileHandler;
import de.holhar.accounting.report.application.port.out.LoadStatementsPort;
import de.holhar.accounting.report.application.service.deserialization.CheckingAccountEntryDeserializer;
import de.holhar.accounting.report.application.service.deserialization.CreditCardEntryDeserializer;
import de.holhar.accounting.report.application.service.deserialization.Deserializer;
import de.holhar.accounting.report.application.service.deserialization.DeserializerStrategy;
import de.holhar.accounting.report.application.service.report.AccountReportManager;
import de.holhar.accounting.report.application.service.report.ReportCalculator;
import de.holhar.accounting.report.application.service.sanitation.FileSanitizer;
import java.text.SimpleDateFormat;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.zalando.jackson.datatype.money.MoneyModule;

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

  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    return new ObjectMapper()
        .registerModule(new MoneyModule().withAmountFieldName("majorUnit"))
        .findAndRegisterModules()
        .setSerializationInclusion(Include.NON_EMPTY)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
  }
}
