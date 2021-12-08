package de.holhar.accounting.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@PropertySource(value = "application.properties")
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String csvPath;
    private String checkingAccountIdentifier;
    private String creditCardIdentifier;
    private List<String> ownTransferIdentifiers;
    private List<String> intendedUseIdentifiers;

    private Expense expense = new Expense();

    public String getCsvPath() {
        return csvPath;
    }

    public void setCsvPath(String csvPath) {
        this.csvPath = csvPath;
    }

    public String getCheckingAccountIdentifier() {
        return checkingAccountIdentifier;
    }

    public void setCheckingAccountIdentifier(String checkingAccountIdentifier) {
        this.checkingAccountIdentifier = checkingAccountIdentifier;
    }

    public String getCreditCardIdentifier() {
        return creditCardIdentifier;
    }

    public void setCreditCardIdentifier(String creditCardIdentifier) {
        this.creditCardIdentifier = creditCardIdentifier;
    }

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
    }

    public List<String> getOwnTransferIdentifiers() {
        return ownTransferIdentifiers;
    }

    public void setOwnTransferIdentifiers(List<String> ownTransferIdentifiers) {
        this.ownTransferIdentifiers = ownTransferIdentifiers;
    }

    public List<String> getIntendedUseIdentifiers() {
        return intendedUseIdentifiers;
    }

    public void setIntendedUseIdentifiers(List<String> intendedUseIdentifiers) {
        this.intendedUseIdentifiers = intendedUseIdentifiers;
    }
}
