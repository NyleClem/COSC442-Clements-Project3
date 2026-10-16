# AI-Generated Unit Test Review

The AI-generated initial suite contained 31 test executions that passed after correcting an error in the original VendingMachine constructor. The AI identified this constructor defect while reviewing the source code. Its tests included meaningful assertions for item management, money handling, and purchasing.

Compared with my manually developed suite of 32 passing tests, the AI-generated suite achieved 100% statement and branch coverage for both `VendingMachine.java` and `VendingMachineItem.java` on its first attempt. My manual suite initially achieved 42/43 statements and 19/20 branches in `VendingMachine.java`, requiring an adjustment to an existing test to cover slot C. Both approaches ultimately achieved 100% coverage. However, coverage alone does not mean that every behavior is correct or adequately tested.

After a follow-up prompt asking the AI to identify remaining behavioral gaps, it generated 18 additional test executions, bringing the total to 49 passing tests. These included tests for restocking, slot independence, invalid purchase codes, and complete purchasing sequences. Although structural coverage remained at 100%, the follow-up added checks of interactions between operations and changes in machine state.

One important limitation was the AI's handling of the `insertMoney()` defect. It correctly identified that the implementation rejected amounts below $1.00 even though the specification allows nonnegative amounts. The supplementary tests expected an exception for those amounts which accepted the incorrect implementation instead of testing the specified behavior. Some tests also asserted implementation-specific behavior, such as `NullPointerException` for null slot codes. This demonstrates the importance of reviewing AI-generated tests against the specification rather than relying solely on passing results or high coverage.

## Coverage Comparison

| Suite                | Test Executions | VendingMachine Statements | VendingMachine Branches | VendingMachineItem Statements | VendingMachineItem Branches |
| -------------------- | --------------: | ------------------------: | ----------------------: | ----------------------------: | --------------------------: |
| Manual (Final)       |              32 |                      100% |                    100% |                          100% |                        100% |
| AI (Initial)         |              31 |                      100% |                    100% |                          100% |                        100% |
| AI (After Follow-up) |              49 |                      100% |                    100% |                          100% |                        100% |

**Source correction:** The original constructor loop was changed from `i <= NUM_SLOTS` to `i < NUM_SLOTS` after the initial AI tests failed during initialization. The `insertMoney()` implementation was left unchanged during the AI evaluation.

**Coverage screenshot:** `/testresults`

**AI conversation link:** https://share.gemini.google/AEfVPdach0ER
