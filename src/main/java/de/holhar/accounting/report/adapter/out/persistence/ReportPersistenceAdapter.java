package de.holhar.accounting.report.adapter.out.persistence;

import de.holhar.accounting.common.PersistenceAdapter;
import de.holhar.accounting.report.application.port.out.LoadReportsPort;
import de.holhar.accounting.report.application.port.out.SaveReportsPort;
import de.holhar.accounting.report.domain.MonthlyReport;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@PersistenceAdapter
public class ReportPersistenceAdapter implements LoadReportsPort, SaveReportsPort {

  private final MonthlyReportRepository monthlyReportRepository;

  @Override
  public List<MonthlyReport> loadAllMonthlyReports() {
    return monthlyReportRepository.findAll();
  }

  @Override
  public Optional<MonthlyReport> loadMonthlyReportByYearAndMonth(int year, Month month) {
    return monthlyReportRepository.findByYearAndMonth(year, month);
  }

  @Override
  public void saveMonthlyReport(MonthlyReport monthlyReport) {
    monthlyReportRepository.save(monthlyReport);
  }
}
