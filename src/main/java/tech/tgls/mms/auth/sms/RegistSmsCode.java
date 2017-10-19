package tech.tgls.mms.auth.sms;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

/**
 * Created by Liya on 2017/4/20.
 */
@Table(name = "t_sms_code")
@Entity
public class RegistSmsCode {
    @Id
    @GenericGenerator(name = "xx", strategy = "tech.tgls.mms.auth.common.IdGenerator")
    @GeneratedValue(generator = "xx")
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @NotNull
    private String tel;

    @Lob
    @NotNull
    private String code;
    private Date created;
}