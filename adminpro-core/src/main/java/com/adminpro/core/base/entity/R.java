package com.adminpro.core.base.entity;

import com.adminpro.core.base.message.Message;
import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.exceptions.APIException;
import com.google.gson.Gson;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.*;

/**
 * 返回数据
 */
public class R<T> implements Serializable {
    final static Logger logger = LoggerFactory.getLogger(R.class);
    private String restCode;

    private String message;

    private boolean success;

    private List<Message> errors = new ArrayList<>();

    private Map<String, String> errorsMap = new HashMap<>();

    private T data;

    public static R ok() {
        R r = new R();
        r.setRestCode(String.valueOf(HttpStatus.OK.value()));
        r.setSuccess(true);
        return r;
    }

    public static R ok(Object result) {
		/*if(result != null && result instanceof String){
			R ok = ok();
			ok.setMessage((String)result);
			return ok;
		}*/
        return ok(HttpStatus.OK.value(), result);
    }

    //有可能是HttpStatus.CREATED等
    public static R ok(int code, Object result) {
        R r = new R();
        r.setRestCode(String.valueOf(code));
        r.setData(result);
        r.setSuccess(true);
        return r;
    }

    public static R error(Exception e) {
        if (!(e instanceof APIException)) {
            logger.error(e.getMessage(), e);
        }
        return error(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), e.getMessage());
    }

    public static R error(MessageBundle bundle) {
        Message[] errorMessages = bundle.getErrorMessages();

        R r = new R();
        r.setRestCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        StringBuffer sb = new StringBuffer();
        if (ArrayUtils.isNotEmpty(errorMessages)) {
            Map<String, String> errorsMap = r.getErrorsMap();
            for (int i = 0; i < errorMessages.length; i++) {
                Message errorMessage = errorMessages[i];
                errorsMap.put(errorMessage.getField(), errorMessage.getMessage());
                sb.append(errorMessage.getMessage());
                if (i < errorMessages.length - 1) {
                    sb.append("\r\n");
                }
            }
            r.setErrors(Arrays.asList(errorMessages));
        }
        r.setSuccess(false);
        r.setMessage(sb.toString());
        logger.error("###请求失败：" + new Gson().toJson(bundle));
        return r;
    }

    public static R error(String code, String errMsg) {
        R r = new R();
        r.setRestCode(code);
        r.setMessage(errMsg);
        r.setSuccess(false);
        logger.error("###请求失败：" + errMsg);
        return r;
    }

    public String getRestCode() {
        return restCode;
    }

    public void setRestCode(String restCode) {
        this.restCode = restCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public static R unauthorized() {
        return error(String.valueOf(HttpStatus.UNAUTHORIZED.value()), "请登录");
    }

    public static R notAcceptable() {
        return error(String.valueOf(HttpStatus.NOT_ACCEPTABLE.value()), "你没有权限访问此接口");
    }

    public static R error(String errMsg) {
        return error(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), errMsg);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Message> getErrors() {
        return errors;
    }

    public void setErrors(List<Message> errors) {
        this.errors = errors;
    }

    public Map<String, String> getErrorsMap() {
        return errorsMap;
    }

    public void setErrorsMap(Map<String, String> errorsMap) {
        this.errorsMap = errorsMap;
    }
}
