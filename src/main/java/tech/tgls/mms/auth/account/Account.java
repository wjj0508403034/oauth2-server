package tech.tgls.mms.auth.account;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

@Entity
public class Account implements Serializable {

	private static final long serialVersionUID = -4550237420396067829L;

	@Id
	@GeneratedValue
	private Long id;

	@Column
	private String username;

	@Column
	private String password;

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
}
