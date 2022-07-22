package de.holhar.accounting.repository;

import de.holhar.accounting.domain.MonthlyReport;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface MonthlyReportRepository extends CrudRepository<MonthlyReport, Long> {

  @Override
  List<MonthlyReport> findAll();

  Optional<MonthlyReport> findByYearAndMonth(int year, Month month);
}
