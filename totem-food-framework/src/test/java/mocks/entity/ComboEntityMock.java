package mocks.entity;

import com.totem.food.framework.adapters.out.persistence.mongo.combo.entity.ComboEntity;
import com.totem.food.framework.adapters.out.persistence.mongo.product.entity.ProductEntity;

import java.util.List;

public class ComboEntityMock {

    public static ComboEntity getMock() {
        var comboEntity = new ComboEntity();
        comboEntity.setId("1");
        comboEntity.setName("Combo da casa");
        comboEntity.setPrice(Double.MIN_NORMAL);
        final var productEntity = ProductEntity.builder().id("1").build();
        comboEntity.setProducts(List.of(productEntity));
        return comboEntity;
    }
}
