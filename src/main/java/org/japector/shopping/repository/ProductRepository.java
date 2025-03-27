package org.japector.shopping.repository;

import java.util.List;
import java.util.Optional;

import org.japector.shopping.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findById(Long id);

    void deleteById(Long id);

    List<ProductEntity> findByCategory(String category);

}