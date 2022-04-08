package de.holhar.accounting.service.report;

import de.holhar.accounting.domain.AccountStatement;
import de.holhar.accounting.domain.Entry;
import de.holhar.accounting.domain.EntryType;
import de.holhar.accounting.domain.MonthlyReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Component
public class AccountStatementReportManager implements ReportManager {

    private static final Logger logger = LoggerFactory.getLogger(AccountStatementReportManager.class);

    private final ReportCalculator reportCalculator;

    public AccountStatementReportManager(ReportCalculator reportCalculator) {
        this.reportCalculator = reportCalculator;
    }

    public MonthlyReport createMonthlyReport(final LocalDate statementDate, Set<AccountStatement> statementSet) {
        if (statementSet.size() != 2) {
            String errorMessage = String.format("Monthly report  from '%s' does contain '%d' AccountStatements for " +
                            "this month -> should be '2', one checking account and one credit card statement",
                    statementDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), statementSet.size());
            throw new IllegalArgumentException(errorMessage);
        }

        String errorMessagePrefix = String.format("Monthly report from '%s' does not contain a ",
                statementDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

        AccountStatement checkingAccountStatement = statementSet.stream()
                .filter(statement -> statement.getType().equals(AccountStatement.Type.CHECKING_ACCOUNT))
                .findFirst().orElseThrow(() -> new IllegalArgumentException(errorMessagePrefix + "CHECKING_ACCOUNT statement"));

        AccountStatement creditCardStatement = statementSet.stream()
                .filter(statement -> statement.getType().equals(AccountStatement.Type.CREDIT_CARD))
                .findFirst().orElseThrow(() -> new IllegalArgumentException(errorMessagePrefix + "CREDIT_CARD statement"));

        MonthlyReport monthlyReport = new MonthlyReport(
                checkingAccountStatement.getFriendlyName(),
                checkingAccountStatement.getFrom()
        );

        calculateAllCosts(checkingAccountStatement, creditCardStatement, monthlyReport);
        calculateOverallProfit(checkingAccountStatement, creditCardStatement, monthlyReport);
        calculateOverallInvestments(checkingAccountStatement, monthlyReport);
        monthlyReport.setExpenditure(reportCalculator.getExpenditure(monthlyReport));
        monthlyReport.calcWinAndSavingRate();

        return monthlyReport;
    }

    private void calculateAllCosts(AccountStatement checkingAccountStatement, AccountStatement creditCardStatement, MonthlyReport monthlyReport) {
        checkingAccountStatement.getEntries().stream()
                .filter(Entry::isExpenditure)
                .forEach(entry -> reportCalculator.addToCostCentres(monthlyReport, entry));

        creditCardStatement.getEntries().stream()
                .filter(Entry::isExpenditure)
                .forEach(entry -> reportCalculator.addToCostCentres(monthlyReport, entry));
    }

    private void calculateOverallProfit(AccountStatement checkingAccountStatement, AccountStatement creditCardStatement, MonthlyReport monthlyReport) {
        checkingAccountStatement.getEntries().stream()
                .filter(entry -> entry.getType().equals(EntryType.INCOME))
                .forEach(entry -> monthlyReport.setIncome(reportCalculator.getProfit(monthlyReport, entry)));

        creditCardStatement.getEntries().stream()
                .filter(entry -> entry.getType().equals(EntryType.INCOME))
                .forEach(entry -> monthlyReport.setIncome(reportCalculator.getProfit(monthlyReport, entry)));
    }

    private void calculateOverallInvestments(AccountStatement checkingAccountStatement, MonthlyReport monthlyReport) {
        checkingAccountStatement.getEntries().stream()
                .filter(entry -> entry.getType().equals(EntryType.DEPOT_TRANSFER))
                .forEach(entry ->  monthlyReport.addToInvestment(entry.getAmount()));
    }
}
