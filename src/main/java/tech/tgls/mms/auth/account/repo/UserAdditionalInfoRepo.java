package tech.tgls.mms.auth.account.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import tech.tgls.mms.auth.account.UserAdditionalInfo;

@Repository
public interface UserAdditionalInfoRepo extends CrudRepository<UserAdditionalInfo, Long>{

	@Query("select t from UserAdditionalInfo t where t.userId = ?1")
	List<UserAdditionalInfo> findUserInfosByUserId(Long userId);
}
