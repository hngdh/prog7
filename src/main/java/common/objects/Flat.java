package common.objects;

import common.enums.Transport;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * The {@code Flat} class represents a flat with attributes such as ID, name, coordinates, ... and
 * contains getters and setters, as well as needed methods to print flat's information.
 */
public class Flat implements Serializable {
  private Integer
      id; // Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно
  // быть уникальным, Значение этого поля должно генерироваться автоматически
  private String name; // Поле не может быть null, Строка не может быть пустой
  private Coordinate coordinates; // Поле не может быть null
  private LocalDate
      creationDate; // Поле не может быть null, Значение этого поля должно генерироваться
  // автоматически
  private long area; // Максимальное значение поля: 700, Значение поля должно быть больше 0
  private int numberOfRooms; // Значение поля должно быть больше 0
  private Long livingSpace; // Значение поля должно быть больше 0
  private boolean centralHeating;
  private Transport transport; // Поле не может быть null
  private House house; // Поле может быть null
  private String creator;

  public Flat() {}

  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Coordinate getCoordinate() {
    return coordinates;
  }

  public void setCoordinate(Coordinate coordinates) {
    this.coordinates = coordinates;
  }

  public LocalDate getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(LocalDate creationDate) {
    this.creationDate = creationDate;
  }

  public long getArea() {
    return area;
  }

  public void setArea(long area) {
    this.area = area;
  }

  public int getNumberOfRooms() {
    return numberOfRooms;
  }

  public void setNumberOfRooms(int numberOfRooms) {
    this.numberOfRooms = numberOfRooms;
  }

  public Long getLivingSpace() {
    return livingSpace;
  }

  public void setLivingSpace(Long livingSpace) {
    this.livingSpace = livingSpace;
  }

  public boolean isCentralHeating() {
    return centralHeating;
  }

  public void setCentralHeating(boolean centralHeating) {
    this.centralHeating = centralHeating;
  }

  public Transport getTransport() {
    return transport;
  }

  public void setTransport(Transport transport) {
    this.transport = transport;
  }

  public House getHouse() {
    return house;
  }

  public void setHouse(House house) {
    this.house = house;
  }

  public List<String> getEverything() {
    List<String> props = new ArrayList<>();
    props.add("ID: " + this.id);
    props.add("Creator: " + this.creator);
    props.add("Name: " + this.name);
    props.add("Coordinate: " + this.coordinates.toString());
    props.add(
        "Creation date: " + this.creationDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
    props.add("Area: " + this.area);
    props.add("Number of rooms: " + this.numberOfRooms);
    props.add("Living space: " + this.livingSpace);
    props.add("Central heating: " + this.centralHeating);
    props.add("Transport: " + this.transport);
    if (house != null) {
      props.add("House's name: " + house.getName());
      props.add("Construction year: " + house.getYear());
      props.add("Number of lifts: " + house.getNumberOfLifts());
    } else {
      props.add("House: no information");
    }
    return props;
  }

  public List<String> getPropsHouse() {
    List<String> props = new ArrayList<>();
    if (house != null) {
      props.add("House's name: " + house.getName());
      props.add("Construction year: " + house.getYear());
      props.add("Number of lifts: " + house.getNumberOfLifts());
    } else {
      props.add("House: no information");
    }
    return props;
  }

  public List<StringBuilder> getAllFields() {
    List<StringBuilder> fields = new LinkedList<>();
    fields.add(new StringBuilder(this.name));
    fields.add(new StringBuilder(String.valueOf(this.coordinates.getX())));
    fields.add(new StringBuilder(String.valueOf(this.coordinates.getY())));
    fields.add(
        new StringBuilder(this.creationDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
    fields.add(new StringBuilder(String.valueOf(this.area)));
    fields.add(new StringBuilder(String.valueOf(this.numberOfRooms)));
    fields.add(new StringBuilder(String.valueOf(this.livingSpace)));
    fields.add(new StringBuilder(String.valueOf(this.centralHeating)));
    fields.add(new StringBuilder(String.valueOf(this.transport)));
    if (this.house != null) {
      fields.add(new StringBuilder(String.valueOf(this.house.getName())));
      fields.add(new StringBuilder(String.valueOf(this.house.getYear())));
      fields.add(new StringBuilder(String.valueOf(this.house.getNumberOfLifts())));
    } else {
      for (int i = 0; i < 3; i++) fields.add(new StringBuilder("null"));
    }
    return fields;
  }
}
