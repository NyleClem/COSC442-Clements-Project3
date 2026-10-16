import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class VendingMachineTest {

    @Test
    void testAddItemWithCodePutsItemAtCodeSlot() {
        // Arrange
        VendingMachine machine = new VendingMachine();
        VendingMachineItem item = new VendingMachineItem("Chips", 1.50);

        // Act
        machine.addItem(item, "B");

        // Assert
        assertEquals(item, machine.getItem("B"));
    }

    @Test
    void testAddItemAtCodeBoundaryPutsItemAtCodeSlot() {
        // Arrange
        VendingMachine machine = new VendingMachine();
        VendingMachineItem itemA = new VendingMachineItem("Chips", 1.50);
        VendingMachineItem itemD = new VendingMachineItem("Candy", 2.00);

        // Act
        machine.addItem(itemA, "A");
        machine.addItem(itemD, "D");

        // Assert
        assertEquals(itemA, machine.getItem("A"));
        assertEquals(itemD, machine.getItem("D"));
    }

    @Test
    void testAddItemWithInvalidCodeThrowsException() {
        // Arrange
        VendingMachine machine = new VendingMachine();
        VendingMachineItem item = new VendingMachineItem("Chips", 1.50);

        // Act & Assert
        assertThrows(VendingMachineException.class,
                () -> machine.addItem(item, "Z"));
    }

    @Test
    void testAddItemToOccupiedSlotThrowsException() {
        // Arrange
        VendingMachine machine = new VendingMachine();
        VendingMachineItem firstItem = new VendingMachineItem("Chips", 1.50);
        VendingMachineItem secondItem = new VendingMachineItem("Candy", 2.00);
        machine.addItem(firstItem, "A");

        // Act & Assert
        assertThrows(VendingMachineException.class,
                () -> machine.addItem(secondItem, "A"));
    }

    @Test
    void testRemoveItemWithCodeEmptiesTheCodeSlot() {
        // Arrange
        VendingMachine machine = new VendingMachine();
        VendingMachineItem item = new VendingMachineItem("Chips", 1.50);
        machine.addItem(item, "B");

        // Act
        VendingMachineItem removedItem = machine.removeItem("B");

        // Assert
        assertEquals(item, removedItem);
        assertNull(machine.getItem("B"));
    }

    @Test
    void testRemoveItemAtCodeBoundaryEmptiesTheCodeSlot() {
        // Arrange
        VendingMachine machine = new VendingMachine();
        VendingMachineItem itemA = new VendingMachineItem("Chips", 1.50);
        VendingMachineItem itemD = new VendingMachineItem("Candy", 2.00);
        machine.addItem(itemA, "A");
        machine.addItem(itemD, "D");

        // Act
        VendingMachineItem removedA = machine.removeItem("A");
        VendingMachineItem removedD = machine.removeItem("D");

        // Assert
        assertEquals(itemA, removedA);
        assertEquals(itemD, removedD);
        assertNull(machine.getItem("A"));
        assertNull(machine.getItem("D"));
    }

    @Test
    void testRemoveItemFromEmptySlotThrowsException() {
        // Arrange
        VendingMachine machine = new VendingMachine();

        // Act & Assert
        assertThrows(VendingMachineException.class,
                () -> machine.removeItem("A"));
    }

    @Test
    void testRemoveItemWithInvalidCodeThrowsException() {
        // Arrange
        VendingMachine machine = new VendingMachine();

        // Act & Assert
        assertThrows(VendingMachineException.class,
                () -> machine.removeItem("Z"));
    }

    @Test
    void testInsertMoneyValidAmountIncreasesBalance() {
        // Arrange
        VendingMachine machine = new VendingMachine();
        machine.insertMoney(2.00);

        // Act
        machine.insertMoney(1.50);

        // Assert
        assertEquals(3.50, machine.getBalance(), 0.001);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 0.10, 1.00, 10.00, 100.00, 1000.00})
    void testInsertMoneyValidAmountsIncreaseBalance(double amount) {
        // Arrange
        VendingMachine machine = new VendingMachine();

        // Act
        machine.insertMoney(amount);

        // Assert
        assertEquals(amount, machine.getBalance(), 0.001);
    }

    @Test
    void testInsertMoneyNegativeAmountThrowsException() {
        // Arrange
        VendingMachine machine = new VendingMachine();

        // Act & Assert
        assertThrows(VendingMachineException.class,
                () -> machine.insertMoney(-3.00));
    }

    @Test
    void testInsertMoneyAtZeroBoundary() {
        // Arrange
        VendingMachine machine = new VendingMachine();

        // Act
        machine.insertMoney(0.00);

        // Assert
        assertEquals(0.00, machine.getBalance(), 0.001);
        assertThrows(VendingMachineException.class,
                () -> machine.insertMoney(-0.01));

        machine.insertMoney(0.01);
        assertEquals(0.01, machine.getBalance(), 0.001);
    }

    @Test
    void testGetBalanceReturnsValidBalance() {
        // Arrange
        VendingMachine machine = new VendingMachine();
        machine.insertMoney(2.50);

        // Act
        double firstBalance = machine.getBalance();
        double secondBalance = machine.getBalance();

        // Assert
        assertEquals(2.50, firstBalance, 0.001);
        assertEquals(2.50, secondBalance, 0.001);
    }

    @Test
    void testGetBalanceAtZeroReturnsValidBalance() {
        // Arrange
        VendingMachine machine = new VendingMachine();

        // Act
        double balance = machine.getBalance();

        // Assert
        assertEquals(0.00, balance, 0.001);
    }

    @Test
    void testMakePurchaseWithEnoughMoneyReturnsTrue() {
        // Arrange
        VendingMachine machine = new VendingMachine();
        VendingMachineItem item = new VendingMachineItem("Chips", 2.00);
        machine.addItem(item, "A");
        machine.insertMoney(5.00);

        // Act
        boolean result = machine.makePurchase("A");

        // Assert
        assertTrue(result);
        assertEquals(3.00, machine.getBalance(), 0.001);
    }

    @Test
    void testMakePurchaseWithInsufficientMoneyReturnsFalse() {
        // Arrange
        VendingMachine machine = new VendingMachine();
        VendingMachineItem item = new VendingMachineItem("Chips", 2.00);
        machine.addItem(item, "A");
        machine.insertMoney(1.50);

        // Act
        boolean result = machine.makePurchase("A");

        // Assert
        assertFalse(result);
        assertEquals(1.50, machine.getBalance(), 0.001);
    }

    @Test
    void testMakePurchaseAtEmptySlotReturnsFalse() {
        // Arrange
        VendingMachine machine = new VendingMachine();
        machine.insertMoney(5.00);

        // Act
        boolean result = machine.makePurchase("A");

        // Assert
        assertFalse(result);
        assertEquals(5.00, machine.getBalance(), 0.001);
    }

    @Test
    void testMakePurchaseAtBoundaryReturnsExpected() {
        // Arrange
        VendingMachine machineBelow = new VendingMachine();
        VendingMachine machineExact = new VendingMachine();
        VendingMachine machineAbove = new VendingMachine();

        VendingMachineItem itemBelow = new VendingMachineItem("Chips", 2.00);
        VendingMachineItem itemExact = new VendingMachineItem("Chips", 2.00);
        VendingMachineItem itemAbove = new VendingMachineItem("Chips", 2.00);

        machineBelow.addItem(itemBelow, "A");
        machineExact.addItem(itemExact, "A");
        machineAbove.addItem(itemAbove, "A");

        machineBelow.insertMoney(1.99);
        machineExact.insertMoney(2.00);
        machineAbove.insertMoney(2.01);

        // Act
        boolean belowResult = machineBelow.makePurchase("A");
        boolean exactResult = machineExact.makePurchase("A");
        boolean aboveResult = machineAbove.makePurchase("A");

        // Assert
        assertFalse(belowResult);

        assertTrue(exactResult);
        assertEquals(0.00, machineExact.getBalance(), 0.001);

        assertTrue(aboveResult);
        assertEquals(0.01, machineAbove.getBalance(), 0.001);
    }

    @Test
    void testReturnChangeReturnsBalanceAndResetsToZero() {
        // Arrange
        VendingMachine machine = new VendingMachine();
        machine.insertMoney(4.25);

        // Act
        double change = machine.returnChange();

        // Assert
        assertEquals(4.25, change, 0.001);
        assertEquals(0.00, machine.getBalance(), 0.001);
    }

    @Test
    void testReturnChangeAtZeroReturnsZero() {
        // Arrange
        VendingMachine machine = new VendingMachine();

        // Act
        double change = machine.returnChange();

        // Assert
        assertEquals(0.00, change, 0.001);
        assertEquals(0.00, machine.getBalance(), 0.001);
    }
}