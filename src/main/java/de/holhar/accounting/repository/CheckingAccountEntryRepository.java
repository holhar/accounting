package de.holhar.accounting.repository;

import de.holhar.accounting.domain.CheckingAccountEntry;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface CheckingAccountEntryRepository extends CrudRepository<CheckingAccountEntry, Long> {

  List<CheckingAccountEntry> findByValueDateBetween(LocalDate start, LocalDate end);
}
