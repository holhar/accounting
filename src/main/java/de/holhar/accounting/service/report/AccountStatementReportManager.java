package de.holhar.accounting.service.report;

import de.holhar.accounting.domain.AccountStatement;
import de.holhar.accounting.domain.MonthlyReport;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Component
public class AccountStatementReportManager implements ReportManager {

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

        BigDecimal expenditure = reportCalculator.getExpenditure(checkingAccountStatement.getEntries());
        BigDecimal profit = reportCalculator.getProfit(checkingAccountStatement.getEntries());

        MonthlyReport monthlyReport = new MonthlyReport(
                checkingAccountStatement.getFriendlyName(),
                checkingAccountStatement.getFrom(),
                profit,
                expenditure);

        checkingAccountStatement.getEntries().stream()
                .filter(entry -> entry.getAmount().compareTo(new BigDecimal("0")) < 0)
                .filter(reportCalculator::isNotOwnTransfer)
                .forEach(entry -> reportCalculator.addToCostCentres(monthlyReport, entry));

        creditCardStatement.getEntries().stream()
                .filter(entry -> entry.getAmount().compareTo(new BigDecimal("0")) < 0)
                .forEach(entry -> reportCalculator.addToCostCentres(monthlyReport, entry));

        // TODO Add confidence test to ensure that the sum of cost centres matches the overall expenditure
        return monthlyReport;
    }
}
