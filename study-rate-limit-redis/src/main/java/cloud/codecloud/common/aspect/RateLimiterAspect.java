package cloud.codecloud.common.aspect;

import cloud.codecloud.common.annotation.RateLimiter;
import cloud.codecloud.common.enums.RedisPrefix;
import cloud.codecloud.common.util.RedisUtil;
import cloud.codecloud.enums.ErrorCodeEnum;
import cloud.codecloud.enums.PunctuationEnum;
import cloud.codecloud.exception.BizException;
import cloud.codecloud.util.WebUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * 限流切面
 *
 * @author zhaoYoung
 * @date 2021/4/25 21:49
 */
@Slf4j
@Aspect
@Component
public class RateLimiterAspect {
    /**
     * 这里使用ThreadLocal是因为IP存在可变的，保证自己的线程的IP不会被其他线程所修改，切记要最后清理ThreadLocal，防止内存泄漏
     */
    private static ThreadLocal<String> ipThreadLocal=new ThreadLocal<>();

    @Resource
    private DefaultRedisScript<Long> limitRedisScript;

    @Pointcut("@annotation(cloud.codecloud.common.annotation.RateLimiter)")
    public void rateLimit() {

    }

    @Around("rateLimit()")
    public Object pointcut(ProceedingJoinPoint point) throws Throwable {
        try {
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            // 通过 AnnotationUtils.findAnnotation 获取 RateLimiter 注解
            RateLimiter rateLimiter = AnnotationUtils.findAnnotation(method, RateLimiter.class);
            if (rateLimiter != null && rateLimiter.max() > 0) {
                StringBuffer key = new StringBuffer(rateLimiter.key());
                // 默认用类名+方法名做限流的 key 前缀
                if (StrUtil.isBlank(key)) {
                    key.append(method.getDeclaringClass().getName()).append(PunctuationEnum.PERIOD.getCode()).append(method.getName());
                }
                // 最终限流的 key 为 前缀 + IP地址
                // TODO: 此时需要考虑局域网多用户访问的情况，因此 key 后续需要加上方法参数更加合理
                boolean restrictionsIp = rateLimiter.restrictionsIp();
                if (restrictionsIp) {
                    ipThreadLocal.set(ServletUtil.getClientIP(WebUtil.getRequest()));
                }
                if(StrUtil.isNotBlank(ipThreadLocal.get())){
                    key.append(PunctuationEnum.COLON.getCode()).append(ipThreadLocal.get());
                }
                long max = rateLimiter.max();
                long timeout = rateLimiter.timeout();
                TimeUnit timeUnit = rateLimiter.timeUnit();
                String message = rateLimiter.message();
                boolean limited = isLimited(key.toString(), max, timeout, timeUnit);
                if (limited) {
                    log.error("-----------Redis+Lua脚本限流策略：方法名：{}，设置的参数为：key：{}，max：{}，超时时长：{}，提示语：{}",
                            key, method.getName(), max, rateLimiter.timeout(), message);
                    throw new BizException(ErrorCodeEnum.REQUEST_FORBIDDEN.getCode(), message);
                }
            }
            return point.proceed();
        }finally {
            ipThreadLocal.remove();
        }
    }

    private boolean isLimited(String key, long max, long timeout, TimeUnit timeUnit) {
        // 最终的 key 格式为：limit:自定义key:IP 或 limit:类名.方法名:IP
        key = RedisPrefix.REDIS_LIMIT_KEY_PREFIX + key;
        // 统一使用单位:毫秒
        long ttl = timeUnit.toMillis(timeout);
        // 当前时间毫秒数
        long now = Instant.now().toEpochMilli();
        long expired = now - ttl;
        // 这里入参需要转为 String，不然会报错：java.lang.ClassCastException: java.lang.Long cannot be cast to java.lang.String
        Long executeTimes = RedisUtil.execute(limitRedisScript, Collections.singletonList(key), now + "", ttl + "", expired + "", max + "");
        if (executeTimes != null) {
            if (executeTimes == 0) {
                log.error("【{}】在单位时间 {} 毫秒内已达到访问上限 {}", key, ttl, max);
                return true;
            }
            log.info("【{}】在单位时间 {} 毫秒内访问 {} 次", key, ttl, executeTimes);
            return false;
        }
        return false;
    }

}
