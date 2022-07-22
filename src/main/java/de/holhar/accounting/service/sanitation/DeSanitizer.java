package de.holhar.accounting.service.sanitation;

import java.nio.file.Path;
import java.util.List;

public interface DeSanitizer {

  List<String> deSanitize(Path path);
}
