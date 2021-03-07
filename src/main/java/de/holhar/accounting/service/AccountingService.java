package de.holhar.accounting.service;

import de.holhar.accounting.domain.AccountStatement;
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
                .collect(Collectors.toMap(e -> e.getFriendlyName(), e -> e, (x1, x2) -> x2, TreeMap::new));

        printResults(reportMap);
    }

    private TreeMap<String, AccountStatement> getAccountingStatementMap(Path csvPath) throws IOException {
        return Files.list(csvPath)
                .parallel()
                .map(sanitationService::cleanUp)
                .map(deserializer::readStatement)
                .collect(Collectors.toMap(e -> e.getFriendlyName(), e -> e, (x1, x2) -> x2, TreeMap::new));
    }

    private MonthlyReport createMonthlyReport(AccountStatement accountStatement) {
        BigDecimal expenditure = accountStatement.getEntries()
                .stream()
                .map(entry -> ((CheckingAccountEntry) entry).getAmount())
                .filter(amount -> amount.intValue() < 0)
                .reduce(new BigDecimal("0"), (x1, x2) -> x1.add(x2));

        BigDecimal profit = accountStatement.getEntries()
                .stream()
                .map(entry -> ((CheckingAccountEntry) entry).getAmount())
                .filter(amount -> amount.intValue() > 0)
                .reduce(new BigDecimal("0"), (x1, x2) -> x1.add(x2));

        return new MonthlyReport(accountStatement.getFriendlyName(), profit, expenditure);
    }

    private void printResults(TreeMap<String, MonthlyReport> reportMap) {
        reportMap.forEach((id, report) -> {
            LOGGER.info(id);
            LOGGER.info(report.toString());
            LOGGER.info("===========================");
        });

        BigDecimal totalWin = reportMap.entrySet().stream()
                .map(e -> e.getValue().getWin())
                .reduce(new BigDecimal("0"), (x1, x2) -> x1.add(x2));
        LOGGER.info("Total win: {}", totalWin);
    }
}
