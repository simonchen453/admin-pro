package com.adminpro.framework.base.entity;

import com.adminpro.framework.base.message.Message;
import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.exceptions.APIException;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.*;

/**
 * 统一响应包装类
 * <p>
 * 所有 API 接口统一返回此格式，HTTP 状态码始终为 200，
 * 通过 restCode 字段区分业务状态码。
 * </p>
 */
@Schema(description = "统一响应格式")
public class R<T> implements Serializable {
    final static Logger logger = LoggerFactory.getLogger(R.class);

    @Schema(description = "业务状态码：200=成功, 400=参数错误, 401=未授权, 403=无权限, 404=不存在, 500=服务器错误", example = "200")
    private String restCode;

    @Schema(description = "响应消息", example = "操作成功")
    private String message;

    @Schema(description = "是否成功", example = "true")
    private boolean success;

    @Schema(description = "错误详情列表")
    private List<Message> errors = new ArrayList<>();

    @Schema(description = "错误字段映射（字段名 -> 错误信息）")
    private Map<String, String> errorsMap = new HashMap<>();

    @Schema(description = "响应数据（具体类型见各接口说明）")
    private T data;

    @Schema(description = "响应时间戳", example = "1705546800000")
    private Long timestamp;

    @Schema(description = "请求追踪ID", example = "abc-123-def")
    private String requestId;

    public static <T> R<T> ok() {
        R<T> r = new R<>();
        r.setRestCode(String.valueOf(HttpStatus.OK.value()));
        r.setSuccess(true);
        r.setTimestamp(System.currentTimeMillis());
        r.setRequestId(MDC.get("requestId"));
        return r;
    }

    public static <T> R<T> ok(T result) {
        /*
         * if(result != null && result instanceof String){
         * R ok = ok();
         * ok.setMessage((String)result);
         * return ok;
         * }
         */
        return ok(HttpStatus.OK.value(), result);
    }

    // 有可能是HttpStatus.CREATED等
    public static <T> R<T> ok(int code, T result) {
        R<T> r = new R<>();
        r.setRestCode(String.valueOf(code));
        r.setData(result);
        r.setSuccess(true);
        r.setTimestamp(System.currentTimeMillis());
        r.setRequestId(MDC.get("requestId"));
        return r;
    }

    public static <T> R<T> error(Exception e) {
        if (!(e instanceof APIException)) {
            logger.error(e.getMessage(), e);
        }
        return error(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), e.getMessage());
    }

    public static <T> R<T> error(MessageBundle bundle) {
        Message[] errorMessages = bundle.getErrorMessages();

        R<T> r = new R<>();
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
        r.setTimestamp(System.currentTimeMillis());
        r.setRequestId(MDC.get("requestId"));
        logger.error("###请求失败：" + new Gson().toJson(bundle));
        return r;
    }

    public static <T> R<T> error(String code, String errMsg) {
        R<T> r = new R<>();
        r.setRestCode(code);
        r.setMessage(errMsg);
        r.setSuccess(false);
        r.setTimestamp(System.currentTimeMillis());
        r.setRequestId(MDC.get("requestId"));
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

    public static <T> R<T> unauthorized() {
        return error(String.valueOf(HttpStatus.UNAUTHORIZED.value()), "请登录");
    }

    public static <T> R<T> notAcceptable() {
        return error(String.valueOf(HttpStatus.NOT_ACCEPTABLE.value()), "你没有权限访问此接口");
    }

    public static <T> R<T> error(String errMsg) {
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

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
