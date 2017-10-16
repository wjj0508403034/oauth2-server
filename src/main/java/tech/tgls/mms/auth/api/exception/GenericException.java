package tech.tgls.mms.auth.api.exception;

import tech.tgls.mms.auth.common.consts.Constants;



/**
 * 所有异常的基类
 * Created with IntelliJ IDEA.
 * User: malone
 * Date: 13-12-10
 * Time: 下午4:40
 * To change this template use File | Settings | File Templates.
 */
public class GenericException extends RuntimeException {

    /**
     *  异常对应的错误码(约定)
     */
    private String errorCode;

    public GenericException(String msg) {
        super(msg);
        this.errorCode = Constants.COMMON_ERROR_CODE;
    }

    public GenericException(String msg, String errorCode) {
        super(msg);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

}