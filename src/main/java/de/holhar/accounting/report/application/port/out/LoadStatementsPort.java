package de.holhar.accounting.report.application.port.out;

import de.holhar.accounting.report.domain.CheckingAccountEntry;
import de.holhar.accounting.report.domain.CreditCardEntry;
import java.time.LocalDate;
import java.util.List;

public interface LoadStatementsPort {
  List<CheckingAccountEntry> loadCheckingAccountStatementsByValueDateBetween(LocalDate start, LocalDate end);
  List<CreditCardEntry> loadCreditCardStatementsByReceiptDateBetween(LocalDate start, LocalDate end);
}
