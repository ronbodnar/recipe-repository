package com.ronbodnar.reciperepository.repository;

import com.ronbodnar.reciperepository.model.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {}