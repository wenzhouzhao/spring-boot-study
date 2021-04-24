package cloud.codecloud.enums;

/**
 * 通用错误码定义
 *
 * @author zhaoYoung
 * @date 2021/4/24 14:51
 */
public enum ErrorCodeEnum implements ExceptionEnum {

    /**
     * 请求成功
     */
    SUCCESS("200", "success"),
    /**
     * 请求错误
     */
    REQUEST_ERROR("400", "request error"),
    /**
     * 请求认证错误
     */
    REQUEST_AUTH_ERROR("401", "request auth error"),
    /**
     * 拒绝请求
     */
    REQUEST_FORBIDDEN("403", "request forbidden"),
    /**
     * 服务未找到
     */
    REQUEST_SERVICE_NOT_FOUND("404", "service not found"),
    /**
     * 路径不存在
     */
    NO_HANDLER_FOUND("404","no handler found"),
    /**
     * 请求方式错误
     */
    REQUEST_METHOD_NOT_SUPPORTED("405", "request method not supported"),
    /**
     * 请求参数错误
     */
    REQUEST_PARAM_ERROR("460", "request parameter error"),

    /**
     * 其他错误(未捕获)
     */
    OTHER_ERROR("470", "other error"),

    /**
     * 服务端错误
     */
    SERVER_ERROR("500", "server error"),
    /**
     * 服务不可用
     */
    SERVER_NOT_AVAILABLE("503", "server not available"),
    /**
     * 自定义错误
     */
    CUSTOM_ERROR("600","自定义错误"),

    ;

    ErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 异常编码
     */
    private String code;
    /**
     * 异常消息
     */
    private String message;

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

}
