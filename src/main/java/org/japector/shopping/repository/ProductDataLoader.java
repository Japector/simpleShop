package org.japector.shopping.repository;

import java.util.List;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.japector.shopping.entity.ProductEntity;
import org.springframework.stereotype.Component;




@Component
@AllArgsConstructor
public class ProductDataLoader {

    private final ProductRepository productRepository;

    @PostConstruct
    public void loadInitialProducts() {
        if (productRepository.count() == 0) {
            List<ProductEntity> products = List.of(
                    createProduct("Apple", "fruits", "pcs", 0.30, "/uploads/categories/fruits/Apple.png"),
                    createProduct("Banana", "fruits", "kg", 1.5, "/uploads/categories/fruits/Banana.png"),
                    createProduct("Orange", "fruits", "kg", 2.0, "/uploads/categories/fruits/Orange.png"),
                    createProduct("Carrot", "vegetables", "kg", 3.0, "/uploads/categories/vegetables/Carrot.png"),
                    createProduct("Potato", "vegetables", "kg", 0.50, "/uploads/categories/vegetables/Potato.png"),
                    createProduct("Tomato", "vegetables", "kg", 4.0, "/uploads/categories/vegetables/Tomato.png"),
                    createProduct("White Bread", "baked", "pcs", 1.50, "/uploads/categories/baked/White Bread.png"),
                    createProduct("Croissant", "baked", "pcs", 1.0, "/uploads/categories/baked/Croissant.png"),
                    createProduct("Milk", "dairy", "l", 0.95, "/uploads/categories/dairy/Milk.png"),
                    createProduct("Cheese", "dairy", "kg", 8.0, "/uploads/categories/dairy/Cheese.png")
            );

            productRepository.saveAll(products);
            System.out.println("Sample products inserted into H2.");
        }
    }

    private ProductEntity createProduct(String name, String category, String unit, double price, String pathToPicture) {
        return ProductEntity.builder()
                            .name(name)
                            .category(category)
                            .unit(unit)
                            .defaultPrice(price)
                            .pathToPicture(pathToPicture)
                            .build();
    }
}