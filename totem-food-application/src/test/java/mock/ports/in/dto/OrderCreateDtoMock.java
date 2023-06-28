package mock.ports.in.dto;

import com.totem.food.application.ports.in.dtos.order.totem.ItemQuantityDto;
import com.totem.food.application.ports.in.dtos.order.totem.OrderCreateDto;

import java.util.List;

public class OrderCreateDtoMock {

    public static OrderCreateDto getMock(String productId, String comboId) {
        var orderCreate = new OrderCreateDto();
        orderCreate.setCustomerId("1");
        orderCreate.setProducts(List.of(new ItemQuantityDto(1, productId)));
        orderCreate.setCombos(List.of(new ItemQuantityDto(1, comboId)));
        return orderCreate;
    }
}