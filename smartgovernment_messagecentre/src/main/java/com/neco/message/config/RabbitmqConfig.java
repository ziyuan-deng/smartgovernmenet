package com.neco.message.config;

import com.neco.message.enums.ExchangeEnum;
import com.neco.message.enums.MessageStatusEnum;
import com.neco.message.enums.QueueEnum;
import com.neco.message.listener.SimpleListener;
import com.neco.message.service.MessageService;
import com.neco.messagecentre.model.MessageInfo;
import com.neco.sglog.utils.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * rabbitMQ配置类
 *
 * @author ziyuan_deng
 * @create 2020-09-13 21:41
 */
@Configuration
public class RabbitmqConfig {

    private static final Logger log= LoggerFactory.getLogger(RabbitmqConfig.class);

    @Autowired
    private Environment env;

    @Autowired
    private CachingConnectionFactory connectionFactory;
    @Autowired
    private SimpleRabbitListenerContainerFactoryConfigurer factoryConfigurer;

    /**
     *配置单一消费者
     * @return
     */
   @Bean(name = "singleListenerContainer")
    public SimpleRabbitListenerContainerFactory listenerContainer(){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        factory.setPrefetchCount(1);
       // factory.setBatchSize(1);
        return factory;
    }

    /**
     * 配置多个消费者
     * @return
     */
    @Bean(name = "multiListenerContainer")
    public SimpleRabbitListenerContainerFactory multiListenerContainer(){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factoryConfigurer.configure(factory,connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.NONE);
        factory.setConcurrentConsumers(env.getProperty("spring.rabbitmq.listener.simple.concurrency",Integer.class));
        factory.setMaxConcurrentConsumers(env.getProperty("spring.rabbitmq.listener.simple.max-concurrency",Integer.class));
        factory.setPrefetchCount(env.getProperty("spring.rabbitmq.listener.simple.prefetch",Integer.class));
        return factory;
    }

