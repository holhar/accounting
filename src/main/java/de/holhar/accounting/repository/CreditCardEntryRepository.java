package de.holhar.accounting.repository;

import de.holhar.accounting.domain.CreditCardEntry;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface CreditCardEntryRepository extends CrudRepository<CreditCardEntry, Long> {

  List<CreditCardEntry> findByReceiptDateBetween(LocalDate start, LocalDate end);
}
