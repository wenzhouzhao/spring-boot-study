package cloud.codecloud.list;

import redis.clients.jedis.Jedis;

/**
 * 秒杀活动案例
 *
 * @author zhaoYoung
 * @date 2021/6/9 11:15
 */
public class SecKillTest {


    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 秒杀抢购请求入队
     *
     * @param secKillRequest
     */
    public void enqueueSecKillRequest(String secKillRequest) {
        jedis.lpush("sec_kill_request_queue", secKillRequest);
    }

    /**
     * 秒杀抢购请求出队
     *
     * @return
     */
    public String dequeueSecKillRequest() {
        return jedis.rpop("sec_kill_request_queue");
    }

    public static void main(String[] args) {
        SecKillTest demo = new SecKillTest();

        for (int i = 0; i < 10; i++) {
            demo.enqueueSecKillRequest("第" + (i + 1) + "个秒杀请求");
        }

        while (true) {
            String secKillRequest = demo.dequeueSecKillRequest();

            if (secKillRequest == null
                    || "null".equals(secKillRequest)
                    || "".equals(secKillRequest)) {
                break;
            }

            System.out.println(secKillRequest + "---------------秒杀成功");
        }
    }


}
