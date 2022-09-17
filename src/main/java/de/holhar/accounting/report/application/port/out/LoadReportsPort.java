package de.holhar.accounting.report.application.port.out;

import de.holhar.accounting.report.domain.MonthlyReport;
import java.time.Month;
import java.util.List;
import java.util.Optional;

public interface LoadReportsPort {
  List<MonthlyReport> loadAllMonthlyReports();
  Optional<MonthlyReport> loadMonthlyReportByYearAndMonth(int year, Month month);
}
