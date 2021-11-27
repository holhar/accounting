package de.holhar.accounting;

import de.holhar.accounting.domain.CheckingAccountEntry;

import java.math.BigDecimal;

public class TestUtils {

    private TestUtils() {
        throw new UnsupportedOperationException("Utils class - do not instantiate");
    }

    public static CheckingAccountEntry getCheckingAccountEntryClientOnly(String client) {
        return new CheckingAccountEntry(null, null, null, client,
                "intendedUse", null, null, null, null, null, null);
    }

    public static CheckingAccountEntry getCheckingAccountEntryAmountOnly(String amount) {
        return new CheckingAccountEntry(null, null, null, null,
                "intendedUse", null, null, new BigDecimal(amount), null, null, null);
    }

    public static CheckingAccountEntry getCheckingAccountEntryAmountAndClientOnly(String amount, String client) {
        return new CheckingAccountEntry(null, null, null, client,
                "intendedUse", null, null, new BigDecimal(amount), null, null, null);
    }
}
