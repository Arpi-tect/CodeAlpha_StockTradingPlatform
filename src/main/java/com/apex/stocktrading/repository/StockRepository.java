package com.apex.stocktrading.repository;

import com.apex.stocktrading.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, String> {
}
