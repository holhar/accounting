package de.holhar.accounting.report.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.javamoney.moneta.Money;

@Entity
@Data
@NoArgsConstructor
public class CheckingAccountEntry implements Entry {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonIgnore
  protected Long id;

  @Column
  private LocalDate bookingDate;

  @Column
  private LocalDate valueDate;
  private String bookingText;
  private String client;

  @Column(length = 1500)
  private String intendedUse;
  private String accountId;
  private String bankCode;

  @Columns(columns = {@Column(name = "amount_currency"), @Column(name = "amount_in_minor_unit")})
  @Type(type = "org.jadira.usertype.moneyandcurrency.moneta.PersistentMoneyMinorAmountAndCurrency")
  private Money amount;
  private String creditorId;
  private String clientReference;
  private String customerReference;

  @Enumerated(EnumType.STRING)
  private EntryType type;

  public CheckingAccountEntry(LocalDate bookingDate, LocalDate valueDate,
      String bookingText, String client, String intendedUse, String accountId,
      String bankCode, Money amount, String creditorId, String clientReference,
      String customerReference, EntryType type) {
    this.bookingDate = bookingDate;
    this.valueDate = valueDate;
    this.bookingText = bookingText;
    this.client = client;
    this.intendedUse = intendedUse;
    this.accountId = accountId;
    this.bankCode = bankCode;
    this.amount = amount;
    this.creditorId = creditorId;
    this.clientReference = clientReference;
    this.customerReference = customerReference;
    this.type = type;
  }
}
