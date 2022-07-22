package de.holhar.accounting.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

@Entity
@Data
@NoArgsConstructor
public class CheckingAccountEntry implements Entry {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonIgnore
  protected Long id;

  private LocalDate bookingDate;
  private LocalDate valueDate;
  private String bookingText;
  private String client;

  @Column(length = 1500)
  private String intendedUse;
  private String accountId;
  private String bankCode;
  private BigDecimal amount;
  private String creditorId;
  private String clientReference;
  private String customerReference;

  @Enumerated(EnumType.STRING)
  private EntryType type;

  public CheckingAccountEntry(LocalDate bookingDate, LocalDate valueDate,
      String bookingText, String client, String intendedUse, String accountId,
      String bankCode, BigDecimal amount, String creditorId, String clientReference,
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
