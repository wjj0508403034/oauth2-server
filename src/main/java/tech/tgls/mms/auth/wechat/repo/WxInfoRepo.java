package tech.tgls.mms.auth.wechat.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import tech.tgls.mms.auth.wechat.domain.WxInfo;

@Repository
public interface WxInfoRepo extends CrudRepository<WxInfo,Long>{

	@Query("select t from WxInfo t where t.wxOpenId = ?1")
	WxInfo findByOpenId(String openId);
}
