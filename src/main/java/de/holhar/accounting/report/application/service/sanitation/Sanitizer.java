package de.holhar.accounting.report.application.service.sanitation;

import java.nio.file.Path;
import java.util.List;

public interface Sanitizer {

  List<String> sanitize(Path path);
}
