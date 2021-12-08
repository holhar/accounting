package de.holhar.accounting.service;

import de.holhar.accounting.TestUtils;
import de.holhar.accounting.domain.AccountStatement;
import de.holhar.accounting.domain.Balance;
import de.holhar.accounting.domain.MonthlyReport;
import de.holhar.accounting.service.deserialization.Deserializer;
import de.holhar.accounting.service.report.ReportManager;
import de.holhar.accounting.service.sanitation.SanitationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountingServiceTest {

    @InjectMocks
    private AccountingService accountingService;

    @Mock
    private SanitationService sanitationService;

    @Mock
    private Deserializer deserializer;

    @Mock
    private ReportManager reportManager;

    @Test
    void createReport() throws IOException {
        List<String> accountStatementLines = Collections.singletonList("AccountStatement");

        AccountStatement accountStatement = new AccountStatement(
                "CHECKING_ACCOUNT_ID",
                AccountStatement.Type.CHECKING_ACCOUNT,
                LocalDate.of(2021, Month.NOVEMBER, 4),
                LocalDate.of(2021, Month.DECEMBER, 3),
                new Balance(new BigDecimal("10000.01"), LocalDate.of(2021, Month.NOVEMBER, 4)),
                Collections.singletonList(TestUtils.getCheckingAccountEntryAmountAndClientOnly("-10.00", "foobar")));

        MonthlyReport monthlyReport = new MonthlyReport(
                "2021_11_CHECKING_ACCOUNT_STATEMENT",
                LocalDate.of(2021, Month.NOVEMBER, 1)
        );
        monthlyReport.setIncome(new BigDecimal("4321.23"));
        monthlyReport.setExpenditure(new BigDecimal("-1834.34"));

        when(sanitationService.cleanUp(any(Path.class))).thenReturn(accountStatementLines);
        when(deserializer.readStatement(accountStatementLines)).thenReturn(accountStatement);
        when(reportManager.createMonthlyReport(any(LocalDate.class), anySet())).thenReturn(monthlyReport);

        accountingService.createReport(Paths.get("src/test/resources/accounting/unprocessed/"));

        verify(sanitationService, times(2)).cleanUp(any(Path.class));
        verify(deserializer, times(2)).readStatement(accountStatementLines);
        verify(reportManager, times(1)).createMonthlyReport(any(LocalDate.class), anySet());
    }
}