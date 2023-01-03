package de.holhar.accounting.report.application.service.deserialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.holhar.accounting.common.MoneyUtils;
import de.holhar.accounting.report.domain.CreditCardEntry;
import de.holhar.accounting.report.domain.Entry;
import de.holhar.accounting.report.domain.EntryType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class CreditCardEntryDeserializerTest {

  @Test
  void readStatement() throws IOException {
    // Given
    CreditCardEntryDeserializer deserializer = new CreditCardEntryDeserializer();
    Path path = Paths.get("src/test/resources/accounting/sanitized/cre_202001.csv");
    List<String> lines = Files.readAllLines(path);

    // When
    Stream<Entry> entryStream = deserializer.readStatement(lines);

    // Then
    List<Entry> entryList = entryStream.collect(Collectors.toList());
    assertEquals(33, entryList.size());

    CreditCardEntry firstEntry = (CreditCardEntry) entryList.get(0);
    assertTrue(firstEntry.isBilledAndNotIncluded());
    assertEquals(LocalDate.parse("2020-02-01"), firstEntry.getValueDate());
    assertEquals(LocalDate.parse("2020-01-30"), firstEntry.getReceiptDate());
    assertEquals("FOOBAR.COM 7899877898Somewhere", firstEntry.getDescription());
    assertEquals(MoneyUtils.ofMinor(-1272L), firstEntry.getAmount());
    assertEquals(EntryType.FOOD_AND_DRUGSTORE, firstEntry.getType());

    CreditCardEntry lastEntry = (CreditCardEntry) entryList.get(32);
    assertTrue(lastEntry.isBilledAndNotIncluded());
    assertEquals(LocalDate.parse("2020-01-02"), lastEntry.getValueDate());
    assertEquals(LocalDate.parse("2020-01-02"), lastEntry.getReceiptDate());
    assertEquals("Einzahlung", lastEntry.getDescription());
    assertEquals(MoneyUtils.ofMinor(15000L), lastEntry.getAmount());
    assertEquals(EntryType.INNER_ACCOUNT_TRANSFER, lastEntry.getType());
  }
}
