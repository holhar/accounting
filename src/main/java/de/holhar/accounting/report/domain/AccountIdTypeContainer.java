package de.holhar.accounting.report.domain;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class AccountIdTypeContainer {

  String id;
  AccountStatement.Type accountType;
}
