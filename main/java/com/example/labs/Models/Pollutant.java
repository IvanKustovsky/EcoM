package com.example.labs.Models;

public class Pollutant {
    int code;
    String name;
    double gdk,mass_consumption;

    public Pollutant(int code, String name, double gdk, double mass_consumption) {
        this.code = code;
        this.name = name;
        this.gdk = gdk;
        this.mass_consumption = mass_consumption;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getGdk() {
        return gdk;
    }

    public void setGdk(double gdk) {
        this.gdk = gdk;
    }

    public double getMass_consumption() {
        return mass_consumption;
    }

    public void setMass_consumption(double mass_consumption) {
        this.mass_consumption = mass_consumption;
    }
}
