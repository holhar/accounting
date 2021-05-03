package de.holhar.accounting.service.report;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
class AccountStatementReportManagerTest {

    private AccountStatementReportManager manager;

    @BeforeEach
    public void init() {
        ReportCalculator reportCalculator = mock(ReportCalculator.class);
        manager = new AccountStatementReportManager(reportCalculator);
    }

    @Test
    void createMonthlyReport() {

    }
}