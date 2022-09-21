package de.holhar.accounting;

import de.holhar.accounting.report.domain.CheckingAccountEntry;
import de.holhar.accounting.report.domain.EntryType;
import java.math.BigDecimal;
import org.javamoney.moneta.Money;

public class TestUtils {

  private TestUtils() {
    throw new UnsupportedOperationException("Utils class - do not instantiate");
  }

  public static CheckingAccountEntry getCheckingAccountEntryClientOnly(String client) {
    return new CheckingAccountEntry(null, null, null, client,
        "intendedUse", null, null, null, null, null, null, null);
  }

  public static CheckingAccountEntry getCheckingAccountEntryAmountOnly(String amount) {
    return new CheckingAccountEntry(null, null, null, null,
        "intendedUse", null, null, Money.of(new BigDecimal(amount), "EUR"), null, null, null, null);
  }

  public static CheckingAccountEntry getCheckingAccountEntryAmountAndClientOnly(String amount,
      String client, EntryType type) {
    return new CheckingAccountEntry(null, null, null, client,
        "intendedUse", null, null, Money.of(new BigDecimal(amount), "EUR"), null, null, null, type);
  }
}
