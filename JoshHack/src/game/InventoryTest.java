package game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Inventory Test Suite - JoshHack Engine")
class InventoryTest {

    private Item potion;
    private Item dagger;
    private Item food;
    private Item armor;

    @BeforeEach
    void setUp() {
        potion = new Item('!', Color.RED, "Health Potion", "red potion");
        potion.setQuaffEffect(null);

        dagger = new Item('/', Color.GRAY, "Dagger", "sharp dagger");
        dagger.modifyAttackValue(5);

        food = new Item('%', Color.GREEN, "Ration", "dry bread");
        food.modifyFoodValue(100);

        armor = new Item('[', Color.BLUE, "Chainmail", "heavy armor");
        armor.modifyDefenseValue(3);
    }

    @Nested
    @DisplayName("Constructor and Capacity Invariants")
    class ConstructorTests {

        @Test
        @DisplayName("Standard creature inventory initializes with exactly 20 empty slots")
        void testStandardCreatureCapacity() {
            Inventory inv = new Inventory(20);

            assertNotNull(inv.getItems());
            assertEquals(20, inv.getItems().length);
            for (int i = 0; i < 20; i++) {
                assertNull(inv.get(i), "Slot " + i + " must start null");
            }
            assertFalse(inv.isFull());
        }

        @Test
        @DisplayName("Inventory supports arbitrary and boundary capacities (e.g., 0 slots)")
        void testBoundaryCapacities() {
            Inventory smallInv = new Inventory(1);
            assertEquals(1, smallInv.getItems().length);
            assertFalse(smallInv.isFull());

            Inventory zeroInv = new Inventory(0);
            assertEquals(0, zeroInv.getItems().length);
            // Size of occupied slots is 0, array length is 0 -> 0 == 0 evaluates to true
            assertTrue(zeroInv.isFull());
        }
    }

    @Nested
    @DisplayName("Pickup and Insertion Mechanics (add)")
    class AddMechanicsTests {

        @Test
        @DisplayName("pickup() fills the lowest available index first")
        void testAddFindsFirstAvailableSlot() {
            Inventory inv = new Inventory(3);

            inv.add(potion);
            assertSame(potion, inv.get(0));
            assertNull(inv.get(1));

            inv.add(dagger);
            assertSame(dagger, inv.get(1));

            inv.add(food);
            assertSame(food, inv.get(2));
        }

        @Test
        @DisplayName("add() fails silently when inventory reaches maximum capacity (20/20)")
        void testAddWhenFullSilentlyDrops() {
            Inventory inv = new Inventory(2);
            inv.add(potion);
            inv.add(dagger);
            assertTrue(inv.isFull());

            // Creature attempts pickup with full inventory
            inv.add(food);

            // Verifies food was rejected and existing contents were not overwritten
            assertSame(potion, inv.get(0));
            assertSame(dagger, inv.get(1));
            assertFalse(inv.contains(food));
        }

        @Test
        @DisplayName("add() on a 0-capacity inventory handles loop termination without error")
        void testAddZeroCapacityBoundary() {
            Inventory zeroInv = new Inventory(0);
            assertDoesNotThrow(() -> zeroInv.add(potion));
            assertEquals(0, zeroInv.getItems().length);
        }
    }

    @Nested
    @DisplayName("Item Consumption, Dropping, and Throwing (remove)")
    class RemoveMechanicsTests {

        @Test
        @DisplayName("Consuming food (eat) or potions (quaff) frees the specific slot")
        void testRemoveExistingItemLeavesGap() {
            Inventory inv = new Inventory(4);
            inv.add(potion);
            inv.add(dagger);
            inv.add(food);

            // Creature quaffs the potion at slot 0
            inv.remove(potion);

            assertNull(inv.get(0), "Slot 0 should now be empty");
            assertSame(dagger, inv.get(1));
            assertSame(food, inv.get(2));
            assertFalse(inv.contains(potion));
        }

        @Test
        @DisplayName("Slot vacated by drop(), eat(), or throwItem() is reused by subsequent pickup()")
        void testSlotReuseAfterRemoval() {
            Inventory inv = new Inventory(3);
            inv.add(potion); // slot 0
            inv.add(dagger); // slot 1
            inv.add(food);   // slot 2

            // Creature throws the dagger from slot 1
            inv.remove(dagger);
            assertNull(inv.get(1));

            // Creature picks up armor; must fill slot 1, not append past slot 2
            inv.add(armor);
            assertSame(armor, inv.get(1));
            assertSame(potion, inv.get(0));
            assertSame(food, inv.get(2));
        }

        @Test
        @DisplayName("remove() silently ignores items not present in inventory")
        void testRemoveNonexistentItem() {
            Inventory inv = new Inventory(2);
            inv.add(potion);

            // Creature attempts to drop an item it is not holding
            assertDoesNotThrow(() -> inv.remove(dagger));

            assertSame(potion, inv.get(0));
            assertNull(inv.get(1));
        }

        @Test
        @DisplayName("remove() operates safely on zero-capacity inventories")
        void testRemoveZeroCapacity() {
            Inventory zeroInv = new Inventory(0);
            assertDoesNotThrow(() -> zeroInv.remove(potion));
        }

        @Test
        @DisplayName("remove() only clears the first instance when duplicate references exist")
        void testRemoveDuplicateInstanceRemovesFirstOnly() {
            Inventory inv = new Inventory(3);
            inv.add(potion);
            inv.add(potion); // Same object reference added twice

            inv.remove(potion);

            assertNull(inv.get(0));
            assertSame(potion, inv.get(1));
            assertTrue(inv.contains(potion), "Second reference must remain intact");
        }
    }

