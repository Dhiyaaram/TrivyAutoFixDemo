package com.examples;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet implementation class addnum
 */
@WebServlet("/addnum")
public class addnum extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public addnum() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		
		
		String a = request.getParameter("a");
		String b = request.getParameter("b");
		long num1 , num2;
		
		if ((a == null && b==null) || a == null ||  b==null ) {
			response.setStatus(400);
			response.setContentType("text/plain");
			response.getWriter().write("Invalid inputs ...");
			return;
		}
		if (a.isEmpty() && b.isEmpty()) {
			response.setStatus(400);
			response.setContentType("text/plain");
			response.getWriter().write("Addtion is not possible with empty inputs ...");
			return;
			
		}
		if(a.isEmpty() || b.isEmpty()) {
			response.setStatus(400);
			response.setContentType("text/plain");
			response.getWriter().write("Addtion is not possible  even with one empty input ...");
			return;
		}
		try {
			 num1 = Long.parseLong(a);
			 num2 = Long.parseLong(b);
		}catch(NumberFormatException e ) {
			response.setStatus(400);
			response.setContentType("text/plain");
			response.getWriter().write("Strings are not numbers ....Its addition of numbers , Not concatenation of strings ....");
			return;
		}catch(Exception e) {
			response.setStatus(400);
			response.setContentType("text/plain");
			response.getWriter().write("Invalid Input");
			return;
		}
		
		
		
		response.getWriter().write("Result after adding : " +(num1+num2));
		response.setStatus(200);
		response.setContentType("application/json");
		response.getWriter().write("\n");
		String json = "{ \"a\": " + num1 +
	              ", \"b\": " + num2 +
	              ", \"sum\": " + (num1+num2) + " }";

	    response.getWriter().write(json);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	/*protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}*/

}
