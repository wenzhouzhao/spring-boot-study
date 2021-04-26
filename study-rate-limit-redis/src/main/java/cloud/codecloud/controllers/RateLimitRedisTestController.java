package cloud.codecloud.controllers;

import cloud.codecloud.common.annotation.RateLimiter;
import cloud.codecloud.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 基于Redis+Lua的分布式限流控制层
 *
 * @author zhaoYoung
 * @date 2021/4/25 23:45
 */
@Slf4j
@RestController
public class RateLimitRedisTestController {

    /**
     * @author zhaoYoung
     * @date 2021/4/24 15:43
     * @description 未被限流
     * @return {@link Result}
     **/
    @GetMapping("/unrestricted")
    public Result<String> unrestricted() {
        log.info("-----------我是未被限流的接口");
        return Result.succeed("我是未被限流的接口");
    }

    /**
     * @author zhaoYoung
     * @date 2021/4/24 15:43
     * @description 被限流--Test1
     * @return {@link Result}
     **/
    @RateLimiter(max = 5, timeout = 1000, message = "您的访问过于频繁，请稍后重试！给你一✊")
    @GetMapping("/rateLimitTest1")
    public Result<String> rateLimitTest1() {
        return Result.succeed("加速访问有惊喜哦~~~");
    }

    /**
     * @author zhaoYoung
     * @date 2021/4/24 15:43
     * @description 被限流--Test2
     * @return {@link Result}
     **/
    @RateLimiter(max = 2, timeout = 300, restrictionsIp = true, message = "您的访问过于频繁，请稍后重试！我是被限流的接口测试2")
    @GetMapping("/rateLimitTest2")
    public Result<String> rateLimitTest2() {
        return Result.succeed("你有本事速度快点访问~~~");
    }

}
