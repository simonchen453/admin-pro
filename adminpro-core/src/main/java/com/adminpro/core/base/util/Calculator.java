package com.adminpro.core.base.util;

import java.math.BigDecimal;

public final class Calculator {

    /**
     * add (+)
     *
     * @param v1
     * @param v2
     * @return
     */
    public static double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    /**
     * subtract (-)
     *
     * @param v1
     * @param v2
     * @return
     */
    public static double sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    /**
     * multiply (*)
     *
     * @param v1
     * @param v2
     * @return
     */
    public static double mul(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    /**
     * divide (/)
     *
     * @param v1
     * @param v2
     * @return
     */
    public static double div(double v1, double v2) {
        return div(v1, v2, 2);
    }

    public static double div(double v1, double v2, int scale) {
        if (v2 == 0) {
            throw new IllegalArgumentException("The divided number cannot be zero.");
        }

        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private Calculator() {
    }
}
