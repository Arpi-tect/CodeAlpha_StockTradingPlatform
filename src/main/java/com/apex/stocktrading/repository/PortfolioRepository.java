package com.apex.stocktrading.repository;

import com.apex.stocktrading.model.PortfolioHolding;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<PortfolioHolding, String> {
}
