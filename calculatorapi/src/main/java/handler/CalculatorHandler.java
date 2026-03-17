package handler;

public class CalculatorHandler {

    public double handle(double a, double b, String operation) {

        switch(operation) {

            case "add":
                return a + b;

            case "sub":
                return a - b;

            case "mult":
                return a * b;

            case "divide":

                if(b == 0)
                    throw new ArithmeticException("Division by zero is not allowed");

                return a / b;

            default:
                throw new IllegalArgumentException(
                        "Unknown operation: " + operation);
        }
    }
}