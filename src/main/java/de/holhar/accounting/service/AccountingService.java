package de.holhar.accounting.service;

import de.holhar.accounting.service.deserialization.Deserializer;
import de.holhar.accounting.service.sanitation.SanitationService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class AccountingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountingService.class);

    private final SanitationService sanitationService;
    private final Deserializer accountStatementDeserializer;

    public AccountingService(SanitationService sanitationService,
                             Deserializer accountStatementDeserializer) throws IOException {
        this.sanitationService = sanitationService;
        this.accountStatementDeserializer = accountStatementDeserializer;
        read();
    }

    public void read() throws IOException {
//        Files.list(Paths.get("src/main/resources/test"))
//                .filter(path -> path.endsWith("acc_202001.csv"))
//                .forEach(path -> {
//                    LOGGER.info(path.toString());
//                    try {
//                        List<String> lines = Files.readAllLines(path, StandardCharsets.ISO_8859_1);
//                        lines.forEach(line -> LOGGER.info(line));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                });

//        FileReader fileReader = new FileReader("src/main/resources/test/nov.csv");
//        CSVParser csvParser = CSVFormat.EXCEL.withHeader().parse(fileReader);
//        csvParser.getRecords().forEach(record -> {
//            String auftraggeber = record.get("Auftraggeber / Begünstigter");
//            LOGGER.info(auftraggeber);
//        });
    }
}
