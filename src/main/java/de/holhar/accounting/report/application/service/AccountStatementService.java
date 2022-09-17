package de.holhar.accounting.report.application.service;

import de.holhar.accounting.common.UseCase;
import de.holhar.accounting.report.application.port.out.SaveStatementsPort;
import de.holhar.accounting.report.application.service.deserialization.Deserializer;
import de.holhar.accounting.report.application.service.sanitation.Sanitizer;
import de.holhar.accounting.report.domain.CheckingAccountEntry;
import de.holhar.accounting.report.domain.CreditCardEntry;
import de.holhar.accounting.report.domain.Entry;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Create beans via AccountingConfiguration
@RequiredArgsConstructor
@UseCase
public class AccountStatementService {

  private static final Logger logger = LoggerFactory.getLogger(AccountStatementService.class);

  private final Sanitizer sanitizer;
  private final Deserializer deserializer;
  private final SaveStatementsPort saveStatementsPort;

  public void importStatements(List<Path> files) {
    List<Entry> entries = files.stream()
          .peek(p -> logger.debug("Start import for file '{}'", p.getFileName()))
          .map(sanitizer::sanitize)
          .flatMap(deserializer::readStatement)
          .collect(Collectors.toList());

    List<CreditCardEntry> creditCardEntries = new ArrayList<>();
    List<CheckingAccountEntry> checkingAccountEntries = new ArrayList<>();
    for (Entry entry : entries) {
      if (entry instanceof CreditCardEntry) {
        creditCardEntries.add((CreditCardEntry) entry);
      } else if (entry instanceof CheckingAccountEntry) {
        checkingAccountEntries.add((CheckingAccountEntry) entry);
      }
      saveStatementsPort.saveAllCheckingAccountEntries(checkingAccountEntries);
      saveStatementsPort.saveAllCreditCardEntries(creditCardEntries);
    }
  }
}
