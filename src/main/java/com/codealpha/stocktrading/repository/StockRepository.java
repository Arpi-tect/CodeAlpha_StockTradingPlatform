package com.codealpha.stocktrading.repository;

import com.codealpha.stocktrading.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, String> {
}
