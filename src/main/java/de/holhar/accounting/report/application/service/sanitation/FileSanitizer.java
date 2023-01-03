package de.holhar.accounting.report.application.service.sanitation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;

public class FileSanitizer implements Sanitizer {

  @Override
  public List<String> sanitize(Path path) {
    try {
      return Files.readAllLines(path)
          .stream()
          .map(line -> line.replace("\"", ""))
          .map(line -> line.replaceAll("\\s\\s+", " "))
          .map(line -> line.replaceAll("^;;+$", ";"))
          .map(line -> line.replaceAll(";$", ""))
          .filter(StringUtils::hasText)
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new IllegalArgumentException("Could not sanitize file under path: " + path, e);
    }
  }
}
