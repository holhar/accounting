package de.holhar.accounting.report.application.service.deserialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.holhar.accounting.common.MoneyUtils;
import de.holhar.accounting.report.domain.CheckingAccountEntry;
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
class CheckingAccountEntryDeserializerTest {

  @Test
  void readStatement() throws IOException {
    // Given
    CheckingAccountEntryDeserializer deserializer = new CheckingAccountEntryDeserializer();
    Path processedFile = Paths.get("src/test/resources/accounting/sanitized/acc_202001.csv");
    List<String> lines = Files.readAllLines(processedFile);

    // When
    Stream<Entry> entryStream = deserializer.readStatement(lines);

    // Then
    List<Entry> entryList = entryStream.collect(Collectors.toList());
    assertEquals(31, entryList.size());

    CheckingAccountEntry firstEntry = (CheckingAccountEntry) entryList.get(0);
    assertEquals(LocalDate.parse("2020-01-31"), firstEntry.getBookingDate());
    assertEquals(LocalDate.parse("2020-01-31"), firstEntry.getValueDate());
    assertEquals("Überweisung", firstEntry.getBookingText());
    assertEquals("XXX VISACARD", firstEntry.getClient());
    assertEquals(
        "9876543210987645 08.39 PETER LUSTIG XXX INTERNET BANKING DATUM 31.01.2020, 08.39 UHR",
        firstEntry.getIntendedUse());
    assertEquals("XX98765432109876543210", firstEntry.getAccountId());
    assertEquals("YYXXCCV9999", firstEntry.getBankCode());
    assertEquals(MoneyUtils.ofMinor(-10000L), firstEntry.getAmount());
    assertTrue(firstEntry.getCreditorId().isBlank());
    assertTrue(firstEntry.getClientReference().isBlank());
    assertEquals("NOTPROVIDED", firstEntry.getCustomerReference());
    assertEquals(EntryType.INNER_ACCOUNT_TRANSFER, firstEntry.getType());

    CheckingAccountEntry lastEntry = (CheckingAccountEntry) entryList.get(30);
    assertEquals(LocalDate.parse("2020-01-02"), lastEntry.getBookingDate());
    assertEquals(LocalDate.parse("2020-01-02"), lastEntry.getValueDate());
    assertEquals("Lastschrift", lastEntry.getBookingText());
    assertEquals("Go Aroung Come Around (GACA)", lastEntry.getClient());
    assertEquals(
        "/RFB/P0000000000/0001, 02.01.2222,42Sub Rate PremiumSubscription 68482646 XXYYX+Blubber Enterprises",
        lastEntry.getIntendedUse());
    assertEquals("TR33333333333333333335", lastEntry.getAccountId());
    assertEquals("BELA66600000", lastEntry.getBankCode());
    assertEquals(MoneyUtils.ofMinor(-6342L), lastEntry.getAmount());
    assertEquals("DA55555555557777778", lastEntry.getCreditorId());
    assertEquals("0001-900000000000", lastEntry.getClientReference());
    assertTrue(lastEntry.getCustomerReference().isBlank());
    assertEquals(EntryType.FOOD_AND_DRUGSTORE, lastEntry.getType());
  }

}
