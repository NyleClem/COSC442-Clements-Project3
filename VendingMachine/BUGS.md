# VendingMachine Bugs

## Bug 1 - VendingMachine Constructor Array Index

**Observed failure:**  
All VendingMachine tests failed when attempting to create a new VendingMachine. The failure reported `ArrayIndexOutOfBoundsException: Index 4 out of bounds for length 4`.

**Test that exposed it:**  
The VendingMachine tests that construct a VendingMachine exposed the fault.

**Source-code fault:**  
The constructor loop used `i <= NUM_SLOTS`. Since `NUM_SLOTS` is 4, the loop attempted to access index 4 even though the valid indexes are 0 through 3.

**Diagnosis:**  
The JUnit failures occurred on `new VendingMachine()`. The failure message showed that index 4 was being accessed in an array of length 4. Inspecting the constructor showed that the loop continued while `i <= NUM_SLOTS`.

**Correction:**  
Changed the loop condition from `i <= NUM_SLOTS` to `i < NUM_SLOTS`.

## Bug 2 - insertMoney Rejects Valid Amounts Below One Dollar

**Observed failure:**  
Tests using valid amounts below 1.00 failed because `insertMoney()` threw a `VendingMachineException`.

**Test that exposed it:**  
`testInsertMoneyAtZeroBoundary()` and the parameterized `testInsertMoneyValidAmountsIncreaseBalance()` exposed the fault with valid values such as `0.01` and `0.10`.

**Source-code fault:**  
The `insertMoney()` method checked `amount < 1`, which incorrectly rejected all amounts below 1.00.

**Diagnosis:**  
The failing tests reported `VendingMachineException: Invalid amount` for amounts that should be valid according to the Javadocs. The Javadocs specify that only amounts less than zero are invalid. Inspecting `insertMoney()` showed that the validation condition used `amount < 1`.

**Correction:**  
Changed the validation condition from `amount < 1` to `amount < 0`.

## Test Sensitivity Experiment

**Injected fault:**  
I temporarily changed `returnChange()` so that it always returns `0` instead of returning the previous balance. The changed line was marked with `// INJECTED FAULT FOR TEST VALIDATION`.

**Test that failed:**  
`testReturnChangeReturnsBalanceAndResetsToZero()`

**JUnit failure message:**  
Expected `[4.25]` but was `[0.0]`.

**Why the test detected the fault:**  
The test inserts `4.25` into the vending machine and then calls `returnChange()`. According to the Javadocs, `returnChange()` should return the amount of change currently in the machine and reset the balance to zero. Since the injected fault caused the method to return `0` instead of `4.25`, the assertion comparing the returned change to `4.25` failed.
