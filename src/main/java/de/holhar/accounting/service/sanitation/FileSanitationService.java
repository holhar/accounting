package de.holhar.accounting.service.sanitation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class FileSanitationService implements SanitationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileSanitationService.class);

  @Override
  public List<String> cleanUp(Path path) {
    try {
      return Files.readAllLines(path, StandardCharsets.ISO_8859_1)
          .stream()
          .map(line -> new String(line.getBytes(StandardCharsets.ISO_8859_1),
              StandardCharsets.UTF_8))
          .map(line -> line.replace("\"", ""))
          .map(line -> line.replaceAll("\\s\\s+", " "))
          .map(line -> line.replaceAll("^;;+$", ";"))
          .map(line -> line.replaceAll(";$", ""))
          .filter(StringUtils::hasText)
          .collect(Collectors.toList());
    } catch (IOException e) {
      LOGGER.error("Could not sanitize file under path '{}'", path, e);
      return Collections.emptyList();
    }
  }
}
