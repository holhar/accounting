package de.holhar.accounting.service.report;

import de.holhar.accounting.TestUtils;
import de.holhar.accounting.domain.AccountStatement;
import de.holhar.accounting.domain.Balance;
import de.holhar.accounting.domain.Entry;
import de.holhar.accounting.domain.EntryType;
import de.holhar.accounting.domain.MonthlyReport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountStatementReportManagerTest {

    @InjectMocks
    private AccountStatementReportManager manager;

    @Mock
    private ReportCalculator reportCalculator;

    @Test
    void createMonthlyReport() {
        List<Entry> checkingAccountEntries = new ArrayList<>();
        checkingAccountEntries.add(TestUtils.getCheckingAccountEntryAmountAndClientOnly("2400.15", "employer", EntryType.INCOME));
        checkingAccountEntries.add(TestUtils.getCheckingAccountEntryAmountAndClientOnly("-500.45", "landlord", EntryType.ACCOMMODATION_AND_COMMUNICATION));
        checkingAccountEntries.add(TestUtils.getCheckingAccountEntryAmountAndClientOnly("-207.96", "groceries", EntryType.FOOD_AND_DRUGSTORE));
        checkingAccountEntries.add(TestUtils.getCheckingAccountEntryAmountAndClientOnly("-200.00", "a friend", EntryType.MISCELLANEOUS));
        AccountStatement checkingAccountStatement = new AccountStatement(
                "CHECKING_ACCOUNT_ID",
                AccountStatement.Type.CHECKING_ACCOUNT,
                LocalDate.of(2021, Month.NOVEMBER, 1),
                LocalDate.of(2021, Month.NOVEMBER, 30),
                new Balance(new BigDecimal("10000.01"), LocalDate.of(2021, Month.NOVEMBER, 1)),
                checkingAccountEntries
        );

        List<Entry> creditCardEntries = new ArrayList<>();
        creditCardEntries.add(TestUtils.getCheckingAccountEntryAmountAndClientOnly("200.00", "CHECKING_ACCOUNT_ID", EntryType.INCOME)); // <= is own transfer
        creditCardEntries.add(TestUtils.getCheckingAccountEntryAmountAndClientOnly("-10.00", "cinema", EntryType.LEISURE_ACTIVITIES_AND_PURCHASES));
        creditCardEntries.add(TestUtils.getCheckingAccountEntryAmountAndClientOnly("-25.96", "restaurant", EntryType.LEISURE_ACTIVITIES_AND_PURCHASES));
        creditCardEntries.add(TestUtils.getCheckingAccountEntryAmountAndClientOnly("-9.99", "music provider", EntryType.LEISURE_ACTIVITIES_AND_PURCHASES));
        AccountStatement creditCardStatement = new AccountStatement(
                "CREDIT_CARD_ID",
                AccountStatement.Type.CREDIT_CARD,
                LocalDate.of(2021, Month.NOVEMBER, 1),
                LocalDate.of(2021, Month.NOVEMBER, 30),
                new Balance(new BigDecimal("243.74"), LocalDate.of(2021, Month.NOVEMBER, 1)),
                creditCardEntries
        );

        Set<AccountStatement> statementSet = new HashSet<>();
        statementSet.add(checkingAccountStatement);
        statementSet.add(creditCardStatement);

        BigDecimal expenditure = new BigDecimal("-908.41");
        when(reportCalculator.getExpenditure(any(MonthlyReport.class))).thenReturn(expenditure);

        BigDecimal profit = new BigDecimal("252.15");
        when(reportCalculator.getProfit(any(MonthlyReport.class), any(Entry.class))).thenReturn(profit);

        MonthlyReport monthlyReport = manager.createMonthlyReport(checkingAccountStatement.getFrom(), statementSet);

        // 6 invokes of 'addToCostCentres' out of 8 entries minus 2 income entries
        verify(reportCalculator, times(6)).addToCostCentres(any(MonthlyReport.class), any(Entry.class));
        // 2 invokes of 'getProfit' out of 8 entries minus 6 expenditure entries
        verify(reportCalculator, times(2)).getProfit(any(MonthlyReport.class), any(Entry.class));
        assertEquals("2021_11_CHECKING_ACCOUNT", monthlyReport.getFriendlyName());
        assertEquals(Month.NOVEMBER, monthlyReport.getMonth());
        assertEquals(2021, monthlyReport.getYear());
        assertEquals(expenditure, monthlyReport.getExpenditure());
        assertEquals(profit, monthlyReport.getIncome());
    }

    @Test
    void createMonthlyReport_statementSetSizeUnequalToTwo_shouldThrowException() {

        List<Entry> checkingAccountEntries = new ArrayList<>();
        checkingAccountEntries.add(TestUtils.getCheckingAccountEntryAmountAndClientOnly("2400.15", "employer", EntryType.INCOME));
        checkingAccountEntries.add(TestUtils.getCheckingAccountEntryAmountAndClientOnly("-500.45", "landlord", EntryType.ACCOMMODATION_AND_COMMUNICATION));
        checkingAccountEntries.add(TestUtils.getCheckingAccountEntryAmountAndClientOnly("-207.96", "groceries", EntryType.FOOD_AND_DRUGSTORE));
        checkingAccountEntries.add(TestUtils.getCheckingAccountEntryAmountAndClientOnly("-200.00", "a friend", EntryType.MISCELLANEOUS));
        AccountStatement checkingAccountStatement = new AccountStatement(
                "CHECKING_ACCOUNT_ID",
                AccountStatement.Type.CHECKING_ACCOUNT,
                LocalDate.of(2021, Month.NOVEMBER, 1),
                LocalDate.of(2021, Month.NOVEMBER, 30),
                new Balance(new BigDecimal("10000.01"), LocalDate.of(2021, Month.NOVEMBER, 1)),
                checkingAccountEntries
        );

        Set<AccountStatement> statementSet = new HashSet<>();
        statementSet.add(checkingAccountStatement);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> manager.createMonthlyReport(checkingAccountStatement.getFrom(), statementSet));

        assertEquals("Monthly report  from '01.11.2021' does contain '1' AccountStatements for " +
                "this month -> should be '2', one checking account and one credit card statement", e.getMessage());
    }
}