package de.holhar.accounting.report.application.service;

import de.holhar.accounting.common.UseCase;
import de.holhar.accounting.report.application.port.in.ImportAccountStatementsUseCase;
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

@UseCase
@RequiredArgsConstructor
public class AccountStatementService implements ImportAccountStatementsUseCase {

  private final Sanitizer sanitizer;
  private final Deserializer deserializer;
  private final SaveStatementsPort saveStatementsPort;

  public void importStatements(List<Path> files) {
    List<Entry> entries = files.stream()
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
    }
    saveStatementsPort.saveAllCheckingAccountEntries(checkingAccountEntries);
    saveStatementsPort.saveAllCreditCardEntries(creditCardEntries);
  }
}
