package de.holhar.accounting.report.application.port.in;

import java.io.IOException;

public interface DownloadMonthlyReportsCsvUseCase {
  String downloadCsvReport() throws IOException;
}
