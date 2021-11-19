package de.holhar.accounting.service.report;

import de.holhar.accounting.config.AppProperties;
import de.holhar.accounting.domain.CheckingAccountEntry;
import de.holhar.accounting.domain.CostCentre;
import de.holhar.accounting.domain.CreditCardEntry;
import de.holhar.accounting.domain.Entry;
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
     * Determines if a given checking account statement represents a transfer to an account belonging to the owner or
     * not. It does this by comparing the client of the statement to a configured list of own accounts or credit cards.
     */
    public boolean isNotOwnTransfer(Entry entry) {
        if (entry instanceof CreditCardEntry) {
            throw new IllegalArgumentException("CreditCardEntry not applicable for isNotOwnTransfer calculation");
        }
        final String client = ((CheckingAccountEntry) entry).getClient();
        return ownTransferIdentifiers.stream().noneMatch(client::equalsIgnoreCase);
    }

    public BigDecimal getExpenditure(List<Entry> statementEntries) {
        return statementEntries.stream()
                .map(Entry::getAmount)
                .filter(amount -> amount.intValue() < 0)
                .reduce(new BigDecimal("0"), BigDecimal::add);
    }

    public BigDecimal getProfit(List<Entry> statementEntries) {
        return statementEntries.stream()
                .map(Entry::getAmount)
                .filter(amount -> amount.intValue() > 0)
                .reduce(new BigDecimal("0"), BigDecimal::add);
    }

    public CostCentre getCostCentre(Entry entry) {
        if (entry.getAmount().compareTo(new BigDecimal("0.000")) > 0) {
            CostCentre profit = new CostCentre(CostCentre.Type.PROFIT);
            profit.addAmount(entry.getAmount());
            return profit;
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