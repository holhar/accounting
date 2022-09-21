package de.holhar.accounting.report.domain;

import org.javamoney.moneta.Money;

public interface Entry {

  Money getAmount();

  EntryType getType();

  default boolean isExpenditure() {
    EntryType type = this.getType();
    return !type.equals(EntryType.INCOME)
        && !type.equals(EntryType.DEPOT_TRANSFER)
        && !type.equals(EntryType.INNER_ACCOUNT_TRANSFER);
  }

  default boolean hasPositiveAmount() {
    return this.getAmount().isPositiveOrZero();
  }

  default boolean hasNegativeAmount() {
    return this.getAmount().isNegative();
  }
}
