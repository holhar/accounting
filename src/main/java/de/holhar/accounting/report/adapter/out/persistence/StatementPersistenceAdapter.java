package de.holhar.accounting.report.adapter.out.persistence;

import de.holhar.accounting.common.PersistenceAdapter;
import de.holhar.accounting.report.application.port.out.LoadStatementsPort;
import de.holhar.accounting.report.application.port.out.SaveStatementsPort;
import de.holhar.accounting.report.domain.CheckingAccountEntry;
import de.holhar.accounting.report.domain.CreditCardEntry;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@PersistenceAdapter
public class StatementPersistenceAdapter implements LoadStatementsPort, SaveStatementsPort {

  private final CheckingAccountEntryRepository checkingAccountEntryRepository;
  private final CreditCardEntryRepository creditCardEntryRepository;

  @Override
  public List<CheckingAccountEntry> loadCheckingAccountStatementsByValueDateBetween(LocalDate start, LocalDate end) {
    return checkingAccountEntryRepository.findByValueDateBetween(start, end);
  }

  @Override
  public List<CreditCardEntry> loadCreditCardStatementsByReceiptDateBetween(LocalDate start, LocalDate end) {
    return creditCardEntryRepository.findByReceiptDateBetween(start, end);
  }

  @Override
  public void saveAllCheckingAccountEntries(List<CheckingAccountEntry> checkingAccountEntries) {
    checkingAccountEntryRepository.saveAll(checkingAccountEntries);
  }

  @Override
  public void saveAllCreditCardEntries(List<CreditCardEntry> creditCardEntries) {
    creditCardEntryRepository.saveAll(creditCardEntries);
  }
}
