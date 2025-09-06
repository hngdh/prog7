package common.objects;

import java.io.Serializable;
import java.util.Objects;

/**
 * The {@code House} class represents a house with attributes name, construction year and number of
 * lifts. It contains properties' getters and setters.
 */
public class House implements Serializable {
  private String name; // Поле не может быть null
  private Integer year; // Значение поля должно быть больше 0
  private Long numberOfLifts; // Значение поля должно быть больше 0

  public House() {}

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getYear() {
    return year.toString();
  }

  public void setYear(Integer year) {
    this.year = year;
  }

  public String getNumberOfLifts() {
    return numberOfLifts.toString();
  }

  public void setNumberOfLifts(Long numberOfLifts) {
    this.numberOfLifts = numberOfLifts;
  }

  @Override
  public String toString() {
    return "(" + name + ", " + year.toString() + ", " + numberOfLifts.toString() + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof House)) return false;
    return name.equals(((House) o).name)
        && year.equals(((House) o).year)
        && numberOfLifts.equals(((House) o).numberOfLifts);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, year, numberOfLifts);
  }
}
