import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import util.Validator;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Validator Tests")
public class ValidatorTest {


    @Nested
    @DisplayName("validateA")
    class ValidateATests {

        @Test
        @DisplayName("accepts a normal positive value")
        void acceptsPositive() {
            assertDoesNotThrow(() -> Validator.validateA(42.0));
        }

        @Test
        @DisplayName("accepts a normal negative value")
        void acceptsNegative() {
            assertDoesNotThrow(() -> Validator.validateA(-42.0));
        }

        @Test
        @DisplayName("accepts zero")
        void acceptsZero() {
            assertDoesNotThrow(() -> Validator.validateA(0));
        }

        @Test
        @DisplayName("accepts the boundary max value 1,000,000,000")
        void acceptsBoundaryMax() {
            assertDoesNotThrow(() -> Validator.validateA(1_000_000_000));
        }

        @Test
        @DisplayName("accepts the boundary min value -1,000,000,000")
        void acceptsBoundaryMin() {
            assertDoesNotThrow(() -> Validator.validateA(-1_000_000_000));
        }

        @Test
        @DisplayName("throws when a is NaN")
        void throwsNaN() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> Validator.validateA(Double.NaN));
            assertEquals("'a' is not a valid number", ex.getMessage());
        }

        @Test
        @DisplayName("throws when a is positive infinity")
        void throwsPositiveInfinity() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> Validator.validateA(Double.POSITIVE_INFINITY));
            assertEquals("'a' cannot be Infinite", ex.getMessage());
        }

        @Test
        @DisplayName("throws when a is negative infinity")
        void throwsNegativeInfinity() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> Validator.validateA(Double.NEGATIVE_INFINITY));
            assertEquals("'a' cannot be Infinite", ex.getMessage());
        }

        @Test
        @DisplayName("throws when a exceeds max (1,000,000,001)")
        void throwsTooLarge() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> Validator.validateA(1_000_000_001));
            assertTrue(ex.getMessage().contains("too large"));
        }

        @Test
        @DisplayName("throws when a is below min (-1,000,000,001)")
        void throwsTooSmall() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> Validator.validateA(-1_000_000_001));
            assertTrue(ex.getMessage().contains("too small"));
        }
    }

    @Nested
    @DisplayName("validateB")
    class ValidateBTests {

        @Test
        @DisplayName("accepts a normal positive value")
        void acceptsPositive() {
            assertDoesNotThrow(() -> Validator.validateB(99.9));
        }

        @Test
        @DisplayName("accepts zero")
        void acceptsZero() {
            assertDoesNotThrow(() -> Validator.validateB(0));
        }

        @Test
        @DisplayName("throws when b is NaN")
        void throwsNaN() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> Validator.validateB(Double.NaN));
            assertEquals("'b' is not a valid number", ex.getMessage());
        }

        @Test
        @DisplayName("throws when b is positive infinity")
        void throwsPositiveInfinity() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> Validator.validateB(Double.POSITIVE_INFINITY));
            assertEquals("'b' cannot be Infinite", ex.getMessage());
        }

        @Test
        @DisplayName("throws when b is negative infinity")
        void throwsNegativeInfinity() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> Validator.validateB(Double.NEGATIVE_INFINITY));
            assertEquals("'b' cannot be Infinite", ex.getMessage());
        }

        @Test
        @DisplayName("throws when b exceeds max")
        void throwsTooLarge() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> Validator.validateB(2_000_000_000));
            assertTrue(ex.getMessage().contains("too large"));
        }

        @Test
        @DisplayName("throws when b is below min")
        void throwsTooSmall() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> Validator.validateB(-2_000_000_000));
            assertTrue(ex.getMessage().contains("too small"));
        }
    }

    @Nested
    @DisplayName("validateOperation")
    class ValidateOperationTests {

        @Test @DisplayName("'add' maps to 'add'")
        void add() { assertEquals("add", Validator.validateOperation("add")); }

        @Test @DisplayName("'addition' maps to 'add'")
        void addition() { assertEquals("add", Validator.validateOperation("addition")); }

        @Test @DisplayName("'ADD' (uppercase) maps to 'add'")
        void addUppercase() { assertEquals("add", Validator.validateOperation("ADD")); }


        @Test @DisplayName("'sub' maps to 'sub'")
        void sub() { assertEquals("sub", Validator.validateOperation("sub")); }

        @Test @DisplayName("'subtract' maps to 'sub'")
        void subtract() { assertEquals("sub", Validator.validateOperation("subtract")); }

        @Test @DisplayName("'subtraction' maps to 'sub'")
        void subtraction() { assertEquals("sub", Validator.validateOperation("subtraction")); }


        @Test @DisplayName("'mul' maps to 'mult'")
        void mul() { assertEquals("mult", Validator.validateOperation("mul")); }

        @Test @DisplayName("'multiply' maps to 'mult'")
        void multiply() { assertEquals("mult", Validator.validateOperation("multiply")); }

        @Test @DisplayName("'multiplication' maps to 'mult'")
        void multiplication() { assertEquals("mult", Validator.validateOperation("multiplication")); }

    
        @Test @DisplayName("'div' maps to 'divide'")
        void div() { assertEquals("divide", Validator.validateOperation("div")); }

        @Test @DisplayName("'divide' maps to 'divide'")
        void divide() { assertEquals("divide", Validator.validateOperation("divide")); }

        @Test @DisplayName("'division' maps to 'divide'")
        void division() { assertEquals("divide", Validator.validateOperation("division")); }

 
        @Test @DisplayName("strips leading/trailing whitespace")
        void stripsWhitespace() {
            assertEquals("add", Validator.validateOperation("  add  "));
        }


        @Test @DisplayName("throws when operation is null")
        void nullOperation() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> Validator.validateOperation(null));
            assertEquals("Operation cannot be null or empty", ex.getMessage());
        }

        @Test @DisplayName("throws when operation is empty string")
        void emptyOperation() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> Validator.validateOperation(""));
            assertEquals("Operation cannot be null or empty", ex.getMessage());
        }

        @Test @DisplayName("throws when operation is blank (spaces only)")
        void blankOperation() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> Validator.validateOperation("   "));
            assertEquals("Operation cannot be null or empty", ex.getMessage());
        }

        @Test @DisplayName("throws for unknown operation 'modulo'")
        void unknownModulo() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> Validator.validateOperation("modulo"));
            assertTrue(ex.getMessage().contains("Invalid operation"));
        }

        @Test @DisplayName("throws for unknown operation 'power'")
        void unknownPower() {
            assertThrows(IllegalArgumentException.class,
                    () -> Validator.validateOperation("power"));
        }
    }
}