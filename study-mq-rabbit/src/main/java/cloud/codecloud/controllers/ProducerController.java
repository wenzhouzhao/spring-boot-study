package cloud.codecloud.controllers;

import cloud.codecloud.common.constants.RabbitConstants;
import cloud.codecloud.enums.ErrorCodeEnum;
import cloud.codecloud.pojo.po.MessageStructPO;
import cloud.codecloud.vo.Result;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 消息生产者
 *
 * @author zhaoYoung
 * @date 2021/5/8 14:10
 */
@RestController
@RequestMapping("/producer")
@Slf4j
public class ProducerController {


    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 延迟队列
     *
     * @param millisecond 毫秒
     */
    @RequestMapping(value = "/sendDelay", method = RequestMethod.GET)
    public Result<String> sendDelay(Long millisecond) {
        if(null == millisecond){
            return Result.fail(ErrorCodeEnum.REQUEST_PARAM_ERROR,"请输入毫秒数");
        }
        DateTime date = DateUtil.date();
        MessageStructPO messageStruct = new MessageStructPO("delay message, delay  " + millisecond / 1000 + "s ," + date);
        rabbitTemplate.convertAndSend(RabbitConstants.DELAY_MODE_QUEUE, RabbitConstants.DELAY_QUEUE, messageStruct, message -> {
            message.getMessageProperties().setHeader("x-delay", millisecond);
            return message;
        });
        return Result.succeed(date.toString());
    }

    /**
     * 直接模式
     */
    @RequestMapping(value = "/sendDirect", method = RequestMethod.GET)
    public Result<String> sendDirect(String message) {
        MessageStructPO messageStruct = new MessageStructPO(StringUtils.isBlank(message) ? "direct message" : message);
        rabbitTemplate.convertAndSend(RabbitConstants.DIRECT_MODE_QUEUE_ONE, messageStruct);
        return Result.succeed("消息内容：【"+messageStruct.getMessage()+"】 发送成功！");
    }

    /**
     * 分列模式
     */
    @RequestMapping(value = "/sendFanout", method = RequestMethod.GET)
    public Result<String> sendFanout(String message) {
        MessageStructPO messageStruct = new MessageStructPO(StringUtils.isBlank(message) ? "fanout message" : message);
        rabbitTemplate.convertAndSend(RabbitConstants.DIRECT_MODE_QUEUE_ONE, messageStruct);
        return Result.succeed("消息内容：【"+messageStruct.getMessage()+"】 发送成功！");
    }

    /**
     * 主题模式发送1
     */
    @RequestMapping(value = "/sendTopic1", method = RequestMethod.GET)
    public Result<String> sendTopic1(String message) {
        MessageStructPO messageStruct = new MessageStructPO(StringUtils.isBlank(message) ? "topic1 message" : message);
        rabbitTemplate.convertAndSend(RabbitConstants.TOPIC_MODE_QUEUE, "queue.aaa.bbb", messageStruct);
        return Result.succeed("消息内容：【"+messageStruct.getMessage()+"】 发送成功！");
    }

    /**
     * 主题模式发送2
     */
    @RequestMapping(value = "/sendTopic2", method = RequestMethod.GET)
    public Result<String> sendTopic2(String message) {
        MessageStructPO messageStruct = new MessageStructPO(StringUtils.isBlank(message) ? "topic2 message" : message);
        rabbitTemplate.convertAndSend(RabbitConstants.TOPIC_MODE_QUEUE, "ccc.queue", messageStruct);
        return Result.succeed("消息内容：【"+messageStruct.getMessage()+"】 发送成功！");
    }

    /**
     * 主题模式发送3
     */
    @RequestMapping(value = "/sendTopic3", method = RequestMethod.GET)
    public Result<String> sendTopic3(String message) {
        MessageStructPO messageStruct = new MessageStructPO(StringUtils.isBlank(message) ? "topic3 message" : message);
        rabbitTemplate.convertAndSend(RabbitConstants.TOPIC_MODE_QUEUE, "3.queue", messageStruct);
        return Result.succeed("消息内容：【"+messageStruct.getMessage()+"】 发送成功！");
    }


}
