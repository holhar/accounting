package de.holhar.accounting.service;

import de.holhar.accounting.config.AppProperties;
import de.holhar.accounting.domain.CheckingAccountEntry;
import de.holhar.accounting.repository.CheckingAccountEntryRepository;
import de.holhar.accounting.domain.CreditCardEntry;
import de.holhar.accounting.repository.CreditCardEntryRepository;
import de.holhar.accounting.domain.Entry;
import de.holhar.accounting.service.deserialization.Deserializer;
import de.holhar.accounting.service.sanitation.Sanitizer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
  private final Path importPath;

  @Autowired
  public AccountStatementService(
      Sanitizer sanitizer,
      Deserializer deserializer,
      CheckingAccountEntryRepository checkingAccountEntryRepository,
      CreditCardEntryRepository creditCardEntryRepository,
      AppProperties properties
  ) {
    this.sanitizer = sanitizer;
    this.deserializer = deserializer;
    this.checkingAccountEntryRepository = checkingAccountEntryRepository;
    this.creditCardEntryRepository = creditCardEntryRepository;

    this.importPath = ServiceUtils.getValidPath(properties.getImportPath());
  }

  public void importStatements() throws IOException {
    List<Entry> entries;
    try (Stream<Path> pathStream = Files.list(importPath)) {
      entries = pathStream
          .peek(p -> logger.info("Start import for report {}", p.getFileName()))
          .map(sanitizer::sanitize)
          .flatMap(deserializer::readStatement)
          .collect(Collectors.toList());
    }
    for (Entry entry : entries) {
      if (entry instanceof CreditCardEntry) {
        creditCardEntryRepository.save((CreditCardEntry) entry);
      } else if (entry instanceof CheckingAccountEntry) {
        checkingAccountEntryRepository.save((CheckingAccountEntry) entry);
      }
    }
  }
}
