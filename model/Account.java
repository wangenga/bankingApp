package model;

import exception.InvalidAmountException;

import java.math.BigDecimal;

/**
 * Abstract representation of a bank account.
 *
 * All accounts in the system (Savings and Investment)
 * inherit the common behaviour implemented here.
 */
public abstract class Account {

    // Current account balance.It is private to preserve encapsulation.
    private BigDecimal balance;

    // Creates an account with an initial balance of zero.
    public Account() {
        this.balance = BigDecimal.ZERO;
    }

    // Deposits money into this account. throws InvalidAmountException if amount is null or not positive.
    public void deposit(BigDecimal amount) throws InvalidAmountException {
        validatePositiveAmount(amount);

        // BigDecimal is immutable, therefore we assign the result.
        balance = balance.add(amount);
    }

    // Withdraws money from this account. .throws InvalidAmountException if amount is invalid or there are insufficient funds
    public void withdraw(BigDecimal amount) throws InvalidAmountException {
        validatePositiveAmount(amount);

        if (balance.compareTo(amount) < 0) {
            throw new InvalidAmountException("Insufficient funds.");
        }

        balance = balance.subtract(amount);
    }

    /**
     * Returns the current account balance.
     *
     * Child classes may override this method
     * to implement additional behaviour
     * (e.g. savings interest).
     *
     * @return current balance
     */
    public BigDecimal getBalance() {
        return balance;
    }

    // Allows subclasses to update the balance
    // without exposing it publicly.
    protected void setBalance(BigDecimal newBalance) {
        this.balance = newBalance;
    }

    // Gives subclasses read access to the raw balance. return current balance
    protected BigDecimal getCurrentBalance() {
        return balance;
    }

    // Validates that an amount is greater than zero, throws InvalidAmountException if invalid
    private void validatePositiveAmount(BigDecimal amount) throws InvalidAmountException {
        if (amount == null) {
            throw new InvalidAmountException("Amount cannot be null.");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero.");
        }
    }
}