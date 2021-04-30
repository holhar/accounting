package de.holhar.accounting.service;

import de.holhar.accounting.domain.AccountStatement;
import de.holhar.accounting.domain.AnnualReport;
import de.holhar.accounting.domain.CheckingAccountEntry;
import de.holhar.accounting.domain.MonthlyReport;
import de.holhar.accounting.service.deserialization.Deserializer;
import de.holhar.accounting.service.sanitation.SanitationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class AccountingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountingService.class);

    private final SanitationService sanitationService;
    private final Deserializer deserializer;

    public AccountingService(SanitationService sanitationService, Deserializer deserializer) {
        this.sanitationService = sanitationService;
        this.deserializer = deserializer;
    }

    public void read(Path csvPath) throws IOException {
        TreeMap<String, MonthlyReport> reportMap = getAccountingStatementMap(csvPath).entrySet()
                .stream()
                .parallel()
                // TODO: Incorporate credit card in monthly reports
                .filter(e -> e.getValue().getType().equals(AccountStatement.Type.CHECKING_ACCOUNT))
                .map(e -> createMonthlyReport(e.getValue()))
                .collect(Collectors.toMap(MonthlyReport::getFriendlyName, e -> e, (x1, x2) -> x2, TreeMap::new));

        printResults(reportMap);
    }

    private TreeMap<String, AccountStatement> getAccountingStatementMap(Path csvPath) throws IOException {
        return Files.list(csvPath)
                .parallel()
                .map(sanitationService::cleanUp)
                .map(deserializer::readStatement)
                .collect(Collectors.toMap(AccountStatement::getFriendlyName, e -> e, (x1, x2) -> x2, TreeMap::new));
    }

    private MonthlyReport createMonthlyReport(AccountStatement accountStatement) {
        BigDecimal expenditure = accountStatement.getEntries()
                .stream()
                .map(entry -> ((CheckingAccountEntry) entry).getAmount())
                .filter(amount -> amount.intValue() < 0)
                .reduce(new BigDecimal("0"), BigDecimal::add);

        BigDecimal profit = accountStatement.getEntries()
                .stream()
                .map(entry -> ((CheckingAccountEntry) entry).getAmount())
                .filter(amount -> amount.intValue() > 0)
                .reduce(new BigDecimal("0"), BigDecimal::add);

        return new MonthlyReport(accountStatement.getFriendlyName(), accountStatement.getFrom(), profit, expenditure);
    }

    private void printResults(TreeMap<String, MonthlyReport> reportMap) {
        Map<Integer, List<MonthlyReport>> monthlyReportsPerYear = reportMap.values().stream().collect(Collectors.groupingBy(MonthlyReport::getYear));
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
