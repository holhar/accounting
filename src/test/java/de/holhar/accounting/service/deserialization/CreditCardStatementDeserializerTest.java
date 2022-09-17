package de.holhar.accounting.service.deserialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class CreditCardStatementDeserializerTest {

//  @Test
//  void readStatement() throws IOException {
//    // Given
//    CreditCardEntryDeserializer deserializer = new CreditCardEntryDeserializer();
//    Path path = Paths.get("src/test/resources/accounting/sanitized/cre_202001.csv");
//    List<String> lines = Files.readAllLines(path);
//
//    // When
//    AccountStatement accountStatement = deserializer.readStatement(lines);
//
//    // Then
//    assertEquals(AccountStatement.Type.CREDIT_CARD, accountStatement.getType());
//    assertEquals("1111********9999", accountStatement.getId());
//    assertEquals(LocalDate.parse("2020-01-01"), accountStatement.getFrom());
//    assertEquals(LocalDate.parse("2020-01-31"), accountStatement.getTo());
//    assertEquals(new BigDecimal("108.39"), accountStatement.getBalance().getValue());
//    assertEquals(LocalDate.parse("2020-02-29"), accountStatement.getBalance().getDate());
//    assertEquals(33, accountStatement.getEntries().size());
//
//    CreditCardEntry firstEntry = (CreditCardEntry) accountStatement.getEntries().get(0);
//    assertTrue(firstEntry.isBilledAndNotIncluded());
//    assertEquals(LocalDate.parse("2020-02-01"), firstEntry.getValueDate());
//    assertEquals(LocalDate.parse("2020-01-30"), firstEntry.getReceiptDate());
//    assertEquals("FOOBAR.COM 7899877898Somewhere", firstEntry.getDescription());
//    assertEquals(new BigDecimal("-12.72"), firstEntry.getAmount());
//    assertEquals(new BigDecimal("-14.99"), firstEntry.getOriginalAmount());
//    assertEquals(EntryType.FOOD_AND_DRUGSTORE, firstEntry.getType());
//
//    CreditCardEntry lastEntry = (CreditCardEntry) accountStatement.getEntries().get(32);
//    assertTrue(lastEntry.isBilledAndNotIncluded());
//    assertEquals(LocalDate.parse("2020-01-02"), lastEntry.getValueDate());
//    assertEquals(LocalDate.parse("2020-01-02"), lastEntry.getReceiptDate());
//    assertEquals("Einzahlung", lastEntry.getDescription());
//    assertEquals(new BigDecimal("150.00"), lastEntry.getAmount());
//    assertEquals(new BigDecimal("0"), lastEntry.getOriginalAmount());
//    assertEquals(EntryType.INNER_ACCOUNT_TRANSFER, lastEntry.getType());
//  }
}
