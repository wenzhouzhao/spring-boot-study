package cloud.codecloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RateLimitRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(RateLimitRedisApplication.class, args);
    }

}
