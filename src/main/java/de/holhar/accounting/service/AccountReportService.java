package de.holhar.accounting.service;

import de.holhar.accounting.config.AppProperties;
import de.holhar.accounting.domain.AnnualReport;
import de.holhar.accounting.domain.CostCentre;
import de.holhar.accounting.domain.EntryType;
import de.holhar.accounting.domain.MonthlyReport;
import de.holhar.accounting.repository.MonthlyReportRepository;
import de.holhar.accounting.service.report.AccountReportManager;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AccountReportService {

  private static final Logger logger = LoggerFactory.getLogger(AccountReportService.class);

  private final MonthlyReportRepository monthlyReportRepository;
  private final AccountReportManager accountReportManager;
  private final LocalDate reportStartDate;
  private final Path reportFile;
  private final DecimalFormat df;

  public AccountReportService(
      MonthlyReportRepository monthlyReportRepository,
      AccountReportManager accountReportManager,
      AppProperties appProperties
  ) {
    this.monthlyReportRepository = monthlyReportRepository;
    this.accountReportManager = accountReportManager;
    this.reportStartDate = LocalDate
        .parse(appProperties.getReportStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    String fileName =
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HH-mm")) + "_report.csv";
    this.reportFile = ServiceUtils.getValidPath(appProperties.getImportPath())
        .getParent().resolve(Paths.get(fileName));

    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    symbols.setDecimalSeparator(',');
    df = new DecimalFormat("0.00", symbols);
  }

  public void createReports() {
    LocalDate monthIterator = reportStartDate;
    AccountReportManager.ReportEntry reportData = accountReportManager.getReportDataSetEntry(monthIterator);

    while (!reportData.getCheckingAccountEntries().isEmpty()
        && !reportData.getCreditCardEntries().isEmpty()) {

      if (monthlyReportIsPresent(monthIterator)) {
        logger.info("Report is present for date '{}' -> continue", reportStartDate);
        monthIterator = monthIterator.plus(1L, ChronoUnit.MONTHS);
        reportData = accountReportManager.getReportDataSetEntry(monthIterator);
        continue;
      }

      MonthlyReport monthlyReport = accountReportManager.createMonthlyReport(monthIterator,
          reportData.getCheckingAccountEntries(), reportData.getCreditCardEntries());
      monthlyReportRepository.save(monthlyReport);

      monthIterator = monthIterator.plus(1L, ChronoUnit.MONTHS);
      reportData = accountReportManager.getReportDataSetEntry(monthIterator);
    }
  }

  public void createCsvReport() throws IOException {
    Map<Integer, List<MonthlyReport>> monthlyReportsPerYear =
        monthlyReportRepository.findAll().stream().collect(Collectors.groupingBy(MonthlyReport::getYear));

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

  private boolean monthlyReportIsPresent(LocalDate monthIterator) {
    return monthlyReportRepository.findByYearAndMonth(monthIterator.getYear(),
        monthIterator.getMonth()).isPresent();
  }

}
