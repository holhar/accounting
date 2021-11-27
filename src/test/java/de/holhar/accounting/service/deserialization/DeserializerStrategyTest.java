package de.holhar.accounting.service.deserialization;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeserializerStrategyTest {

    @InjectMocks
    private DeserializerStrategy deserializerStrategy;

    @Mock
    private CreditCardStatementDeserializer creditCardStatementDeserializer;

    @Mock
    private AccountStatementDeserializer accountStatementDeserializer;

    @Test
    void readStatement_emptyLines_shouldThrowException() {
        List<String> lines = Collections.emptyList();
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                deserializerStrategy.readStatement(lines));
        assertEquals("Invalid lines given, size must be greater than zero", e.getMessage());
    }

    @Test
    void readStatement_checkingAccountLines_shouldExecuteCreditCardStatementDeserializer() {
        List<String> lines = Collections.singletonList("Kreditkarte XYZ");
        deserializerStrategy.readStatement(lines);
        verify(creditCardStatementDeserializer).readStatement(lines);
    }

    @Test
    void readStatement_checkingAccountLines_shouldExecuteAccountStatementDeserializer() {
        List<String> lines = Collections.singletonList("Kontonummer XYZ");
        deserializerStrategy.readStatement(lines);
        verify(accountStatementDeserializer).readStatement(lines);
    }
}