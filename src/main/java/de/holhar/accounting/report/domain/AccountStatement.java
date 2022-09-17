package de.holhar.accounting.report.domain;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class AccountStatement {

  String id;
  Type type;
  LocalDate from;
  LocalDate to;
  Balance balance;
  List<Entry> entries;

  public String getFriendlyName() {
    String month = "" + from.getMonthValue();
    month = month.length() < 2 ? "0" + month : month;
    return from.getYear() + "_" + month + "_" + type.name();
  }

  public enum Type {
    CHECKING_ACCOUNT, CREDIT_CARD
  }
}
