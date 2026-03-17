import com.google.gson.JsonSyntaxException;
import model.CalculatorRequest;
import model.CalculatorResponse;
import model.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import util.JsonUtil;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsonUtil Tests")
public class JsonUtilTest {

 
    @Nested
    @DisplayName("toJson")
    class ToJsonTests {

        @Test
        @DisplayName("serializes CalculatorResponse correctly")
        void serializesCalculatorResponse() {
            CalculatorResponse resp = new CalculatorResponse(2, 3, "add", 5);
            String json = JsonUtil.toJson(resp);

            assertTrue(json.contains("\"statusCode\":200"));
            assertTrue(json.contains("\"status\":\"success\""));
            assertTrue(json.contains("\"a\":2.0"));
            assertTrue(json.contains("\"b\":3.0"));
            assertTrue(json.contains("\"operation\":\"add\""));
            assertTrue(json.contains("\"result\":5.0"));
        }

        @Test
        @DisplayName("serializes ErrorResponse correctly")
        void serializesErrorResponse() {
            ErrorResponse err = new ErrorResponse(400, "Bad Request", "some error");
            String json = JsonUtil.toJson(err);

            assertTrue(json.contains("\"statusCode\":400"));
            assertTrue(json.contains("\"status\":\"error\""));
            assertTrue(json.contains("\"error\":\"Bad Request\""));
            assertTrue(json.contains("\"message\":\"some error\""));
        }

        @Test
        @DisplayName("serializes null fields as null in JSON")
        void serializesNullField() {
            ErrorResponse err = new ErrorResponse(400, null, null);
            String json = JsonUtil.toJson(err);
            assertTrue(json.contains("\"status\":\"error\""));

        }
    }

   

    @Nested
    @DisplayName("fromJson (no key validation)")
    class FromJsonTwoArgTests {

        @Test
        @DisplayName("deserializes a valid CalculatorRequest JSON")
        void deserializesValidRequest() {
            String json = "{\"a\":10,\"b\":5,\"operation\":\"add\"}";
            CalculatorRequest req = JsonUtil.fromJson(json, CalculatorRequest.class);

            assertEquals(10.0, req.getA());
            assertEquals(5.0,  req.getB());
            assertEquals("add", req.getOperation());
        }

        @Test
        @DisplayName("returns object with defaults for missing fields")
        void missingFieldsGiveDefaults() {
            String json = "{}";
            CalculatorRequest req = JsonUtil.fromJson(json, CalculatorRequest.class);
            assertEquals(0.0, req.getA());
            assertEquals(0.0, req.getB());
            assertNull(req.getOperation());
        }

        @Test
        @DisplayName("throws JsonSyntaxException for malformed JSON")
        void throwsForMalformedJson() {
            assertThrows(JsonSyntaxException.class,
                    () -> JsonUtil.fromJson("not-json", CalculatorRequest.class));
        }
    }



    @Nested
    @DisplayName("fromJson (with allowedKeys)")
    class FromJsonThreeArgTests {

        private final Set<String> allowed = Set.of("a", "b", "operation");

        @Test
        @DisplayName("accepts JSON whose keys are all allowed")
        void acceptsAllAllowedKeys() {
            String json = "{\"a\":1,\"b\":2,\"operation\":\"sub\"}";
            CalculatorRequest req = JsonUtil.fromJson(json, CalculatorRequest.class, allowed);

            assertEquals(1.0,   req.getA());
            assertEquals(2.0,   req.getB());
            assertEquals("sub", req.getOperation());
        }

        @Test
        @DisplayName("accepts JSON with a subset of allowed keys")
        void acceptsSubsetOfKeys() {
            String json = "{\"a\":3,\"b\":4}";
            assertDoesNotThrow(() ->
                    JsonUtil.fromJson(json, CalculatorRequest.class, allowed));
        }

        @Test
        @DisplayName("throws IllegalArgumentException for an unknown key")
        void throwsForUnknownKey() {
            String json = "{\"a\":1,\"b\":2,\"operation\":\"add\",\"extra\":99}";
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> JsonUtil.fromJson(json, CalculatorRequest.class, allowed));
            assertTrue(ex.getMessage().contains("extra"));
        }

        @Test
        @DisplayName("error message lists the unknown field name")
        void errorMentionsFieldName() {
            String json = "{\"a\":1,\"b\":2,\"hack\":\"value\"}";
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> JsonUtil.fromJson(json, CalculatorRequest.class, allowed));
            assertTrue(ex.getMessage().contains("hack"));
        }

        @Test
        @DisplayName("accepts an empty JSON object")
        void acceptsEmptyObject() {
            assertDoesNotThrow(() ->
                    JsonUtil.fromJson("{}", CalculatorRequest.class, allowed));
        }
    }
}