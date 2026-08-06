package model;

import exception.InvalidAmountException;
import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

public class InvestmentAccount extends Account {

    // Represents funds not yet invested
    private BigDecimal notInvestedBalance = BigDecimal.ZERO;

    // Represents funds invested in specific funds
    private final Map<Fund, BigDecimal> investments;

    public InvestmentAccount() {
        this.investments = new EnumMap<>(Fund.class);
        this.notInvestedBalance = BigDecimal.ZERO;

        for (Fund fund : Fund.values()) {
            investments.put(fund, BigDecimal.ZERO);
        }

        // Initial total balance calculation
        updateTotalBalance();
    }


    // Total balance is the sum of notInvestedBalance and all investments
    private void updateTotalBalance() {
        BigDecimal total = notInvestedBalance;

        for (BigDecimal investedAmount : investments.values()) {
            total = total.add(investedAmount);
        }

        // Call setBalance from Account class
        setBalance(total);
    }

    @Override
    public void deposit(BigDecimal amount) throws InvalidAmountException {
        validatePositive(amount, "Deposit amount must be positive.");

        // Depositing into investment account adds to the notInvestedBalance
        // Add amount to internal balance
        this.notInvestedBalance = this.notInvestedBalance.add(amount);
        updateTotalBalance();
    }

    @Override
    public void withdraw(BigDecimal amount)throws InvalidAmountException {
        validatePositive(amount, "Withdraw amount must be positive.");

        if (this.notInvestedBalance.compareTo(amount) < 0) {
            throw new InvalidAmountException("Insufficient funds");
        }

        // Withdrawal from investment account comes from notInvestedBalance
        // Subtract amount from internal balance
        this.notInvestedBalance = this.notInvestedBalance.subtract(amount);
        updateTotalBalance();
    }

    // Withdraw all invested amounts back to not invested balance
    public void withdrawAllInvestments() {
        for (Fund fund : Fund.values()) {
            this.notInvestedBalance = this.notInvestedBalance.add(investments.get(fund));
            investments.put(fund, BigDecimal.ZERO);
        }

        updateTotalBalance();
    }

    // Transfer funds from not invested to a specific fund
    public void invest(Fund fund, BigDecimal amount)throws InvalidAmountException  {
        validatePositive(amount, "Investment amount must be positive.");

        if (this.notInvestedBalance.compareTo(amount) < 0) {
            throw new InvalidAmountException("Insufficient funds.");
        }

        // Update internal balances
        this.notInvestedBalance = this.notInvestedBalance.subtract(amount);
        BigDecimal currentInvestment = investments.get(fund);
        investments.put(fund, currentInvestment.add(amount));
        updateTotalBalance();
    }

    // Calculate gains for each fund based on its appreciation rate
    public void calculateFundGains() {
        for (Map.Entry<Fund, BigDecimal> entry : investments.entrySet()) {
            Fund fund = entry.getKey();
            BigDecimal currentAmount = entry.getValue();

            if (currentAmount.compareTo(BigDecimal.ZERO) > 0) {
                // Calculate gains then update investment amounts
                BigDecimal gain = currentAmount.multiply(fund.getAppreciationRate());
                investments.put(fund, currentAmount.add(gain));
            }
        }

        // Update total balance after calculating gains
        updateTotalBalance();
    }

    // Get balance of funds not invested
    public BigDecimal getNotInvestedBalance() {
        return notInvestedBalance;
    }

    // Get balance of a single fund
    public BigDecimal getInvestmentBalance(Fund fund) {
        return investments.getOrDefault(fund, BigDecimal.ZERO);
    }

    // Get total of ALL invested funds and their balances
    public Map<Fund, BigDecimal> getInvestments() {
        return investments;
    }

    // Check if number received is positive
    private void validatePositive(BigDecimal amount, String msg) throws InvalidAmountException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(msg);
        }
    }
}
