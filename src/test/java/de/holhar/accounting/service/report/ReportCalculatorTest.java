package de.holhar.accounting.service.report;

import de.holhar.accounting.TestUtils;
import de.holhar.accounting.config.AppProperties;
import de.holhar.accounting.config.Expense;
import de.holhar.accounting.domain.CheckingAccountEntry;
import de.holhar.accounting.domain.CostCentre;
import de.holhar.accounting.domain.CreditCardEntry;
import de.holhar.accounting.domain.Entry;
import de.holhar.accounting.domain.MonthlyReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
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
        expense.setAccommodation(Arrays.asList("accommodationId1", "accommodationId2"));
        expense.setFood(Arrays.asList("superMarket1", "superMarket2", "superMarket3"));
        expense.setHealth(Arrays.asList("awesomePharmacy", "intendedUse"));
        expense.setPurchases(Arrays.asList("thatRetailerEveryoneUses", "sportsEquipment"));
        expense.setTransportation(Arrays.asList("cityTicket", "sirFlightALot"));
        when(appProperties.getExpense()).thenReturn(expense);
        when(appProperties.getOwnTransferIdentifiers()).thenReturn(Arrays.asList("Own Account one", "Own Account two"));
        when(appProperties.getIntendedUseIdentifiers()).thenReturn(Collections.singletonList("doNotUseClientField"));
        reportCalculator = new ReportCalculator(appProperties);
    }

    @Test
    void getOwnTransferIdentifiers() {
        assertEquals(2, reportCalculator.getOwnTransferIdentifiers().size());
        assertTrue(reportCalculator.getOwnTransferIdentifiers().contains("Own Account one"));
        assertTrue(reportCalculator.getOwnTransferIdentifiers().contains("Own Account two"));
    }

    @Test
    void isNotOwnTransfer_givenCheckingAccountEntryIsOwnAccount_shouldReturnFalse() {
        Entry entry = TestUtils.getCheckingAccountEntryClientOnly("Own Account one");
        boolean actual = reportCalculator.isNotOwnTransfer(entry);
        assertFalse(actual);
    }

    @Test
    void isNotOwnTransfer_givenCheckingAccountEntryIsNOTOwnAccount_shouldReturnTrue() {
        Entry entry = TestUtils.getCheckingAccountEntryClientOnly("Different account");
        boolean actual = reportCalculator.isNotOwnTransfer(entry);
        assertTrue(actual);
    }

    @Test
    void isNotOwnTransfer_givenCreditCardAccountEntryIsOwnAccount_shouldReturnFalse() {
        Entry entry = new CreditCardEntry(false, null, null, "Own Account one", null, null);
        boolean actual = reportCalculator.isNotOwnTransfer(entry);
        assertFalse(actual);
    }

    @Test
    void isNotOwnTransfer_givenCreditCardAccountEntryIsNOTOwnAccount_shouldReturnTrue() {
        Entry entry = new CreditCardEntry(false, null, null, "Different account", null, null);
        boolean actual = reportCalculator.isNotOwnTransfer(entry);
        assertTrue(actual);
    }

    @Test
    void getExpenditure_monthlyReportWithCostCentresGiven_shouldSumUpExpenditures() {
        MonthlyReport monthlyReport = new MonthlyReport(
                "2021_11_CHECKING_ACCOUNT_STATEMENT",
                LocalDate.of(2021, Month.NOVEMBER, 1)
        );
        monthlyReport.setIncome(new BigDecimal("4321.23"));
        monthlyReport.setExpenditure(new BigDecimal("-1834.34"));

        CostCentre costCentre1 = new CostCentre(CostCentre.Type.FOOD);
        costCentre1.addAmount(new BigDecimal("-100.82"));
        monthlyReport.getCostCentres().add(costCentre1);
        CostCentre costCentre2 = new CostCentre(CostCentre.Type.ACCOMMODATION);
        costCentre2.addAmount(new BigDecimal("-1084.21"));
        monthlyReport.getCostCentres().add(costCentre2);

        BigDecimal actual = reportCalculator.getExpenditure(monthlyReport);

        assertEquals(new BigDecimal("-1185.03"), actual);
    }

    @Test
    void getProfit_positiveEntries_shoulAddPositiveEntries() {
        List<Entry> entries = Arrays.asList(
                TestUtils.getCheckingAccountEntryAmountOnly("100.45"),
                TestUtils.getCheckingAccountEntryAmountOnly("23.98"),
                TestUtils.getCheckingAccountEntryAmountOnly("2359.54")
        );

        MonthlyReport monthlyReport = new MonthlyReport(
                "2021_11_CHECKING_ACCOUNT_STATEMENT",
                LocalDate.of(2021, Month.NOVEMBER, 1)
        );

        monthlyReport.setIncome(reportCalculator.getProfit(monthlyReport, entries.get(0)));
        monthlyReport.setIncome(reportCalculator.getProfit(monthlyReport, entries.get(1)));
        monthlyReport.setIncome(reportCalculator.getProfit(monthlyReport, entries.get(2)));

        assertEquals(new BigDecimal("2483.97"), monthlyReport.getIncome());
    }

    @Test
    void getProfit_negativeEntry_shouldThrowException() {
        CheckingAccountEntry entry = TestUtils.getCheckingAccountEntryAmountOnly("-100.45");

        MonthlyReport monthlyReport = new MonthlyReport(
                "2021_11_CHECKING_ACCOUNT_STATEMENT",
                LocalDate.of(2021, Month.NOVEMBER, 1)
        );

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> reportCalculator.getProfit(monthlyReport, entry));

        assertEquals("Given entry amount must be above zero (a profit), but was -100.45", e.getMessage());
    }

    @Test
    void addToCostCentres() {
        MonthlyReport monthlyReport = new MonthlyReport(
                "2021_11_CHECKING_ACCOUNT_STATEMENT",
                LocalDate.of(2021, Month.NOVEMBER, 1)
        );

        Entry entry1 = TestUtils.getCheckingAccountEntryAmountAndClientOnly("-69.99", "XX sportsEquipment YY");
        Entry entry2 = TestUtils.getCheckingAccountEntryAmountAndClientOnly("-112.83", "XX sportsEquipment ZZ");

        reportCalculator.addToCostCentres(monthlyReport, entry1);
        reportCalculator.addToCostCentres(monthlyReport, entry2);

        assertEquals(1, monthlyReport.getCostCentres().size());
        BigDecimal amount = monthlyReport.getCostCentres().stream()
                .filter(c -> c.getType().equals(CostCentre.Type.LEISURE_ACTIVITIES_AND_PURCHASES))
                .map(CostCentre::getAmount)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Should contain LEISURE_ACTIVITIES_AND_PURCHASES cost centre"));
        assertEquals(0, new BigDecimal("-182.82").compareTo(amount));
    }

    @Test
    void getCostCentre_positiveAmount_shouldReturnProfit() {
        CheckingAccountEntry entry = TestUtils.getCheckingAccountEntryAmountOnly("0.01");
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> reportCalculator.getCostCentre(entry));
        assertEquals("Given entry amount must be below zero (an expenditure), but was 0.01", e.getMessage());
    }

    @Test
    void getCostCentre_checkingAccountEntryWithAccommodationExpense_shouldReturnAccommodationCostCentre() {
        CheckingAccountEntry entry = TestUtils.getCheckingAccountEntryAmountAndClientOnly("-10.04", "accommodationId2");
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
    void resolveCostCentreType_checkingAccountEntry_matches() {
        CheckingAccountEntry entry = TestUtils.getCheckingAccountEntryClientOnly("this superMarket3, you know");
        CostCentre.Type actual = reportCalculator.resolveCostCentreType(entry);
        assertEquals(CostCentre.Type.FOOD, actual);
    }

    @Test
    void resolveCostCentreType_checkingAccountEntry_matchesForIntendedUse() {
        CheckingAccountEntry entry = TestUtils.getCheckingAccountEntryClientOnly("doNotUseClientField");
        CostCentre.Type actual = reportCalculator.resolveCostCentreType(entry);
        assertEquals(CostCentre.Type.HEALTH, actual);
    }

    @Test
    void resolveCostCentreType_checkingAccountEntry_doesNotMatch() {
        CheckingAccountEntry entry = TestUtils.getCheckingAccountEntryClientOnly("somethingCompletelyDifferent");
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
        List<String> costCentreCandidates = Arrays.asList("accommodationId1", "accommodationId2");
        boolean actual = reportCalculator.matchCostCentreCandidate(costCentreCandidates, "accommodationId2 bla");
        assertTrue(actual);
    }

    @Test
    void matchCostCentreCandidate_clientOrDescriptionDoesNotContainCandidate_shouldReturnFalse() {
        List<String> costCentreCandidates = Arrays.asList("accommodationId1 bla", "accommodationId2 blub");
        boolean actual = reportCalculator.matchCostCentreCandidate(costCentreCandidates, "somethingCompletelyDifferent");
        assertFalse(actual);
    }
}