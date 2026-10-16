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

@DisplayName("VendingMachine Supplementary & Gap Coverage Tests")
class VendingMachineSupplementaryTest {

    private static final double DELTA = 1e-6;
    private VendingMachine machine;

    @BeforeEach
    void setUp() {
        machine = new VendingMachine();
    }

    @Nested
    @DisplayName("Specification Discrepancy & Boundary Tests: insertMoney")
    class InsertMoneyEdgeCases {

        @Test
        @DisplayName("Boundary value: Exactly 1.00 is accepted by the implementation constraint (amount < 1)")
        void testInsertMoneyExactBoundaryOneDollar() {
            machine.insertMoney(1.00);
            assertEquals(1.00, machine.getBalance(), DELTA);
        }

        @ParameterizedTest(name = "Deposit of ${0} according to Javadoc precondition (amount >= 0)")
        @ValueSource(doubles = {0.00, 0.05, 0.10, 0.25, 0.50, 0.75, 0.99})
        @DisplayName("Exposes bug: Coin insertions < 1.00 should be valid per Javadoc but fail implementation check")
        void testInsertFractionalCoinsMatchesSpecOrFailsKnownBug(double coin) {
            /*
             * Javadoc: "Precondition: amount >= 0 ... Throws VendingMachineException if amount < 0"
             * Code: "if (amount < 1) throw new VendingMachineException(...)"
             *
             * Under current implementation, this asserts that the bug manifests by throwing VendingMachineException.
             */
            VendingMachineException ex = assertThrows(
                VendingMachineException.class,
                () -> machine.insertMoney(coin),
                "Fails because code requires amount >= 1 instead of amount >= 0"
            );
            assertEquals("Invalid amount.  Amount must be >= 0", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Null Parameter Robustness")
    class NullParameterTests {

        @Test
        @DisplayName("Passing null slot code to getItem throws NullPointerException in getSlotIndex")
        void testGetItemWithNullCodeThrowsNPE() {
            // Documenting current implementation behavior: code.equals(...) throws NPE on null code
            assertThrows(NullPointerException.class, () -> machine.getItem(null));
        }

        @Test
        @DisplayName("Passing null slot code to makePurchase propagates NullPointerException")
        void testMakePurchaseWithNullCodeThrowsNPE() {
            assertThrows(NullPointerException.class, () -> machine.makePurchase(null));
        }

        @Test
        @DisplayName("Passing null slot code to removeItem throws NullPointerException")
        void testRemoveItemWithNullCodeThrowsNPE() {
            assertThrows(NullPointerException.class, () -> machine.removeItem(null));
        }
    }

    @Nested
    @DisplayName("makePurchase Exceptional Behavior & Invalid Slot Fall-through")
    class MakePurchaseGapTests {

        @ParameterizedTest
        @ValueSource(strings = {"E", "X", "1", "invalid"})
        @DisplayName("makePurchase with invalid code propagates VendingMachineException from getItem")
        void testMakePurchaseInvalidCodeThrowsException(String invalidCode) {
            machine.insertMoney(5.00);

            // makePurchase calls getItem(code) directly without wrapping try-catch
            assertThrows(
                VendingMachineException.class,
                () -> machine.makePurchase(invalidCode),
                "Expected VendingMachineException to propagate out of makePurchase for invalid slot codes"
            );
            assertEquals(5.00, machine.getBalance(), DELTA, "Balance must remain intact if exception occurs");
        }
    }

    @Nested
    @DisplayName("Slot Independence and Lifecycle Transitions")
    class LifecycleAndSlotIndependenceTests {

        @Test
        @DisplayName("Re-stocking: An emptied slot can be refilled with a new item")
        void testRestockSlotAfterRemovalAndPurchase() {
            VendingMachineItem item1 = new VendingMachineItem("Chips", 1.25);
            VendingMachineItem item2 = new VendingMachineItem("Crackers", 1.50);

            machine.addItem(item1, VendingMachine.A_CODE);
            machine.insertMoney(2.00);

            // Purchase item1, leaving slot A empty
            assertTrue(machine.makePurchase(VendingMachine.A_CODE));
            assertNull(machine.getItem(VendingMachine.A_CODE));

            // Restock slot A with item2
            machine.addItem(item2, VendingMachine.A_CODE);
            assertSame(item2, machine.getItem(VendingMachine.A_CODE), "Slot A should hold the newly restocked item");
        }

        @Test
        @DisplayName("Slot isolation: Actions on one slot do not alter other populated slots")
        void testSlotIndependence() {
            VendingMachineItem itemA = new VendingMachineItem("Item A", 1.00);
            VendingMachineItem itemB = new VendingMachineItem("Item B", 2.00);
            VendingMachineItem itemC = new VendingMachineItem("Item C", 3.00);
            VendingMachineItem itemD = new VendingMachineItem("Item D", 4.00);

            machine.addItem(itemA, VendingMachine.A_CODE);
            machine.addItem(itemB, VendingMachine.B_CODE);
            machine.addItem(itemC, VendingMachine.C_CODE);
            machine.addItem(itemD, VendingMachine.D_CODE);

            machine.insertMoney(2.00);

            // Purchase slot B only
            boolean purchasedB = machine.makePurchase(VendingMachine.B_CODE);

            assertAll("Cross-slot integrity verification",
                () -> assertTrue(purchasedB),
                () -> assertSame(itemA, machine.getItem(VendingMachine.A_CODE), "Slot A must remain untouched"),
                () -> assertNull(machine.getItem(VendingMachine.B_CODE), "Slot B must be empty after purchase"),
                () -> assertSame(itemC, machine.getItem(VendingMachine.C_CODE), "Slot C must remain untouched"),
                () -> assertSame(itemD, machine.getItem(VendingMachine.D_CODE), "Slot D must remain untouched")
            );
        }

        @Test
        @DisplayName("Full machine cycle: Populate all 4 slots, purchase all 4, verify zero inventory")
        void testFullMachineLifecycle() {
            machine.addItem(new VendingMachineItem("Item A", 1.00), VendingMachine.A_CODE);
            machine.addItem(new VendingMachineItem("Item B", 1.00), VendingMachine.B_CODE);
            machine.addItem(new VendingMachineItem("Item C", 1.00), VendingMachine.C_CODE);
            machine.addItem(new VendingMachineItem("Item D", 1.00), VendingMachine.D_CODE);

            machine.insertMoney(10.00);

            boolean boughtA = machine.makePurchase(VendingMachine.A_CODE);
            boolean boughtB = machine.makePurchase(VendingMachine.B_CODE);
            boolean boughtC = machine.makePurchase(VendingMachine.C_CODE);
            boolean boughtD = machine.makePurchase(VendingMachine.D_CODE);

            assertAll("Complete drain of machine inventory",
                () -> assertTrue(boughtA),
                () -> assertTrue(boughtB),
                () -> assertTrue(boughtC),
                () -> assertTrue(boughtD),
                () -> assertEquals(6.00, machine.getBalance(), DELTA, "Balance should reflect 4 x $1.00 deductions"),
                () -> assertNull(machine.getItem(VendingMachine.A_CODE)),
                () -> assertNull(machine.getItem(VendingMachine.B_CODE)),
                () -> assertNull(machine.getItem(VendingMachine.C_CODE)),
                () -> assertNull(machine.getItem(VendingMachine.D_CODE))
            );

            // Second purchase on any emptied slot returns false
            assertFalse(machine.makePurchase(VendingMachine.A_CODE));
            assertEquals(6.00, machine.getBalance(), DELTA);
        }
    }
}