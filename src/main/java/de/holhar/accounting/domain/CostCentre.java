package de.holhar.accounting.domain;

import java.math.BigDecimal;
import java.util.Objects;

public class CostCentre implements Comparable<CostCentre> {

    public enum Type {
        ACCOMMODATION,
        FOOD,
        HEALTH,
        TRANSPORTATION,
        LEISURE_ACTIVITIES_AND_PURCHASES,
        MISCELLANEOUS
    }

    private final Type type;
    private BigDecimal amount = new BigDecimal("0");

    public CostCentre(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
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
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public int compareTo(CostCentre c) {
        return this.type.toString().compareTo(c.type.toString());
    }
}
