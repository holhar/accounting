package de.holhar.accounting.service.report;

import de.holhar.accounting.domain.AccountStatement;
import de.holhar.accounting.domain.MonthlyReport;
import java.time.LocalDate;
import java.util.Set;

public interface ReportManager {

  MonthlyReport createMonthlyReport(LocalDate statementDate, Set<AccountStatement> statementSet);
}
