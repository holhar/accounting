package de.holhar.accounting.service.sanitation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class FileSanitationServiceTest {

    @Autowired
    private SanitationService fileSanitationService;

    @Test
    public void cleanUp_accountingStatement_Succeeds() throws IOException {
        // Given
        Path unprocessedFile = Paths.get("src/test/resources/accounting/unprocessed/acc_202001.csv");

        // When
        List<String> resultLines = fileSanitationService.cleanUp(unprocessedFile);

        // Then
        Path processedFile = Paths.get("src/test/resources/accounting/sanitized/acc_202001.csv");
        List<String> sanitizedLines = Files.readAllLines(processedFile);
        assertEquals(sanitizedLines, resultLines);
    }

    @Test
    public void cleanUp_creditCardStatement_Succeeds() throws IOException {
        // Given
        Path unprocessedFile = Paths.get("src/test/resources/accounting/unprocessed/cre_202001.csv");

        // When
        List<String> resultLines = fileSanitationService.cleanUp(unprocessedFile);

        // Then
        Path processedFile = Paths.get("src/test/resources/accounting/sanitized/cre_202001.csv");
        List<String> sanitizedLines = Files.readAllLines(processedFile);
        assertEquals(sanitizedLines, resultLines);
    }

}