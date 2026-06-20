# ApexTrade - Advanced Stock Portfolio Simulator

An enterprise-grade, full-stack financial stock trading simulator dashboard built with a **Java Spring Boot** backend engine and a dynamic, responsive **React.js & Tailwind CSS** web dashboard.

---

## 🚀 Features

*   **Live Market Terminal**: Mock tickers (AAPL, MSFT, GOOG, AMZN, TSLA) with prices that fluctuate randomly by ±1.5% every 2.5 seconds via background API simulation.
*   **Real-time SVG Price Line Charts**: Selected stock renders a live-drawing SVG line chart plotting price history trends over the last 10 simulation ticks in real-time.
*   **Interactive Asset Donut Chart**: Renders a custom SVG donut chart showing your portfolio's asset allocation breakdown (Cash vs Stocks owned value percentages).
*   **Portfolio ROI Analysis**: Tracks cash balance ($10k starting capital), average buy-in cost, current price, and absolute ROI percentage gains/losses for each holding.
*   **Asset Watchlist**: Toggle star icons on any stock to customize your watchlist, allowing instant table filtering between "All Assets" and "Watchlist".
*   **Audit Audit Ledger**: Scrolling transaction table tracking execution timestamp, action type (BUY/SELL), quantity, share prices, and total value.
*   **Google OAuth & Guest Login**: Security gateway supporting real Google Identity Services authentication or one-click guest dashboard bypass.

---

## 🛠️ Technology Stack

*   **Backend**: Java 17, Spring Boot 3.x, Spring Data JPA, H2 Database.
*   **Frontend**: React (ES6+), Tailwind CSS, Babel, HTML5, Custom Responsive SVG Line/Donut Charts.
*   **Build Tool**: Maven Wrapper (included).

---

## 💻 How to Run Locally

You only need **Java 17 or higher** installed.

### Step 1: Clone the repository
```bash
git clone https://github.com/Arpi-tect/CodeAlpha_StockTradingPlatform.git
cd CodeAlpha_StockTradingPlatform
```

### Step 2: Start the Application
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

The server will build and initialize the database on port `8081`.

### Step 3: Open in Browser
👉 **[http://localhost:8081](http://localhost:8081)**

---

## 📁 Repository Structure
```text
StockTradingPlatform/
├── pom.xml                     # Maven configurations
├── mvnw & mvnw.cmd             # Maven Wrapper scripts
├── data/                       # H2 database file storage
└── src/
    └── main/
        ├── java/com/apex/stocktrading/
        │   ├── StockTradingPlatformApplication.java  # Main Boot class
        │   ├── model/                  # JPA Database Entities
        │   │   ├── Stock.java          # Market Stock prices
        │   │   ├── PortfolioHolding.java  # Owned Assets
        │   │   └── TransactionLog.java  # Trade Logs
        │   ├── repository/             # JPA data access layers
        │   └── controller/             # REST controllers
        │       └── TradingController.java  # Business API endpoints
        └── resources/
            ├── application.properties               # Config file (Port 8081)
            └── static/
                └── index.html                       # React Web Client
```

---

## 📡 REST API Documentation

*   **Get Live Market Prices**: `GET /api/trading/stocks`
*   **Get Portfolio Values**: `GET /api/trading/portfolio` (Returns cash balance, asset holdings value, overall profit/loss metrics)
*   **Execute Order**: `POST /api/trading/trade?symbol=AAPL&quantity=10&type=BUY`
*   **Get Transaction Logs**: `GET /api/trading/transactions`
*   **Simulate Market Tick**: `POST /api/trading/simulate` (Triggers random price fluctuations)
