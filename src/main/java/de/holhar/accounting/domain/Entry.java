package de.holhar.accounting.domain;

import java.math.BigDecimal;

public interface Entry {

  BigDecimal getAmount();

  EntryType getType();

  default boolean isExpenditure() {
    EntryType type = this.getType();
    return !type.equals(EntryType.INCOME)
        && !type.equals(EntryType.DEPOT_TRANSFER)
        && !type.equals(EntryType.INNER_ACCOUNT_TRANSFER);
  }

  default boolean hasPositiveAmount() {
    return this.getAmount().compareTo(new BigDecimal("0")) >= 0;
  }

  default boolean hasNegativeAmount() {
    return this.getAmount().compareTo(new BigDecimal("0")) < 0;
  }
}
