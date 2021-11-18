package de.holhar.accounting.service.report;

import de.holhar.accounting.config.AppProperties;
import de.holhar.accounting.config.Expense;
import de.holhar.accounting.domain.CheckingAccountEntry;
import de.holhar.accounting.domain.CostCentre;
import de.holhar.accounting.domain.CreditCardEntry;
import de.holhar.accounting.domain.Entry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
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
        expense.setAccommodation("accommodationId1,accommodationId2");
        expense.setFood("superMarket1,superMarket2,superMarket3");
        expense.setHealth("awesomePharmacy");
        expense.setPurchases("thatRetailerEveryoneUses,sportsEquipment");
        expense.setTransportation("cityTicket,sirFlightALot");
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
        Entry entry = getCheckingAccountEntryClientOnly("Own Account one");
        boolean actual = reportCalculator.isNotOwnTransfer(entry);
        assertFalse(actual);
    }

    @Test
    void isNotOwnTransfer_givenCheckingAccountEntryIsNOTOwnAccount_shouldReturnFalse() {
        Entry entry = getCheckingAccountEntryClientOnly("NOT Own Account one");
        boolean actual = reportCalculator.isNotOwnTransfer(entry);
        assertTrue(actual);
    }

    @Test
    void isNotOwnTransfer_givenCreditCardAccountEntryIsOwnAccount_shouldThrowException() {
        Entry entry = new CreditCardEntry(false, null, null, null, null, null);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> reportCalculator.isNotOwnTransfer(entry));
        assertEquals("CreditCardEntry not applicable for isNotOwnTransfer calculation", e.getMessage());
    }

    @Test
    void getExpenditure_negativeAndPositiveEntries_shouldOnlyAddNegativeEntries() {
        List<Entry> entries = Arrays.asList(
                getCheckingAccountEntryAmountOnly("100.45"),
                getCheckingAccountEntryAmountOnly("-100.82"),
                getCheckingAccountEntryAmountOnly("23.98"),
                getCheckingAccountEntryAmountOnly("2359.54"),
                getCheckingAccountEntryAmountOnly("-1084.21")
        );

        BigDecimal actual = reportCalculator.getExpenditure(entries);

        assertEquals(new BigDecimal("-1185.03"), actual);
    }

    @Test
    void getExpenditures_positiveEntriesOnly_shouldResultInZeroExpenditures() {
        List<Entry> entries = Arrays.asList(
           getCheckingAccountEntryAmountOnly("100.45"),
           getCheckingAccountEntryAmountOnly("0.23"),
           getCheckingAccountEntryAmountOnly("23.98"),
           getCheckingAccountEntryAmountOnly("2359.54"),
           getCheckingAccountEntryAmountOnly("5.45")
        );

        BigDecimal actual = reportCalculator.getExpenditure(entries);

        assertEquals(new BigDecimal("0"), actual);
    }

    @Test
    void getExpenditures_emptyEntriesList_shouldResultInZeroExpenditures() {
        List<Entry> entries = Collections.emptyList();
        BigDecimal actual = reportCalculator.getExpenditure(entries);
        assertEquals(new BigDecimal("0"), actual);
    }

    @Test
    void getProfit_negativeAndPositiveEntries_shouldOnlyAddPositiveEntries() {
        List<Entry> entries = Arrays.asList(
                getCheckingAccountEntryAmountOnly("100.45"),
                getCheckingAccountEntryAmountOnly("-100.82"),
                getCheckingAccountEntryAmountOnly("23.98"),
                getCheckingAccountEntryAmountOnly("2359.54"),
                getCheckingAccountEntryAmountOnly("-1084.21")
        );

        BigDecimal actual = reportCalculator.getProfit(entries);

        assertEquals(new BigDecimal("2483.97"), actual);
    }

    @Test
    void getProfits_positiveEntriesOnly_shouldResultInZeroProfit() {
        List<Entry> entries = Arrays.asList(
           getCheckingAccountEntryAmountOnly("-100.45"),
           getCheckingAccountEntryAmountOnly("-0.23"),
           getCheckingAccountEntryAmountOnly("-23.98"),
           getCheckingAccountEntryAmountOnly("-2359.54"),
           getCheckingAccountEntryAmountOnly("-5.45")
        );

        BigDecimal actual = reportCalculator.getProfit(entries);

        assertEquals(new BigDecimal("0"), actual);
    }

    @Test
    void getProfits_emptyEntriesList_shouldResultInZeroProfit() {
        List<Entry> entries = Collections.emptyList();
        BigDecimal actual = reportCalculator.getProfit(entries);
        assertEquals(new BigDecimal("0"), actual);
    }

    @Test
    void resolveCostCentreType_CheckingAccountEntry_matches() {
        CheckingAccountEntry entry = getCheckingAccountEntryClientOnly("this superMarket3, you know");
        CostCentre.Type actual = reportCalculator.resolveCostCentreType(entry);
        assertEquals(CostCentre.Type.FOOD, actual);
    }

    @Test
    void getCostCentre_positiveAmount_shouldReturnProfit() {
        CheckingAccountEntry entry = getCheckingAccountEntryAmountOnly("0.01");
        CostCentre actual = reportCalculator.getCostCentre(entry);
        assertEquals(CostCentre.Type.PROFIT, actual.getType());
        assertEquals(0, new BigDecimal("0.01").compareTo(actual.getAmount()));
    }

    @Test
    void getCostCentre_checkingAccountEntryWithAccommodationExpense_shouldReturnAccommodationCostCentre() {
        CheckingAccountEntry entry = getCheckingAccountEntryAmountAndClientOnly("-10.04", "accommodationId2");
        CostCentre actual = reportCalculator.getCostCentre(entry);
        assertEquals(CostCentre.Type.ACCOMMODATION, actual.getType());
        assertEquals(0, new BigDecimal("-10.04").compareTo(actual.getAmount()));
    }

    @Test
    void getCostCentre_creditCardEntryWithTransportationExpense_shouldReturnTransportationCostCentre() {
        CreditCardEntry entry = new CreditCardEntry(false, null, null, "cityTicket", new BigDecimal("-2.80"), null);
        CostCentre actual = reportCalculator.getCostCentre(entry);
        assertEquals(CostCentre.Type.TRANSPORTATION, actual.getType());
        assertEquals(0, new BigDecimal("-2.8").compareTo(actual.getAmount()));
    }

    @Test
    void resolveCostCentreType_CheckingAccountEntry_doesNotMatch() {
        CheckingAccountEntry entry = getCheckingAccountEntryClientOnly("somethingCompletelyDifferent");
        CostCentre.Type actual = reportCalculator.resolveCostCentreType(entry);
        assertEquals(CostCentre.Type.MISCELLANEOUS, actual);
    }

    @Test
    void resolveCostCentreType_CreditCardEntry_matches() {
        CreditCardEntry entry = new CreditCardEntry(false, null, null, "this superMarket3, you know", null, null);
        CostCentre.Type actual = reportCalculator.resolveCostCentreType(entry);
        assertEquals(CostCentre.Type.FOOD, actual);
    }

    @Test
    void resolveCostCentreType_CreditCardEntry_doesNotMatch() {
        CreditCardEntry entry = new CreditCardEntry(false, null, null, "somethingCompletelyDifferent", null, null);
        CostCentre.Type actual = reportCalculator.resolveCostCentreType(entry);
        assertEquals(CostCentre.Type.MISCELLANEOUS, actual);
    }

    @Test
    void matchCostCentreCandidate_clientOrDescriptionContainsCandidate_shouldReturnTrue() {
        List<String> costCentreCandidates = Arrays.asList("accommodationId1","accommodationId2");
        boolean actual = reportCalculator.matchCostCentreCandidate(costCentreCandidates, "accommodationId2 bla");
        assertTrue(actual);
    }

    @Test
    void matchCostCentreCandidate_clientOrDescriptionDoesNotContainCandidate_shouldReturnFalse() {
        List<String> costCentreCandidates = Arrays.asList("accommodationId1 bla","accommodationId2 blub");
        boolean actual = reportCalculator.matchCostCentreCandidate(costCentreCandidates,"somethingCompletelyDifferent");
        assertFalse(actual);
    }

    private CheckingAccountEntry getCheckingAccountEntryClientOnly(String client) {
        return new CheckingAccountEntry(null, null, null, client,
                null, null, null, null, null, null, null);
    }

    private CheckingAccountEntry getCheckingAccountEntryAmountOnly(String amount) {
        return new CheckingAccountEntry(null, null, null, null,
                null, null, null, new BigDecimal(amount), null, null, null);
    }

    private CheckingAccountEntry getCheckingAccountEntryAmountAndClientOnly(String amount, String client) {
        return new CheckingAccountEntry(null, null, null, client,
                null, null, null, new BigDecimal(amount), null, null, null);
    }

}