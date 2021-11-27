package de.holhar.accounting.service.report;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountStatementReportManagerTest {

    @InjectMocks
    private AccountStatementReportManager manager;

    @Mock
    private ReportCalculator reportCalculator;

    // TODO write test
    @Test
    void createMonthlyReport() {

    }
}