package com.totem.food.framework.adapters.in.rest.payment.adapter;

import com.totem.food.application.ports.in.dtos.payment.PaymentCreateDto;
import com.totem.food.application.ports.in.dtos.payment.PaymentDto;
import com.totem.food.application.ports.in.dtos.payment.PaymentFilterDto;
import com.totem.food.application.ports.in.dtos.payment.PaymentQRCodeDto;
import com.totem.food.application.usecases.commons.ICreateImageUseCase;
import com.totem.food.application.usecases.commons.ICreateUseCase;
import com.totem.food.application.usecases.commons.ISearchUniqueUseCase;
import com.totem.food.application.usecases.commons.ISearchUseCase;
import com.totem.food.application.usecases.commons.IUpdateStatusUseCase;
import com.totem.food.domain.payment.PaymentDomain;
import com.totem.food.framework.test.utils.TestUtils;
import mocks.dtos.PaymentMocks;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.totem.food.framework.adapters.in.rest.constants.Routes.API_VERSION_1;
import static com.totem.food.framework.adapters.in.rest.constants.Routes.PAYMENT_ORDER_ID;
import static com.totem.food.framework.adapters.in.rest.constants.Routes.PAYMENT_ORDER_ID_AND_STATUS;
import static com.totem.food.framework.adapters.in.rest.constants.Routes.TOTEM_PAYMENT;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.platform.commons.util.StringUtils.isNotBlank;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class TotemPaymentRestApiAdapterTest {

    @Mock
    private ICreateUseCase<PaymentCreateDto, PaymentQRCodeDto> iCreateUseCase;
    @Mock
    private ISearchUniqueUseCase<String, Optional<PaymentDto>> iSearchUniqueUseCase;
    @Mock
    private ICreateImageUseCase<PaymentDto, byte[]> iCreateImageUseCase;
    @Mock
    private ISearchUseCase<PaymentFilterDto, Optional<PaymentDto>> iSearchUseCase;
    @Mock
    private IUpdateStatusUseCase<PaymentDto> iUpdateStatusUseCase;

    private MockMvc mockMvc;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setup() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        final var totemPaymentRestApiAdapter = new TotemPaymentRestApiAdapter(
                iCreateUseCase,
                iSearchUniqueUseCase,
                iCreateImageUseCase,
                iSearchUseCase,
                iUpdateStatusUseCase
        );
        mockMvc = MockMvcBuilders.standaloneSetup(totemPaymentRestApiAdapter).build();
    }

    @AfterEach
    void after() throws Exception {
        autoCloseable.close();
    }

    @ParameterizedTest
    @ValueSource(strings = API_VERSION_1 + TOTEM_PAYMENT)
    void create(String endpoint) throws Exception {

        //### Given - Mocks
        final var paymentCreateDto = PaymentMocks.paymentCreateDto(UUID.randomUUID().toString(), "123");
        final var paymentQRCodeDto = PaymentMocks.paymentQRCodeDto(1, PaymentDomain.PaymentStatus.PENDING.key);

        when(iCreateUseCase.createItem(any(PaymentCreateDto.class))).thenReturn(paymentQRCodeDto);

        //### When
        final var json = TestUtils.toJSON(paymentCreateDto).orElseThrow();
        final var httpServletRequest = post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        final var resultActions = mockMvc.perform(httpServletRequest);

        //### Then
        resultActions.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        final var responseJson = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(responseJson);
        assertTrue(isNotBlank(responseJson));

        verify(iCreateUseCase, times(1)).createItem(any(PaymentCreateDto.class));
    }

    @ParameterizedTest
    @ValueSource(strings = API_VERSION_1 + TOTEM_PAYMENT + PAYMENT_ORDER_ID)
    void testReturnByteImageQrCode(String endpoint) throws Exception {

        //### Objects - Mocks
        var paymentDto = PaymentMocks.paymentDto();

        //### Given - Mocks
        when(iSearchUniqueUseCase.item(anyString())).thenReturn(Optional.of(paymentDto));
        when(iCreateImageUseCase.createImage(paymentDto)).thenReturn(new byte[32]);

        final var httpServletRequest = get(endpoint, paymentDto.getId())
                .header("x-with-image-qrcode", true);

        //### When
        final var resultActions = mockMvc.perform(httpServletRequest);

        //### Then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG));

        final var result = resultActions.andReturn();
        final var responseJson = result.getResponse();

        assertNotNull(responseJson.getContentAsByteArray());

        verify(iSearchUniqueUseCase, times(1)).item(anyString());
    }


    @ParameterizedTest
    @ValueSource(strings = API_VERSION_1 + TOTEM_PAYMENT + PAYMENT_ORDER_ID)
    void testReturnInfoPayment(String endpoint) throws Exception {

        //### Objects - Mocks
        var paymentDto = PaymentMocks.paymentDto();

        //### Given - Mocks
        when(iSearchUniqueUseCase.item(anyString())).thenReturn(Optional.of(paymentDto));

        final var httpServletRequest = get(endpoint, paymentDto.getId())
                .header("x-with-image-qrcode", false);

        //### When
        final var resultActions = mockMvc.perform(httpServletRequest);

        //### Then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

        final var result = resultActions.andReturn();
        final var responseJson = result.getResponse().getContentAsString();
        final var paymentDtoResponseOpt = TestUtils.toObject(responseJson, PaymentDto.class);
        final var paymentDtoResponse = paymentDtoResponseOpt.orElseThrow();

        Assertions.assertThat(paymentDtoResponse)
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(ZonedDateTime.class)
                .isNotNull();

        verify(iCreateImageUseCase, never()).createImage(paymentDto);
    }

    @ParameterizedTest
    @ValueSource(strings = API_VERSION_1 + TOTEM_PAYMENT + PAYMENT_ORDER_ID_AND_STATUS)
    void testUpdateInfoPayment(String endpoint) throws Exception {

        //### Objects - Mocks
        var paymentDto = PaymentMocks.paymentDto();

        //### Given - Mocks
        when(iSearchUseCase.items(any(PaymentFilterDto.class))).thenReturn(Optional.of(paymentDto));

        final var httpServletRequest = get(endpoint, paymentDto.getOrder(), paymentDto.getStatus());

        //### When
        final var resultActions = mockMvc.perform(httpServletRequest);

        //### Then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

        final var responseJson = resultActions.andReturn().getResponse().getContentAsString();
        final var paymentDtoResponse = TestUtils.toObject(responseJson, PaymentDto.class).orElseThrow();

        Assertions.assertThat(paymentDtoResponse)
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(ZonedDateTime.class)
                .isNotNull()
                .isEqualTo(paymentDto);

        verify(iSearchUseCase, times(1)).items(any(PaymentFilterDto.class));
    }

    @ParameterizedTest
    @ValueSource(strings = API_VERSION_1 + TOTEM_PAYMENT + PAYMENT_ORDER_ID_AND_STATUS)
    void testUpdateInfoPaymentWhenReturnStatusNoContent(String endpoint) throws Exception {

        //### Objects - Mocks
        var paymentDto = PaymentMocks.paymentDto();

        //### Given - Mocks
        when(iSearchUseCase.items(any(PaymentFilterDto.class))).thenReturn(Optional.empty());

        final var httpServletRequest = get(endpoint, paymentDto.getOrder(), paymentDto.getStatus());

        //### When
        final var resultActions = mockMvc.perform(httpServletRequest);

        //### Then
        resultActions.andDo(print())
                .andExpect(status().isNoContent());

        verify(iSearchUseCase, times(1)).items(any(PaymentFilterDto.class));
    }

}