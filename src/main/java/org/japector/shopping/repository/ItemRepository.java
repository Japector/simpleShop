package org.japector.shopping.repository;

import java.util.List;
import java.util.Optional;

import org.japector.shopping.entity.ItemEntity;
import org.japector.shopping.entity.ProductEntity;
import org.japector.shopping.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
    List<ItemEntity> findByUser(UserEntity user);

    void deleteAllByUser(UserEntity user);

    void deleteByUserAndProduct(UserEntity user, ProductEntity product);

    Optional<ItemEntity> findByUserAndProduct(UserEntity user, ProductEntity product);

    Optional<ItemEntity> findByUserIdAndProductId(Long userId, Long productId);

}
