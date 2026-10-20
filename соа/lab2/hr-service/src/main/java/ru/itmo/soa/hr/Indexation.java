package ru.itmo.soa.hr;

import java.math.BigDecimal;
import java.math.RoundingMode;

final class Indexation {
    private static final BigDecimal MIN_ROUNDABLE = new BigDecimal("0.5");
    private static final BigDecimal MAX_EXCLUSIVE = new BigDecimal("2147483647.5");

    private Indexation() {}

    static long id(String value) {
        try {
            if (!value.matches("[0-9]+")) {
                throw new NumberFormatException();
            }
            long id = Long.parseLong(value);
            if (id <= 0) {
                throw new NumberFormatException();
            }
            return id;
        } catch (NumberFormatException e) {
            throw new ApiException(400, "Invalid identifier");
        }
    }

    static BigDecimal coefficient(String value) {
        try {
            BigDecimal coefficient = new BigDecimal(value);
            if (coefficient.signum() <= 0) {
                throw new NumberFormatException();
            }
            return coefficient;
        } catch (NumberFormatException e) {
            throw new ApiException(400, "Invalid coefficient");
        }
    }

    static int salary(int salary, BigDecimal coefficient) {
        try {
            BigDecimal result = BigDecimal.valueOf(salary).multiply(coefficient);
            // Check salary limits before rounding.
            if (result.compareTo(MIN_ROUNDABLE) < 0 || result.compareTo(MAX_EXCLUSIVE) >= 0) {
                throw new ArithmeticException();
            }
            return result.setScale(0, RoundingMode.HALF_UP).intValueExact();
        } catch (ArithmeticException e) {
            throw new ApiException(422, "Indexed salary must be between 1 and 2147483647");
        }
    }
}
