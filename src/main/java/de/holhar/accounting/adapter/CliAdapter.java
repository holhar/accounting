package de.holhar.accounting.adapter;

import de.holhar.accounting.domain.AnnualReport;
import de.holhar.accounting.domain.MonthlyReport;
import de.holhar.accounting.service.AccountingService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

@Profile("cli")
@Component
public class CliAdapter {

    private static final Logger logger = LoggerFactory.getLogger(CliAdapter.class);

    private final AccountingService accountingService;
    private final DecimalFormat df;

    public CliAdapter(AccountingService accountingService) {
        this.accountingService = accountingService;

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        df = new DecimalFormat("0.00", symbols);
    }

    public void startApplication() {
        Scanner scanner = new Scanner(System.in);
        try {
            logger.info("Provide absolute path to csv files to scan:");
            Path csvPath = Paths.get(scanner.nextLine());
            if (!Files.isDirectory(csvPath) || !csvPath.isAbsolute()) {
                throw new IllegalArgumentException("Provided path must be absolute and must be a directory");
            }
            Map<Integer, Set<MonthlyReport>> monthlyReportsPerYear = accountingService.createReport(csvPath);
            createCSVReport(csvPath, monthlyReportsPerYear);
        } catch (Exception e) {
            logger.error("Failed to create report: '{}'", e.getMessage(), e);
            logger.info("Restart report creation process? (Y|n):");
            String restartString = scanner.nextLine();
            boolean restart = restartString.isEmpty() || "y".equalsIgnoreCase(restartString);
            if (restart) {
                startApplication();
            }
        }
        logger.info("Finished report creation");
    }

    private void createCSVReport(Path csvPath, Map<Integer, Set<MonthlyReport>> monthlyReportsPerYear) throws IOException {
        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HH-mm")) + "_report.csv";
        Path reportFile = csvPath.getParent().resolve(Paths.get(fileName));
        try (BufferedWriter writer = Files.newBufferedWriter(reportFile);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                     .withHeader("ID", "Year", "Month", "Income", "Expenditure", "Win", "SavingRate"))
        ) {
            List<AnnualReport> annualReportList = new ArrayList<>();
            for (Map.Entry<Integer, Set<MonthlyReport>> entry : monthlyReportsPerYear.entrySet()) {
                Integer year = entry.getKey();
                Set<MonthlyReport> reportSet = entry.getValue();
                AnnualReport annualReport = new AnnualReport(year + "_ANNUAL_REPORT", year, new BigDecimal("0.00"), new BigDecimal("0.00"));
                processReports(csvPrinter, reportSet, annualReport);
                annualReportList.add(annualReport);
            }
            csvPrinter.printRecord();
            for (AnnualReport a : annualReportList) {
                String incomeString = df.format(a.getIncome());
                String expenditureString = df.format(a.getExpenditure());
                String winString = df.format(a.getWin());
                String savingRateString = df.format(a.getSavingRate());
                csvPrinter.printRecord(a.getFriendlyName(), a.getYear(), "--", incomeString, expenditureString, winString, savingRateString);
            }
            csvPrinter.flush();
        }
    }

    private void processReports(CSVPrinter csvPrinter, Set<MonthlyReport> reportSet, AnnualReport annualReport) throws IOException {
        for (MonthlyReport r : reportSet) {
            String incomeString = df.format(r.getIncome());
            String expenditureString = df.format(r.getExpenditure());
            String varString = df.format(r.getWin());
            String savingRateString = df.format(r.getSavingRate());
            csvPrinter.printRecord(r.getFriendlyName(), r.getYear(), r.getMonth(), incomeString, expenditureString, varString, savingRateString);
            annualReport.addProfitAndExpenses(r.getIncome(), r.getExpenditure());
        }
    }
}
