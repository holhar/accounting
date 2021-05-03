package de.holhar.accounting.service;

import de.holhar.accounting.domain.AccountStatement;
import de.holhar.accounting.domain.AnnualReport;
import de.holhar.accounting.domain.MonthlyReport;
import de.holhar.accounting.service.deserialization.Deserializer;
import de.holhar.accounting.service.report.ReportManager;
import de.holhar.accounting.service.sanitation.SanitationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccountingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountingService.class);

    private final SanitationService sanitationService;
    private final Deserializer deserializer;
    private final ReportManager reportManager;

    @Autowired
    public AccountingService(SanitationService sanitationService, Deserializer deserializer, ReportManager reportManager) {
        this.sanitationService = sanitationService;
        this.deserializer = deserializer;
        this.reportManager = reportManager;
    }

    public void read(Path csvPath) throws IOException {
        Map<LocalDate, Set<AccountStatement>> accountingStatementsPerMonthMap = Files.list(csvPath)
                .parallel()
                .map(sanitationService::cleanUp)
                .map(deserializer::readStatement)
                .collect(Collectors.groupingBy(AccountStatement::getFrom, TreeMap::new, Collectors.toSet()));

        Map<Integer, Set<MonthlyReport>> monthlyReportsPerYear = accountingStatementsPerMonthMap.entrySet().stream()
                        .map(entry -> reportManager.createMonthlyReport(entry.getKey(), entry.getValue()))
                        .collect(Collectors.groupingBy(MonthlyReport::getYear, TreeMap::new, Collectors.toSet()));

        monthlyReportsPerYear.forEach((year, reportList) -> {
            AnnualReport annualReport = new AnnualReport(year + "_ANNUAL_REPORT", year, new BigDecimal("0.00"), new BigDecimal("0.00"));
            reportList.forEach(monthlyReport -> {
                LOGGER.info(monthlyReport.toString());
                LOGGER.info("--------------------------------------------------------------------------------------");
                annualReport.addProfitAndExpenses(monthlyReport.getIncome(), monthlyReport.getExpenditure());
            });
            LOGGER.info(annualReport.toString());
            LOGGER.info("==================================================================================");
        });
    }
}
