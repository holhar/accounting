package de.holhar.accounting.service.deserialization;

import de.holhar.accounting.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AccountStatementDeserializer extends AbstractStatementDeserializer implements Deserializer {

    @Override
    public AccountStatement readStatement(List<String> lines) {
        ArrayDeque<String> lineQueue = new ArrayDeque<>(lines);

        AccountIdTypeContainer idTypeContainer = getAccountIdTypeContainer(lineQueue.pop());
        LocalDate from = getDate(lineQueue.pop(), "From line is not parsable");
        LocalDate to = getDate(lineQueue.pop(), "To line is not parsable");
        Balance balance = getBalance(lineQueue);

        // Get rid of the table header
        lineQueue.pop();
        List<Entry> entries = lineQueue.stream()
                .map(this::getEntry)
                .collect(Collectors.toList());

        return new AccountStatement(idTypeContainer.getId(), idTypeContainer.getAccountType(), from, to, balance, entries);
    }

    private AccountIdTypeContainer getAccountIdTypeContainer(String typeAndIdLine) {
        List<String> typeAndIdLineList = Arrays.asList(typeAndIdLine.split(";"));
        String typeAndIdListEntry = Optional.ofNullable(typeAndIdLineList.get(1))
                .orElseThrow(() -> new IllegalArgumentException("Type/Id line is not parsable"));
        String id = typeAndIdListEntry.substring(0, typeAndIdListEntry.indexOf("/")).trim();
        String typeStringValue = typeAndIdListEntry.substring(typeAndIdListEntry.indexOf("/") + 1).trim();
        AccountStatement.Type type = resolveAccountingType(typeStringValue);
        return new AccountIdTypeContainer(id, type);
    }

    private AccountStatement.Type resolveAccountingType(String typeStringValue) {
        if ("Girokonto".equalsIgnoreCase(typeStringValue)) {
            return AccountStatement.Type.CHECKING_ACCOUNT;
        } else {
            return AccountStatement.Type.CREDIT_CARD;
        }
    }

    private Balance getBalance(ArrayDeque<String> lineQueue) {
        String balanceLine = lineQueue.pop();
        List<String> balanceLineList = Arrays.asList(balanceLine.split(";"));
        String balanceValueString = Optional.ofNullable(balanceLineList.get(1))
                .orElseThrow(() -> new IllegalArgumentException("Balance value is not parsable"));
        BigDecimal balanceValue = getBalanceValue(balanceValueString);
        LocalDate balanceDate = getBalanceDate(balanceLineList);
        return new Balance(balanceValue, balanceDate);
    }

    private BigDecimal getBalanceValue(String balanceValueString) {
        balanceValueString = balanceValueString.substring(0, balanceValueString.indexOf("EUR") - 1);
        balanceValueString = balanceValueString.replace(".", "").replace(",", ".");
        return new BigDecimal(balanceValueString);
    }

    private LocalDate getBalanceDate(List<String> balanceLineList) {
        String balanceDateString = Optional.ofNullable(balanceLineList.get(0))
                .orElseThrow(() -> new IllegalArgumentException("Balance date is not parsable"));
        Pattern datePattern = Pattern.compile(".*?(\\d\\d\\.\\d\\d\\.\\d\\d\\d\\d).*?");
        Matcher balanceDateMatcher = datePattern.matcher(balanceDateString);
        if (balanceDateMatcher.matches()) {
            return LocalDate.parse(balanceDateMatcher.group(1), formatter);
        } else {
            throw new IllegalArgumentException("Balance date is not parsable");
        }
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
        return new CheckingAccountEntry(bookingDate, valueDate, bookingText, client, intendedUse, accountId, bankCode,
                new BigDecimal(amountString), creditorId, clientReference, customerReference);
    }
}
