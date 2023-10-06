package com.example.labs.Models;

public class Pollution {
    int id_pollution,year;
    String nameObject,location,name;
    double value_pollution;

    public Pollution(int id_pollution, String nameObject, String location, String name, double value_pollution,int year) {
        this.id_pollution = id_pollution;
        this.year = year;
        this.nameObject = nameObject;
        this.location = location;
        this.name = name;
        this.value_pollution = value_pollution;
    }

    public int getId_pollution() {
        return id_pollution;
    }

    public void setId_pollution(int id_pollution) {
        this.id_pollution = id_pollution;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getNameObject() {
        return nameObject;
    }

    public void setNameObject(String nameObject) {
        this.nameObject = nameObject;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue_pollution() {
        return value_pollution;
    }

    public void setValue_pollution(double value_pollution) {
        this.value_pollution = value_pollution;
    }
}
