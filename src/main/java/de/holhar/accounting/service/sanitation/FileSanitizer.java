package de.holhar.accounting.service.sanitation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class FileSanitizer implements Sanitizer {

  @Override
  public List<String> sanitize(Path path) {
    try {
      return Files.readAllLines(path, StandardCharsets.ISO_8859_1)
          .stream()
          .map(line -> new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.ISO_8859_1))
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
