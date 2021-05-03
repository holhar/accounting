package de.holhar.accounting.api;

import de.holhar.accounting.config.AppProperties;
import de.holhar.accounting.service.AccountingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/report/create")
public class AccountingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountingController.class);

    private final AccountingService accountingService;
    private final String csvDirString;

    @Autowired
    public AccountingController(AccountingService accountingService, AppProperties appProperties) {
        this.accountingService = accountingService;
        this.csvDirString = appProperties.getCsvPath();
    }

    @GetMapping
    public void createReport() {
        try {
            Path csvPath = Paths.get(csvDirString).toRealPath();
            accountingService.createReport(csvPath);
        } catch (IOException e) {
            LOGGER.error("Could not create report", e);
        }
    }
}
