package de.holhar.accounting.report.adapter.out.persistence;

import de.holhar.accounting.report.domain.MonthlyReport;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface MonthlyReportRepository extends CrudRepository<MonthlyReport, Long> {

  @Override
  List<MonthlyReport> findAll();

  Optional<MonthlyReport> findByYearAndMonth(int year, Month month);
}
