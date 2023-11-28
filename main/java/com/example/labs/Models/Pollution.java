package com.example.labs.Models;

public class Pollution {
    private int id_pollution,year;
    private String nameObject,name;
    private double value_pollution,concentration, hq, cr, compensation;

    public Pollution(int id_pollution, String nameObject, String name, double value_pollution,
                     double concentration, double hq, double cr, double compensation, int year) {
        this.id_pollution = id_pollution;
        this.year = year;
        this.nameObject = nameObject;
        this.name = name;
        this.value_pollution = value_pollution;
        this.concentration = concentration;
        this.hq = hq;
        this.cr = cr;
        this.compensation = compensation;
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

    public double getConcentration() {
        return concentration;
    }

    public void setConcentration(double concentration) {
        this.concentration = concentration;
    }

    public double getHq() {
        return hq;
    }

    public void setHq(double hq) {
        this.hq = hq;
    }

    public double getCr() {
        return cr;
    }

    public void setCr(double cr) {
        this.cr = cr;
    }

    public double getCompensation() {
        return compensation;
    }

    public void setCompensation(double compensation) {
        this.compensation = compensation;
    }

}
