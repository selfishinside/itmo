package com.example.project.utils;

public class Validator {
    public static boolean validateArgs(float x, float y, float r) {
        return validateX(x) && validateY(y) && validateR(r);
    }

    private static boolean validateX(float x) {
        return (x >= -5 && x <= 3);
    }
    private static boolean validateY(float y) {
        return (y >= -3 && y <= 5);
    }
    private static boolean validateR(float r) {
        return (r >= 1 && r <= 4);
    }
}
