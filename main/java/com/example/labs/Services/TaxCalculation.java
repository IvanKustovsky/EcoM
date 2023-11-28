package com.example.labs.Services;

import java.util.HashMap;

public class TaxCalculation {

    private static final double TAX_RATE_FOR_THE_FIRST_DANGER_CLASS = 18413.24;
    private static final double TAX_RATE_FOR_THE_SECOND_DANGER_CLASS = 4216.92;
    private static final double TAX_RATE_FOR_THE_THIRD_DANGER_CLASS = 628.32;
    private static final double TAX_RATE_FOR_THE_FORTH_DANGER_CLASS = 145.50;
    public static double defineTaxRate(double tax_rate, int danger_class){
        if(tax_rate != 0){
            return tax_rate;
        }
        HashMap<Integer,Double> taxRatesPerTon = new HashMap<>();
        taxRatesPerTon.put(1,TAX_RATE_FOR_THE_FIRST_DANGER_CLASS);
        taxRatesPerTon.put(2,TAX_RATE_FOR_THE_SECOND_DANGER_CLASS);
        taxRatesPerTon.put(3,TAX_RATE_FOR_THE_THIRD_DANGER_CLASS);
        taxRatesPerTon.put(4,TAX_RATE_FOR_THE_FORTH_DANGER_CLASS);
        return taxRatesPerTon.get(danger_class);
    }
    public static double calcTaxSum(double pollutionValue, double taxRate){
        return pollutionValue * taxRate;
    }
}
