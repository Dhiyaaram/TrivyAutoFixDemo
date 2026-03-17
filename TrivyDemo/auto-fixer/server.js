const express = require("express");
const cors = require("cors");
const { execSync } = require("child_process");
const fs = require("fs");
const path = require("path");

const app = express();
app.use(cors());
app.use(express.json());

const GROQ_API_KEY = process.env.GROQ_API_KEY;
//const GROQ_API_KEY = "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"; // ← paste your Groq key here
const POM_PATH = path.join(__dirname, "..", "pom.xml");

// Step 1: Run Trivy and return report
app.get("/scan", (req, res) => {
  try {
    const result = execSync(
      `trivy fs "${path.join(__dirname, "..")}" --severity HIGH,CRITICAL --format table`,
      { encoding: "utf8" }
    );
    res.json({ success: true, report: result });
  } catch (err) {
    res.json({ success: true, report: err.stdout || err.message });
  }
});

// Step 2: Send to Groq and get fixed pom.xml
app.post("/fix", async (req, res) => {
  try {
    const { report } = req.body;
    const pom = fs.readFileSync(POM_PATH, "utf8");

    const response = await fetch(
      "https://api.groq.com/openai/v1/chat/completions",
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${GROQ_API_KEY}`,
        },
        body: JSON.stringify({
          model: "llama-3.3-70b-versatile",
          temperature: 0.1,
          messages: [
            {
              role: "system",
              content: "You are a Java security expert. You only respond with valid JSON, no markdown, no extra text.",
            },
            {
              role: "user",
              content: `Trivy found these vulnerabilities:
${report}

Current pom.xml:
${pom}

Respond ONLY in this exact JSON format, no markdown, no extra text:
{
  "vulnerabilities": [
    {
      "library": "groupId:artifactId",
      "cve": "CVE-XXXX-XXXXX",
      "severity": "CRITICAL or HIGH",
      "installedVersion": "x.x.x",
      "fixedVersion": "x.x.x",
      "action": "what to do"
    }
  ],
  "updatedPom": "complete fixed pom.xml as a single string with \\n for newlines"
}`,
            },
          ],
        }),
      }
    );

    const data = await response.json();
    console.log("Groq response:", JSON.stringify(data, null, 2));

    const raw = data.choices?.[0]?.message?.content || "";
    console.log("Raw text:", raw.slice(0, 500));

    const clean = raw.replace(/```json|```/g, "").trim();
    const jsonStart = clean.indexOf("{");
    const jsonEnd = clean.lastIndexOf("}");

    if (jsonStart === -1 || jsonEnd === -1) {
      throw new Error("AI did not return valid JSON. Raw: " + clean.slice(0, 200));
    }

    const jsonStr = clean.slice(jsonStart, jsonEnd + 1);
    const parsed = JSON.parse(jsonStr);

    // Auto-update pom.xml
    const fixedPom = parsed.updatedPom.replace(/\\n/g, "\n");
    fs.writeFileSync(POM_PATH, fixedPom, "utf8");

    res.json({ success: true, result: parsed });
  } catch (err) {
    res.status(500).json({ success: false, error: err.message });
  }
});

app.listen(3000, () => {
  console.log("✅ Auto-fixer server running at http://localhost:3000");
});