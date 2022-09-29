package de.holhar.accounting;

import de.holhar.accounting.common.MoneyUtils;
import de.holhar.accounting.report.domain.CheckingAccountEntry;
import de.holhar.accounting.report.domain.EntryType;
import org.javamoney.moneta.Money;

public class TestUtils {

  private TestUtils() {
    throw new UnsupportedOperationException("Utils class - do not instantiate");
  }

  public static CheckingAccountEntry getCheckingAccountEntryClientOnly(String client) {
    return new CheckingAccountEntry(null, null, null, client,
        "intendedUse", null, null, null, null, null, null, null);
  }

  public static CheckingAccountEntry getCheckingAccountEntryAmountOnly(long amount) {
    Money moneyAmount = MoneyUtils.ofMinor(amount);
    return new CheckingAccountEntry(null, null, null, null,
        "intendedUse", null, null, moneyAmount, null, null, null, null);
  }

  public static CheckingAccountEntry getCheckingAccountEntryAmountAndClientOnly(long amount,
      String client, EntryType type) {
    Money moneyAmount = MoneyUtils.ofMinor(amount);
    return new CheckingAccountEntry(null, null, null, client,
        "intendedUse", null, null, moneyAmount, null, null, null, type);
  }
}
