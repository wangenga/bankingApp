package model;

import exception.InvalidAmountException;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Abstract representation of a bank account.
 *
 * All accounts in the system (Savings and Investment)
 * inherit the common behaviour implemented here.
 */
public abstract class Account {

    // Current account balance.It is private to preserve encapsulation.
    private BigDecimal balance = BigDecimal.ZERO;

    // Creates an account with an initial balance of zero.
    public Account() {
        this.balance = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    // Deposits money into this account. throws InvalidAmountException if amount is null or not positive.
    public void deposit(BigDecimal amount) throws InvalidAmountException {
        validatePositiveAmount(amount);

        // BigDecimal is immutable, therefore we assign the result.
        balance = balance.add(amount.setScale(2, RoundingMode.HALF_UP));
    }

    // Withdraws money from this account. .throws InvalidAmountException if amount is invalid or there are insufficient funds
    public void withdraw(BigDecimal amount) throws InvalidAmountException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new InvalidAmountException("Withdrawal failed: amount must be positive");
        };

        if (balance.compareTo(amount) < 0) {
            throw new InvalidAmountException("Withdrawal failed: Insufficient funds");
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
        return balance.setScale(2, RoundingMode.HALF_UP);
    }

    // Allows subclasses to update the balance
    // without exposing it publicly.
    public void setBalance(BigDecimal newBalance) {
        this.balance = newBalance;
    }

    // Gives subclasses read access to the raw balance. return current balance
    protected BigDecimal getCurrentBalance() {
        return balance.setScale(2, RoundingMode.HALF_UP);
    }

    // Validates that an amount is greater than zero, throws InvalidAmountException if invalid
    private void validatePositiveAmount(BigDecimal amount) throws InvalidAmountException {
        if (amount == null) {
            throw new InvalidAmountException("Amount cannot be null.");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Deposit failed: amount must be positive");
        }
    }
}