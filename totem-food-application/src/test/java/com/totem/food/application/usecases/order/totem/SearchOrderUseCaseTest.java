package com.totem.food.application.usecases.order.totem;

import com.totem.food.application.ports.in.dtos.order.totem.OrderFilterDto;
import com.totem.food.application.ports.in.mappers.order.totem.IOrderMapper;
import com.totem.food.application.ports.out.persistence.commons.ISearchRepositoryPort;
import com.totem.food.domain.order.totem.OrderDomain;
import lombok.SneakyThrows;
import mock.domain.OrderDomainMock;
import mock.ports.in.dto.OrderDtoMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class SearchOrderUseCaseTest {

    @Spy
    private IOrderMapper iOrderMapper = Mappers.getMapper(IOrderMapper.class);

    @Mock
    private ISearchRepositoryPort<OrderFilterDto, List<OrderDomain>> iSearchOrderRepositoryPort;

    private SearchOrderUseCase searchOrderUseCase;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        searchOrderUseCase = new SearchOrderUseCase(iOrderMapper, iSearchOrderRepositoryPort);
    }

    @SneakyThrows
    @AfterEach
    void tearDown() {
        closeable.close();
    }

    @Test
    void items() {

        //## Mock - Objects
        var orderFilterDto = OrderFilterDto.builder().orderId("1").customerId("1").build();
        var orderDomain = OrderDomainMock.getStatusNewMock();
        var orderDto = OrderDtoMock.getMock();


    }
}