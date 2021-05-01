package edu.mta.repository;

import edu.mta.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer>{

	@Query("SELECT u FROM Account u WHERE u.username = :username and u.isActive = 1")
	public Account getUserByUsername(@Param("username") String username);

	boolean existsByUsername(String username);
	
	Optional<Account> findByEmailAndPassword(String email, String password);
	
	List<Account> findAll();

	List<Account> findByRoles(int role);

	Optional<Account> findByEmail(String email);
	
}
