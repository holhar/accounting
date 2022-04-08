package de.holhar.accounting.domain;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class AccountIdTypeContainer {

    String id;
    AccountStatement.Type accountType;
}
