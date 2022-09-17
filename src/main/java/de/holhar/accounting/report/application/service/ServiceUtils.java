package de.holhar.accounting.report.application.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServiceUtils {

  private ServiceUtils() {
    throw new UnsupportedOperationException("Utils class - do not instantiate");
  }

  public static Path getValidPath(String pathString) {
    Path path = Paths.get(pathString);
    if (!Files.isDirectory(path) || !path.isAbsolute()) {
      var errorMsg = String.format("path from String '%s' must be absolute and must be a directory", pathString);
      throw new IllegalArgumentException(errorMsg);
    }
    return path;
  }
}
