package util;

public class Validator {

 
    public static void validateA(double a) {

        if (Double.isNaN(a)) {
            throw new IllegalArgumentException(
                "'a' is not a valid number");
        }
        if (Double.isInfinite(a)) {
            throw new IllegalArgumentException(
                "'a' cannot be Infinite");
        }
        if (a > 1_000_000_000) {
            throw new IllegalArgumentException(
                "'a' is too large. Max allowed: 1,000,000,000");
        }
        if (a < -1_000_000_000) {
            throw new IllegalArgumentException(
                "'a' is too small. Min allowed: -1,000,000,000");
        }
    }

 
    public static void validateB(double b) {

        if (Double.isNaN(b)) {
            throw new IllegalArgumentException(
                "'b' is not a valid number");
        }
        if (Double.isInfinite(b)) {
            throw new IllegalArgumentException(
                "'b' cannot be Infinite");
        }
        if (b > 1_000_000_000) {
            throw new IllegalArgumentException(
                "'b' is too large. Max allowed: 1,000,000,000");
        }
        if (b < -1_000_000_000) {
            throw new IllegalArgumentException(
                "'b' is too small. Min allowed: -1,000,000,000");
        }
    }

   
    public static String validateOperation(String operation) {

        if (operation == null || operation.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Operation cannot be null or empty");
        } 

        switch (operation.trim().toLowerCase()) {

            case "add":
            case "addition":
                return "add";

            case "sub":
            case "subtract":
            case "subtraction":
                return "sub";

            case "mul":
            case "multiply":
            case "multiplication":
                return "mult";

            case "div":
            case "divide":
            case "division":
                return "divide";

            default:
                throw new IllegalArgumentException(
                    "Invalid operation '" + operation +
                    "'. Allowed: add, sub, mul, div");
        }
    }
}