package de.holhar.accounting.report.adapter.out.persistence;

import de.holhar.accounting.report.domain.CheckingAccountEntry;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface CheckingAccountEntryRepository extends CrudRepository<CheckingAccountEntry, Long> {
  List<CheckingAccountEntry> findByValueDateBetween(LocalDate start, LocalDate end);
}
