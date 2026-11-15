package com.hrydzina.task3.parser;

import com.hrydzina.task3.config.PortConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PortConfigParser {
  private static final String KEY_VALUE_DELIMITER = "=";
  private static final int KEY_VALUE_PARTS = 2;
  private static final String STORAGE_CAPACITY = "STORAGE_CAPACITY";
  private static final String STORAGE_CONTAINERS = "STORAGE_CONTAINERS";
  private static final String BERTHS = "BERTHS";

  public PortConfig parse(List<String> lines) {
    Map<String, Integer> values = new HashMap<>();
    for (String line : lines) {
      String[] parts = line.split(KEY_VALUE_DELIMITER);
      if (parts.length != KEY_VALUE_PARTS) {
        throw new IllegalArgumentException("Expected KEY=VALUE, but was: " + line);
      }
      String key = parts[0].trim();
      if (!key.equals(STORAGE_CAPACITY) && !key.equals(STORAGE_CONTAINERS) && !key.equals(BERTHS)) {
        throw new IllegalArgumentException("Unknown parameter: " + key);
      }
      if (values.containsKey(key)) {
        throw new IllegalArgumentException("Duplicate parameter: " + key);
      }
      values.put(key, parseNumber(parts[1], line));
    }
    return new PortConfig(getRequired(values, STORAGE_CAPACITY),
            getRequired(values, STORAGE_CONTAINERS),
            getRequired(values, BERTHS));
  }

  private int getRequired(Map<String, Integer> values, String key) {
    Integer value = values.get(key);
    if (value == null) {
      throw new IllegalArgumentException("Missing required parameter: " + key);
    }
    return value;
  }

  private int parseNumber(String value, String line) {
    String trimmed = value.trim();
    if (trimmed.isEmpty()) {
      throw new IllegalArgumentException("Empty value in line: " + line);
    }
    try {
      return Integer.parseInt(trimmed);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Not a number '" + trimmed + "' in line: " + line, e);
    }
  }
}

