package de.holhar.accounting.common;

import javax.money.Monetary;
import org.javamoney.moneta.Money;

public class MoneyUtils {

  public static final Money ZERO = ofMinor(0);

  private MoneyUtils() {
    throw new UnsupportedOperationException("Utils class - do not instantiate");
  }

  public static Money ofMinor(long amount) {
    return Money.ofMinor(Monetary.getCurrency("EUR"), amount);
  }
}
