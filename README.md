# Green Day Bank

A command-line banking application implemented in Java as the final project for **kood**.

The application models a small banking system in which users can hold cash, deposit and withdraw money from savings, transfer money between savings and investment accounts, invest in predefined funds, and withdraw their investments.

The project was developed collaboratively by three team members with responsibilities divided according to domain areas.

---

# Table of Contents

* [1. Project Overview](#1-project-overview)
* [2. Team Responsibilities](#2-team-responsibilities)
* [3. Requirements](#3-requirements)
* [4. Architecture](#4-architecture)
* [5. Domain Model](#5-domain-model)
* [6. Account Design](#6-account-design)
* [7. Money and BigDecimal](#7-money-and-bigdecimal)
* [8. User and Cash Model](#8-user-and-cash-model)
* [9. Savings Account](#9-savings-account)
* [10. Investment System](#10-investment-system)
* [11. Banking Service](#11-banking-service)
* [12. Session Management](#12-session-management)
* [13. Validation and Exceptions](#13-validation-and-exceptions)
* [14. CLI Output Contract](#14-cli-output-contract)
* [15. EOF Handling](#15-eof-handling)
* [16. State and Transaction Rules](#16-state-and-transaction-rules)
* [17. Testing Strategy](#17-testing-strategy)
* [18. Engineering Lessons](#18-engineering-lessons)
* [19. Known Design Considerations](#19-known-design-considerations)
* [20. Project Structure](#20-project-structure)
* [21. Running the Application](#21-running-the-application)
* [22. Final Checklist](#22-final-checklist)

---

# 1. Project Overview

Green Day Bank is a stateful command-line banking application.

The system supports four predefined users:

```text
Alice
Bob
Charlie
Diana
```

Each user starts with:

```text
Cash: $1000.00
Savings account: $0.00
Investment account: $0.00
```

The user can then interact with the banking system through the following menu:

```text
--- Banking App Menu ---
1. Show balance
2. Deposit money
3. Withdraw money
4. Send money to a person
5. Invest in funds
6. Transfer between accounts
7. Withdraw all investments
8. Logout
9. Exit
```

The application is intentionally implemented as an object-oriented system where banking concepts are represented by domain classes rather than being implemented entirely inside the CLI.

---

# 2. Team Responsibilities

The project was divided between three members.

| Team Member | Responsibility                    | Main Classes                                                                         |
| -------- | --------------------------------- | ------------------------------------------------------------------------------------ |
| Jack     | Investment functionality          | `InvestmentAccount.java`, `Fund.java`                                                |
| Francis  | Account and savings functionality | `Account.java`, `SavingsAccount.java`                                                |
| Daisy    | Application/session orchestration | `BankingApp.java`, `BankingService.java`, `User.java`, `InvalidAmountException.java` |

The division was based on separating **domain logic** from **application orchestration**.

The intention was that each member could develop and test their area independently while exposing a small, understandable interface to the other components.

---

# 3. Requirements

The application must provide:

* Login for Alice, Bob, Charlie and Diana.
* Persistent user state during the application lifetime.
* Initial cash of `$1000.00` per user.
* Savings accounts.
* Investment accounts.
* 1% savings interest.
* Three investment funds.
* Fund appreciation.
* Deposits.
* Withdrawals.
* Transfers between savings and investment accounts.
* Transfers between users.
* Withdrawal of all investments.
* Logout.
* Exit.
* Graceful EOF handling.
* Validation of invalid monetary operations.
* Exact CLI output expected by the automated tests.

The application must also:

* Use exactly one `Scanner`.
* Use `BigDecimal` for monetary values.
* Avoid `System.exit()`.
* Avoid static methods except `main()`.
* Handle invalid input without crashing.

---

# 4. Architecture

The application is divided into three conceptual layers.

```text
                 ┌─────────────────────┐
                 │    BankingApp        │
                 │     Entry Point      │
                 └──────────┬──────────┘
                            │
                            ▼
                 ┌─────────────────────┐
                 │   BankingService    │
                 │ CLI + Orchestration  │
                 └──────────┬──────────┘
                            │
             ┌──────────────┼───────────────┐
             ▼              ▼               ▼
          User          SavingsAccount  InvestmentAccount
             │              │               │
             │              │               ▼
             │              │             Fund
             │              │
             └──────────────┴───────► Account
```

### Entry point

`BankingApp`

Responsible for:

* Creating the single `Scanner`.
* Creating the `BankingService`.
* Starting the application.

### Application/service layer

`BankingService`

Responsible for:

* Login.
* Logout.
* Menu display.
* Reading user input.
* Routing menu choices.
* Coordinating operations between domain objects.
* Displaying user-facing messages.

### Domain layer

`User`

Represents a bank customer.

`Account`

Represents common account behaviour.

`SavingsAccount`

Represents a savings account and its interest behaviour.

`InvestmentAccount`

Represents investment funds and uninvested investment money.

`Fund`

Represents the predefined investment fund types.

### Exception layer

`InvalidAmountException`

Represents invalid banking operations involving monetary amounts.

---

# 5. Domain Model

A user owns three forms of money/state:

```text
User
│
├── cash
│
├── SavingsAccount
│
└── InvestmentAccount
     │
     ├── notInvestedBalance
     │
     └── investments
          ├── LOW_RISK
          ├── MEDIUM_RISK
          └── HIGH_RISK
```

The important distinction is between **cash**, **savings**, and **investment money**.

They are not interchangeable without an explicit operation.

---

# 6. Account Design

`Account` is the common abstraction for bank accounts.

The account abstraction exists because savings and investment accounts share common concepts:

* Balance.
* Deposit.
* Withdrawal.
* Positive amount validation.

The design avoids duplicating the fundamental account behaviour in every subclass.

Conceptually:

```text
             Account
                ▲
                │
       ┌────────┴────────┐
       │                 │
SavingsAccount    InvestmentAccount
```

## Encapsulation

Account balance is maintained internally.

External classes should not directly manipulate the balance.

Instead, state changes happen through operations such as:

```text
deposit()
withdraw()
```

This keeps validation and state transitions inside the appropriate domain object.

---

# 7. Money and BigDecimal

All monetary values use:

```java
BigDecimal
```

rather than:

```java
double
```

or:

```java
float
```

## Reason

Floating-point arithmetic can introduce precision errors.

For example, decimal financial calculations should not depend on binary floating-point representation.

Therefore:

```java
new BigDecimal("1000.00")
```

is preferred over:

```java
new BigDecimal(1000.00)
```

The application also uses explicit decimal formatting when displaying monetary values.

Expected display:

```text
$0.00
$100.00
$1000.00
```

rather than:

```text
$0
$100
$1000
```

## Money formatting decision

The CLI uses a two-decimal representation for displayed monetary values.

For example:

```java
amount.setScale(2)
```

is used where the output requires exactly two decimal places.

---

# 8. User and Cash Model

`User` represents a customer of the bank.

The class contains:

```text
name
cash
savingsAccount
investmentAccount
```

A new user is initialized with:

```text
Cash = $1000.00
Savings = $0.00
Investment = $0.00
```

## Why cash belongs to User

Cash is deliberately not represented as an `Account`.

The project specification says that each user starts with `$1000` in cash that has **not yet been deposited into the bank**.

Therefore:

```text
User.cash
```

represents money held outside the bank.

Only when the user deposits money does it move:

```text
Cash
  │
  │ Deposit
  ▼
SavingsAccount
```

When withdrawing:

```text
SavingsAccount
      │
      │ Withdraw
      ▼
    Cash
```

This distinction prevents accidental creation of money.

---

# 9. Savings Account

`SavingsAccount` extends `Account`.

Its primary special behaviour is the bank's 1% savings interest.

## Interest rule

Every time the user views the balance, 1% interest is applied.

Conceptually:

```text
Current balance
      │
      ▼
Calculate 1%
      │
      ▼
Add interest
      │
      ▼
Updated balance
```

For example:

```text
Initial balance: $1000.00

First view:
$1000.00 × 1% = $10.00
New balance = $1010.00

Second view:
$1010.00 × 1% = $10.10
New balance = $1020.10
```

This means the interest compounds because each calculation operates on the updated balance.

## Important implementation decision

The interest calculation is tied to the balance-view operation because that is how the project specification defines the behaviour.

This is not intended to represent a real-world banking interest schedule.

---

# 10. Investment System

The investment system consists of:

```text
InvestmentAccount
        │
        └── Fund
```

Three funds are predefined.

| Fund          | Appreciation |
| ------------- | -----------: |
| `LOW_RISK`    |           2% |
| `MEDIUM_RISK` |           5% |
| `HIGH_RISK`   |          10% |

The rates are stored as `BigDecimal`.

```java
LOW_RISK(BigDecimal.valueOf(0.02))
MEDIUM_RISK(BigDecimal.valueOf(0.05))
HIGH_RISK(BigDecimal.valueOf(0.10))
```

---

## InvestmentAccount state

An investment account contains:

```text
notInvestedBalance
investments
```

The `investments` collection is represented using:

```java
EnumMap<Fund, BigDecimal>
```

because the keys are enum values.

This is preferable to a general-purpose `HashMap` because the domain explicitly defines a fixed set of fund types.

The map is initialized with all available funds:

```text
LOW_RISK       -> $0.00
MEDIUM_RISK    -> $0.00
HIGH_RISK      -> $0.00
```

---

# 11. Investment Account Invariant

The total investment account balance is defined as:

```text
Total Investment Balance
=
Not Invested Balance
+
Sum of All Fund Balances
```

This invariant is maintained by recalculating the total after state-changing operations.

Conceptually:

```java
setBalance(
    notInvestedBalance + investedTotal
);
```

This prevents the account's total balance from becoming inconsistent with the individual components.

---

# 12. Investing

Money must first exist in the investment account's uninvested balance.

The flow is:

```text
Savings
   │
   │ Transfer
   ▼
Investment Account
   │
   │ Invest
   ▼
Fund
```

Investing therefore does not create money.

It moves money from:

```text
notInvestedBalance
```

to:

```text
investments[Fund]
```

---

# 13. Fund Appreciation

Fund gains are calculated using the fund's appreciation rate.

Conceptually:

```text
newFundValue
=
currentFundValue
+
(currentFundValue × appreciationRate)
```

For example, a `$100.00` investment in a 5% fund becomes:

```text
$100.00
+
$5.00
=
$105.00
```

The project specifies that appreciation occurs when the account balance is viewed.

Therefore `getInvestments()` triggers the gain calculation before returning the investment map.

---

# 14. Withdrawing Investments

The user can withdraw all investments.

The operation moves all money from the individual funds into:

```text
notInvestedBalance
```

and resets each fund to:

```text
$0.00
```

Conceptually:

```text
LOW_RISK       ─┐
MEDIUM_RISK    ─┼──► Not Invested Balance
HIGH_RISK      ─┘
```

The total investment account balance remains conserved.

---

# 15. Banking Service

`BankingService` is the main application coordinator.

It receives the single `Scanner` created by `BankingApp`.

```java
BankingService service = new BankingService(scanner);
service.start();
```

The service initializes the four supported users:

```text
Alice
Bob
Charlie
Diana
```

Each receives an independent `User` object.

---

# 16. Login System

The login process is:

```text
Enter your name to login:
        │
        ▼
Read input
        │
        ▼
Search users
        │
   ┌────┴─────┐
   │          │
 Found      Not Found
   │          │
   ▼          ▼
Welcome     Error
   │
   ▼
Menu
```

User comparison is case-insensitive.

For example:

```text
alice
Alice
ALICE
```

all resolve to the Alice user.

The displayed welcome message uses the user's canonical stored name.

Example:

```text
Welcome, Alice!
```

---

# 17. Session Management

The active session is represented by:

```java
User currentUser
```

When no user is logged in:

```java
currentUser == null
```

The application asks for a login.

When a user is logged in:

```java
currentUser != null
```

the banking menu is displayed.

This produces a simple state machine:

```text
             ┌──────────────┐
             │   LOGGED OUT │
             └──────┬───────┘
                    │ login
                    ▼
             ┌──────────────┐
             │  LOGGED IN   │
             └──────┬───────┘
                    │
          ┌─────────┴─────────┐
          │                   │
        logout              exit
          │                   │
          ▼                   ▼
    LOGGED OUT              STOP
```

---

# 18. Logout

Logout does not destroy the user.

It simply clears the active session:

```java
currentUser = null;
```

The user's state remains stored in `userList`.

Therefore, if Alice deposits money, logs out, and later logs in again, Alice's previous state remains available.

The application displays:

```text
You have been logged out.
```

---

# 19. Exit

Exit sets the application running state to false.

```java
isRunning = false;
```

The application deliberately does **not** call:

```java
System.exit()
```

because the automated tests need the Java process to terminate naturally and retain control of the test environment.

The application displays:

```text
Thank you for using our banking app. Goodbye!
```

---

# 20. Supported Banking Operations

## 1. Show Balance

Displays:

```text
Savings account balance: $...
Investment account balance:
* Not Invested: $...
```

Active investments are displayed individually.

Example:

```text
* LOW_RISK: $102.00
```

Viewing the balance also triggers:

* Savings interest calculation.
* Investment fund appreciation.

---

## 2. Deposit Money

Money moves:

```text
User cash
    │
    ▼
Savings account
```

The operation requires:

* Valid numeric amount.
* Positive amount.
* Sufficient cash.

If successful:

```text
Deposit successful.
```

---

## 3. Withdraw Money

Money moves:

```text
Savings account
      │
      ▼
   User cash
```

The operation requires:

* Valid amount.
* Positive amount.
* Sufficient savings balance.

---

## 4. Send Money

Money moves:

```text
Current user's savings
          │
          ▼
Recipient's savings
```

The operation validates:

* Recipient exists.
* Recipient is not the current user.
* Amount is numeric.
* Amount is positive.
* Sender has sufficient savings funds.

---

## 5. Invest in Funds

The user selects one of:

```text
LOW_RISK
MEDIUM_RISK
HIGH_RISK
```

Money moves:

```text
Investment account
       │
       │ invest
       ▼
Selected Fund
```

The operation requires:

* Valid fund.
* Positive amount.
* Sufficient uninvested investment balance.

---

## 6. Transfer Between Accounts

Two directions are supported:

```text
1. Savings → Investment
2. Investment → Savings
```

This operation does not create or destroy money.

It simply moves the specified amount between the two accounts.

---

## 7. Withdraw All Investments

All money currently held in funds is moved into the investment account's uninvested balance.

---

## 8. Logout

Clears the current session.

---

## 9. Exit

Stops the main application loop without using `System.exit()`.

---

# 21. Validation and Exceptions

Invalid monetary operations are represented by:

```java
InvalidAmountException
```

The system validates amounts before changing state.

General validation rule:

```text
amount > 0
```

Zero and negative amounts are rejected.

Insufficient funds are also rejected.

Examples of domain failures include:

```text
Insufficient funds
Deposit failed: Insufficient cash on hand
```

The service layer translates domain failures into user-facing CLI messages where required by the automated tests.

---

# 22. Transaction Safety

An important engineering principle is:

> Validate before mutating state.

For example, a deposit should conceptually execute in this order:

```text
Read amount
   ↓
Parse amount
   ↓
Validate amount
   ↓
Validate available cash
   ↓
Deduct cash
   ↓
Deposit into savings
```

The application should not deduct money before confirming that the subsequent operation can succeed.

Similarly, withdrawals validate the account balance before changing it.

This protects against partial state updates.

---

# 23. CLI Output Contract

One of the most important lessons from the automated tests is that **CLI output is part of the application contract**.

The tests compare stdout exactly.

Therefore these are different:

```text
Deposit successful.
```

and:

```text
Deposit Successful.
```

Likewise:

```text
Transfer
```

and:

```text
Tranfer
```

are different.

Capitalization, punctuation, spaces, and newlines matter.

The final implementation therefore treats user-facing messages as contractual output rather than informal text.

---

## Logout

```text
You have been logged out.
```

## Exit

```text
Thank you for using our banking app. Goodbye!
```

---

# 25. EOF Handling

The application must handle EOF gracefully.

Before reading user input, the application checks:

```java
scanner.hasNextLine()
```

This prevents the application from attempting to read input that does not exist.

For example:

```text
Program starts
        ↓
Enter your name to login:
        ↓
EOF
        ↓
Application stops gracefully
```

This is particularly important for automated testing where stdin may terminate unexpectedly.

---

# 26. Scanner Design Decision

The project requires only one `Scanner` instance during the lifetime of the program.

The implementation follows this design:

```java
Scanner scanner = new Scanner(System.in);
```

in `BankingApp`.

That same scanner is passed to:

```java
BankingService
```

The service does not create another scanner.

This avoids multiple objects competing for the same `System.in` stream.

---

# 27. Static Method Restriction

The project specifically prohibits static methods except `main()`.

The implementation therefore keeps application and domain behaviour as instance methods.

The only static method is:

```java
public static void main(String[] args)
```

This is important for automated tests because tests may instantiate classes directly and expect normal object state.

---

# 28. Project Structure

The final project follows this structure:

```text
.
├── BankingApp.java
│
├── model
│   ├── Account.java
│   ├── Fund.java
│   ├── InvestmentAccount.java
│   ├── SavingsAccount.java
│   └── User.java
│
├── service
│   └── BankingService.java
│
└── exception
    └── InvalidAmountException.java
```

Compiled classes are kept separately when using a `bin` directory.

---

# 29. Class Responsibilities

## BankingApp

**Responsibility:** Application entry point.

Creates:

```text
Scanner
BankingService
```

and starts the service.

It should contain minimal business logic.

---

## BankingService

**Responsibility:** Application orchestration and CLI.

Handles:

* Login.
* Menu.
* Input.
* Session state.
* Operation selection.
* User-facing output.

It coordinates domain objects rather than owning their internal financial state.

---

## User

**Responsibility:** Represent a customer.

Contains:

```text
name
cash
SavingsAccount
InvestmentAccount
```

---

## Account

**Responsibility:** Common account behaviour.

Provides the foundation for:

```text
SavingsAccount
InvestmentAccount
```

---

## SavingsAccount

**Responsibility:** Savings-specific behaviour.

Adds:

```text
1% interest
```

to the common account behaviour.

---

## InvestmentAccount

**Responsibility:** Investment state and operations.

Manages:

```text
notInvestedBalance
Fund → balance
```

and handles:

* Investing.
* Fund appreciation.
* Investment withdrawal.
* Investment account balance.

---

## Fund

**Responsibility:** Define available investment products.

Each enum value contains its appreciation rate.

---

## InvalidAmountException

**Responsibility:** Represent invalid financial operations.

Separating this into its own exception makes monetary validation explicit and allows the service layer to respond appropriately.

---

#

# 31. Automated Testing Lessons

The automated tests exposed several important engineering realities.

## Output is an API

For a CLI application, stdout is effectively part of the public API.

A test can distinguish:

```text
Deposit successful.
```

from:

```text
Deposit Successful.
```

Therefore output strings must be treated as constants/contracts.

---

## Hidden whitespace matters

A test can fail because of:

* One extra space.
* Missing space.
* Extra newline.
* Missing newline.
* Capitalization.
* Punctuation.

When debugging such failures, compare output using tools such as:

```bash
diff
```

and:

```bash
cat -A
```

to expose invisible characters.

---

## Tests provide behavioural specifications

The assignment intentionally leaves some implementation details undisclosed.

Automated test feedback therefore becomes an additional source of specification.

The engineering process used was:

```text
Implement
   ↓
Run tests
   ↓
Read exact diff
   ↓
Identify behavioural mismatch
   ↓
Fix
   ↓
Run tests again
```

This is effectively a black-box contract discovery process.

---

# 32. Engineering Decisions Summary

| Decision                         | Reason                                                      |
| -------------------------------- | ----------------------------------------------------------- |
| `BigDecimal` for money           | Financial precision                                         |
| `Account` as abstraction         | Avoid duplicated account logic                              |
| Cash belongs to `User`           | Cash starts outside the bank                                |
| `EnumMap` for funds              | Fixed enum-based keys                                       |
| One `Scanner`                    | Required by specification and avoids input-stream conflicts |
| `currentUser` for session        | Simple explicit session state                               |
| `System.exit()` avoided          | Required for automated testing                              |
| Interest on balance view         | Matches project specification                               |
| Fund gains on balance view       | Matches project specification                               |
| Validation before mutation       | Prevent partial transactions                                |
| Instance methods                 | Required by project restrictions                            |
| Exact output strings             | Automated tests compare stdout                              |
| EOF checked with `hasNextLine()` | Prevent input-related crashes                               |

---

# 33. Data Flow Examples

## Deposit

```text
User
 │
 │ cash = $1000
 │
 │ deposit $100
 ▼
Validate amount
 │
 ▼
Validate cash >= $100
 │
 ├───────────────┐
 ▼               ▼
Cash - $100     Savings + $100
 │               │
 ▼               ▼
$900            $100
```

Total money remains:

```text
$900 + $100 = $1000
```

---

## Savings → Investment

```text
Savings
$100
 │
 │ transfer $100
 ▼
Savings
$0

Investment
$0
 │
 ▼
Investment
$100
```

The transfer changes location, not total wealth.

---

## Investment

```text
Investment account
Not Invested: $100
        │
        │ invest $100
        ▼
LOW_RISK: $100

Not Invested: $0
```

After a 2% appreciation event:

```text
LOW_RISK: $102
```

---

## Investment Withdrawal

```text
LOW_RISK: $102
MEDIUM_RISK: $50
HIGH_RISK: $0
        │
        │ withdraw all
        ▼
Not Invested: $152

LOW_RISK: $0
MEDIUM_RISK: $0
HIGH_RISK: $0
```

---

# 34. State Invariants

The following invariants are important to preserve.

### User cash

```text
cash >= 0
```

### Savings balance

```text
savings balance >= 0
```

### Investment uninvested balance

```text
notInvestedBalance >= 0
```

### Individual investment balances

```text
fund balance >= 0
```

### Investment total

```text
investment account balance
=
not invested balance
+
all fund balances
```

### Transfer conservation

Transfers should move existing money rather than create money.

For example:

```text
Savings before + Investment before
=
Savings after + Investment after
```

for an internal transfer.

---

# 35. Separation of Concerns

A central design principle of the project is:

> The CLI should coordinate banking operations, not implement every banking rule itself.

For example, the service can request:

```java
savingsAccount.withdraw(amount);
```

instead of manually modifying the account's balance.

Similarly, investment logic belongs in:

```java
InvestmentAccount
```

rather than being duplicated in `BankingService`.

This keeps the system easier to reason about and test.

---

# 36. Why This Architecture Is Extendable

The current architecture makes it possible to add functionality without rewriting the entire application.

Potential future additions could include:

* More users.
* Additional account types.
* More investment funds.
* Transaction history.
* Persistent storage.
* Scheduled interest.
* Authentication.
* Database integration.
* REST API.
* Audit logging.

For example, a future `CheckingAccount` could extend:

```text
Account
```

without changing the fundamental account abstraction.

Likewise, adding a new fund only requires another enum value:

```text
Fund
```

with its corresponding appreciation rate.

---

# 37. Current Limitations

This project is intentionally a simplified educational banking system.

It does not currently provide:

* Database persistence.
* Authentication passwords.
* Concurrent sessions.
* Transaction history.
* Real-world financial interest schedules.
* Monetary transaction IDs.
* External banking integrations.
* Thread safety.
* Persistent user storage.

The user state exists only for the lifetime of the application process.

These limitations are intentional and keep the implementation aligned with the assignment scope.

---

# 38. Running the Application

From the project root, compile the source files into a separate output directory.

For example:

```bash
rm -rf bin
mkdir bin
javac -d bin $(find . -name "*.java")
```

Then run:

```bash
java -cp bin BankingApp
```

The exact compilation command may vary depending on the development environment.

---



# 40. Final Engineering Perspective

The main engineering lesson from Green Day Bank is that a small application still benefits from deliberate architecture.

The project separates:

```text
Application Entry Point
        ↓
Service / CLI
        ↓
Domain Objects
        ↓
Financial State
```

The most important design principles applied are:

1. **Encapsulation**
   Financial state is managed by the objects that own it.

2. **Single Responsibility**
   Users, accounts, funds, exceptions, and CLI orchestration have distinct responsibilities.

3. **Inheritance where behaviour is shared**
   `SavingsAccount` and `InvestmentAccount` derive common account behaviour from `Account`.

4. **Composition for ownership**
   `User` owns its cash and accounts.

5. **Precise financial arithmetic**
   `BigDecimal` is used instead of floating-point types.

6. **Validation before mutation**
   Invalid transactions are rejected before state changes occur.

7. **State-machine thinking**
   The application explicitly models logged-out, logged-in, and terminated states.

8. **Contract-driven CLI development**
   Automated tests define exact output requirements.

9. **Graceful termination**
   EOF and normal exit are handled without forcibly terminating the JVM.

10. **Test feedback as specification**
    Automated test diffs were used to discover undocumented behavioural and formatting requirements.

---


# Conclusion

Green Day Bank demonstrates the implementation of a stateful banking domain using Java object-oriented design.

Although the application is intentionally small, the implementation applies several principles that scale to larger systems:

```text
Clear domain model
        +
Encapsulation
        +
Validation
        +
Precise financial representation
        +
Separation of concerns
        +
Explicit state management
        +
Automated testing
        =
Maintainable banking application
```

The project should be viewed not merely as a CLI exercise, but as practice in translating a textual specification into a set of domain models, invariants, state transitions, interfaces, and externally observable behaviour.

