package de.holhar.accounting.service.deserialization;

import de.holhar.accounting.domain.AccountStatement;

import java.util.List;

public interface Deserializer {

    public AccountStatement readStatement(List<String> lines);
}
