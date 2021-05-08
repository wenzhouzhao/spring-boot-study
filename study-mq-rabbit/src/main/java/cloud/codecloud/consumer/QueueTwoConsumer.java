package cloud.codecloud.consumer;

import cloud.codecloud.common.constants.RabbitConstants;
import cloud.codecloud.pojo.po.MessageStructPO;
import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 队列2 消费者
 *
 * @author zhaoYoung
 * @date 2021/5/8 10:11
 */
@Slf4j
@RabbitListener(queues = RabbitConstants.QUEUE_TWO)
@Component
public class QueueTwoConsumer {

    /**
     * 如果 spring.rabbitmq.listener.direct.acknowledge-mode: auto，则可以用这个方式，会自动ack
     */
    // @RabbitHandler
    public void directHandlerAutoAck(MessageStructPO message) {
        log.info("队列2消费者，接收消息：{}", JSONUtil.toJsonStr(message));
    }

    @RabbitHandler
    public void directHandlerManualAck(MessageStructPO messageStruct, Message message, Channel channel) {
        //  如果手动ACK,消息会被监听消费,但是消息在队列中依旧存在,如果 未配置 acknowledge-mode 默认是会在消费完毕后自动ACK掉
        final long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            log.info("队列2，手动ACK，接收消息：{}", JSONUtil.toJsonStr(messageStruct));
            // 通知 MQ 消息已被成功消费,可以ACK了
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            try {
                // 处理失败,重新压入MQ
                channel.basicRecover();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

}
