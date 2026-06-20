package com.codealpha.stocktrading.repository;

import com.codealpha.stocktrading.model.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionLogRepository extends JpaRepository<TransactionLog, Long> {
}
