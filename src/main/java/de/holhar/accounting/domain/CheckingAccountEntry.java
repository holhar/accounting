package de.holhar.accounting.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class CheckingAccountEntry implements Entry {

  LocalDate bookingDate;
  LocalDate valueDate;
  String bookingText;
  String client;
  String intendedUse;
  String accountId;
  String bankCode;
  BigDecimal amount;
  String creditorId;
  String clientReference;
  String customerReference;
  EntryType type;
}
