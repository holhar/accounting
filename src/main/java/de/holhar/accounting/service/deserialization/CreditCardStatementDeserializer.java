package de.holhar.accounting.service.deserialization;

import de.holhar.accounting.domain.AccountIdTypeContainer;
import de.holhar.accounting.domain.AccountStatement;
import de.holhar.accounting.domain.Balance;
import de.holhar.accounting.domain.CreditCardEntry;
import de.holhar.accounting.domain.Entry;
import de.holhar.accounting.domain.EntryType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component(value = "creditCardStatementDeserializer")
public class CreditCardStatementDeserializer extends AbstractStatementDeserializer implements
    Deserializer {

  @Override
  public AccountStatement readStatement(List<String> lines) {
    ArrayDeque<String> linesQueue = new ArrayDeque<>(lines);
    AccountIdTypeContainer idTypeContainer = getAccountIdTypeContainer(linesQueue.pop());
    LocalDate fromDate = getDate(linesQueue.pop(), "fromDate not parsable");
    LocalDate toDate = getDate(linesQueue.pop(), "toDate not parsable");
    String balanceString = linesQueue.pop().split(";")[1].replace("EUR", "").trim();
    LocalDate balanceDate = getDate(linesQueue.pop(), "balance date not parsable");
    Balance balance = new Balance(new BigDecimal(balanceString), balanceDate);

    // Get rid of table header
    linesQueue.pop();
    List<Entry> creditCardEntries = linesQueue.stream()
        .map(this::getCreditCardEntry)
        .collect(Collectors.toList());

    return new AccountStatement(idTypeContainer.getId(), idTypeContainer.getAccountType(), fromDate,
        toDate,
        balance, creditCardEntries);
  }

  private AccountIdTypeContainer getAccountIdTypeContainer(String creditCardIdLine) {
    String[] idLineParts = creditCardIdLine.split(";");
    String typeString = idLineParts[0];
    AccountStatement.Type type;
    if (typeString.startsWith("Kreditkarte")) {
      type = AccountStatement.Type.CREDIT_CARD;
    } else {
      throw new IllegalArgumentException("account statement type is not valid : " + typeString);
    }
    String idString = idLineParts[1];
    return new AccountIdTypeContainer(idString, type);
  }

  private CreditCardEntry getCreditCardEntry(String entryLine) {
    ArrayDeque<String> entryFields = new ArrayDeque<>(Arrays.asList(entryLine.split(";")));
    boolean billedAndNotIncluded = "ja".equalsIgnoreCase(entryFields.pop());
    LocalDate valueDate = LocalDate.parse(entryFields.pop(), formatter);
    LocalDate receiptDate = LocalDate.parse(entryFields.pop(), formatter);
    String description = entryFields.pop();
    String amountString = entryFields.pop().replace(".", "").replace(",", ".").trim();
    EntryType type = EntryType.fromValue(
        entryFields.getLast().isBlank() ? "" : entryFields.removeLast().trim());
    String originalAmountString = entryFields.isEmpty() || entryFields.peek().isBlank()
        ? "0"
        : entryFields.pop()
            .replace(".", "")
            .replace(",", ".")
            .replace("USD", "")
            .trim();

    CreditCardEntry entry = new CreditCardEntry(billedAndNotIncluded, valueDate, receiptDate,
        description,
        new BigDecimal(amountString), new BigDecimal(originalAmountString), type);

    if ((entry.isExpenditure() && entry.hasPositiveAmount())
        || (entry.getType().equals(EntryType.INCOME) && entry.hasNegativeAmount())) {
      String errMsg = String.format("Entry '%s' is invalid: type '%s', amount 's'",
          entry.getDescription(),
          entry.getType().getValue(), entry.getAmount());
      throw new IllegalStateException(errMsg);
    }
    return entry;
  }
}
