package cloud.codecloud.handler;

import cloud.codecloud.enums.ErrorCodeEnum;
import cloud.codecloud.exception.BizException;
import cloud.codecloud.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 全局统一异常处理
 *
 * @author zhaoYoung
 * @date 2021/4/24 15:06
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public Result bizException(BizException e){
        return Result.fail(e.getCode(),e.getMessage());
    }

    /**
     * 请求方式错误
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result requestMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.error("------------请求方式错误，method：{}",e.getMethod());
        return Result.fail(ErrorCodeEnum.REQUEST_METHOD_NOT_SUPPORTED);
    }

    /**
     * 路径不存在
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public Result handlerNoFoundException(NoHandlerFoundException e) {
        log.error("------------路径不存在，httpMethod：{}，requestURL：{}",e.getHttpMethod(),e.getRequestURL());
        return Result.fail(ErrorCodeEnum.NO_HANDLER_FOUND);
    }

    /**
     * 注解：捕获表单效验异常
     * 随机捕获一个异常并返回前台
     */
    @ExceptionHandler(BindException.class)
    public Result handleBindException(BindException ex) {
        Map<String, Object> model = ex.getModel();
        Field[] modelNames = model.get(ex.getBindingResult().getObjectName()).getClass().getDeclaredFields();
        // ex.getFieldError():随机返回一个对象属性的异常信息。如果要一次性返回所有对象属性异常信息，则调用ex.getAllErrors()
        List<FieldError> fieldError = ex.getFieldErrors();
        List<ObjectError> resultError = new ArrayList<>();
        for (Field field : modelNames) {
            for (FieldError temp : fieldError) {
                if (temp.getField().equals(field.getName())) {
                    resultError.add(temp);
                }
            }
        }
        String errorInfo = resultError.get(0).getDefaultMessage();
        return Result.fail(ErrorCodeEnum.REQUEST_PARAM_ERROR, errorInfo);
    }

}
