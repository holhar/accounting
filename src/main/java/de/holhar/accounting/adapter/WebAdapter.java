package de.holhar.accounting.adapter;

import de.holhar.accounting.domain.MonthlyReport;
import de.holhar.accounting.domain.MonthlyReportRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/reports")
public class WebAdapter {

  private final MonthlyReportRepository repository;

  @GetMapping
  public List<MonthlyReport> getMonthlyReports() {
    return repository.findAll();
  }
}
