package de.holhar.accounting.domain;

import java.util.Arrays;

public enum EntryType {
  INCOME("0"),
  ACCOMMODATION_AND_COMMUNICATION("1"),
  FOOD_AND_DRUGSTORE("2"),
  HEALTH_AND_FITNESS("3"),
  TRANSPORTATION_AND_TRAVELLING("4"),
  LEISURE_ACTIVITIES_AND_PURCHASES("5"),
  MISCELLANEOUS("6"),
  INNER_ACCOUNT_TRANSFER("10"),
  DEPOT_TRANSFER("11");

  private final String value;

  EntryType(String value) {
    this.value = value;
  }

  public static EntryType fromValue(String value) {
    return Arrays.stream(EntryType.values()).sequential()
        .filter(type -> type.getValue().equals(value))
        .findAny()
        .orElseThrow(
            () -> new IllegalArgumentException("Invalid type param '" + value + "' provided"));
  }

  public String getValue() {
    return value;
  }
}
