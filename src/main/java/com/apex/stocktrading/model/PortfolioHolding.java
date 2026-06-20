package com.apex.stocktrading.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class PortfolioHolding {
    @Id
    private String symbol;
    private int sharesOwned;
    private double averageCost;

    public PortfolioHolding() {}

    public PortfolioHolding(String symbol, int sharesOwned, double averageCost) {
        this.symbol = symbol;
        this.sharesOwned = sharesOwned;
        this.averageCost = averageCost;
    }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public int getSharesOwned() { return sharesOwned; }
    public void setSharesOwned(int sharesOwned) { this.sharesOwned = sharesOwned; }
    public double getAverageCost() { return averageCost; }
    public void setAverageCost(double averageCost) { this.averageCost = averageCost; }
}
