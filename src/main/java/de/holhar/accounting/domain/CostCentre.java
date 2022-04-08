package de.holhar.accounting.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode
public class CostCentre implements Comparable<CostCentre> {

    private final EntryType entryType;
    private BigDecimal amount = new BigDecimal("0");

    public void addAmount(BigDecimal newAmount) {
        this.amount = this.amount.add(newAmount);
    }

    @Override
    public int compareTo(CostCentre c) {
        return this.entryType.toString().compareTo(c.entryType.toString());
    }
}
