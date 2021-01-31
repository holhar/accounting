package de.holhar.accounting.service.deserialization;

import de.holhar.accounting.domain.AccountStatement;
import de.holhar.accounting.domain.CreditCardEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
public class CreditCardStatementDeserializerTest {

    @Test
    public void readStatement() throws IOException {
        // Given
        CreditCardStatementDeserializer deserializer = new CreditCardStatementDeserializer();
        Path path = Paths.get("src/test/resources/accounting/sanitized/cre_202001.csv");
        List<String> lines = Files.readAllLines(path);

        // When
        AccountStatement accountStatement = deserializer.readStatement(lines);

        // Then
        assertEquals(AccountStatement.Type.CREDIT_CARD, accountStatement.getType());
        assertEquals("1111********9999", accountStatement.getId());
        assertEquals(LocalDate.parse("2020-01-01"), accountStatement.getFrom());
        assertEquals(LocalDate.parse("2020-01-31"), accountStatement.getTo());
        assertEquals(new BigDecimal("108.39"), accountStatement.getBalance().getValue());
        assertEquals(LocalDate.parse("2020-02-29"), accountStatement.getBalance().getDate());
        assertEquals(33, accountStatement.getEntries().size());

        CreditCardEntry firstEntry = (CreditCardEntry)accountStatement.getEntries().get(0);
        assertTrue(firstEntry.isBilledAndNotIncluded());
        assertEquals(LocalDate.parse("2020-02-01"), firstEntry.getValueDate());
        assertEquals(LocalDate.parse("2020-01-30"), firstEntry.getReceiptDate());
        assertEquals("FOOBAR.COM 7899877898Somewhere", firstEntry.getDescription());
        assertEquals(new BigDecimal("-1.99"), firstEntry.getAmount());
        assertEquals(new BigDecimal("0"), firstEntry.getOriginalAmount());

        CreditCardEntry lastEntry = (CreditCardEntry)accountStatement.getEntries().get(32);
        assertTrue(lastEntry.isBilledAndNotIncluded());
        assertEquals(LocalDate.parse("2020-01-02"), lastEntry.getValueDate());
        assertEquals(LocalDate.parse("2020-01-02"), lastEntry.getReceiptDate());
        assertEquals("Einzahlung", lastEntry.getDescription());
        assertEquals(new BigDecimal("150.00"), lastEntry.getAmount());
        assertEquals(new BigDecimal("0"), lastEntry.getOriginalAmount());
    }
}
