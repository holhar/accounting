package de.holhar.accounting.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value = "application.properties")
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private boolean readStatementsEnabled;
    private String csvPath;

    private Expense expense = new Expense();

    public boolean isReadStatementsEnabled() {
        return readStatementsEnabled;
    }

    public void setReadStatementsEnabled(boolean readStatementsEnabled) {
        this.readStatementsEnabled = readStatementsEnabled;
    }

    public String getCsvPath() {
        return csvPath;
    }

    public void setCsvPath(String csvPath) {
        this.csvPath = csvPath;
    }

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
    }
}
