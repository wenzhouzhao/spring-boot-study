package cloud.codecloud.exception;

import cloud.codecloud.enums.ExceptionEnum;
import lombok.Data;

/**
 * 业务异常类基类
 *
 * @author zhaoYoung
 * @date 2021/4/24 14:49
 */
@Data
public class BizException extends RuntimeException{

    private String code;

    public BizException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public BizException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.getMessage());
        this.code = exceptionEnum.getCode();
    }

    public BizException(ExceptionEnum exceptionEnum, Throwable cause) {
        super(exceptionEnum.getMessage(), cause);
        this.code = exceptionEnum.getCode();
    }
}
