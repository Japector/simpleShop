package org.japector.shopping.model;

import java.util.StringJoiner;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ItemDto {

    @NotNull
    private Long productId;

    @NotNull
    private String name;

    @PositiveOrZero
    private Double quantity;

    @NotNull
    private Double price;

    @NotNull
    private String unit;

    @Override
    public String toString() {
        return new StringJoiner(", ", ItemDto.class.getSimpleName() + "[", "]")
                .add("productId=" + productId)
                .add("name='" + name + "'")
                .add("quantity=" + quantity)
                .add("price=" + price)
                .toString();
    }
}

