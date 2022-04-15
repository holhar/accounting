package de.holhar.accounting.adapter;

import de.holhar.accounting.config.AppProperties;
import de.holhar.accounting.domain.AnnualReport;
import de.holhar.accounting.domain.CostCentre;
import de.holhar.accounting.domain.EntryType;
import de.holhar.accounting.domain.MonthlyReport;
import de.holhar.accounting.service.AccountingService;
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
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CliAdapter {

  private static final Logger logger = LoggerFactory.getLogger(CliAdapter.class);

  private final AccountingService accountingService;
  private final String defaultCsvPath;
  private final DecimalFormat df;
  private final boolean skipImport;

  public CliAdapter(AccountingService accountingService, AppProperties appProperties) {
    this.accountingService = accountingService;

    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    symbols.setDecimalSeparator(',');
    df = new DecimalFormat("0.00", symbols);

    this.defaultCsvPath = appProperties.getCsvPath();
    this.skipImport = appProperties.isSkipImport();
  }

  public void startApplication() {
    if (skipImport) {
      return;
    }

    Scanner scanner = new Scanner(System.in);
    try {
      logger.info(
          "Provide absolute path to csv files to scan (or leave blank to use default path '{}'):",
          defaultCsvPath);
      String csvPathString = scanner.nextLine();
      csvPathString = StringUtils.hasLength(csvPathString) ? csvPathString : defaultCsvPath;
      Path csvPath = Paths.get(csvPathString);
      if (!Files.isDirectory(csvPath) || !csvPath.isAbsolute()) {
        throw new IllegalArgumentException(
            "Provided path must be absolute and must be a directory");
      }
      Map<Integer, List<MonthlyReport>> monthlyReportsPerYear = accountingService.createReport(
          csvPath);
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

  private void createCSVReport(Path csvPath,
      Map<Integer, List<MonthlyReport>> monthlyReportsPerYear) throws IOException {
    String fileName =
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HH-mm")) + "_report.csv";
    Path reportFile = csvPath.getParent().resolve(Paths.get(fileName));
    try (BufferedWriter writer = Files.newBufferedWriter(reportFile);
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
            .withHeader("ID", "Year", "Month", "Income", "Expenditure", "Win", "SavingRate",
                "Invest", "####",
                "Accommodation", "Food", "Health", "Transportation", "Leisure", "Misc"))
    ) {
      List<AnnualReport> annualReportList = new ArrayList<>();
      for (Map.Entry<Integer, List<MonthlyReport>> entry : monthlyReportsPerYear.entrySet()) {
        Integer year = entry.getKey();
        List<MonthlyReport> reportSet = entry.getValue();
        AnnualReport annualReport = new AnnualReport(year + "_ANNUAL_REPORT", year);
        processReports(csvPrinter, reportSet, annualReport);
        annualReportList.add(annualReport);
      }
      csvPrinter.printRecord();
      for (AnnualReport a : annualReportList) {
        String incomeString = df.format(a.getIncome());
        String expenditureString = df.format(a.getExpenditure());
        String winString = df.format(a.getWin());
        String savingRateString = df.format(a.getSavingRate());
        String investmentString = df.format(a.getInvestment());
        csvPrinter.printRecord(a.getFriendlyName(), a.getYear(), "----", incomeString,
            expenditureString, winString, savingRateString, investmentString);
      }
      csvPrinter.flush();
    }
  }

  private void processReports(CSVPrinter csvPrinter, List<MonthlyReport> reportSet,
      AnnualReport annualReport) throws IOException {
    for (MonthlyReport r : reportSet) {
      String incomeString = df.format(r.getIncome());
      String expenditureString = df.format(r.getExpenditure());
      String varString = df.format(r.getWin());
      String savingRateString = df.format(r.getSavingRate());
      String investment = df.format(r.getInvestment());
      Map<EntryType, List<CostCentre>> cMap = getCostCentresAsMap(r.getCostCentres());
      csvPrinter.printRecord(
          r.getFriendlyName(),
          r.getYear(),
          r.getMonth(),
          incomeString,
          expenditureString,
          varString,
          savingRateString,
          investment,
          "----",
          getCostCentreAmountFormatted(cMap, EntryType.ACCOMMODATION_AND_COMMUNICATION),
          getCostCentreAmountFormatted(cMap, EntryType.FOOD_AND_DRUGSTORE),
          getCostCentreAmountFormatted(cMap, EntryType.HEALTH_AND_FITNESS),
          getCostCentreAmountFormatted(cMap, EntryType.TRANSPORTATION_AND_TRAVELLING),
          getCostCentreAmountFormatted(cMap, EntryType.LEISURE_ACTIVITIES_AND_PURCHASES),
          getCostCentreAmountFormatted(cMap, EntryType.MISCELLANEOUS)
      );
      annualReport.addProfitAndExpenses(r.getIncome(), r.getExpenditure(), r.getInvestment());
    }
  }

  private Map<EntryType, List<CostCentre>> getCostCentresAsMap(Set<CostCentre> costCentres) {
    return costCentres.stream().collect(Collectors.groupingBy(CostCentre::getEntryType));
  }

  private String getCostCentreAmountFormatted(Map<EntryType, List<CostCentre>> cMap,
      EntryType type) {
    if (cMap.get(type) != null) {
      BigDecimal amount = Optional.ofNullable(cMap.get(type).get(0)).orElse(new CostCentre(type))
          .getAmount();
      return df.format(amount);
    }
    return "0";
  }
}
