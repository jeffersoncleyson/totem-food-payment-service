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

import java.util.Optional;

@AllArgsConstructor
@Component
public class SearchOnePaymentRepositoryAdapter implements ISearchRepositoryPort<PaymentFilterDto, Optional<PaymentModel>> {

    @Repository
    protected interface PaymentRepositoryMongoDB extends BaseRepository<PaymentEntity, Integer> {
        @Query("select p from PaymentEntity p where p.order = ?1 and p.status = ?2")
        PaymentEntity findByFilter(String order, String status);
    }

    private final PaymentRepositoryMongoDB repository;
    private final IPaymentEntityMapper iPaymentMapper;

    @Override
    public Optional<PaymentModel> findAll(PaymentFilterDto item) {
        if(StringUtils.isNotBlank(item.getOrderId()) && StringUtils.isNotBlank(item.getStatus())){
            return Optional.ofNullable(repository.findByFilter(item.getOrderId(), item.getStatus())).map(iPaymentMapper::toModel);
        }
        return Optional.empty();

    }

}
