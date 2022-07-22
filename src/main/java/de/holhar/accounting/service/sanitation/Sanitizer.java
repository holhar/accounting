package de.holhar.accounting.service.sanitation;

import java.nio.file.Path;
import java.util.List;

public interface Sanitizer {

  List<String> sanitize(Path path);
}
