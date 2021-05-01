package de.holhar.accounting.service.report;

import de.holhar.accounting.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AccountStatementReportManager implements ReportManager {

    private final Map<CostCentre.Type, List<String>> costCentreTypeMap = new HashMap<>();

    public AccountStatementReportManager() {
        // TODO Externalize this configuration
        costCentreTypeMap.put(CostCentre.Type.FOOD, Arrays.asList("Acme", "Stuff", "Blub"));
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

        // TODO: Refactor method so that entries get iterated only once
        BigDecimal expenditure = checkingAccountStatement.getEntries()
                .stream()
                .map(Entry::getAmount)
                .filter(amount -> amount.intValue() < 0)
                .reduce(new BigDecimal("0"), BigDecimal::add);

        // TODO: Refactor method so that entries get iterated only once
        BigDecimal profit = checkingAccountStatement.getEntries()
                .stream()
                .map(Entry::getAmount)
                .filter(amount -> amount.intValue() > 0)
                .reduce(new BigDecimal("0"), BigDecimal::add);

        MonthlyReport monthlyReport = new MonthlyReport(checkingAccountStatement.getFriendlyName(), checkingAccountStatement.getFrom(), profit, expenditure);

        // TODO: Refactor method so that entries get iterated only once
        checkingAccountStatement.getEntries().forEach(entry -> assignCostCentre(monthlyReport, entry));
        creditCardStatement.getEntries().forEach(entry -> assignCostCentre(monthlyReport, entry));
        return monthlyReport;
    }

    // FIXME Does not calculate cost centres correctly
    private void assignCostCentre(MonthlyReport monthlyReport, Entry entry) {
        CostCentre.Type type;
        if (entry instanceof CheckingAccountEntry) {
            type = resolveCostCentreType((CheckingAccountEntry) entry);
        } else if (entry instanceof CreditCardEntry) {
            type = resolveCostCentreType((CreditCardEntry) entry);
        } else {
            throw new IllegalArgumentException("Invalid account statement entry type " + entry.getClass());
        }
        CostCentre costCentre = new CostCentre(type);
        costCentre.addAmount(entry.getAmount());
        monthlyReport.addToCostCentres(costCentre);
    }

    private CostCentre.Type resolveCostCentreType(CheckingAccountEntry checkingAccountEntry) {
        return costCentreTypeMap.entrySet().stream()
                .filter(mapEntry -> matchCostCentreCandidate(mapEntry.getValue(), checkingAccountEntry.getBookingText()))
                .map(Map.Entry::getKey)
                .findFirst().orElse(CostCentre.Type.MISCELLANEOUS);
    }

    private CostCentre.Type resolveCostCentreType(CreditCardEntry entry) {
        return costCentreTypeMap.entrySet().stream()
                .filter(mapEntry -> matchCostCentreCandidate(mapEntry.getValue(), entry.getDescription()))
                .map(Map.Entry::getKey)
                .findFirst().orElse(CostCentre.Type.MISCELLANEOUS);
    }

    private boolean matchCostCentreCandidate(List<String> costCentreCandidates, String bookingText) {
        return costCentreCandidates.stream().anyMatch(bookingText::contains);
    }
}