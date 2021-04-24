package cloud.codecloud.vo;

import cloud.codecloud.enums.ErrorCodeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回
 *
 * @author zhaoYoung
 * @date 2021/4/24 14:59
 */
@Data
public class Result<T> implements Serializable {

    private T data;
    private  String message;
    private boolean success;
    private String code;

    private Result() {

    }

    private Result(boolean success, String code, String message, T data){
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    private Result(boolean success, String code, String message){
        this.success = success;
        this.code = code;
        this.message = message;
    }

    public static <T> Result<T> succeed(){
        return new Result(true, ErrorCodeEnum.SUCCESS.getCode(),ErrorCodeEnum.SUCCESS.getMessage());
    }

    public static <T> Result<T> succeed(T data){
        return new Result(true,ErrorCodeEnum.SUCCESS.getCode(),ErrorCodeEnum.SUCCESS.getMessage(),data);
    }

    public static <T> Result<T> fail(T data){
        return new Result(false, ErrorCodeEnum.SERVER_ERROR.getCode(), ErrorCodeEnum.SERVER_ERROR.getMessage(),data);
    }

    public static <T> Result<T> fail(ErrorCodeEnum errorCodeEnum){
        return new Result(false, errorCodeEnum.getCode(), errorCodeEnum.getMessage());
    }

    public static <T> Result<T> fail(ErrorCodeEnum errorCodeEnum,String message){
        return new Result(false, errorCodeEnum.getCode(), message);
    }

    public static <T> Result<T> fail(ErrorCodeEnum errorCodeEnum,T data){
        return new Result(false, errorCodeEnum.getCode(), errorCodeEnum.getMessage(),data);
    }

    public static <T> Result<T> fail(String code,String message){
        return new Result(false, code, message);
    }

    public static <T> Result<T> fail(String code,String message,T data){
        return new Result(false, code, message,data);
    }

    public static <T> Result<T> fail(ErrorCodeEnum errorCodeEnum,String message,T data){
        return new Result(false, errorCodeEnum.getCode(), message,data);
    }

}
