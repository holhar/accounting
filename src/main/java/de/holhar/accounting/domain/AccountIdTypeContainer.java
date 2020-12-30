package de.holhar.accounting.domain;

public class AccountIdTypeContainer {

    private final String id;
    private final AccountStatement.Type accountType;

    public AccountIdTypeContainer(String id, AccountStatement.Type accountType) {
        this.id = id;
        this.accountType = accountType;
    }

    public String getId() {
        return id;
    }

    public AccountStatement.Type getAccountType() {
        return accountType;
    }
}
