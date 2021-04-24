package cloud.codecloud.controllers;

import cloud.codecloud.common.annotation.RateLimiter;
import cloud.codecloud.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 基于Guava的单机限流控制层
 *
 * @author zhaoYoung
 * @date 2021/4/24 15:42
 */
@Slf4j
@RestController
public class RateLimitGuavaTestController {

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
    @RateLimiter(qps = 1.0, timeout = 300, message = "您的访问过于频繁，请稍后重试！我是被限流的接口测试1")
    @GetMapping("/rateLimitTest1")
    public Result<String> rateLimitTest1() {
        log.info("-----------我是被限流的接口测试1");
        return Result.succeed();
    }

    /**
     * @author zhaoYoung
     * @date 2021/4/24 15:43
     * @description 被限流--Test2
     * @return {@link Result}
     **/
    @RateLimiter(qps = 2.0, timeout = 300, message = "您的访问过于频繁，请稍后重试！我是被限流的接口测试2")
    @GetMapping("/rateLimitTest2")
    public Result<String> rateLimitTest2() {
        log.info("-----------我是被限流的接口测试1");
        return Result.succeed();
    }

}
