package de.holhar.accounting.service.report;

import de.holhar.accounting.config.AppProperties;
import de.holhar.accounting.config.Expense;
import de.holhar.accounting.domain.CheckingAccountEntry;
import de.holhar.accounting.domain.CreditCardEntry;
import de.holhar.accounting.domain.Entry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportCalculatorTest {

    private ReportCalculator reportCalculator;

    @BeforeEach
    public void setup() {
        AppProperties appProperties = mock(AppProperties.class);
        Expense expense = new Expense();
        expense.setAccommodation("");
        expense.setFood("");
        expense.setHealth("");
        expense.setPurchases("");
        expense.setTransportation("");
        when(appProperties.getExpense()).thenReturn(expense);
        when(appProperties.getOwnTransferIdentifiers()).thenReturn(Arrays.asList("Own Account one", "Own Account two"));
        reportCalculator = new ReportCalculator(appProperties);
    }

    @Test
    void getOwnTransferIdentifiers() {
        assertEquals(2, reportCalculator.getOwnTransferIdentifiers().size());
        assertTrue(reportCalculator.getOwnTransferIdentifiers().contains("Own Account one"));
        assertTrue(reportCalculator.getOwnTransferIdentifiers().contains("Own Account two"));
    }

    @Test
    void isNotOwnTransfer_givenCheckingAccountEntryIsOwnAccount_shouldReturnTrue() {
        // Given
        Entry entry = getCheckingAccountEntryClientOnly("Own Account one");

        // When
        boolean actual = reportCalculator.isNotOwnTransfer(entry);

        // Then
        assertFalse(actual);
    }

    @Test
    void isNotOwnTransfer_givenCheckingAccountEntryIsNOTOwnAccount_shouldReturnFalse() {
        // Given
        Entry entry = getCheckingAccountEntryClientOnly("NOT Own Account one");

        // When
        boolean actual = reportCalculator.isNotOwnTransfer(entry);

        // Then
        assertTrue(actual);
    }

    @Test
    void isNotOwnTransfer_givenCreditCardAccountEntryIsOwnAccount_shouldThrowException() {
        // Given
        Entry entry = new CreditCardEntry(false, null, null, null, null, null);

        // When
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> reportCalculator.isNotOwnTransfer(entry));

        // Then
        assertEquals("CreditCardEntry not applicable for isNotOwnTransfer calculation", e.getMessage());
    }

    @Test
    void getExpenditure() {
        List<Entry> entries = Arrays.asList(
                getCheckingAccountEntryAmountOnly(new BigDecimal("100.45")),
                getCheckingAccountEntryAmountOnly(new BigDecimal("-100.82")),
                getCheckingAccountEntryAmountOnly(new BigDecimal("23.98")),
                getCheckingAccountEntryAmountOnly(new BigDecimal("2359.54")),
                getCheckingAccountEntryAmountOnly(new BigDecimal("-1084.21"))
        );

        BigDecimal actual = reportCalculator.getExpenditure(entries);

        assertEquals(new BigDecimal("-1185.03"), actual);
    }

    private CheckingAccountEntry getCheckingAccountEntryClientOnly(String client) {
        return new CheckingAccountEntry(null, null, null, client,
                null, null, null, null, null, null, null);
    }

    private CheckingAccountEntry getCheckingAccountEntryAmountOnly(BigDecimal amount) {
        return new CheckingAccountEntry(null, null, null, null,
                null, null, null, amount, null, null, null);
    }

}