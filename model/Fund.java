package model;

import java.math.BigDecimal;

public enum Fund {
    LOW_RISK(BigDecimal.valueOf("0.02")),    // 2% appreciation
    MEDIUM_RISK(BigDecimal.valueOf("0.05")), // 5% appreciation
    HIGH_RISK(BigDecimal.valueOf("0.10"));   // 10% appreciation

    private final BigDecimal appreciationRate;

    Fund(BigDecimal appreciationRate) {
        this.appreciationRate = appreciationRate;
    }

    public BigDecimal getAppreciationRate() {
        return appreciationRate;
    }
}
