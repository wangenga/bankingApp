package model;

import java.math.BigDecimal;

public class User{
    private String name;
    private BigDecimal cash;
    private SavingsAccount SavingsAccount;
    private InvestmentAccount InvestmentAccount;

    public User(String name, BigDecimal initialCash){
        this.name = name;
        this.cash = initialCash;
        this.savingsAccount = new SavingsAccount(BigDecimal.ZERO);
        this.investmentAccount = new InvestmentAccount(BigDecimal.ZERO);
    
    }
    public String getName(){
        return name;
    }

    public BigDecimal getCash(){
        return cash;
    }

    public SavingsAccount getSavingAccount(){
        return savingsAccount;
    }

    public InvestmentAccount getInvestmentAccount(){
        return investmentAccount;
    }

}