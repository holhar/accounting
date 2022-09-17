package de.holhar.accounting.report.application.service.deserialization;

import de.holhar.accounting.report.domain.Entry;
import java.util.List;
import java.util.stream.Stream;

public interface Deserializer {

  Stream<Entry> readStatement(List<String> lines);
}
