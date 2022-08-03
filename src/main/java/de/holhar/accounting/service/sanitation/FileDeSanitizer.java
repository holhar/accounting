package de.holhar.accounting.service.sanitation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class FileDeSanitizer implements DeSanitizer {

  @Override
  public List<String> deSanitize(Path path) {
    try {
      return Files.readAllLines(path, StandardCharsets.ISO_8859_1).stream().sequential()
          .map(line -> new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.ISO_8859_1))
          .map(line -> line.replaceAll("^;;+$", ""))
          .map(line -> line.replace(";", "\";\""))
          .map(line -> {
            if (!line.isEmpty() && !line.isBlank()) {
              return "\"" + line + "\";";
            }
            return line;
          })
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new RuntimeException("Failed to de-sanitize file", e);
    }
  }
}
