package com.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.nio.file.*;

public class AutoFixerServlet extends HttpServlet {

	private static final String GROQ_API_KEY = System.getenv("GROQ_API_KEY");
	//private static final String GROQ_API_KEY = "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";
    private static final String PROJECT_PATH = "C:/Users/hp/eclipse-workspace/newworkplace/TrivyDemo";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        res.setContentType("application/json");
        res.setHeader("Access-Control-Allow-Origin", "*");

        try {
        	ProcessBuilder pb = new ProcessBuilder(
        		    "trivy", "fs", PROJECT_PATH,
        		    "--severity", "HIGH,CRITICAL",
        		    "--format", "table",
        		    "--skip-dirs", "auto-fixer"
        		);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );
            StringBuilder report = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                String cleanLine = line
                    .replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", "")
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\t", " ");
                report.append(cleanLine).append("\\n");
            }

            String json = "{\"success\":true,\"report\":\"" + report.toString() + "\"}";
            res.getWriter().write(json);

        } catch (Exception e) {
            res.getWriter().write("{\"success\":false,\"error\":"
                + escapeJson(e.getMessage()) + "}");
        }
    }

   
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        res.setContentType("application/json");
        res.setHeader("Access-Control-Allow-Origin", "*");

        try {

            BufferedReader reader = req.getReader();
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
            String raw = body.toString();
            System.out.println("Raw body: " + raw.substring(0, Math.min(200, raw.length())));


            int reportKey = raw.indexOf("\"report\":");
            if (reportKey == -1) throw new Exception("No report field found");
            String afterKey = raw.substring(reportKey + 9).trim();
            if (afterKey.startsWith("\"")) afterKey = afterKey.substring(1);
            if (afterKey.endsWith("\"}")) afterKey = afterKey.substring(0, afterKey.length() - 2);

     
            String report = afterKey
                .replace("\\n", "\n")
                .replace("\\t", " ")
                .replace("\\r", "")
                .replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]", "");

            System.out.println("Report: " + report.substring(0, Math.min(200, report.length())));

            
            String pomPath = PROJECT_PATH + "/pom.xml";
            String pom = new String(Files.readAllBytes(Paths.get(pomPath)));

          
            String fullPrompt = "You are a Java security expert. "
                + "Trivy found these vulnerabilities: " + report
                + " Current pom.xml: " + pom
                + " Respond ONLY in this exact JSON format, no markdown: "
                + "{\"vulnerabilities\":[{\"library\":\"groupId:artifactId\","
                + "\"cve\":\"CVE-XXXX\",\"severity\":\"CRITICAL or HIGH\","
                + "\"installedVersion\":\"x.x.x\",\"fixedVersion\":\"x.x.x\","
                + "\"action\":\"what to do\"}],"
                + "\"updatedPom\":\"complete fixed pom.xml with \\n for newlines\"}";

           
            fullPrompt = fullPrompt
                .replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]", "")
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", " ");

            String groqBody = "{"
                + "\"model\":\"llama-3.3-70b-versatile\","
                + "\"temperature\":0.1,"
                + "\"messages\":["
                + "{\"role\":\"system\",\"content\":\"You are a Java security expert. Only respond with valid JSON.\"},"
                + "{\"role\":\"user\",\"content\":\"" + fullPrompt + "\"}"
                + "]}";

            System.out.println("Sending to Groq...");

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest groqReq = HttpRequest.newBuilder()
                .uri(URI.create("https://api.groq.com/openai/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + GROQ_API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(groqBody))
                .build();

            HttpResponse<String> groqRes = client.send(
                groqReq, HttpResponse.BodyHandlers.ofString()
            );
            String groqJson = groqRes.body();
            System.out.println("Groq response: " + groqJson);


            int contentStart = groqJson.indexOf("\"content\":\"") + 11;
            int contentEnd = groqJson.indexOf("\"},\"logprobs\"");
            if (contentEnd == -1) contentEnd = groqJson.indexOf("\"}],\"usage\"");
            if (contentEnd == -1) contentEnd = groqJson.lastIndexOf("\"}");

            String content = groqJson.substring(contentStart, contentEnd)
                .replace("\\n", "\n")
                .replace("\\\"", "\"")
                .replace("\\u003c", "<")
                .replace("\\u003e", ">")
                .replace("```json", "")
                .replace("```", "")
                .trim();

            System.out.println("Content: " + content);

            int start = content.indexOf("{");
            int end = content.lastIndexOf("}");
            if (start == -1 || end == -1) {
                throw new Exception("AI did not return valid JSON: "
                    + content.substring(0, Math.min(200, content.length())));
            }
            String jsonStr = content.substring(start, end + 1);

            int pomStart = jsonStr.indexOf("\"updatedPom\":\"") + 14;
            int pomEnd = jsonStr.lastIndexOf("\"");
            if (pomStart > 14 && pomEnd > pomStart) {
                String fixedPom = jsonStr.substring(pomStart, pomEnd)
                    .replace("\\n", "\n")
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");
                Files.write(Paths.get(pomPath), fixedPom.getBytes());
                System.out.println(" pom.xml updated!");
            }


            String savedPom = new String(Files.readAllBytes(Paths.get(pomPath)));
            String escapedPom = savedPom
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "  ");

            int removePomStart = jsonStr.indexOf(",\"updatedPom\":");
            String safeJson;
            if (removePomStart > -1) {
                safeJson = jsonStr.substring(0, removePomStart) + "}";
            } else {
                safeJson = jsonStr;
            }

            res.getWriter().write("{\"success\":true,\"result\":" + safeJson 
                + ",\"updatedPom\":\"" + escapedPom + "\"}");

        } catch (Exception e) {
            e.printStackTrace();
            res.getWriter().write("{\"success\":false,\"error\":"
                + escapeJson(e.getMessage()) + "}");
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type");
        res.setStatus(HttpServletResponse.SC_OK);
    }


    private String escapeJson(String s) {
        if (s == null) return "\"null\"";
        return "\"" + s.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r") + "\"";
    }
}