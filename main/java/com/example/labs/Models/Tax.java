package com.example.labs.Models;

public class Tax {

    private String nameObject, name;
    private int danger_class;
    private int year, pollution_id;
    private double value_pollution, rate, sum;

    public Tax(int pollution_id, String nameObject, String name, int danger_class, double value_pollution, double rate, double sum, int year) {
        this.pollution_id = pollution_id;
        this.nameObject = nameObject;
        this.name = name;
        this.danger_class = danger_class;
        this.year = year;
        this.value_pollution = value_pollution;
        this.rate = rate;
        this.sum = sum;
    }

    public int getPollution_id() {
        return pollution_id;
    }

    public void setPollution_id(int pollution_id) {
        this.pollution_id = pollution_id;
    }

    public String getNameObject() {
        return nameObject;
    }

    public void setNameObject(String nameObject) {
        this.nameObject = nameObject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDanger_class() {
        return danger_class;
    }

    public void setDanger_class(int danger_class) {
        this.danger_class = danger_class;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getValue_pollution() {
        return value_pollution;
    }

    public void setValue_pollution(double value_pollution) {
        this.value_pollution = value_pollution;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }
}
