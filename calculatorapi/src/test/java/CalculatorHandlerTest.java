import handler.CalculatorHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CalculatorHandler Tests")
public class CalculatorHandlerTest {

    private CalculatorHandler handler;

    @BeforeEach
    void setUp() {
        handler = new CalculatorHandler();
    }

   
    @Nested
    @DisplayName("add")
    class AddTests {

        @Test
        @DisplayName("adds two positive numbers")
        void addTwoPositives() {
            assertEquals(5.0, handler.handle(2, 3, "add"));
        }

        @Test
        @DisplayName("adds positive and negative number")
        void addPositiveAndNegative() {
            assertEquals(-1.0, handler.handle(2, -3, "add"));
        }

        @Test
        @DisplayName("adds two negative numbers")
        void addTwoNegatives() {
            assertEquals(-5.0, handler.handle(-2, -3, "add"));
        }

        @Test
        @DisplayName("adds zero to a number")
        void addZero() {
            assertEquals(7.0, handler.handle(7, 0, "add"));
        }

        @Test
        @DisplayName("adds two decimal numbers")
        void addDecimals() {
            assertEquals(0.3, handler.handle(0.1, 0.2, "add"), 1e-9);
        }
    }

 

    @Nested
    @DisplayName("sub")
    class SubTests {

        @Test
        @DisplayName("subtracts two positive numbers")
        void subTwoPositives() {
            assertEquals(2.0, handler.handle(5, 3, "sub"));
        }

        @Test
        @DisplayName("result is negative when b > a")
        void subResultNegative() {
            assertEquals(-1.0, handler.handle(2, 3, "sub"));
        }

        @Test
        @DisplayName("subtracts negative number (effectively adds)")
        void subNegative() {
            assertEquals(5.0, handler.handle(2, -3, "sub"));
        }

        @Test
        @DisplayName("subtracts zero")
        void subZero() {
            assertEquals(7.0, handler.handle(7, 0, "sub"));
        }

        @Test
        @DisplayName("result is zero when a == b")
        void subEqual() {
            assertEquals(0.0, handler.handle(4, 4, "sub"));
        }
    }

  
    @Nested
    @DisplayName("mult")
    class MultTests {

        @Test
        @DisplayName("multiplies two positive numbers")
        void multTwoPositives() {
            assertEquals(6.0, handler.handle(2, 3, "mult"));
        }

        @Test
        @DisplayName("multiplies positive and negative")
        void multPositiveNegative() {
            assertEquals(-6.0, handler.handle(2, -3, "mult"));
        }

        @Test
        @DisplayName("multiplies two negatives gives positive")
        void multTwoNegatives() {
            assertEquals(6.0, handler.handle(-2, -3, "mult"));
        }

        @Test
        @DisplayName("multiplies by zero gives zero")
        void multByZero() {
            assertEquals(0.0, handler.handle(5, 0, "mult"));
        }

        @Test
        @DisplayName("multiplies by one gives same value")
        void multByOne() {
            assertEquals(7.0, handler.handle(7, 1, "mult"));
        }

        @Test
        @DisplayName("multiplies two decimals")
        void multDecimals() {
            assertEquals(0.06, handler.handle(0.2, 0.3, "mult"), 1e-9);
        }
    }

   

    @Nested
    @DisplayName("divide")
    class DivideTests {

        @Test
        @DisplayName("divides two positive numbers evenly")
        void divideEven() {
            assertEquals(2.0, handler.handle(6, 3, "divide"));
        }

        @Test
        @DisplayName("divides giving a decimal result")
        void divideDecimalResult() {
            assertEquals(2.5, handler.handle(5, 2, "divide"));
        }

        @Test
        @DisplayName("divides negative by positive")
        void divideNegativeByPositive() {
            assertEquals(-3.0, handler.handle(-9, 3, "divide"));
        }

        @Test
        @DisplayName("divides negative by negative gives positive")
        void divideNegativeByNegative() {
            assertEquals(3.0, handler.handle(-9, -3, "divide"));
        }

        @Test
        @DisplayName("divides zero by a number gives zero")
        void divideZeroByNumber() {
            assertEquals(0.0, handler.handle(0, 5, "divide"));
        }

        @Test
        @DisplayName("throws ArithmeticException when dividing by zero")
        void divideByZeroThrows() {
            ArithmeticException ex = assertThrows(ArithmeticException.class,
                    () -> handler.handle(5, 0, "divide"));
            assertEquals("Division by zero is not allowed", ex.getMessage());
        }
    }

   

    @Nested
    @DisplayName("unknown operation")
    class UnknownOperationTests {

        @Test
        @DisplayName("throws IllegalArgumentException for unknown operation")
        void unknownOperation() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> handler.handle(1, 2, "modulo"));
            assertTrue(ex.getMessage().contains("Unknown operation"));
        }

        @Test
        @DisplayName("throws IllegalArgumentException for empty string operation")
        void emptyOperation() {
            assertThrows(IllegalArgumentException.class,
                    () -> handler.handle(1, 2, ""));
        }
    }
}