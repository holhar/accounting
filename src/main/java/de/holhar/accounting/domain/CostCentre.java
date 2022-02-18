package de.holhar.accounting.domain;

import java.math.BigDecimal;
import java.util.Objects;

public class CostCentre implements Comparable<CostCentre> {

    private final EntryType entryType;
    private BigDecimal amount = new BigDecimal("0");

    public CostCentre(EntryType entryType) {
        this.entryType = entryType;
    }

    public EntryType getEntryType() {
        return entryType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void addAmount(BigDecimal newAmount) {
        this.amount = this.amount.add(newAmount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CostCentre that = (CostCentre) o;
        return entryType == that.entryType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(entryType);
    }

    @Override
    public int compareTo(CostCentre c) {
        return this.entryType.toString().compareTo(c.entryType.toString());
    }
}
