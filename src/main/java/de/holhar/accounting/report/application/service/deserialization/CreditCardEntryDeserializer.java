package de.holhar.accounting.report.application.service.deserialization;

import de.holhar.accounting.report.domain.CreditCardEntry;
import de.holhar.accounting.report.domain.Entry;
import de.holhar.accounting.report.domain.EntryType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Stream;
import javax.money.Monetary;
import org.javamoney.moneta.Money;

public class CreditCardEntryDeserializer implements Deserializer {

  protected static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
  @Override
  public Stream<Entry> readStatement(List<String> lines) {
    ConcurrentLinkedDeque<String> linesQueue = new ConcurrentLinkedDeque<>(lines);
    while (linesQueue.peek() != null
        && !linesQueue.peek().startsWith("Ja;")
        && !linesQueue.peek().startsWith("Nein;")) {
      linesQueue.pop();
    }
    return linesQueue.stream().map(this::getCreditCardEntry);
  }

  private CreditCardEntry getCreditCardEntry(String entryLine) {
    ArrayDeque<String> entryFields = new ArrayDeque<>(Arrays.asList(entryLine.split(";")));
    boolean billedAndNotIncluded = "ja".equalsIgnoreCase(entryFields.pop());
    LocalDate valueDate = LocalDate.parse(entryFields.pop(), formatter);
    LocalDate receiptDate = LocalDate.parse(entryFields.pop(), formatter);
    String description = entryFields.pop();

    // TODO: Extract and write test
    String amountString = entryFields.pop()
        .replace(".", "")
        .replace(",", "")
        .replace("-", "")
        .trim();
    Money amount = Money.ofMinor(Monetary.getCurrency("EUR"), Long.parseLong(amountString));

    // originalAmountString contains some irrelevant value, so just pop it out of the deque
    entryFields.pop();
    EntryType type = EntryType.fromValue(entryFields.getLast().isBlank() ? "" : entryFields.removeLast().trim());

    return new CreditCardEntry(billedAndNotIncluded, valueDate, receiptDate, description, amount, type);
  }
}
