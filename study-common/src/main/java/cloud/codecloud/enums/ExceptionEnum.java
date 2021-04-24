package cloud.codecloud.enums;

/**
 * 异常编码接口
 *
 * @author zhaoYoung
 * @date 2021/4/24 14:53
 */
public interface ExceptionEnum {

    /**
     * 返回异常编码
     *
     * @return 异常编码
     */
    String getCode();

    /**
     * 返回异常消息
     *
     * @return 异常消息
     */
    String getMessage();

}
