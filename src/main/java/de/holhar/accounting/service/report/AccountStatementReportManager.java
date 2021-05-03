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
            String errorMessage = String.format("Monthly report '%s' does contain '%d' AccountStatements for this month",
                    statementDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), statementSet.size());
            throw new IllegalStateException(errorMessage);
        }

        String errorMessage = String.format("Monthly report '%s' does not contain a ",
                statementDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

        AccountStatement checkingAccountStatement = statementSet.stream()
                .filter(statement -> statement.getType().equals(AccountStatement.Type.CHECKING_ACCOUNT))
                .findFirst().orElseThrow(() -> new IllegalArgumentException(errorMessage + "CHECKING_ACCOUNT statement"));

        AccountStatement creditCardStatement = statementSet.stream()
                .filter(statement -> statement.getType().equals(AccountStatement.Type.CREDIT_CARD))
                .findFirst().orElseThrow(() -> new IllegalArgumentException(errorMessage + "CREDIT_CARD statement"));

        BigDecimal expenditure = reportCalculator.getExpenditure(checkingAccountStatement.getEntries());
        BigDecimal profit = reportCalculator.getProfit(checkingAccountStatement.getEntries());

        MonthlyReport monthlyReport = new MonthlyReport(
                checkingAccountStatement.getFriendlyName(),
                checkingAccountStatement.getFrom(),
                profit,
                expenditure);

        checkingAccountStatement.getEntries().stream()
                .filter(reportCalculator::isNotOwnTransfer)
                .forEach(entry -> monthlyReport.addToCostCentres(reportCalculator.getCostCentre(entry)));

        creditCardStatement.getEntries().forEach(entry -> monthlyReport.addToCostCentres(reportCalculator.getCostCentre(entry)));

        // TODO Add confidence test to ensure that the sum of cost centres matches the overall expenditure
        return monthlyReport;
    }
}
