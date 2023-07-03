package com.totem.food.application.usecases.combo;

import com.totem.food.application.exceptions.ElementExistsException;
import com.totem.food.application.exceptions.ElementNotFoundException;
import com.totem.food.application.ports.in.dtos.combo.ComboCreateDto;
import com.totem.food.application.ports.in.dtos.combo.ComboDto;
import com.totem.food.application.ports.in.dtos.product.ProductFilterDto;
import com.totem.food.application.ports.in.mappers.combo.IComboMapper;
import com.totem.food.application.ports.in.mappers.product.IProductMapper;
import com.totem.food.application.ports.out.persistence.combo.ComboModel;
import com.totem.food.application.ports.out.persistence.commons.ICreateRepositoryPort;
import com.totem.food.application.ports.out.persistence.commons.IExistsRepositoryPort;
import com.totem.food.application.ports.out.persistence.commons.ISearchRepositoryPort;
import com.totem.food.application.ports.out.persistence.product.ProductModel;
import com.totem.food.application.usecases.commons.ICreateUseCase;
import com.totem.food.domain.combo.ComboDomain;
import com.totem.food.domain.product.ProductDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateComboUseCaseTest {

    @Spy
    private IComboMapper iComboMapper = Mappers.getMapper(IComboMapper.class);
    @Spy
    private IProductMapper iProductMapper = Mappers.getMapper(IProductMapper.class);
    @Mock
    private ICreateRepositoryPort<ComboModel> iCreateRepositoryPort;

    @Mock
    private IExistsRepositoryPort<ComboModel, Boolean> iSearchRepositoryPort;

    @Mock
    private ISearchRepositoryPort<ProductFilterDto, List<ProductModel>> iSearchProductRepositoryPort;

    private ICreateUseCase<ComboCreateDto, ComboDto> iCreateUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.iCreateUseCase = new CreateComboUseCase(iComboMapper, iProductMapper, iCreateRepositoryPort, iSearchRepositoryPort, iSearchProductRepositoryPort);
    }

    @Test
    void createItem() {

        //## Given
        final var productId = UUID.randomUUID().toString();
        final var productDomain = ProductModel.builder().id(productId).build();
        final var productModel = ProductModel.builder().id(productId).build();
        final var comboDomain = new ComboModel("1", "Combo da casa", Double.MAX_VALUE, List.of(productDomain), ZonedDateTime.now(ZoneOffset.UTC), ZonedDateTime.now(ZoneOffset.UTC));

        //### Given - Mocks
        when(iSearchProductRepositoryPort.findAll(any(ProductFilterDto.class))).thenReturn(List.of(productModel));
        when(iCreateRepositoryPort.saveItem(any(ComboModel.class))).thenReturn(comboDomain);

        //## When
        final var comboCreateDto = new ComboCreateDto("Combo da casa", BigDecimal.TEN, List.of(productId));
        final var comboDto = iCreateUseCase.createItem(comboCreateDto);

        //## Then
        assertNotNull(comboDto);
        assertEquals(comboCreateDto.getName(), comboDto.getName());
    }

    @Test
    void elementExistsException() {

        //## Given
        final var productId = UUID.randomUUID().toString();
        final var productDomain = ProductDomain.builder().id(productId).name("Fanta").build();
        final var comboCreateDto = new ComboCreateDto("Combo da casa", BigDecimal.TEN, List.of(productId));


        when(iSearchRepositoryPort.exists(any())).thenReturn(true);

        //## When
        var exception = assertThrows(ElementExistsException.class,
                () -> iCreateUseCase.createItem(comboCreateDto));

        //## Then
        assertEquals(exception.getMessage(), String.format("Combo [%s] already registered", comboCreateDto.getName()));
    }

    @Test
    void ElementNotFoundException() {

        //## Given
        final var productId = UUID.randomUUID().toString();
        final var comboCreateDto = new ComboCreateDto("Combo da casa", BigDecimal.TEN, List.of(productId));

        //## When
        var exception = assertThrows(ElementNotFoundException.class,
                () -> iCreateUseCase.createItem(comboCreateDto));

        //## Then
        assertEquals(exception.getMessage(), String.format("Products [%s] some products are invalid", comboCreateDto.getProducts()));
    }
}