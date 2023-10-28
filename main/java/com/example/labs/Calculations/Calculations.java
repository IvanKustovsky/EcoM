package com.example.labs.Calculations;
public class Calculations {
    private static final double Tout = 8;
    private static final double Tin = 16;
    private static final double V_out = 1.4;
    private static final double Vin = 0.63;
    private static final double EF = 350;
    private static final double ED = 30;
    private static final double BW = 70;
    private static final double AT = 70;

    public static double CalcCR(double concentration, double sf){
        long rounded = Math.round(CalcLADD(concentration) * sf * 1_000_00000);
        return rounded / 1_000_00000.0;
    }

    public static double CalcHq(double concentration, double rfc){
        long rounded = Math.round((concentration/rfc)*1_000_00000);
        return rounded / 1_000_00000.0;
    }
    private static double CalcLADD(double concentration){
        return ((concentration * Tout * V_out) + (concentration * Tin * Vin)) * EF * ED /(BW * AT * 365);
    }

}
