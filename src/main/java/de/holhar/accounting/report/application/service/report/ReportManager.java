package de.holhar.accounting.report.application.service.report;

import de.holhar.accounting.report.domain.CheckingAccountEntry;
import de.holhar.accounting.report.domain.CreditCardEntry;
import de.holhar.accounting.report.domain.MonthlyReport;
import java.time.LocalDate;
import java.util.List;

public interface ReportManager {

  MonthlyReport createMonthlyReport(
      final LocalDate statementDate,
      List<CheckingAccountEntry> checkingAccountEntries, 
      List<CreditCardEntry> creditCardEntries
  );
}
