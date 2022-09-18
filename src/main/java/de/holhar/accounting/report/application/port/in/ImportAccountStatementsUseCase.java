package de.holhar.accounting.report.application.port.in;

import java.nio.file.Path;
import java.util.List;

public interface ImportAccountStatementsUseCase {
  void importStatements(List<Path> files);
}
