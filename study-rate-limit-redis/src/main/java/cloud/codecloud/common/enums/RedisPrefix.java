package cloud.codecloud.common.enums;

import cloud.codecloud.enums.PunctuationEnum;

/**
 * redis key 前缀
 * 默认key规范 数据的意义_系统编码_唯一的标识
 * @author zhaoYoung
 * @date 2021/4/25 22:20
 */
public class RedisPrefix {

    /**
     * 限流 key 前缀
     */
    public static final String REDIS_LIMIT_KEY_PREFIX = "limit"+ PunctuationEnum.COLON.getCode();

}