    @Autowired
    private MessageService messageService;
    @Bean
    public RabbitTemplate rabbitTemplate(){
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setPublisherReturns(true);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReceiveTimeout(10000L);
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                String dataId = correlationData.getId();
                String[] arrStrs = dataId.split("@@");
                if (arrStrs.length>1 && "true".equals(arrStrs[1])) {
                    MessageInfo messageInfo = new MessageInfo();
                    messageInfo.setId(arrStrs[0]);
                    messageInfo.setStatus(MessageStatusEnum.SENDED.getCode());
                    boolean success = messageService.updateById(messageInfo);
                }
                log.info("消息发送成功:correlationData({}),ack({}),cause({})",correlationData,ack,cause);
            }
        });

        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                log.info("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}",exchange,routingKey,replyCode,replyText,message);
            }
        });

        return rabbitTemplate;
    }
    //基础测试队列设置
    @Bean
    public DirectExchange basicDirectExchange(){
        return  new DirectExchange(env.getProperty("basic.info.mq.exchange.name"));
    }
    @Bean("basicQueue")
    public Queue basicQueue(){
        String name = env.getProperty("basic.info.mq.queue.name");
        return new Queue(env.getProperty("basic.info.mq.queue.name"),true);
    }
    @Bean
    public Binding basicBinding(){
        return BindingBuilder.bind(basicQueue())
                .to(basicDirectExchange())
                .with(env.getProperty("basic.info.mq.routing.key.name"));
    }

    //抢购队列有关设置
    @Bean
    public DirectExchange robbingDirectExchange(){
        return new DirectExchange(env.getProperty("product.robbing.mq.exchange.name"));
    }
    @Bean(name = "robbingQueue")
    public Queue robbingQueue(){
        return new Queue(env.getProperty("product.robbing.mq.queue.name"));
    }
    @Bean
    public Binding robbingBinding(){
        String key = env.getProperty("product.robbing.mq.routing.key.name");
        return BindingBuilder.bind(robbingQueue())
                 .to(robbingDirectExchange())
                 .with(env.getProperty("product.robbing.mq.routing.key.name"));
    }

    @Bean
    public TopicExchange simpleTopicExchange(){
        return new TopicExchange(env.getProperty("simple.mq.exchange.name"));
    }
    @Bean(name = "simpleQueue")
    public Queue simpleQueue(){
        return new Queue(env.getProperty("simple.mq.queue.name"));
    }
    @Bean
    public Binding simpleBinding(){
        return BindingBuilder.bind(simpleQueue()).to(simpleTopicExchange()).with(env.getProperty("simple.mq.routing.key.name"));
    }


    @Autowired
    private SimpleListener simpleListener;
    @Bean(name = "simpleContainer")
    public SimpleMessageListenerContainer simpleCpmtainer(@Qualifier("simpleQueue") Queue simpleQueue) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
       // container.setMessagePropertiesConverter(new Jackson2JsonMessageConverter());
        container.setConcurrentConsumers(env.getProperty("spring.rabbitmq.listener.simple.concurrency",Integer.class));
        container.setMaxConcurrentConsumers(env.getProperty("spring.rabbitmq.listener.simple.max-concurrency",Integer.class));
        container.setPrefetchCount(env.getProperty("spring.rabbitmq.listener.simple.prefetch",Integer.class));

        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.addQueues(simpleQueue);
        container.setMessageListener(simpleListener);
        return container;
    }



    /**
     * @return java.lang.Object
     * @description 动态创建交换机
     * @create 2020/9/16 0016 9:28
     */
    @Bean("createExchange")
    public Object createExchange() {

        // 遍历交换机枚举
        ExchangeEnum.toList().forEach(exchangeEnum -> {
                    // 声明交换机
                    Exchange exchange;
                    // 根据交换机模式 生成不同的交换机
                    switch (exchangeEnum.getExchangeType()) {
                        case 1:
                            //exchange = ExchangeBuilder.fanoutExchange(exchangeEnum.getExchangeName()).durable(exchangeEnum.isDurable()).autoDelete(exchangeEnum).build();
                            exchange = new FanoutExchange(exchangeEnum.getExchangeName(),exchangeEnum.isDurable(),exchangeEnum.isAutoDelete());
                            break;
                        case 3:
                            //exchange = ExchangeBuilder.topicExchange(exchangeEnum.getExchangeName()).durable(exchangeEnum.isDurable()).build();
                            exchange = new TopicExchange(exchangeEnum.getExchangeName(),exchangeEnum.isDurable(),exchangeEnum.isAutoDelete());
                            break;
                        case 4:
                            //exchange = ExchangeBuilder.headersExchange(exchangeEnum.getExchangeName()).durable(exchangeEnum.isDurable()).build();
                            exchange = new HeadersExchange(exchangeEnum.getExchangeName(),exchangeEnum.isDurable(),exchangeEnum.isAutoDelete());
                            break;
                        case 2:
                        default:
                            //exchange = ExchangeBuilder.directExchange(exchangeEnum.getExchangeName()).durable(exchangeEnum.isDurable()).build();
                            exchange = new DirectExchange(exchangeEnum.getExchangeName(),exchangeEnum.isDurable(),exchangeEnum.isAutoDelete());
                            break;
                    }
                    // 将交换机注册到spring bean工厂 让spring实现交换机的管理
                    if (exchange != null) {
                        SpringContextHolder.registerBean(exchangeEnum.toString() + "_exchange", exchange);
                    }
                }
        );
        // 不返回任何对象 该方法只用于在spring初始化时 动态的将bean对象注册到spring bean工厂
        return null;
    }

    /**
     * @return java.lang.Object
     * @description 动态创建队列
     * @create 2020/9/16 0016 9:29
     */
    @Bean("createQueue")
    public Object createQueue() {
        // 遍历队列枚举 将队列注册到spring bean工厂 让spring实现队列的管理
        QueueEnum.toList().forEach(queueEnum -> SpringContextHolder.registerBean(queueEnum.toString() + "_queue",
                new Queue(queueEnum.getQueueName(), queueEnum.isDurable(), queueEnum.isExclusive(), queueEnum.isAutoDelete(), queueEnum.getArguments())));
        // 不返回任何对象 该方法只用于在spring初始化时 动态的将bean对象注册到spring bean工厂
        return null;
    }


    /**
     * 动态创建绑定关系
     * @return java.lang.Object
     * @description 动态将交换机及队列绑定
     * @create 2020/9/16 0016 9:29
     */
    @Bean("createBinding")
    public Object createBinding() {
        // 遍历队列枚举 将队列绑定到指定交换机
        QueueEnum.toList().forEach(queueEnum -> {
                    // 从spring bean工厂中获取队列对象（刚才注册的）
                    Queue queue = SpringContextHolder.getBean(queueEnum.toString() + "_queue", Queue.class);
                    // 声明绑定关系
                    Binding binding;
                    // 根据不同的交换机模式 获取不同的交换机对象（注意：刚才注册时使用的是父类Exchange，这里获取的时候将类型获取成相应的子类）生成不同的绑定规则
                    switch (queueEnum.getExchangeEnum().getExchangeType()) {
                        case 1:
                            FanoutExchange fanoutExchange = SpringContextHolder.getBean(queueEnum.getExchangeEnum().toString() + "_exchange", FanoutExchange.class);
                            binding = BindingBuilder.bind(queue).to(fanoutExchange);
                            break;
                        case 3:
                            TopicExchange topicExchange = SpringContextHolder.getBean(queueEnum.getExchangeEnum().toString() + "_exchange", TopicExchange.class);
                            binding = BindingBuilder.bind(queue).to(topicExchange).with(queueEnum.getRoutingKey());
                            break;
                        case 4:
                            HeadersExchange headersExchange = SpringContextHolder.getBean(queueEnum.getExchangeEnum().toString() + "_exchange", HeadersExchange.class);
                            if (queueEnum.isWhereAll()) {
                                // whereAll表示全部匹配
                                binding = BindingBuilder.bind(queue).to(headersExchange).whereAll(queueEnum.getHeaders()).match();
                            } else {
                                // whereAny表示部分匹配
                                binding = BindingBuilder.bind(queue).to(headersExchange).whereAny(queueEnum.getHeaders()).match();
                            }
                            break;
                        case 2:
                        default:
                            DirectExchange directExchange = SpringContextHolder.getBean(queueEnum.getExchangeEnum().toString() + "_exchange", DirectExchange.class);
                            binding = BindingBuilder.bind(queue).to(directExchange).with(queueEnum.getRoutingKey());
                            break;
                    }
                    // 将绑定关系注册到spring bean工厂 让spring实现绑定关系的管理
                    if (binding != null) {
                        SpringContextHolder.registerBean(queueEnum.toString() + "_binding", binding);
                    }
                }
        );
        // 不返回任何对象 该方法只用于在spring初始化时 动态的将bean对象注册到spring bean工厂
        return null;
    }

}
