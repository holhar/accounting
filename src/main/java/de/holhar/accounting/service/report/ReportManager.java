package de.holhar.accounting.service.report;

import de.holhar.accounting.domain.CheckingAccountEntry;
import de.holhar.accounting.domain.CreditCardEntry;
import de.holhar.accounting.domain.MonthlyReport;
import java.time.LocalDate;
import java.util.List;

public interface ReportManager {

  MonthlyReport createMonthlyReport(final LocalDate statementDate,
      List<CheckingAccountEntry> checkingAccountEntries, List<CreditCardEntry> creditCardEntries);
}
