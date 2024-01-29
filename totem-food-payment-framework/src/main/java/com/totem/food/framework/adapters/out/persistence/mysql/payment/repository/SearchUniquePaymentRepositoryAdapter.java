package com.totem.food.framework.adapters.out.persistence.mysql.payment.repository;

import com.totem.food.application.ports.out.persistence.commons.ISearchUniqueRepositoryPort;
import com.totem.food.application.ports.out.persistence.payment.PaymentModel;
import com.totem.food.framework.adapters.out.persistence.mysql.commons.BaseRepository;
import com.totem.food.framework.adapters.out.persistence.mysql.payment.entity.PaymentEntity;
import com.totem.food.framework.adapters.out.persistence.mysql.payment.mapper.IPaymentEntityMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@AllArgsConstructor
@Component
public class SearchUniquePaymentRepositoryAdapter implements ISearchUniqueRepositoryPort<Optional<PaymentModel>> {

    @Repository
    protected interface PaymentRepositoryMongoDB extends BaseRepository<PaymentEntity, Integer> {

    }

    private final PaymentRepositoryMongoDB repository;
    private final IPaymentEntityMapper iPaymentMapper;

    @Override
    public Optional<PaymentModel> findById(Integer id) {
        return repository.findById(id).map(iPaymentMapper::toModel);
    }

}
