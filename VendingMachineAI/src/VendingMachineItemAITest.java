import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("VendingMachineItem Unit Tests")
class VendingMachineItemAITest {

    private static final double DELTA = 1e-6;

    @Test
    @DisplayName("Constructor successfully stores item name and price")
    void testConstructorValidValues() {
        VendingMachineItem item = new VendingMachineItem("Pretzels", 1.75);

        assertAll("Item properties verification",
            () -> assertEquals("Pretzels", item.getName(), "Item name should match constructor input"),
            () -> assertEquals(1.75, item.getPrice(), DELTA, "Item price should match constructor input")
        );
    }

    @Test
    @DisplayName("Constructor allows zero price")
    void testConstructorZeroPrice() {
        VendingMachineItem item = new VendingMachineItem("Free Sample", 0.0);

        assertAll("Zero-priced item verification",
            () -> assertEquals("Free Sample", item.getName()),
            () -> assertEquals(0.0, item.getPrice(), DELTA)
        );
    }

    @Test
    @DisplayName("Constructor accepts null item name")
    void testConstructorNullName() {
        VendingMachineItem item = new VendingMachineItem(null, 1.25);
        assertNull(item.getName(), "Name should be null when initialized with null");
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.01, -1.0, -100.0, -Double.MIN_VALUE})
    @DisplayName("Constructor throws VendingMachineException when price is negative")
    void testConstructorNegativePriceThrowsException(double negativePrice) {
        VendingMachineException exception = assertThrows(
            VendingMachineException.class,
            () -> new VendingMachineItem("Chips", negativePrice),
            "Expected negative price to throw VendingMachineException"
        );
        assertEquals("Price cannot be less than zero", exception.getMessage());
    }
}