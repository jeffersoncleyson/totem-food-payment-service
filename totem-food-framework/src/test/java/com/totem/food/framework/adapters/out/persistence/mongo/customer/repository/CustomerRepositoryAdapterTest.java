package com.totem.food.framework.adapters.out.persistence.mongo.customer.repository;

import com.totem.food.application.ports.in.dtos.customer.CustomerFilterDto;
import com.totem.food.framework.adapters.out.persistence.mongo.customer.entity.CustomerEntity;
import com.totem.food.framework.adapters.out.persistence.mongo.customer.mapper.ICustomerEntityMapper;
import com.totem.food.framework.adapters.out.web.cognito.request.SearchCustomerRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.util.List;

import static mocks.adapters.out.persistence.mongo.customer.entity.CustomerEntityMock.getMock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomerRepositoryAdapterTest {

    private SearchCustomerRepositoryAdapter customerRepositoryAdapter;
    @Spy
    private ICustomerEntityMapper iCustomerEntityMapper = Mappers.getMapper(ICustomerEntityMapper.class);

    @Mock
    private Environment env;

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.openMocks(this);
        customerRepositoryAdapter = new SearchCustomerRepositoryAdapter(env, iCustomerEntityMapper);
    }

    @Test
    void findAll() {

        //## Given
        final var customersEntity = List.of(getMock(), getMock());
        final var customerFilter = new CustomerFilterDto("Name");

        //## When
        var listCategoryDomain = customerRepositoryAdapter.findAll(customerFilter);

        //## Then
        assertThat(listCategoryDomain).usingRecursiveComparison().isEqualTo(customersEntity);
        verify(iCustomerEntityMapper, times(2)).toModel(any(CustomerEntity.class));
    }

}