import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VendingMachineItemTest {

    @Test
    void testConstructorWithValidValuesCreatesItem() {
        // Arrange
        String name = "Chips";
        double price = 1.50;

        // Act
        VendingMachineItem item = new VendingMachineItem(name, price);

        // Assert
        assertEquals("Chips", item.getName());
        assertEquals(1.50, item.getPrice(), 0.001);
    }

    @Test
    void testConstructorWithNegativePriceThrowsException() {
        // Arrange
        String name = "Chips";
        double price = -0.01;

        // Act & Assert
        assertThrows(VendingMachineException.class,
                () -> new VendingMachineItem(name, price));
    }

    @Test
    void testConstructorAtZeroPriceCreatesItem() {
        // Arrange
        String name = "Free Item";
        double price = 0.00;

        // Act
        VendingMachineItem item = new VendingMachineItem(name, price);

        // Assert
        assertEquals(0.00, item.getPrice(), 0.001);
    }

    @Test
    void testGetNameReturnsItemName() {
        // Arrange
        VendingMachineItem item = new VendingMachineItem("Candy", 1.25);

        // Act
        String name = item.getName();

        // Assert
        assertEquals("Candy", name);
    }

    @Test
    void testGetPriceReturnsItemPrice() {
        // Arrange
        VendingMachineItem item = new VendingMachineItem("Soda", 2.50);

        // Act
        double price = item.getPrice();

        // Assert
        assertEquals(2.50, price, 0.001);
    }
}