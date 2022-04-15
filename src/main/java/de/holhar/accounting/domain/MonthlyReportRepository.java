package de.holhar.accounting.domain;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface MonthlyReportRepository extends CrudRepository<MonthlyReport, Long> {

  @Override
  List<MonthlyReport> findAll();
}
