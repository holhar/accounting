package de.holhar.accounting.report.application.service.deserialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import de.holhar.accounting.AccountingConfigProperties;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class DeserializerStrategyTest {

  @InjectMocks
  private DeserializerStrategy deserializerStrategy;

  @Mock
  private CreditCardEntryDeserializer creditCardEntryDeserializer;

  @Mock
  private CheckingAccountEntryDeserializer checkingAccountEntryDeserializer;

  @Mock
  private AccountingConfigProperties accountingConfigProperties;

  @BeforeEach
  public void init() {
    ReflectionTestUtils.setField(deserializerStrategy, "checkingAccountIdentifier",
        "CheckingAccount");
    ReflectionTestUtils.setField(deserializerStrategy, "creditCardIdentifier", "CreditCard");
  }

  @Test
  void readStatement_emptyLines_shouldThrowException() {
    List<String> lines = Collections.emptyList();
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
        deserializerStrategy.readStatement(lines));
    assertEquals("Invalid lines given, size must be greater than zero", e.getMessage());
  }

  @Test
  void readStatement_checkingAccountLines_shouldExecuteCreditCardStatementDeserializer() {
    List<String> lines = Collections.singletonList("CreditCard XYZ");
    deserializerStrategy.readStatement(lines);
    verify(creditCardEntryDeserializer).readStatement(lines);
  }

  @Test
  void readStatement_checkingAccountLines_shouldExecuteAccountStatementDeserializer() {
    List<String> lines = Collections.singletonList("CheckingAccount XYZ");
    deserializerStrategy.readStatement(lines);
    verify(checkingAccountEntryDeserializer).readStatement(lines);
  }
}
