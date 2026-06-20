import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class StockTradingPlatform extends JFrame {
    // Financial metrics
    private double cashBalance = 10000.0;
    private double portfolioValue = 10000.0;

    // Data structures
    private Map<String, Stock> marketStocks;
    private Map<String, Integer> portfolioHoldings; // Symbol -> Shares Owned
    private Map<String, Double> portfolioAvgCost;   // Symbol -> Avg Purchase Cost
    
    // UI Elements
    private DefaultTableModel marketTableModel;
    private DefaultTableModel portfolioTableModel;
    private JLabel cashLabel;
    private JLabel portfolioValLabel;
    private JLabel profitLossLabel;
    private JComboBox<String> stockComboBox;
    private JTextField quantityField;
    private JTextArea logArea;
    
    private final String PORTFOLIO_FILE = "portfolio.csv";
    private final Random random = new Random();

    public StockTradingPlatform() {
        marketStocks = new HashMap<>();
        portfolioHoldings = new HashMap<>();
        portfolioAvgCost = new HashMap<>();
        
        initializeMarket();
        initUI();
        loadPortfolioFromCSV();
        updateUIValues();
        
        // Start Stock Market Simulation Timer (flucuates prices every 2 seconds)
        Timer marketTimer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulateMarketFlucuation();
            }
        });
        marketTimer.start();
    }

    private void initializeMarket() {
        marketStocks.put("AAPL", new Stock("AAPL", "Apple Inc.", 175.50));
        marketStocks.put("MSFT", new Stock("MSFT", "Microsoft Corp.", 420.20));
        marketStocks.put("GOOG", new Stock("GOOG", "Alphabet Inc.", 150.10));
        marketStocks.put("AMZN", new Stock("AMZN", "Amazon.com Inc.", 178.40));
        marketStocks.put("TSLA", new Stock("TSLA", "Tesla Inc.", 170.80));
    }

    private void initUI() {
        setTitle("CodeAlpha - Stock Trading Platform");
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Styling
        Color primaryColor = new Color(32, 56, 100);
        Color accentColor = new Color(31, 90, 168);
        Color bgColor = new Color(245, 245, 245);
        getContentPane().setBackground(bgColor);

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(primaryColor);
        JLabel titleLabel = new JLabel("SIMULATED STOCK TRADING PLATFORM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Main Center Panel (Grid Layout with Tables)
        JPanel mainPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top half: Stock Market List
        JPanel marketPanel = new JPanel(new BorderLayout());
        marketPanel.setBorder(BorderFactory.createTitledBorder("Live Stock Market prices"));
        marketPanel.setBackground(Color.WHITE);

        String[] marketColumns = {"Symbol", "Company Name", "Price ($)", "Change (%)"};
        marketTableModel = new DefaultTableModel(marketColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        
        for (Stock s : marketStocks.values()) {
            marketTableModel.addRow(new Object[]{s.symbol, s.name, String.format("%.2f", s.price), "0.00%"});
        }
        
        JTable marketTable = new JTable(marketTableModel);
        marketTable.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane marketScroll = new JScrollPane(marketTable);
        marketPanel.add(marketScroll, BorderLayout.CENTER);
        mainPanel.add(marketPanel);

        // Bottom half: Portfolio Holdings
        JPanel portfolioPanel = new JPanel(new BorderLayout());
        portfolioPanel.setBorder(BorderFactory.createTitledBorder("My Portfolio Holdings"));
        portfolioPanel.setBackground(Color.WHITE);

        String[] portColumns = {"Symbol", "Shares Owned", "Avg Cost ($)", "Current Price ($)", "Total Value ($)", "P&L ($)"};
        portfolioTableModel = new DefaultTableModel(portColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable portfolioTable = new JTable(portfolioTableModel);
        JScrollPane portScroll = new JScrollPane(portfolioTable);
        portfolioPanel.add(portScroll, BorderLayout.CENTER);
        mainPanel.add(portfolioPanel);

        add(mainPanel, BorderLayout.CENTER);

        // East Panel - Actions, Logs and Account Balance Details
        JPanel eastPanel = new JPanel(new GridBagLayout());
        eastPanel.setPreferredSize(new Dimension(280, 500));
        eastPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.gridwidth = 1;

        // Account Stats Card
        JPanel statsCard = new JPanel(new GridLayout(3, 1, 5, 5));
        statsCard.setBorder(BorderFactory.createTitledBorder("Account Balance Summary"));
        statsCard.setBackground(Color.WHITE);

        cashLabel = new JLabel("Available Cash: $10,000.00");
        cashLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statsCard.add(cashLabel);

        portfolioValLabel = new JLabel("Portfolio Value: $10,000.00");
        portfolioValLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statsCard.add(portfolioValLabel);

        profitLossLabel = new JLabel("Net P&L: $0.00 (0.00%)");
        profitLossLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statsCard.add(profitLossLabel);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weighty = 0.1;
        eastPanel.add(statsCard, gbc);

        // Trading Panel
        JPanel tradePanel = new JPanel(new GridBagLayout());
        tradePanel.setBorder(BorderFactory.createTitledBorder("Execute Trade Order"));
        tradePanel.setBackground(Color.WHITE);
        GridBagConstraints tGbc = new GridBagConstraints();
        tGbc.insets = new Insets(4, 4, 4, 4);
        tGbc.fill = GridBagConstraints.HORIZONTAL;

        tGbc.gridx = 0; tGbc.gridy = 0;
        tradePanel.add(new JLabel("Select Stock:"), tGbc);

        stockComboBox = new JComboBox<>(marketStocks.keySet().toArray(new String[0]));
        tGbc.gridx = 1;
        tradePanel.add(stockComboBox, tGbc);

        tGbc.gridx = 0; tGbc.gridy = 1;
        tradePanel.add(new JLabel("Quantity:"), tGbc);

        quantityField = new JTextField("10");
        tGbc.gridx = 1;
        tradePanel.add(quantityField, tGbc);

        JButton buyButton = new JButton("Buy Shares");
        buyButton.setBackground(new Color(40, 110, 40));
        buyButton.setForeground(Color.WHITE);
        buyButton.setFocusPainted(false);
        tGbc.gridx = 0; tGbc.gridy = 2;
        tradePanel.add(buyButton, tGbc);

        JButton sellButton = new JButton("Sell Shares");
        sellButton.setBackground(new Color(150, 40, 40));
        sellButton.setForeground(Color.WHITE);
        sellButton.setFocusPainted(false);
        tGbc.gridx = 1;
        tradePanel.add(sellButton, tGbc);

        gbc.gridy = 1; gbc.weighty = 0.2;
        eastPanel.add(tradePanel, gbc);

        // Transaction Logs
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Transaction History Log"));
        logArea = new JTextArea(8, 20);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        logArea.setBackground(new Color(248, 248, 248));
        JScrollPane logScroll = new JScrollPane(logArea);
        logPanel.add(logScroll, BorderLayout.CENTER);

        gbc.gridy = 2; gbc.weighty = 0.7; gbc.fill = GridBagConstraints.BOTH;
        eastPanel.add(logPanel, gbc);

        add(eastPanel, BorderLayout.EAST);

        // Add action listeners
        buyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeTrade(true);
            }
        });

        sellButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeTrade(false);
            }
        });
    }

    private void executeTrade(boolean isBuy) {
        String symbol = (String) stockComboBox.getSelectedItem();
        String qtyStr = quantityField.getText().trim();

        if (qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid stock quantity.", "Order Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int qty = Integer.parseInt(qtyStr);
            if (qty <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than 0.", "Order Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Stock stock = marketStocks.get(symbol);
            double price = stock.price;
            double totalCost = price * qty;

            if (isBuy) {
                // Buy logic
                if (cashBalance < totalCost) {
                    JOptionPane.showMessageDialog(this, "Insufficient funds to execute buy order.", "Order Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                cashBalance -= totalCost;
                
                int owned = portfolioHoldings.getOrDefault(symbol, 0);
                double avgCost = portfolioAvgCost.getOrDefault(symbol, 0.0);
                
                // Calculate new average cost
                double totalValueOwned = (owned * avgCost) + totalCost;
                int newOwned = owned + qty;
                double newAvgCost = totalValueOwned / newOwned;
                
                portfolioHoldings.put(symbol, newOwned);
                portfolioAvgCost.put(symbol, newAvgCost);
                
                logTransaction(String.format("BOUGHT %d shares of %s at $%.2f", qty, symbol, price));
            } else {
                // Sell logic
                int owned = portfolioHoldings.getOrDefault(symbol, 0);
                if (owned < qty) {
                    JOptionPane.showMessageDialog(this, "You do not own enough shares of " + symbol + " to sell.", "Order Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                cashBalance += totalCost;
                int newOwned = owned - qty;
                
                if (newOwned == 0) {
                    portfolioHoldings.remove(symbol);
                    portfolioAvgCost.remove(symbol);
                } else {
                    portfolioHoldings.put(symbol, newOwned);
                }
                
                logTransaction(String.format("SOLD %d shares of %s at $%.2f", qty, symbol, price));
            }
            
            savePortfolioToCSV();
            updateUIValues();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantity must be an integer.", "Order Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void simulateMarketFlucuation() {
        for (String symbol : marketStocks.keySet()) {
            Stock s = marketStocks.get(symbol);
            // Flucuation between -1.5% and +1.5%
            double changePercent = (random.nextDouble() * 3.0) - 1.5;
            double changeVal = s.price * (changePercent / 100.0);
            s.price += changeVal;
            s.changePercent = changePercent;
        }

        // Update Stock Market Table
        for (int i = 0; i < marketTableModel.getRowCount(); i++) {
            String sym = (String) marketTableModel.getValueAt(i, 0);
            Stock s = marketStocks.get(sym);
            marketTableModel.setValueAt(String.format("%.2f", s.price), i, 2);
            marketTableModel.setValueAt(String.format("%+.2f%%", s.changePercent), i, 3);
        }

        updateUIValues();
    }

    private void updateUIValues() {
        // Calculate holdings total value
        double holdingsValue = 0.0;
        
        // Clear portfolio table model and redraw
        portfolioTableModel.setRowCount(0);
        
        for (String symbol : portfolioHoldings.keySet()) {
            int qty = portfolioHoldings.get(symbol);
            double avgCost = portfolioAvgCost.get(symbol);
            Stock stock = marketStocks.get(symbol);
            double currentPrice = stock.price;
            double currentValue = currentPrice * qty;
            holdingsValue += currentValue;
            
            double totalCost = avgCost * qty;
            double pnl = currentValue - totalCost;
            
            portfolioTableModel.addRow(new Object[]{
                symbol,
                qty,
                String.format("%.2f", avgCost),
                String.format("%.2f", currentPrice),
                String.format("%.2f", currentValue),
                String.format("%+.2f", pnl)
            });
        }
        
        portfolioValue = cashBalance + holdingsValue;
        
        // Display values
        cashLabel.setText(String.format("Available Cash: $%.2f", cashBalance));
        portfolioValLabel.setText(String.format("Portfolio Value: $%.2f", portfolioValue));
        
        double overallPnl = portfolioValue - 10000.0; // Starting with $10,000
        double overallPnlPercent = (overallPnl / 10000.0) * 100.0;
        
        profitLossLabel.setText(String.format("Net P&L: %+.2f (%+.2f%%)", overallPnl, overallPnlPercent));
        
        if (overallPnl >= 0) {
            profitLossLabel.setForeground(new Color(40, 110, 40));
        } else {
            profitLossLabel.setForeground(new Color(150, 40, 40));
        }
    }

    private void logTransaction(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void savePortfolioToCSV() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PORTFOLIO_FILE))) {
            writer.println(cashBalance); // First line is cash balance
            for (String symbol : portfolioHoldings.keySet()) {
                int qty = portfolioHoldings.get(symbol);
                double avgCost = portfolioAvgCost.get(symbol);
                writer.println(symbol + "," + qty + "," + avgCost);
            }
        } catch (IOException e) {
            System.err.println("Error saving portfolio: " + e.getMessage());
        }
    }

    private void loadPortfolioFromCSV() {
        File file = new File(PORTFOLIO_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line != null) {
                cashBalance = Double.parseDouble(line.trim());
            }
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String symbol = parts[0];
                    int qty = Integer.parseInt(parts[1]);
                    double avgCost = Double.parseDouble(parts[2]);
                    portfolioHoldings.put(symbol, qty);
                    portfolioAvgCost.put(symbol, avgCost);
                }
            }
            logTransaction("Loaded portfolio from " + PORTFOLIO_FILE);
        } catch (Exception e) {
            System.err.println("Error loading portfolio: " + e.getMessage());
        }
    }

    // Custom Stock Data Class
    private static class Stock {
        String symbol;
        String name;
        double price;
        double changePercent = 0.0;

        public Stock(String symbol, String name, double price) {
            this.symbol = symbol;
            this.name = name;
            this.price = price;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new StockTradingPlatform().setVisible(true);
            }
        });
    }
}
