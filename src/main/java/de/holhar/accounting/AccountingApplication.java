package de.holhar.accounting;

import de.holhar.accounting.service.AccountingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication
public class AccountingApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(AccountingApplication.class);

	private static final String READ_ACCOUNT_STATEMENT_ARG = "readStatementsEnabled";
	private static final String CSV_PATH_ARG = "csvPath";

	private static Map<String, Object> argumentsMap = new HashMap<>();

	private final AccountingService accountingService;

	@Autowired
	public AccountingApplication(AccountingService accountingService) {
		this.accountingService = accountingService;
	}

	public static void main(String[] args) {
		parseArgs(Arrays.asList(args));
		SpringApplication.run(AccountingApplication.class, args);
	}

	@PostConstruct
	private void runService() {
		if (argumentsMap.get(READ_ACCOUNT_STATEMENT_ARG) != null
				&& (Boolean)argumentsMap.get(READ_ACCOUNT_STATEMENT_ARG)) {
			try {
				Path csvPath = Paths.get((String)argumentsMap.get(CSV_PATH_ARG)).toRealPath();
				accountingService.read(csvPath);
			} catch (IOException e) {
				LOGGER.error("Exiting program - no valid csv path provided: {}", e.getMessage());
			}
		}
	}

	private static void parseArgs(List<String> args) {
		argumentsMap = args.stream()
				.filter(arg -> arg.contains("="))
				.map(arg -> Arrays.asList(arg.split("=")))
				.collect(Collectors.toMap(argEntry -> argEntry.get(0),
						argEntry -> parseArgValue(argEntry), (x1, x2) -> x2));
	}

	private static Object parseArgValue(List<String> argEntry) {
		if (argEntry.get(0).endsWith("Enabled")) {
			return Boolean.parseBoolean(argEntry.get(1));
		} else {
			return argEntry.get(1);
		}
	}
}
