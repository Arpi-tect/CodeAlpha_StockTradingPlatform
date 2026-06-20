package com.codealpha.stocktrading.controller;

import com.codealpha.stocktrading.model.PortfolioHolding;
import com.codealpha.stocktrading.model.Stock;
import com.codealpha.stocktrading.model.TransactionLog;
import com.codealpha.stocktrading.repository.PortfolioRepository;
import com.codealpha.stocktrading.repository.StockRepository;
import com.codealpha.stocktrading.repository.TransactionLogRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/trading")
@CrossOrigin(origins = "*")
public class TradingController {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private TransactionLogRepository transactionLogRepository;

    private double cashBalance = 10000.0;
    private final Random random = new Random();

    @PostConstruct
    public void seedStocks() {
        if (stockRepository.count() == 0) {
            stockRepository.save(new Stock("AAPL", "Apple Inc.", 175.50));
            stockRepository.save(new Stock("MSFT", "Microsoft Corp.", 420.20));
            stockRepository.save(new Stock("GOOG", "Alphabet Inc.", 150.10));
            stockRepository.save(new Stock("AMZN", "Amazon.com Inc.", 178.40));
            stockRepository.save(new Stock("TSLA", "Tesla Inc.", 170.80));
        }
    }

    @GetMapping("/stocks")
    public List<Stock> getStocks() {
        return stockRepository.findAll();
    }

    @GetMapping("/portfolio")
    public Map<String, Object> getPortfolio() {
        List<PortfolioHolding> holdings = portfolioRepository.findAll();
        double holdingsValue = 0.0;
        double totalCost = 0.0;

        for (PortfolioHolding h : holdings) {
            Stock s = stockRepository.findById(h.getSymbol()).orElse(null);
            if (s != null) {
                holdingsValue += s.getPrice() * h.getSharesOwned();
            }
            totalCost += h.getAverageCost() * h.getSharesOwned();
        }

        double portfolioValue = cashBalance + holdingsValue;
        double profitLoss = portfolioValue - 10000.0; // Initial cash
        double profitLossPercent = (profitLoss / 10000.0) * 100.0;

        Map<String, Object> result = new HashMap<>();
        result.put("cash", cashBalance);
        result.put("holdingsValue", holdingsValue);
        result.put("portfolioValue", portfolioValue);
        result.put("profitLoss", profitLoss);
        result.put("profitLossPercent", profitLossPercent);
        result.put("holdings", holdings);

        return result;
    }

    @PostMapping("/trade")
    public Map<String, Object> executeTrade(@RequestParam String symbol, @RequestParam int quantity, @RequestParam String type) {
        Map<String, Object> response = new HashMap<>();
        Stock stock = stockRepository.findById(symbol).orElse(null);

        if (stock == null) {
            response.put("success", false);
            response.put("message", "Stock not found.");
            return response;
        }

        double price = stock.getPrice();
        double cost = price * quantity;

        if ("BUY".equalsIgnoreCase(type)) {
            if (cashBalance < cost) {
                response.put("success", false);
                response.put("message", "Insufficient funds.");
                return response;
            }

            cashBalance -= cost;
            PortfolioHolding holding = portfolioRepository.findById(symbol).orElse(new PortfolioHolding(symbol, 0, 0.0));
            
            double previousTotalValue = holding.getSharesOwned() * holding.getAverageCost();
            int newShares = holding.getSharesOwned() + quantity;
            double newAvgCost = (previousTotalValue + cost) / newShares;

            holding.setSharesOwned(newShares);
            holding.setAverageCost(newAvgCost);
            portfolioRepository.save(holding);

            transactionLogRepository.save(new TransactionLog("BUY", symbol, quantity, price));
            response.put("success", true);
            response.put("message", "Shares purchased successfully.");
        } else if ("SELL".equalsIgnoreCase(type)) {
            PortfolioHolding holding = portfolioRepository.findById(symbol).orElse(null);
            if (holding == null || holding.getSharesOwned() < quantity) {
                response.put("success", false);
                response.put("message", "Not enough shares owned.");
                return response;
            }

            cashBalance += cost;
            int newShares = holding.getSharesOwned() - quantity;

            if (newShares == 0) {
                portfolioRepository.delete(holding);
            } else {
                holding.setSharesOwned(newShares);
                portfolioRepository.save(holding);
            }

            transactionLogRepository.save(new TransactionLog("SELL", symbol, quantity, price));
            response.put("success", true);
            response.put("message", "Shares sold successfully.");
        }

        return response;
    }

    @GetMapping("/transactions")
    public List<TransactionLog> getTransactions() {
        return transactionLogRepository.findAll();
    }

    @PostMapping("/simulate")
    public List<Stock> simulateMarket() {
        List<Stock> stocks = stockRepository.findAll();
        for (Stock s : stocks) {
            double changePercent = (random.nextDouble() * 3.0) - 1.5;
            double newPrice = s.getPrice() * (1 + changePercent / 100.0);
            s.setPrice(newPrice);
            s.setChangePercent(changePercent);
            stockRepository.save(s);
        }
        return stocks;
    }
}
