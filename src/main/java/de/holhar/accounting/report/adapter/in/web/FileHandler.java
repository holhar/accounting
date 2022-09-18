package de.holhar.accounting.report.adapter.in.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileHandler {

  public List<Path> unpackZipFile(MultipartFile zipFile) throws IOException {
    // Save zipFile to temp
    File zip = File.createTempFile(UUID.randomUUID().toString(), "_temp");
    try (FileOutputStream outputStream = new FileOutputStream(zip)) {
      IOUtils.copy(zipFile.getInputStream(), outputStream);
    }

    List<Path> files = new ArrayList<>();
    try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zip))) {
      // List files in zip
      ZipEntry zipEntry = zis.getNextEntry();

      while (zipEntry != null) {
        if (!zipEntry.getName().endsWith(File.separator)) {
          File tempFile = File.createTempFile(UUID.randomUUID().toString(), "_temp_" + zipEntry.getName());
          try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            for (int c = zis.read(); c != -1; c = zis.read()) {
              fos.write(c);
            }
          }
          files.add(tempFile.toPath());
        }
        zipEntry = zis.getNextEntry();
      }
    }
    return files;
  }
}
