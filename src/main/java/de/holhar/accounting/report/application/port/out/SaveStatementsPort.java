package de.holhar.accounting.report.application.port.out;

import de.holhar.accounting.report.domain.CheckingAccountEntry;
import de.holhar.accounting.report.domain.CreditCardEntry;
import java.util.List;

public interface SaveStatementsPort {
  void saveAllCheckingAccountEntries(List<CheckingAccountEntry> checkingAccountEntries);
  void saveAllCreditCardEntries(List<CreditCardEntry> creditCardEntries);
}
