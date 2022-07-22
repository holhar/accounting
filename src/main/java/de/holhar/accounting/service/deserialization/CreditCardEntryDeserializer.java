package de.holhar.accounting.service.deserialization;

import de.holhar.accounting.domain.CreditCardEntry;
import de.holhar.accounting.domain.Entry;
import de.holhar.accounting.domain.EntryType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

@Component(value = "creditCardStatementDeserializer")
public class CreditCardEntryDeserializer implements Deserializer {

  protected static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
  @Override
  public Stream<Entry> readStatement(List<String> lines) {
    ConcurrentLinkedDeque<String> linesQueue = new ConcurrentLinkedDeque<>(lines);
    while (linesQueue.peek() != null && !linesQueue.peek().startsWith("Ja;")) {
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
    String amountString = entryFields.pop().replace(".", "").replace(",", ".").trim();
    // originalAmountString is null, blank, or contains some value I'm not interested in, so I set
    // it to '0' so that its value can be used to create a BigDecimal
    String originalAmountString = entryFields.pop();
    originalAmountString = "0";
    EntryType type = EntryType.fromValue(entryFields.getLast().isBlank() ? "" : entryFields.removeLast().trim());

    CreditCardEntry entry = new CreditCardEntry(billedAndNotIncluded, valueDate, receiptDate,
        description, new BigDecimal(amountString), new BigDecimal(originalAmountString), type);

//    if ((entry.isExpenditure() && entry.hasPositiveAmount())
//        || (entry.getType().equals(EntryType.INCOME) && entry.hasNegativeAmount())) {
//      String errMsg = String.format("Entry '%s' is invalid: type '%s', amount '%s'",
//          entry.getDescription(), entry.getType().getValue(), entry.getAmount());
//      throw new IllegalStateException(errMsg);
//    }
    return entry;
  }
}
