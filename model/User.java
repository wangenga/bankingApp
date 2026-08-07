package model;

import java.math.BigDecimal;

import exception.InvalidAmountException;

public class User{
    private String name;
    private BigDecimal cash;
    private SavingsAccount savingsAccount;
    private InvestmentAccount investmentAccount;

    public User(String name, BigDecimal initialCash){
        this.name = name;
        this.cash = initialCash;
        this.savingsAccount = new SavingsAccount(BigDecimal.ZERO);
        this.investmentAccount = new InvestmentAccount();
    
    }

    public String getName(){
        return name;
    }

    public BigDecimal getCash(){
        return cash;
    }

    public SavingsAccount getSavingsAccount(){
        return savingsAccount;
    }

    public void deductCash (BigDecimal amount) throws InvalidAmountException{
        if (this.cash.compareTo(amount) < 0) {
            throw new InvalidAmountException("Deposit failed: Insufficient cash on hand");
        }
        this.cash = this.cash.subtract(amount);
    }

    public void addCash (BigDecimal amount){
        this.cash = this.cash.add(amount);
    }

    public InvestmentAccount getInvestmentAccount(){
        return investmentAccount;
    }

}