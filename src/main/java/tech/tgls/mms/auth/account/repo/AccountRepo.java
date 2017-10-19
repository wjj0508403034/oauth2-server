package tech.tgls.mms.auth.account.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import tech.tgls.mms.auth.account.Account;

@Repository
public interface AccountRepo extends CrudRepository<Account, Long> {

	@Query("select t from Account t where t.username = ?1")
	Account findByUsername(String username);

	@Query("select t from Account t where t.username = ?1 and t.role = 'ADMIN'")
	Account findAdminByUsername(String username);
}
