package tech.tgls.mms.auth.common.jsonbean;

import java.util.ArrayList;
import java.util.List;

public class JsonResultBean {
    private int stat = 1;//0 - fail, 1 - success
    private List<FieldError> fieldErrors = new ArrayList<FieldError>();
    private Object data;

    public int getStat() {
        return stat;
    }

    public void setStat(int stat) {
        this.stat = stat;
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(List<FieldError> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public void addFieldError(FieldError error) {
        fieldErrors.add(error);
    }

    public void addFieldError(String field, String message) {
        addFieldError(new FieldError(field, message));
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}