package util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Set;

public class JsonUtil {

    private static final Gson gson = new Gson();

  
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }


    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }


    public static <T> T fromJson(String json,
                                  Class<T> clazz,
                                  Set<String> allowedKeys) {


        JsonObject jsonObject =
                JsonParser.parseString(json).getAsJsonObject();

     
        //for (String key : jsonObject.keySet()) {

 
           // if (!allowedKeys.contains(key)) {
                //throw new IllegalArgumentException(
                   // "Unknown field '" + key +
                    //"'. Allowed fields: " + allowedKeys
                //);
            //}
        //}

   
        return gson.fromJson(json, clazz);
    }
}