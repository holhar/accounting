package de.holhar.accounting.report.application.service.deserialization;

import de.holhar.accounting.report.domain.CheckingAccountEntry;
import de.holhar.accounting.report.domain.Entry;
import de.holhar.accounting.report.domain.EntryType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

@Component(value = "accountStatementDeserializer")
public class CheckingAccountEntryDeserializer implements Deserializer {

  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
  private static final Pattern DATE_PATTERN = Pattern.compile("\\d{2}\\.\\d{2}\\.20\\d{2}.*?");
  @Override
  public Stream<Entry> readStatement(List<String> lines) {
    ConcurrentLinkedDeque<String> linesQueue = new ConcurrentLinkedDeque<>(lines);
    if (linesQueue.peek() == null) {
      throw new IllegalArgumentException("Line should not be null");
    }
    Matcher matcher = DATE_PATTERN.matcher(linesQueue.peek());
    while (linesQueue.peek() != null && !matcher.matches()) {
      linesQueue.pop();
      if (linesQueue.peek() == null) {
        throw new IllegalArgumentException("Line should not be null");
      }
      matcher = DATE_PATTERN.matcher(linesQueue.peek());
    }
    return linesQueue.stream().map(this::getEntry);
  }

  private Entry getEntry(String entryLine) {
    ArrayDeque<String> entryFields = new ArrayDeque<>(Arrays.asList(entryLine.split(";")));
    LocalDate bookingDate = LocalDate.parse(entryFields.pop(), formatter);
    LocalDate valueDate = LocalDate.parse(entryFields.pop(), formatter);
    String bookingText = entryFields.pop().trim();
    String client = entryFields.pop().trim();
    String intendedUse = entryFields.pop().trim();
    String accountId = entryFields.pop().trim();
    String bankCode = entryFields.pop().trim();
    String amountString = entryFields.pop().replace(".", "").replace(",", ".").trim();
    String creditorId = entryFields.isEmpty() ? "" : entryFields.pop().trim();
    String clientReference = entryFields.isEmpty() ? "" : entryFields.pop().trim();
    String customerReference = entryFields.isEmpty() ? "" : entryFields.pop().trim();
    EntryType type = EntryType.fromValue(entryFields.getLast().isBlank() ? "" : entryFields.removeLast().trim());
    CheckingAccountEntry entry = new CheckingAccountEntry(bookingDate, valueDate, bookingText,
        client, intendedUse, accountId, bankCode, new BigDecimal(amountString), creditorId, clientReference, customerReference, type);

    if ((entry.isExpenditure() && entry.hasPositiveAmount())
        || (entry.getType().equals(EntryType.INCOME) && entry.hasNegativeAmount())) {
      String errMsg = String.format("Entry '%s' is invalid: type '%s', amount '%s'",
          entry.getBookingText(), entry.getType().getValue(), entry.getAmount());
      throw new IllegalStateException(errMsg);
    }
    return entry;
  }
}
