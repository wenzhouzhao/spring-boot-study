package cloud.codecloud.common.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 限流注解，添加了 {@link AliasFor} 必须通过 {@link AnnotationUtils} 获取，才会生效
 *
 * @author zhaoYoung
 * @date 2021/4/25 21:43
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {

    /**
     * max 最大请求数
     */
    @AliasFor("max") long value() default 10;

    /**
     * max 最大请求数
     */
    @AliasFor("value") long max() default 10;

    /**
     * 限流key
     */
    String key() default "";

    /**
     * 超时时长，默认0毫秒
     */
    long timeout() default 0;

    /**
     * 超时时间单位，默认毫秒
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    /**
     *是否限制IP,默认 否
     */
    boolean restrictionsIp() default false;

    /**
     * 提示语
     */
    String message() default  "您的访问过于频繁，请稍后重试";
}
