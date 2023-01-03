package de.holhar.accounting.report.application.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.holhar.accounting.TestUtils;
import de.holhar.accounting.report.application.port.out.SaveStatementsPort;
import de.holhar.accounting.report.application.service.deserialization.Deserializer;
import de.holhar.accounting.report.application.service.sanitation.Sanitizer;
import de.holhar.accounting.report.domain.CheckingAccountEntry;
import de.holhar.accounting.report.domain.CreditCardEntry;
import de.holhar.accounting.report.domain.Entry;
import de.holhar.accounting.report.domain.EntryType;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountStatementServiceTest {

  @InjectMocks
  private AccountStatementService accountStatementService;

  @Mock
  private Sanitizer sanitizer;

  @Mock
  private Deserializer deserializer;

  @Mock
  private SaveStatementsPort saveStatementsPort;

  @Captor
  private ArgumentCaptor<List<CheckingAccountEntry>> checkingAccountEntriesCaptor;

  @Captor
  private ArgumentCaptor<List<CreditCardEntry>> creditCardEntriesCaptor;

  @Test
  void createReport() {
    // Given
    Path accPath = Paths.get("src/test/resources/accounting/unprocessed/acc_202001");
    Path crePath = Paths.get("src/test/resources/accounting/unprocessed/cre_202001");
    List<Path> paths = Arrays.asList(accPath, crePath);

    List<String> checkAccountStatementLines = Collections.singletonList("AccountStatement");
    List<String> creditCardStatementLines = Collections.singletonList("CreditCardStatement");

    when(sanitizer.sanitize(accPath)).thenReturn(checkAccountStatementLines);
    when(sanitizer.sanitize(crePath)).thenReturn(creditCardStatementLines);

    CheckingAccountEntry checkingAccountEntry = TestUtils.getCheckAccEntry(-1000L, "foo", EntryType.LEISURE_ACTIVITIES_AND_PURCHASES);
    Stream<Entry> checkingAccountEntries = Stream.of(checkingAccountEntry);
    CreditCardEntry creditCardEntry = TestUtils.getCreditCardEntry(-100L, EntryType.FOOD_AND_DRUGSTORE);
    Stream<Entry> creditCardEntries = Stream.of(creditCardEntry);

    when(deserializer.readStatement(checkAccountStatementLines)).thenReturn(checkingAccountEntries);
    when(deserializer.readStatement(creditCardStatementLines)).thenReturn(creditCardEntries);

    // When
    accountStatementService.importStatements(paths);

    // Then
    verify(saveStatementsPort).saveAllCheckingAccountEntries(checkingAccountEntriesCaptor.capture());
    List<CheckingAccountEntry> actualCheckingAccountEntries = checkingAccountEntriesCaptor.getValue();
    assertThat(actualCheckingAccountEntries.size()).isEqualTo(1);
    assertThat(actualCheckingAccountEntries.get(0)).isEqualTo(checkingAccountEntry);

    verify(saveStatementsPort).saveAllCreditCardEntries(creditCardEntriesCaptor.capture());
    List<CreditCardEntry> actualCreditCardEntries = creditCardEntriesCaptor.getValue();
    assertThat(actualCreditCardEntries.size()).isEqualTo(1);
    assertThat(actualCreditCardEntries.get(0)).isEqualTo(creditCardEntry);
  }
}
