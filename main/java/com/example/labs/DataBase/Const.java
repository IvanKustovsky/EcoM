package com.example.labs.DataBase;

public class Const {
    //Tables
    public static final String OBJECT_TABLE = "object";
    public static final String POLLUTION_TABLE = "pollution";
    public static final String POLLUTANT_TABLE = "pollutant";
    public static final String TAX_TABLE = "tax";
    //Object
    public static final String OBJECT_ID = "id";
    public static final String OBJECT_NAME = "nameObject";
    public static final String OBJECT_LOCATION = "location";
    public static final String OBJECT_DESCRIPTION = "description";
    //Pollutant
    public static final String POLLUTANT_CODE = "code";
    public static final String POLLUTANT_NAME = "name";
    public static final String POLLUTANT_GDK = "gdk";
    public static final String POLLUTANT_MASS_CONSUMPTION = "mass_consumption";
    public static final String POLLUTANT_RFC = "rfc";
    public static final String POLLUTANT_SF = "sf";
    public static final String POLLUTANT_TAX_RATE = "tax_rate";
    public static final String POLLUTANT_CLASS_DANGER = "danger_class";
    //Pollution
    public static final String POLLUTION_ID = "id_pollution";
    public static final String POLLUTION_OBJECT_ID = "id_object";
    public static final String POLLUTION_CODE_POLLUTANT = "code_pollutant";
    public static final String POLLUTION_VALUE = "value_pollution";
    public static final String POLLUTION_YEAR = "year";
    public static final String POLLUTION_CONCENTRATION = "concentration";
    public static final String POLLUTION_HQ = "hq";
    public static final String POLLUTION_CR = "cr";
    public static final String POLLUTION_COMPENSATION = "compensation";

    //Tax
    public static final String TAX_POLLUTION_ID = "pollution_id";
    public static final String TAX_RATE = "rate";
    public static final String TAX_SUM= "sum";

}
