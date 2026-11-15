package com.hrydzina.task3.reader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ConfigReader {

  private static final Logger logger = LogManager.getLogger(ConfigReader.class);
  private static final String COMMENT_PREFIX = "#";

  public List<String> readLines(String filePath) throws IOException {
    List<String> lines = new ArrayList<>();
    try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (!line.isEmpty() && !line.startsWith(COMMENT_PREFIX)) {
          lines.add(line);
        }
      }
    }
    logger.info("{} lines were read from {}", lines.size(), filePath);
    return lines;
  }
}

