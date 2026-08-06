package model;

import java.math.BigDecimal;

// Represents a savings account. Every time the balance is viewed,the account earns 1% interest.
public class SavingsAccount extends Account {

    // Savings account interest rate.
    private static final BigDecimal INTEREST_RATE = new BigDecimal("0.01");
    private BigDecimal balance; 

    // Creates an empty savings account.
    public SavingsAccount(BigDecimal balance) {
        super();
        this.balance = balance;
    }

    /**
     * Returns the balance after applying
     * 1% compound interest.
     *
     * Every call to this method permanently
     * updates the stored balance.
     *
     * @return updated balance
     */
    @Override
    public BigDecimal getBalance() {

        BigDecimal currentBalance = getCurrentBalance();

        BigDecimal interest =
                currentBalance.multiply(INTEREST_RATE);

        BigDecimal updatedBalance =
                currentBalance.add(interest);

        setBalance(updatedBalance);

        return updatedBalance;
    }
}