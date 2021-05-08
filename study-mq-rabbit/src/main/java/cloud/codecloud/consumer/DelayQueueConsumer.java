package cloud.codecloud.consumer;

import cloud.codecloud.common.constants.RabbitConstants;
import cloud.codecloud.pojo.po.MessageStructPO;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 延迟队列消费者
 *
 * @author zhaoYoung
 * @date 2021/5/8 9:53
 */
@Slf4j
@Component
@RabbitListener(queues = RabbitConstants.DELAY_QUEUE)
public class DelayQueueConsumer {


    @RabbitHandler
    public void directHandlerManualAck(MessageStructPO messageStruct, Message message, Channel channel) {
        //  如果手动ACK,消息会被监听消费,但是消息在队列中依旧存在,如果 未配置 acknowledge-mode 默认是会在消费完毕后自动ACK掉
        final long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            log.info("延迟队列，手动ACK，接收消息：{}，当前时间：{}", JSONUtil.toJsonStr(messageStruct), DateUtil.date());
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

    /**
     * 如果不在类上标明队列，可以在方法上标明监听哪个队列
     */
    //@RabbitListener(queues = {RabbitConstants.DELAY_QUEUE})
    public void directHandlerManualAck(Message message, Channel channel) {
        MessageStructPO messageStruct = JSONUtil.toBean(new String(message.getBody()), MessageStructPO.class);
        final long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            log.info("延迟队列，手动ACK，接收消息：{}，当前时间：{}", JSONUtil.toJsonStr(messageStruct), DateUtil.date());
            // 通知 MQ 消息已被成功消费,可以ACK了
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            try {
                // 处理失败,重新压入MQ
                channel.basicPublish(message.getMessageProperties().getReceivedExchange(),
                        message.getMessageProperties().getReceivedRoutingKey(), MessageProperties.PERSISTENT_TEXT_PLAIN,
                        message.getBody());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
