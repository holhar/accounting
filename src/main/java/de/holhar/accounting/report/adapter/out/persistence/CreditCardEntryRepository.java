package de.holhar.accounting.report.adapter.out.persistence;

import de.holhar.accounting.report.domain.CreditCardEntry;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface CreditCardEntryRepository extends CrudRepository<CreditCardEntry, Long> {

  List<CreditCardEntry> findByReceiptDateBetween(LocalDate start, LocalDate end);
}
