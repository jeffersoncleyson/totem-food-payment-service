package com.totem.food.framework.adapters.out.persistence.mysql.payment.repository;

import com.totem.food.application.ports.out.persistence.commons.IUpdateRepositoryPort;
import com.totem.food.application.ports.out.persistence.payment.PaymentModel;
import com.totem.food.framework.adapters.out.persistence.mysql.commons.BaseRepository;
import com.totem.food.framework.adapters.out.persistence.mysql.payment.entity.PaymentEntity;
import com.totem.food.framework.adapters.out.persistence.mysql.payment.mapper.IPaymentEntityMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@AllArgsConstructor
@Component
public class UpdatePaymentRepositoryAdapter implements IUpdateRepositoryPort<PaymentModel> {

	@Repository
	protected interface PaymentRepositoryMongoDB extends BaseRepository<PaymentEntity, Integer> {
	}

	private final PaymentRepositoryMongoDB repository;
	private final IPaymentEntityMapper iPaymentEntityMapper;

	@Override
	public PaymentModel updateItem(PaymentModel item) {
		final var entity = iPaymentEntityMapper.toEntity(item);
		final var savedEntity = repository.save(entity);
		return iPaymentEntityMapper.toModel(savedEntity);
	}

}
