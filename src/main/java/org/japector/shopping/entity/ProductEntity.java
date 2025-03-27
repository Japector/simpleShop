package org.japector.shopping.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String category;

    @NotNull
    private String unit;

    @Positive
    private Double defaultPrice;

    @NotNull
    private String pathToPicture;


    public void copyFrom(ProductEntity source) {
        this.setName(source.getName());
        this.setUnit(source.getUnit());
        this.setDefaultPrice(source.getDefaultPrice());
        this.setCategory(source.getCategory());
        this.setPathToPicture(source.getPathToPicture());
    }

}
