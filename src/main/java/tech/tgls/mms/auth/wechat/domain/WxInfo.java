package tech.tgls.mms.auth.wechat.domain;


import org.hibernate.annotations.GenericGenerator;

import tech.tgls.mms.auth.account.Account;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.util.Date;

/**
 * Created by darcy on 2016/12/7.
 *
 * FIXME: wxInfo 类名首字母小写, 不符合JAVA规范, 需要重构
 *
 */
@Entity
public class WxInfo {

    @Id
    @GenericGenerator(name = "x", strategy = "tech.tgls.mms.core.common.IdGenerator")
    @GeneratedValue(generator = "x")
    private long id;
    
    @ManyToOne
    @JoinColumn(name = "userId")
    private Account user;

    @NotNull
    private String wxOpenId;

    private String wxUnionId;

    private String nickName;

    private String avatarUrl;

    private Date createTime;

    private String wxAppId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWxOpenId() {
        return wxOpenId;
    }

    public void setWxOpenId(String wxOpenId) {
        this.wxOpenId = wxOpenId;
    }

    public String getWxUnionId() {
        return wxUnionId;
    }

    public void setWxUnionId(String wxUnionId) {
        this.wxUnionId = wxUnionId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getWxAppId() {
        return wxAppId;
    }

    public void setWxAppId(String wxAppId) {
        this.wxAppId = wxAppId;
    }

    @Override
    public String toString() {
        return "wxInfo{" +
                "id=" + id +
                ", wxOpenId='" + wxOpenId + '\'' +
                ", wxUnionId='" + wxUnionId + '\'' +
                ", nickName='" + nickName + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", createTime=" + createTime +
                '}';
    }

	public Account getUser() {
		return user;
	}

	public void setUser(Account user) {
		this.user = user;
	}
}