    @Nested
    @DisplayName("Equip Invariant and Identity Matching (contains)")
    class ContainsAndIdentityTests {

        @Test
        @DisplayName("contains() accurately reflects presence of items")
        void testContainsBasicPresence() {
            Inventory inv = new Inventory(5);
            inv.add(dagger);

            assertTrue(inv.contains(dagger));
            assertFalse(inv.contains(armor));
        }

        @Test
        @DisplayName("contains() uses reference equality (==), distinguishing items with identical attributes")
        void testContainsDistinguishesIdenticalTwins() {
            Inventory inv = new Inventory(3);
            // Two identical daggers dropped in dungeon
            Item dagger1 = new Item('/', Color.GRAY, "Dagger", "sharp dagger");
            Item dagger2 = new Item('/', Color.GRAY, "Dagger", "sharp dagger");

            inv.add(dagger1);

            assertTrue(inv.contains(dagger1));
            assertFalse(inv.contains(dagger2), "Separate instances with identical stats must not match");
        }

        @Test
        @DisplayName("contains() returns false on empty and 0-capacity inventories")
        void testContainsEmptyAndZeroCapacity() {
            Inventory emptyInv = new Inventory(20);
            assertFalse(emptyInv.contains(potion));

            Inventory zeroInv = new Inventory(0);
            assertFalse(zeroInv.contains(potion));
        }
    }

    @Nested
    @DisplayName("Capacity Thresholds (isFull)")
    class IsFullTests {

        @Test
        @DisplayName("isFull() returns false when slots are available")
        void testIsFullWhenOpenSlotsRemain() {
            Inventory inv = new Inventory(3);
            assertFalse(inv.isFull());

            inv.add(potion);
            assertFalse(inv.isFull());

            inv.add(dagger);
            assertFalse(inv.isFull());
        }

        @Test
        @DisplayName("isFull() returns true only when every slot is occupied")
        void testIsFullWhenCompletelyPopulated() {
            Inventory inv = new Inventory(2);
            inv.add(potion);
            inv.add(dagger);

            assertTrue(inv.isFull());
        }

        @Test
        @DisplayName("Dropping or consuming an item immediately toggles isFull() back to false")
        void testIsFullTogglesOnRemoval() {
            Inventory inv = new Inventory(1);
            inv.add(potion);
            assertTrue(inv.isFull());

            inv.remove(potion);
            assertFalse(inv.isFull());
        }

        @Test
        @DisplayName("isFull() on 0-capacity inventory returns true")
        void testIsFullZeroCapacity() {
            Inventory zeroInv = new Inventory(0);
            assertTrue(zeroInv.isFull());
        }
    }

    @Nested
    @DisplayName("Index Access, Boundary Violations, and Leakage")
    class AccessAndEncapsulationTests {

        @Test
        @DisplayName("get(i) retrieves the correct item slot reference")
        void testGetByIndex() {
            Inventory inv = new Inventory(3);
            inv.add(potion);
            inv.add(dagger);

            assertSame(potion, inv.get(0));
            assertSame(dagger, inv.get(1));
            assertNull(inv.get(2));
        }

        @Test
        @DisplayName("get(i) throws ArrayIndexOutOfBoundsException on invalid bounds")
        void testGetOutOfBounds() {
            Inventory inv = new Inventory(20);
            assertThrows(ArrayIndexOutOfBoundsException.class, () -> inv.get(-1));
            assertThrows(ArrayIndexOutOfBoundsException.class, () -> inv.get(20));
        }

        @Test
        @DisplayName("getItems() leaks internal backing array allowing external tampering")
        void testGetItemsExposureLeak() {
            Inventory inv = new Inventory(5);
            Item[] rawArray = inv.getItems();

            assertNotNull(rawArray);
            assertEquals(5, rawArray.length);

            // Simulating an external system (e.g., Creature death-drop) modifying the array directly
            rawArray[0] = armor;
            assertSame(armor, inv.get(0));
            assertTrue(inv.contains(armor));
        }
    }

    @Nested
    @DisplayName("Null Parameter Edge Cases and Implementation Flaws")
    class NullHandlingEdgeCases {

        @Test
        @DisplayName("contains(null) returns true when empty slots exist (Defect: null == null)")
        void testContainsNullDefectWhenEmptySlotsExist() {
            Inventory inv = new Inventory(2);
            inv.add(potion);

            // Slot 1 is null; contains checks `i == null`, evaluating to true
            assertTrue(inv.contains(null), "contains(null) unexpectedly returns true if any slot is empty");

            inv.add(dagger);
            // Now slots 0 and 1 are non-null
            assertFalse(inv.contains(null), "contains(null) returns false when completely full");
        }

        @Test
        @DisplayName("remove(null) prematurely exits after matching the first null slot")
        void testRemoveNullDefect() {
            Inventory inv = new Inventory(2);
            inv.add(potion);

            // Slot 1 is already null. Calling remove(null) matches items[1] == null and returns
            assertDoesNotThrow(() -> inv.remove(null));
            assertSame(potion, inv.get(0));
            assertNull(inv.get(1));
        }

        @Test
        @DisplayName("add(null) consumes an empty slot by writing null to it")
        void testAddNullSlotConsumption() {
            Inventory inv = new Inventory(2);

            // Adding null matches items[0] == null and assigns items[0] = null
            inv.add(null);
            assertNull(inv.get(0));

            // A subsequent add still fills slot 0 because it remains null
            inv.add(potion);
            assertSame(potion, inv.get(0));
        }
    }
}