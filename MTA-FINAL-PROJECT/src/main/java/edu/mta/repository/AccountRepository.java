package edu.mta.repository;

import java.util.List;
import java.util.Optional;

import edu.mta.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer>{
	
	Optional<Account> findByEmailAndPassword(String email, String password);
	
	List<Account> findAll();

	List<Account> findByRole(int role);

	Optional<Account> findByEmail(String email);
	
}
