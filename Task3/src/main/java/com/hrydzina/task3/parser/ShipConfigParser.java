package com.hrydzina.task3.parser;

import com.hrydzina.task3.config.ShipConfig;

import java.util.ArrayList;
import java.util.List;

public class ShipConfigParser {

  private static final String DELIMITER = ",";
  private static final int FIELD_COUNT = 5;
  private static final int ID_INDEX = 0;
  private static final int CAPACITY_INDEX = 1;
  private static final int CONTAINERS_INDEX = 2;
  private static final int UNLOAD_COUNT_INDEX = 3;
  private static final int LOAD_COUNT_INDEX = 4;

  public List<ShipConfig> parse(List<String> lines) {
    List<ShipConfig> ships = new ArrayList<>();
    for (String line : lines) {
      ships.add(parseShip(line));
    }
    return ships;
  }

  private ShipConfig parseShip(String line) {
    String[] values = line.split(DELIMITER, -1);
    if (values.length != FIELD_COUNT) {
      throw new IllegalArgumentException("Expected " + FIELD_COUNT + " fields, but was "
              + values.length + " in line: " + line);
    }
    int id = parseNumber(values[ID_INDEX], line);
    int capacity = parseNumber(values[CAPACITY_INDEX], line);
    int containers = parseNumber(values[CONTAINERS_INDEX], line);
    int unloadCount = parseNumber(values[UNLOAD_COUNT_INDEX], line);
    int loadCount = parseNumber(values[LOAD_COUNT_INDEX], line);
    return new ShipConfig(id, capacity, containers, unloadCount, loadCount);
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

