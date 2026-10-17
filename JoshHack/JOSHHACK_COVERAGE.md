# JoshHack Inventory Unit Testing and Coverage

## Initial Coverage

Before adding Inventory-specific tests, the existing ItemTest.java suite produced the following coverage for Inventory.java:

| Metric     | Covered | Coverage |
| ---------- | ------- | -------- |
| Statements | 0/24    | 0%       |
| Functions  | 0/7     | 0%       |
| Branches   | 0/18    | 0%       |

## Test Development

Gemini generated JUnit 5 tests for Inventory.java after reviewing the Inventory and Item implementations and relevant gameplay behavior from Creature.java.

The tests cover inventory construction, item insertion, removal, slot reuse, capacity checks, reference-based membership, index access, duplicate references, and null-related edge cases.

All generated tests passed without requiring changes to the production Inventory.java implementation.

## Final Coverage

| Metric     | Covered | Coverage |
| ---------- | ------- | -------- |
| Statements | 24/24   | 100%     |
| Functions  | 7/7     | 100%     |
| Branches   | 18/18   | 100%     |

The test suite achieved full statement, function, and branch coverage for Inventory.java.

## Observations

The tests confirmed that Inventory uses reference equality to identify items and reuses the first available slot after an item is removed.

## Evidence

Final coverage screenshot: /testresults
