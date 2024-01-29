package com.totem.food.framework.adapters.out.persistence.mysql.commons;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<S, K> extends JpaRepository<S, K> {
}
