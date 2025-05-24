package com.affiliate.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.affiliate.model.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	// Example method to fetch latest products
	List<Product> findTop4ByOrderByCreatedAtDesc(Pageable pageable);
}
