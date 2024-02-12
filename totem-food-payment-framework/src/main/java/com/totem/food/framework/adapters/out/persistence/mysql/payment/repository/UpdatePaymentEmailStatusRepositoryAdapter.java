package com.totem.food.framework.adapters.out.persistence.mysql.payment.repository;

import com.totem.food.application.exceptions.InvalidInput;
import com.totem.food.application.ports.out.persistence.commons.IUpdateRepositoryPort;
import com.totem.food.application.ports.out.persistence.payment.PaymentEmailUpdateModel;
import com.totem.food.application.ports.out.persistence.payment.PaymentModel;
import com.totem.food.framework.adapters.out.persistence.mysql.commons.BaseRepository;
import com.totem.food.framework.adapters.out.persistence.mysql.payment.entity.PaymentEntity;
import com.totem.food.framework.adapters.out.persistence.mysql.payment.mapper.IPaymentEntityMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Component
public class UpdatePaymentEmailStatusRepositoryAdapter implements IUpdateRepositoryPort<PaymentEmailUpdateModel> {

	@Repository
	@Transactional
	protected interface PaymentRepositoryMongoDB extends BaseRepository<PaymentEntity, Integer> {

		@Modifying
		@Query("update PaymentEntity pE set pE.email = ?1 where pE.id = ?2")
		int updateEmail(int isEmailSent, int id);
	}

	private final PaymentRepositoryMongoDB repository;
	private final IPaymentEntityMapper iPaymentEntityMapper;

	@Override
	public PaymentEmailUpdateModel updateItem(PaymentEmailUpdateModel item) {
		final var isSaved = repository.updateEmail(item.getEmail(), item.getId());

		if(isSaved == 0){
			throw new InvalidInput("Invalid update process for payment ".concat(String.valueOf(item.getId())));
		}

		return repository.findById(item.getId()).map(iPaymentEntityMapper::toModelEmail).orElseThrow();
	}

}
