package de.holhar.accounting.service.report;

import de.holhar.accounting.TestUtils;
import de.holhar.accounting.domain.AccountStatement;
import de.holhar.accounting.domain.Balance;
import de.holhar.accounting.domain.Entry;
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
import java.util.Collections;
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
        checkingAccountEntries.add(TestUtils.getCheckingAccountEntryAmountAndClientOnly("2400.15", "employer"));
        checkingAccountEntries.add(TestUtils.getCheckingAccountEntryAmountAndClientOnly("-500.45", "landlord"));
        checkingAccountEntries.add(TestUtils.getCheckingAccountEntryAmountAndClientOnly("-207.96", "groceries"));
        checkingAccountEntries.add(TestUtils.getCheckingAccountEntryAmountAndClientOnly("-200.00", "a friend"));
        AccountStatement checkingAccountStatement = new AccountStatement(
                "CHECKING_ACCOUNT_ID",
                AccountStatement.Type.CHECKING_ACCOUNT,
                LocalDate.of(2021, Month.NOVEMBER, 1),
                LocalDate.of(2021, Month.NOVEMBER, 30),
                new Balance(new BigDecimal("10000.01"), LocalDate.of(2021, Month.NOVEMBER, 1)),
                checkingAccountEntries
        );

        List<Entry> creditCardEntries = new ArrayList<>();
        creditCardEntries.add(TestUtils.getCheckingAccountEntryAmountAndClientOnly("200.00", "CHECKING_ACCOUNT_ID")); // <= is own transfer
        creditCardEntries.add(TestUtils.getCheckingAccountEntryAmountAndClientOnly("-10.00", "cinema"));
        creditCardEntries.add(TestUtils.getCheckingAccountEntryAmountAndClientOnly("-25.96", "restaurant"));
        creditCardEntries.add(TestUtils.getCheckingAccountEntryAmountAndClientOnly("-9.99", "music provider"));
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
        when(reportCalculator.getExpenditure(checkingAccountStatement.getEntries())).thenReturn(expenditure);

        BigDecimal profit = new BigDecimal("2400.15");
        when(reportCalculator.getProfit(checkingAccountStatement.getEntries())).thenReturn(profit);

        when(reportCalculator.isNotOwnTransfer(any(Entry.class))).thenReturn(true);

        MonthlyReport monthlyReport = manager.createMonthlyReport(checkingAccountStatement.getFrom(), statementSet);

        // 6 invokes of 'addToCostCentres' out of 8 entries minus 2 income entries
        verify(reportCalculator, times(6)).addToCostCentres(any(MonthlyReport.class), any(Entry.class));
        assertEquals("2021_11_CHECKING_ACCOUNT", monthlyReport.getFriendlyName());
        assertEquals(Month.NOVEMBER, monthlyReport.getMonth());
        assertEquals(2021, monthlyReport.getYear());
        assertEquals(expenditure, monthlyReport.getExpenditure());
        assertEquals(profit, monthlyReport.getIncome());
    }

    @Test
    void createMonthlyReport_statementSetSizeUnequalToTwo_shouldThrowException() {

        List<Entry> checkingAccountEntries = new ArrayList<>();
        checkingAccountEntries.add(TestUtils.getCheckingAccountEntryAmountAndClientOnly("2400.15", "employer"));
        checkingAccountEntries.add(TestUtils.getCheckingAccountEntryAmountAndClientOnly("-500.45", "landlord"));
        checkingAccountEntries.add(TestUtils.getCheckingAccountEntryAmountAndClientOnly("-207.96", "groceries"));
        checkingAccountEntries.add(TestUtils.getCheckingAccountEntryAmountAndClientOnly("-200.00", "a friend"));
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