package de.holhar.accounting.service.deserialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class AccountStatementDeserializerTest {

//  @Test
//  void readStatement() throws IOException {
//    // Given
//    CheckingAccountEntryDeserializer deserializer = new CheckingAccountEntryDeserializer();
//    Path processedFile = Paths.get("src/test/resources/accounting/sanitized/acc_202001.csv");
//    List<String> lines = Files.readAllLines(processedFile);
//
//    // When
//    AccountStatement accountStatement = deserializer.readStatement(lines);
//
//    // Then
//    assertEquals(AccountStatement.Type.CHECKING_ACCOUNT, accountStatement.getType());
//    assertEquals("XX01234567890123456789", accountStatement.getId());
//    assertEquals(LocalDate.parse("2020-01-01"), accountStatement.getFrom());
//    assertEquals(LocalDate.parse("2020-01-31"), accountStatement.getTo());
//    assertEquals(new BigDecimal("1234.56"), accountStatement.getBalance().getValue());
//    assertEquals(LocalDate.parse("2020-01-31"), accountStatement.getBalance().getDate());
//    assertEquals(31, accountStatement.getEntries().size());
//
//    CheckingAccountEntry firstEntry = (CheckingAccountEntry) accountStatement.getEntries().get(0);
//    assertEquals(LocalDate.parse("2020-01-31"), firstEntry.getBookingDate());
//    assertEquals(LocalDate.parse("2020-01-31"), firstEntry.getValueDate());
//    assertEquals("Überweisung", firstEntry.getBookingText());
//    assertEquals("XXX VISACARD", firstEntry.getClient());
//    assertEquals(
//        "9876543210987645 08.39 PETER LUSTIG XXX INTERNET BANKING DATUM 31.01.2020, 08.39 UHR",
//        firstEntry.getIntendedUse());
//    assertEquals("XX98765432109876543210", firstEntry.getAccountId());
//    assertEquals("YYXXCCV9999", firstEntry.getBankCode());
//    assertEquals(new BigDecimal("-100.00"), firstEntry.getAmount());
//    assertTrue(firstEntry.getCreditorId().isBlank());
//    assertTrue(firstEntry.getClientReference().isBlank());
//    assertEquals("NOTPROVIDED", firstEntry.getCustomerReference());
//    assertEquals(EntryType.INNER_ACCOUNT_TRANSFER, firstEntry.getType());
//
//    CheckingAccountEntry lastEntry = (CheckingAccountEntry) accountStatement.getEntries().get(30);
//    assertEquals(LocalDate.parse("2020-01-02"), lastEntry.getBookingDate());
//    assertEquals(LocalDate.parse("2020-01-02"), lastEntry.getValueDate());
//    assertEquals("Lastschrift", lastEntry.getBookingText());
//    assertEquals("Go Aroung Come Around (GACA)", lastEntry.getClient());
//    assertEquals(
//        "/RFB/P0000000000/0001, 02.01.2222,42Sub Rate PremiumSubscription 68482646 XXYYX+Blubber Enterprises",
//        lastEntry.getIntendedUse());
//    assertEquals("TR33333333333333333335", lastEntry.getAccountId());
//    assertEquals("BELA66600000", lastEntry.getBankCode());
//    assertEquals(new BigDecimal("-63.42"), lastEntry.getAmount());
//    assertEquals("DA55555555557777778", lastEntry.getCreditorId());
//    assertEquals("0001-900000000000", lastEntry.getClientReference());
//    assertTrue(lastEntry.getCustomerReference().isBlank());
//    assertEquals(EntryType.FOOD_AND_DRUGSTORE, lastEntry.getType());
//  }

}
