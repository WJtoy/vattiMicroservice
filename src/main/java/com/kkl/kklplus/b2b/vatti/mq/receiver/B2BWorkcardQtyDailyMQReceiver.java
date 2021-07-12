package com.kkl.kklplus.b2b.vatti.mq.receiver;

import com.kkl.kklplus.entity.b2b.mq.B2BMQConstant;
import com.kkl.kklplus.entity.b2b.pb.MQB2BWorkcardQtyDailyMessage;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class B2BWorkcardQtyDailyMQReceiver {

    //@RabbitListener(queues = B2BMQConstant.MQ_B2BCENTER_B2BWORKCARDQTYDAILY)
    public void onMessage(Message message, Channel channel) throws Exception {
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        try {
            MQB2BWorkcardQtyDailyMessage.B2BWorkcardQtyDailyMessage b2BWorkcardQtyDailyMessage =
                    MQB2BWorkcardQtyDailyMessage.B2BWorkcardQtyDailyMessage.parseFrom(message.getBody());
            log.error("{}", b2BWorkcardQtyDailyMessage.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
