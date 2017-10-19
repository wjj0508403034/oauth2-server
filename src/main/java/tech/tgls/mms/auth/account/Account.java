package tech.tgls.mms.auth.account;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

@Table(name = "t_account")
@Entity
public class Account implements Serializable {

	private static final long serialVersionUID = -4550237420396067829L;

	@Id
	@GenericGenerator(name = "x", strategy = "tech.tgls.mms.auth.common.IdGenerator")
	@GeneratedValue(generator = "x")
	private Long id;

	@Column(length = 128)
	private String username;

	@Column(length = 128)
	private String password;

	@Column(length = 16)
	private String role = Role.USER;

	@Column(length = 32)
	private String accountType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Transient
	public String getPasswordOrDefault() {

		if (StringUtils.isEmpty(this.password)) {
			return "--";
		}

		return this.password;
	}

	@Transient
	public List<GrantedAuthority> getGrantedAuthorityList() {
		String role = this.getRole();
		if (StringUtils.isEmpty(role)) {
			role = Role.USER;
		}

		return AuthorityUtils.createAuthorityList("ROLE_" + role);
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
}
