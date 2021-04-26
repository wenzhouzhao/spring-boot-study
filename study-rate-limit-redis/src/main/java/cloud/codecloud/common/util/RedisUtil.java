package cloud.codecloud.common.util;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Redis 工具类
 *
 * @author zhaoYoung
 * @date 2021/4/25 23:22
 */
@Component
public class RedisUtil implements InitializingBean{

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static RedisUtil redisUtil;

    @PostConstruct
    public void init() {
        redisUtil = this;
    }

    public static Long execute(DefaultRedisScript<Long> script, List<String> keys, Object... args) {
        return redisUtil.redisTemplate.execute(script, keys,args);
    }

    /**
     * 如果不序列化的话，执行 Lua 脚本时会报错：@user_script:7: @user_script: 7: Lua redis() command arguments must be strings or integers
     */
    @Override
    public void afterPropertiesSet() {
        StringRedisSerializer stringSerializer = new StringRedisSerializer(StandardCharsets.UTF_8);
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(stringSerializer);
    }
}
