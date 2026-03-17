package controller;

import handler.CalculatorHandler;
import model.CalculatorRequest;
import model.CalculatorResponse;
import model.ErrorResponse;
import util.JsonUtil;
import util.Validator;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

//@WebServlet("/api/v1/calculator")
public class CalculatorController extends HttpServlet {

    private final CalculatorHandler handler = new CalculatorHandler();

   
    private Set<String> allowedKeys;

   
    @Override
    public void init(ServletConfig config) {

       
        ServletContext context = config.getServletContext();


        String param = context.getInitParameter("allowedKeys");
        

        
        allowedKeys = new HashSet<>(
                Arrays.asList(param.split(","))
        );
    
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws IOException {

    
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BufferedReader reader = request.getReader();
        StringBuilder body = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }
        String bodyStr = body.toString().trim();


        if (bodyStr.isEmpty()) {
            response.setStatus(400);
            response.getWriter().write(
                JsonUtil.toJson(new ErrorResponse(
                    400,
                    "Bad Request",
                    "Request body cannot be empty"
                ))
            );
            return;
        }

        try {

         
            CalculatorRequest calcRequest =
                    JsonUtil.fromJson(
                            bodyStr,
                            CalculatorRequest.class,
                            allowedKeys         
                    );

         
            Validator.validateA(calcRequest.getA());

          
            Validator.validateB(calcRequest.getB());

            
            String operation =
                    Validator.validateOperation(
                            calcRequest.getOperation());

            
            double result = handler.handle(
                    calcRequest.getA(),
                    calcRequest.getB(),
                    operation
            );

          
            response.setStatus(200);
            CalculatorResponse calcResponse =
                    new CalculatorResponse(
                            calcRequest.getA(),
                            calcRequest.getB(),
                            calcRequest.getOperation(),
                            result
                    );
            response.getWriter().write(
                    JsonUtil.toJson(calcResponse));

        } catch (Exception e) {

            
            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(
                JsonUtil.toJson(new ErrorResponse(
                    400,
                    "Bad Request",
                    e.getMessage()
                ))
            );
        }
    }
}







