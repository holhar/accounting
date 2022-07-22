package de.holhar.accounting.service.deserialization;

import de.holhar.accounting.domain.Entry;
import java.util.List;
import java.util.stream.Stream;

public interface Deserializer {

  Stream<Entry> readStatement(List<String> lines);
}
