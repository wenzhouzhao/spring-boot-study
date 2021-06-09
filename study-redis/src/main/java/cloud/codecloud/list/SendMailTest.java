package cloud.codecloud.list;

import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * 注册之后发送邮件的案例
 *
 * @author zhaoYoung
 * @date 2021/6/9 11:19
 */
public class SendMailTest {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 让发送邮件任务入队列
     *
     * @param sendMailTask
     */
    public void enqueueSendMailTask(String sendMailTask) {
        jedis.lpush("send_mail_task_queue", sendMailTask);
    }

    /**
     * 阻塞式获取发送邮件任务
     *
     * @return
     */
    public List<String> takeSendMailTask() {
        return jedis.brpop(5, "send_mail_task_queue");
    }

    public static void main(String[] args) {
        SendMailTest demo = new SendMailTest();

        System.out.println("尝试阻塞式的获取发送邮件任务......");

        demo.enqueueSendMailTask("第一个邮件发送任务");
        List<String> sendMailTasks = demo.takeSendMailTask();
        System.out.println(sendMailTasks);
    }


}
