package com.example.labs.Models;

public class Pollutant {
    private int code, danger_class;
    private String name;
    private double gdk,mass_consumption,rfc,sf,tax_rate;
    public Pollutant(int code, String name, double mass_consumption,double gdk, double rfc,
                     double sf, int danger_class, double tax_rate) {
        this.code = code;
        this.name = name;
        this.gdk = gdk;
        this.mass_consumption = mass_consumption;
        this.rfc = rfc;
        this.sf = sf;
        this.danger_class = danger_class;
        this.tax_rate = tax_rate;
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

    public double getRfc() {
        return rfc;
    }

    public void setRfc(double rfc) {
        this.rfc = rfc;
    }

    public double getSf() {
        return sf;
    }

    public void setSf(double sf) {
        this.sf = sf;
    }

    public int getDanger_class() {
        return danger_class;
    }

    public void setDanger_class(int danger_class) {
        this.danger_class = danger_class;
    }

    public double getTax_rate() {
        return tax_rate;
    }

    public void setTax_rate(double tax_rate) {
        this.tax_rate = tax_rate;
    }
}
