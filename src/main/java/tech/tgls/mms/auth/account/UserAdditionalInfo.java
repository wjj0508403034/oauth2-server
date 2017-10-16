package tech.tgls.mms.auth.account;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class UserAdditionalInfo {
	@Id
	@GeneratedValue
	private Long id;

	@Column
	private Long userId;

	@Column
	private String name;

	@Column
	private String value;

	public UserAdditionalInfo() {

	}

	public UserAdditionalInfo(Long userId, String name, String value) {
		this.userId = userId;
		this.name = name;
		this.value = value;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
