package com.example.labs.Services;
public class Calculations {
    private static final double Tout = 8;
    private static final double Tin = 16;
    private static final double V_out = 1.4;
    private static final double V_in = 0.63;
    private static final double EF = 350;
    private static final double ED = 30;
    private static final double BW = 70;
    private static final double AT = 70;
    private static final double MinWage = 6700;
    private final static double K_population = 1.55;
    private final static double Kf = 1.25;
    private final static int Time = 365*24;
    private final static double Kzi = 1;

    public static double calcCompensation(double pollution_value, double mass_consumption,double gdk){
        double Kt = K_population * Kf;
        double Ai = (gdk > 1) ? (10/gdk) : (1/gdk);
        double convertedPollutionValue = pollution_value * Math.pow(10,6) / (365*24*60*60); // т/рік -> г/с
        double convertedMassConsumption = mass_consumption / 3600; // г/год -> г/с
        if(convertedPollutionValue - convertedMassConsumption <= 0) return 0;
        return CalcPollutionMass(convertedPollutionValue,convertedMassConsumption,Time) * 1.1 * MinWage * Ai * Kt * Kzi;
    }

    private static double CalcPollutionMass(double pollution_value, double mass_consumption, int t){
        return 3.6 * Math.pow(10,-3) * (pollution_value - mass_consumption) * t;
    }

    public static double CalcCR(double concentration, double sf){
        long rounded = Math.round(CalcLADD(concentration) * sf * 1_000_00000);
        return rounded / 1_000_00000.0;
    }

    public static double CalcHq(double concentration, double rfc){
        long rounded = Math.round((concentration/rfc)*1_000_00000);
        return rounded / 1_000_00000.0;
    }
    private static double CalcLADD(double concentration){
        return ((concentration * Tout * V_out) + (concentration * Tin * V_in)) * EF * ED /(BW * AT * 365);
    }

}
