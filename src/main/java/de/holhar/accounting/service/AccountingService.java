package de.holhar.accounting.service;

import de.holhar.accounting.domain.AccountStatement;
import de.holhar.accounting.domain.MonthlyReport;
import de.holhar.accounting.service.deserialization.Deserializer;
import de.holhar.accounting.service.report.ReportManager;
import de.holhar.accounting.service.sanitation.SanitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AccountingService {

    private final SanitationService sanitationService;
    private final Deserializer deserializer;
    private final ReportManager reportManager;

    @Autowired
    public AccountingService(SanitationService sanitationService, Deserializer deserializer, ReportManager reportManager) {
        this.sanitationService = sanitationService;
        this.deserializer = deserializer;
        this.reportManager = reportManager;
    }

    public Map<Integer, Set<MonthlyReport>> createReport(Path csvPath) throws IOException {
        Map<LocalDate, Set<AccountStatement>> accountingStatementsPerMonthMap;
        try (Stream<Path> pathStream = Files.list(csvPath)) {
            accountingStatementsPerMonthMap = pathStream.parallel()
                    .map(sanitationService::cleanUp)
                    .map(deserializer::readStatement)
                    .collect(Collectors.groupingBy(AccountStatement::getFrom, TreeMap::new, Collectors.toSet()));
        }
        return accountingStatementsPerMonthMap.entrySet().stream()
                .map(entry -> reportManager.createMonthlyReport(entry.getKey(), entry.getValue()))
                .collect(Collectors.groupingBy(MonthlyReport::getYear, TreeMap::new, Collectors.toSet()));
    }
}
