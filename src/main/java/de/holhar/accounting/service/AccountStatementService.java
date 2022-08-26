package de.holhar.accounting.service;

import de.holhar.accounting.domain.CheckingAccountEntry;
import de.holhar.accounting.domain.CreditCardEntry;
import de.holhar.accounting.domain.Entry;
import de.holhar.accounting.repository.CheckingAccountEntryRepository;
import de.holhar.accounting.repository.CreditCardEntryRepository;
import de.holhar.accounting.service.deserialization.Deserializer;
import de.holhar.accounting.service.sanitation.Sanitizer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountStatementService {

  private static final Logger logger = LoggerFactory.getLogger(AccountStatementService.class);

  private final Sanitizer sanitizer;
  private final Deserializer deserializer;
  private final CheckingAccountEntryRepository checkingAccountEntryRepository;
  private final CreditCardEntryRepository creditCardEntryRepository;

  @Autowired
  public AccountStatementService(
      Sanitizer sanitizer,
      Deserializer deserializer,
      CheckingAccountEntryRepository checkingAccountEntryRepository,
      CreditCardEntryRepository creditCardEntryRepository
  ) {
    this.sanitizer = sanitizer;
    this.deserializer = deserializer;
    this.checkingAccountEntryRepository = checkingAccountEntryRepository;
    this.creditCardEntryRepository = creditCardEntryRepository;
  }

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
      creditCardEntryRepository.saveAll(creditCardEntries);
      checkingAccountEntryRepository.saveAll(checkingAccountEntries);
    }
  }
}
