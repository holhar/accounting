package de.holhar.accounting.service.sanitation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FileSanitationService implements SanitationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSanitationService.class);

    @Override
    public List<String> cleanUp(Path path) {
        try {
            return Files.readAllLines(path)
                    .stream()
                    .filter(StringUtils::hasText)
                    .map(line -> line.replaceAll("\"", ""))
                    .map(line -> line.replaceAll("\\s\\s+", " "))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            LOGGER.error("Could not sanitize file under path '{}'", path);
            return Collections.emptyList();
        }
    }
}
