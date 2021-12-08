package de.holhar.accounting.service.report;

import de.holhar.accounting.config.AppProperties;
import de.holhar.accounting.domain.CheckingAccountEntry;
import de.holhar.accounting.domain.CostCentre;
import de.holhar.accounting.domain.CreditCardEntry;
import de.holhar.accounting.domain.Entry;
import de.holhar.accounting.domain.MonthlyReport;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ReportCalculator {

    private final List<String> ownTransferIdentifiers;
    private final List<String> intendedUseIdentifiers;
    private final Map<CostCentre.Type, List<String>> costCentreTypeMap = new HashMap<>();

    public ReportCalculator(AppProperties appProperties) {
        this.ownTransferIdentifiers = appProperties.getOwnTransferIdentifiers();
        this.intendedUseIdentifiers = appProperties.getIntendedUseIdentifiers();
        initCostCentreTypeMap(appProperties);
    }

    private void initCostCentreTypeMap(AppProperties props) {
        List<String> accommodation = Optional.ofNullable(props.getExpense().getAccommodation()).orElse(Collections.emptyList());
        List<String> food = Optional.ofNullable(props.getExpense().getFood()).orElse(Collections.emptyList());
        List<String> health = Optional.ofNullable(props.getExpense().getHealth()).orElse(Collections.emptyList());
        List<String> transportation = Optional.ofNullable(props.getExpense().getTransportation()).orElse(Collections.emptyList());
        List<String> purchases = Optional.ofNullable(props.getExpense().getPurchases()).orElse(Collections.emptyList());

        costCentreTypeMap.put(CostCentre.Type.ACCOMMODATION, accommodation);
        costCentreTypeMap.put(CostCentre.Type.FOOD, food);
        costCentreTypeMap.put(CostCentre.Type.HEALTH, health);
        costCentreTypeMap.put(CostCentre.Type.TRANSPORTATION, transportation);
        costCentreTypeMap.put(CostCentre.Type.LEISURE_ACTIVITIES_AND_PURCHASES, purchases);
    }

    /**
     * Determines if a given entry represents a transfer to an account belonging to the owner or not. It does this by
     * comparing the client/description of the statement to a configured list of own accounts or credit cards.
     */
    public boolean isNotOwnTransfer(Entry entry) {
        if (entry instanceof CheckingAccountEntry) {
            final String client = ((CheckingAccountEntry) entry).getClient();
            return ownTransferIdentifiers.stream().noneMatch(client::equalsIgnoreCase);
        } else if (entry instanceof CreditCardEntry) {
            final String description = ((CreditCardEntry) entry).getDescription();
            return ownTransferIdentifiers.stream().noneMatch(description::equalsIgnoreCase);
        } else {
            throw new IllegalArgumentException("Given type of entry must be either CreditCard- or CheckingAccountEntry");
        }
    }

    public BigDecimal getExpenditure(MonthlyReport monthlyReport) {
        return monthlyReport.getCostCentres().stream()
                .map(CostCentre::getAmount)
                .reduce(new BigDecimal("0"), BigDecimal::add);
    }

    public BigDecimal getProfit(MonthlyReport monthlyReport, Entry entry) {
        if (entry.getAmount().compareTo(new BigDecimal("0")) <= 0) {
            throw new IllegalArgumentException("Given entry amount must be above zero (a profit), but was " + entry.getAmount());
        }
        return monthlyReport.getIncome().add(entry.getAmount());
    }

    public void addToCostCentres(MonthlyReport monthlyReport, Entry entry) {
        CostCentre costCentre = getCostCentre(entry);
        if (monthlyReport.getCostCentres().contains(costCentre)) {
            monthlyReport.getCostCentres().stream()
                    .filter(c -> c.getType().equals(costCentre.getType()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Could not match given cost centre type " + costCentre.getType()))
                    .addAmount(costCentre.getAmount());
        } else {
            monthlyReport.getCostCentres().add(costCentre);
        }
    }

    CostCentre getCostCentre(Entry entry) {
        if (entry.getAmount().compareTo(new BigDecimal("0")) >= 0) {
            throw new IllegalArgumentException("Given entry amount must be below zero (an expenditure), but was " + entry.getAmount());
        }
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
        return costCentre;
    }

    CostCentre.Type resolveCostCentreType(CheckingAccountEntry entry) {
        var entryClientOrIntendedUse = intendedUseIdentifiers.stream()
                .filter(id -> entry.getClient().contains(id))
                .map(id -> entry.getIntendedUse())
                .findFirst().orElse(entry.getClient());
        return costCentreTypeMap.entrySet().stream()
                .filter(mapEntry -> matchCostCentreCandidate(mapEntry.getValue(), entryClientOrIntendedUse))
                .map(Map.Entry::getKey)
                .findFirst().orElse(CostCentre.Type.MISCELLANEOUS);
    }

    CostCentre.Type resolveCostCentreType(CreditCardEntry entry) {
        return costCentreTypeMap.entrySet().stream()
                .filter(mapEntry -> matchCostCentreCandidate(mapEntry.getValue(), entry.getDescription()))
                .map(Map.Entry::getKey)
                .findFirst().orElse(CostCentre.Type.MISCELLANEOUS);
    }

    boolean matchCostCentreCandidate(List<String> costCentreCandidates, String clientOrDescription) {
        return costCentreCandidates.stream().anyMatch(clientOrDescription::contains);
    }

    List<String> getOwnTransferIdentifiers() {
        return new ArrayList<>(ownTransferIdentifiers);
    }
}
