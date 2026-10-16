import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("VendingMachine Unit Tests")
class VendingMachineAITest {

    private static final double DELTA = 1e-6;
    private VendingMachine machine;
    private VendingMachineItem defaultItem;

    @BeforeEach
    void setUp() {
        // NOTE: Fix the constructor bug (i <= NUM_SLOTS -> i < NUM_SLOTS)
        // in VendingMachine before running these tests.
        machine = new VendingMachine();
        defaultItem = new VendingMachineItem("Cola", 1.50);
    }

    @Nested
    @DisplayName("Initialization & Balance State")
    class InitialStateTests {

        @Test
        @DisplayName("New machine initializes with zero balance and empty slots")
        void testInitialMachineState() {
            assertAll("Initial state checks",
                () -> assertEquals(0.0, machine.getBalance(), DELTA, "Balance should initially be 0"),
                () -> assertNull(machine.getItem(VendingMachine.A_CODE)),
                () -> assertNull(machine.getItem(VendingMachine.B_CODE)),
                () -> assertNull(machine.getItem(VendingMachine.C_CODE)),
                () -> assertNull(machine.getItem(VendingMachine.D_CODE))
            );
        }
    }

    @Nested
    @DisplayName("Item Management (Add / Get / Remove)")
    class ItemManagementTests {

        @ParameterizedTest
        @ValueSource(strings = {"A", "B", "C", "D"})
        @DisplayName("Items can be placed into all valid slots")
        void testAddItemValidSlots(String slotCode) {
            machine.addItem(defaultItem, slotCode);
            assertSame(defaultItem, machine.getItem(slotCode), "Item retrieved should be identical to the one added");
        }

        @Test
        @DisplayName("Adding to an occupied slot throws VendingMachineException")
        void testAddSlotAlreadyOccupiedThrows() {
            machine.addItem(defaultItem, VendingMachine.A_CODE);
            VendingMachineItem newItem = new VendingMachineItem("Chips", 1.00);

            VendingMachineException exception = assertThrows(
                VendingMachineException.class,
                () -> machine.addItem(newItem, VendingMachine.A_CODE)
            );
            assertEquals("Slot A already occupied", exception.getMessage());
        }

        @ParameterizedTest
        @ValueSource(strings = {"E", "a", "1", "", " "})
        @DisplayName("Adding an item with invalid slot code throws VendingMachineException")
        void testAddItemInvalidCodeThrows(String invalidCode) {
            assertThrows(VendingMachineException.class, () -> machine.addItem(defaultItem, invalidCode));
        }

        @Test
        @DisplayName("removeItem clears the slot and returns the item")
        void testRemoveItemSuccess() {
            machine.addItem(defaultItem, VendingMachine.B_CODE);

            VendingMachineItem removed = machine.removeItem(VendingMachine.B_CODE);
            assertAll("Removal verification",
                () -> assertSame(defaultItem, removed, "Removed item should match inserted item"),
                () -> assertNull(machine.getItem(VendingMachine.B_CODE), "Slot should be empty after removal")
            );
        }

        @Test
        @DisplayName("Removing an item from an empty slot throws VendingMachineException")
        void testRemoveItemFromEmptySlotThrows() {
            VendingMachineException exception = assertThrows(
                VendingMachineException.class,
                () -> machine.removeItem(VendingMachine.C_CODE)
            );
            assertEquals("Slot C is empty -- cannot remove item", exception.getMessage());
        }

        @Test
        @DisplayName("Removing an item with invalid code throws VendingMachineException")
        void testRemoveItemInvalidCodeThrows() {
            assertThrows(VendingMachineException.class, () -> machine.removeItem("Z"));
        }
    }

    @Nested
    @DisplayName("Money Operations")
    class MoneyOperationTests {

        @Test
        @DisplayName("insertMoney increases balance accurately")
        void testInsertMoneySuccess() {
            machine.insertMoney(2.00);
            machine.insertMoney(1.50);
            assertEquals(3.50, machine.getBalance(), DELTA);
        }

        @ParameterizedTest
        @ValueSource(doubles = {-0.01, -1.0, -10.0})
        @DisplayName("insertMoney with negative amounts throws VendingMachineException")
        void testInsertMoneyNegativeAmountThrows(double negativeAmount) {
            VendingMachineException exception = assertThrows(
                VendingMachineException.class,
                () -> machine.insertMoney(negativeAmount)
            );
            assertEquals("Invalid amount.  Amount must be >= 0", exception.getMessage());
        }

        @Test
        @DisplayName("returnChange returns balance and resets balance to 0")
        void testReturnChange() {
            machine.insertMoney(5.00);
            double changeReturned = machine.returnChange();

            assertAll("Return change checks",
                () -> assertEquals(5.00, changeReturned, DELTA, "Returned change must match previous balance"),
                () -> assertEquals(0.0, machine.getBalance(), DELTA, "Balance must be reset to 0.0")
            );
        }

        @Test
        @DisplayName("returnChange on empty balance returns 0.0")
        void testReturnChangeWhenZeroBalance() {
            assertEquals(0.0, machine.returnChange(), DELTA);
            assertEquals(0.0, machine.getBalance(), DELTA);
        }
    }

    @Nested
    @DisplayName("Purchasing Flow")
    class PurchaseTests {

        @Test
        @DisplayName("makePurchase successfully deducts price, clears slot, and returns true")
        void testMakePurchaseSuccess() {
            machine.addItem(defaultItem, VendingMachine.A_CODE); // Price: 1.50
            machine.insertMoney(2.00);

            boolean result = machine.makePurchase(VendingMachine.A_CODE);

            assertAll("Successful purchase validation",
                () -> assertTrue(result, "Purchase should return true when balance is sufficient"),
                () -> assertEquals(0.50, machine.getBalance(), DELTA, "Balance should reflect price deduction"),
                () -> assertNull(machine.getItem(VendingMachine.A_CODE), "Purchased slot should now be null")
            );
        }

        @Test
        @DisplayName("makePurchase with exact balance leaves 0.0 balance")
        void testMakePurchaseExactChange() {
            machine.addItem(defaultItem, VendingMachine.A_CODE);
            machine.insertMoney(1.50);

            boolean result = machine.makePurchase(VendingMachine.A_CODE);

            assertTrue(result);
            assertEquals(0.0, machine.getBalance(), DELTA);
            assertNull(machine.getItem(VendingMachine.A_CODE));
        }

        @Test
        @DisplayName("makePurchase returns false and retains balance if balance is insufficient")
        void testMakePurchaseInsufficientFunds() {
            machine.addItem(defaultItem, VendingMachine.A_CODE); // Price: 1.50
            machine.insertMoney(1.00);

            boolean result = machine.makePurchase(VendingMachine.A_CODE);

            assertAll("Insufficient funds validation",
                () -> assertFalse(result, "Purchase should fail due to insufficient funds"),
                () -> assertEquals(1.00, machine.getBalance(), DELTA, "Balance must remain unchanged"),
                () -> assertNotNull(machine.getItem(VendingMachine.A_CODE), "Item should remain in the machine")
            );
        }

        @Test
        @DisplayName("makePurchase returns false if selected slot is empty")
        void testMakePurchaseEmptySlot() {
            machine.insertMoney(5.00);

            boolean result = machine.makePurchase(VendingMachine.B_CODE);

            assertFalse(result, "Should return false when attempting to purchase from an empty slot");
            assertEquals(5.00, machine.getBalance(), DELTA, "Balance must remain unchanged");
        }
    }
}