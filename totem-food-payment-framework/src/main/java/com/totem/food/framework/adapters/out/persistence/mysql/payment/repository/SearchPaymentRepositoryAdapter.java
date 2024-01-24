package com.totem.food.framework.adapters.out.persistence.mysql.payment.repository;

import com.totem.food.application.ports.in.dtos.payment.PaymentFilterDto;
import com.totem.food.application.ports.out.persistence.commons.ISearchRepositoryPort;
import com.totem.food.application.ports.out.persistence.payment.PaymentModel;
import com.totem.food.framework.adapters.out.persistence.mysql.commons.BaseRepository;
import com.totem.food.framework.adapters.out.persistence.mysql.payment.entity.PaymentEntity;
import com.totem.food.framework.adapters.out.persistence.mysql.payment.mapper.IPaymentEntityMapper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Component
public class SearchPaymentRepositoryAdapter implements ISearchRepositoryPort<PaymentFilterDto, List<PaymentModel>> {

    @Repository
    protected interface PaymentRepositoryMongoDB extends BaseRepository<PaymentEntity, Integer> {
        @Query("select p from PaymentEntity p where p.order = ?1 and p.token = ?2")
        PaymentEntity findByFilter(String order, String token);

        @Query("select p from PaymentEntity p where p.order = ?1 and p.status = ?2")
        PaymentEntity findPaymentByOrderAndStatus(String order, String status);

        List<PaymentEntity> findByStatusAndModifiedAtAfter(String status, ZonedDateTime localDateTime);
    }

    private final PaymentRepositoryMongoDB repository;
    private final IPaymentEntityMapper iPaymentMapper;

    @Override
    public List<PaymentModel> findAll(PaymentFilterDto item) {

        if (Objects.nonNull(item.getStatus()) && Objects.nonNull(item.getTimeLastOrders())) {
            List<PaymentEntity> entities = repository.findByStatusAndModifiedAtAfter(item.getStatus(), item.getTimeLastOrders());
            return iPaymentMapper.toModel(entities);
        }

        if (StringUtils.isNotEmpty(item.getOrderId()) && StringUtils.isNotEmpty(item.getToken())) {
            final var entity = repository.findByFilter(item.getOrderId(), item.getToken());
            return Collections.singletonList(iPaymentMapper.toModel(entity));
        }

        if (StringUtils.isNotEmpty(item.getOrderId()) && StringUtils.isNotEmpty(item.getStatus())) {
            final var entity = repository.findPaymentByOrderAndStatus(item.getOrderId(), item.getStatus());
            return Collections.singletonList(iPaymentMapper.toModel(entity));
        }

        return Collections.emptyList();

    }

}
