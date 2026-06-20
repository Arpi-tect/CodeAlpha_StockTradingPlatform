package com.codealpha.stocktrading.repository;

import com.codealpha.stocktrading.model.PortfolioHolding;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<PortfolioHolding, String> {
}
