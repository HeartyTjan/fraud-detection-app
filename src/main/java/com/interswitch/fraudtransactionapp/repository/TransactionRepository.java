package com.interswitch.fraudtransactionapp.repository;

import com.interswitch.fraudtransactionapp.model.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transactions, Long>{
}
