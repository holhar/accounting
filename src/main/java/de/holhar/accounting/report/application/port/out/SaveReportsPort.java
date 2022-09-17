package de.holhar.accounting.report.application.port.out;

import de.holhar.accounting.report.domain.MonthlyReport;

public interface SaveReportsPort {
  void saveMonthlyReport(MonthlyReport monthlyReport);
}
