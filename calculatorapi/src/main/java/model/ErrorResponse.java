package model;

public class ErrorResponse {

    private int statusCode;
    private String status;
    private String error;
    private String message;

    public ErrorResponse(int statusCode,
                         String error,
                         String message) {

        this.statusCode = statusCode;
        this.status = "error";
        this.error = error;
        this.message = message;
    }
}