package de.holhar.accounting;

import static org.mockito.Mockito.mock;

import de.holhar.accounting.adapter.CliAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TestConfig {

  @Bean
  @Primary
  public CliAdapter cliAdapter() {
    return mock(CliAdapter.class);
  }
}
