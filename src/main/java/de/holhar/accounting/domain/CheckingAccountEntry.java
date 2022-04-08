package de.holhar.accounting.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

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
