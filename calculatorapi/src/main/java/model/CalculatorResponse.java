package model;

public class CalculatorResponse {

    private int statusCode;
    private String status;
    private double a;
    private double b;
    private String operation;
    private double result;

    public CalculatorResponse(double a, double b,
                              String operation, double result) {

        this.statusCode = 200;
        this.status = "success";
        this.a = a;
        this.b = b;
        this.operation = operation;
        this.result = result;
    }
}