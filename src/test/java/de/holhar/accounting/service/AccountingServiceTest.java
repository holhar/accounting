package de.holhar.accounting.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import de.holhar.accounting.repository.MonthlyReportRepository;
import de.holhar.accounting.service.deserialization.Deserializer;
import de.holhar.accounting.service.report.ReportManager;
import de.holhar.accounting.service.sanitation.Sanitizer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountingServiceTest {

  @InjectMocks
  private AccountStatementService accountStatementService;

  @Mock
  private Sanitizer sanitizer;

  @Mock
  private Deserializer deserializer;

  @Mock
  private ReportManager reportManager;

  @Mock
  private MonthlyReportRepository repository;

//  @Test
//  void createReport() throws IOException {
//    List<String> accountStatementLines = Collections.singletonList("AccountStatement");
//
//    AccountStatement accountStatement = new AccountStatement(
//        "CHECKING_ACCOUNT_ID",
//        AccountStatement.Type.CHECKING_ACCOUNT,
//        LocalDate.of(2021, Month.NOVEMBER, 4),
//        LocalDate.of(2021, Month.DECEMBER, 3),
//        new Balance(new BigDecimal("10000.01"), LocalDate.of(2021, Month.NOVEMBER, 4)),
//        Collections.singletonList(
//            TestUtils.getCheckingAccountEntryAmountAndClientOnly("-10.00", "foobar",
//                EntryType.FOOD_AND_DRUGSTORE)));
//
//    MonthlyReport monthlyReport = new MonthlyReport(
//        "2021_11_CHECKING_ACCOUNT_STATEMENT",
//        LocalDate.of(2021, Month.NOVEMBER, 1)
//    );
//    monthlyReport.setIncome(new BigDecimal("4321.23"));
//    monthlyReport.setExpenditure(new BigDecimal("-1834.34"));
//
//    when(sanitizer.cleanUp(any(Path.class))).thenReturn(accountStatementLines);
//    when(deserializer.readStatement(accountStatementLines)).thenReturn(accountStatement);
//    when(reportManager.createMonthlyReport(any(LocalDate.class), anySet())).thenReturn(
//        monthlyReport);
//
//    accountStatementService.createReport(Paths.get("src/test/resources/accounting/unprocessed/"));
//
//    verify(sanitizer, times(2)).cleanUp(any(Path.class));
//    verify(deserializer, times(2)).readStatement(accountStatementLines);
//    verify(reportManager).createMonthlyReport(any(LocalDate.class), anySet());
//    verify(repository).save(monthlyReport);
//  }
}
