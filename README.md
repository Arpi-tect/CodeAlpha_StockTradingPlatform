# CodeAlpha_StockTradingPlatform

An advanced, full-stack financial stock trading simulator application built with a **Java Spring Boot** backend engine and a dynamic, responsive **React.js & Tailwind CSS** web dashboard.

## 🚀 Features
*   **Live Market Simulation**: Mock tickers (AAPL, MSFT, GOOG, AMZN, TSLA) with prices that fluctuate randomly by ±1.5% every 2.5 seconds via background API requests.
*   **Portfolio Manager**: Tracks cash balance ($10k starting capital), stock assets owned, average buy costs, and total portfolio valuation.
*   **Order Execution Panel**: Input validation for buying and selling shares (prevents buying beyond cash balance or selling unowned shares).
*   **Transaction Logs**: Scrolling audit logs tracking trade action type, quantity, share prices, and timestamps.
*   **Database Persistence**: Automatically saves cash, active portfolio holdings, and transaction logs using local file-based H2 database storage.
*   **Modern Analytics Dashboard**: Sleek visual grid card widgets displaying overall profit/loss value and percentages (color-coded red/green).

---

## 🛠️ Technology Stack
*   **Backend**: Java 17+, Spring Boot 3.x, Spring Data JPA, H2 Database.
*   **Frontend**: React (ES6+), Tailwind CSS, Babel, HTML5.
*   **Build Tool**: Maven Wrapper (included).

---

## 💻 How to Run Locally

You only need **Java 17 or higher** installed.

### Step 1: Clone the repository
```bash
git clone https://github.com/<your-username>/CodeAlpha_StockTradingPlatform.git
cd CodeAlpha_StockTradingPlatform
```

### Step 2: Boot up the Spring Boot Application
Run the Maven wrapper command in your terminal:

*   **On Windows**:
    ```cmd
    mvnw.cmd spring-boot:run
    ```
*   **On Linux / macOS**:
    ```bash
    chmod +x mvnw
    ./mvnw spring-boot:run
    ```

The server will build and initialize the local H2 file database, starting the Tomcat server on port `8081`.

### Step 3: Open in Browser
Open your browser and navigate to:
👉 **[http://localhost:8081](http://localhost:8081)**

---

## 📁 Repository Structure
```text
StockTradingPlatform/
├── pom.xml                     # Maven configurations
├── mvnw & mvnw.cmd             # Maven Wrapper scripts
├── data/                       # Local database file storage
└── src/
    └── main/
        ├── java/com/codealpha/stocktrading/
        │   ├── StockTradingPlatformApplication.java  # Main Boot class
        │   ├── model/                  # JPA Database Entities
        │   │   ├── Stock.java          # Market Stock prices
        │   │   ├── PortfolioHolding.java  # Owned Assets
        │   │   └── TransactionLog.java  # Trade Logs
        │   ├── repository/             # JPA data access layers
        │   └── controller/             # REST controllers
        │       └── TradingController.java  # Core business API endpoints
        └── resources/
            ├── application.properties               # Database properties (Port 8081)
            └── static/
                └── index.html                       # React & Tailwind Web Client
```

---

## 📡 REST API Documentation

*   **Get Live Market Price Tick**: `GET /api/trading/stocks`
*   **Get Portfolio Values**: `GET /api/trading/portfolio` (Returns cash balance, asset holdings value, overall profit/loss metrics)
*   **Execute Order**: `POST /api/trading/trade?symbol=AAPL&quantity=10&type=BUY`
*   **Get Transaction logs**: `GET /api/trading/transactions`
*   **Simulate Market Tick**: `POST /api/trading/simulate` (Triggers random stock price fluctuation)
