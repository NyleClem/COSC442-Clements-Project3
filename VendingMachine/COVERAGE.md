# VendingMachine Code Coverage Report

## 1. Initial and Final Coverage

| Class                   | Initial Statement Coverage | Initial Branch Coverage | Final Statement Coverage | Final Branch Coverage |
| ----------------------- | -------------------------- | ----------------------- | ------------------------ | --------------------- |
| VendingMachine.java     | 42/43 (97.22%)             | 19/20 (95%)             | 43/43 (100%)             | 20/20 (100%)          |
| VendingMachineItem.java | 8/8 (100%)                 | 2/2 (100%)              | 8/8 (100%)               | 2/2 (100%)            |

All 32 tests pass after the final changes.

## 2. Coverage-Guided Test Improvement

The initial coverage results showed that the `getSlotIndex()` method in `VendingMachine.java` did not execute the branch associated with slot C.

Instead of adding a redundant test solely to increase coverage, I modified the existing `testAddItemToOccupiedSlotThrowsException()` test to use slot C instead of slot A. This preserved the original purpose of verifying that an occupied slot rejects an additional item while also exercising the previously uncovered slot mapping.

After the modification, both `VendingMachine.java` and `VendingMachineItem.java` achieved 100% statement and branch coverage.

## 3. Condition Coverage Analysis

The `makePurchase()` method contains the Boolean expression:

```java
if ((item != null) && (this.balance >= item.getPrice()))
```

The expression contains two individual conditions:

- **Condition 1:** `item != null`
- **Condition 2:** `this.balance >= item.getPrice()`

The relevant scenarios for condition coverage are:

| Scenario                         | Condition 1 | Condition 2   | Expected Behavior |
| -------------------------------- | ----------- | ------------- | ----------------- |
| Empty slot                       | False       | Not evaluated | Purchase rejected |
| Item present, insufficient funds | True        | False         | Purchase rejected |
| Item present, sufficient funds   | True        | True          | Purchase accepted |

The existing tests should be checked against these scenarios to confirm that both conditions have evaluated to true and false.

## 4. Test-Sensitivity Experiment

A temporary fault was introduced into `makePurchase()` by replacing the greater-than-or-equal-to operator (`>=`) with the equality operator (`==`).

**Original expression:**

```java
this.balance >= item.getPrice()
```

**Injected fault:**

```java
this.balance == item.getPrice()
```

The fault incorrectly required the customer's balance to equal the item's price exactly, preventing purchases when the customer had more money than necessary.

Running the existing test suite detected the fault through two failing tests:

### testMakePurchaseWithEnoughMoneyReturnsTrue()

The test inserted $5.00 for an item costing $2.00. The expected result was a successful purchase with $3.00 remaining. The injected fault rejected the purchase because $5.00 was not equal to $2.00.

### testMakePurchaseAtBoundaryReturnsExpected()

The test examined balances of $1.99, $2.00, and $2.01 for a $2.00 item. The injected fault incorrectly rejected the $2.01 purchase, causing the assertion expecting a successful purchase to fail.

The fault was committed separately, then removed by restoring the original comparison operator. All 32 tests passed again after restoration.

The final coverage screenshot is stored in `/TESTRESULTS`.
