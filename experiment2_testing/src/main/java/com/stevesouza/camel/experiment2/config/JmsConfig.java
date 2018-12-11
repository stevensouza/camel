package com.stevesouza.camel.experiment2.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

/**
 * This class was configured per directions from these 2 links
 * <p>
 * https://codenotfound.com/spring-jms-activemq-example.html
 * https://medium.com/@bdarfler/connectionfactories-and-caching-with-spring-and-activemq-681fd912a678
 */
@Configuration
@EnableJms // enable support for the @JmsListener annotation
public class JmsConfig {

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Value("${jms.listener.concurrency}")
    private String concurrency;

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        // Standard connection
        //  ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();

        // note the following defaults to ActiveMQConnectionFactory if ssl transport isn't defined
        ActiveMQSslConnectionFactory connectionFactory = new ActiveMQSslConnectionFactory();
        connectionFactory.setBrokerURL(brokerUrl);

        //        ActiveMQSslConnectionFactory connectionFactory = new ActiveMQSslConnectionFactory(brokerUrl);
////        connectionFactory.setTrustStore(trustStore);
////        connectionFactory.setTrustStorePassword(trustStorePassword);
//        connectionFactory.setUserName(userName);
//        connectionFactory.setPassword(password);
//        connectionFactory.setBrokerURL(brokerUrl);
        return connectionFactory;
    }

    @Bean
    public SingleConnectionFactory singleConnectionFactory() {
        SingleConnectionFactory connectionFactory = new SingleConnectionFactory(connectionFactory());
        connectionFactory.setReconnectOnException(true);
        return connectionFactory;
    }

    // use this name in camel route.  For example, where 'jms' is the bean name:
    //   from("activemq://queue:randomdata_queue?connectionFactory=#jms")
    @Bean("jms")
    public CachingConnectionFactory cachingConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(singleConnectionFactory());
        connectionFactory.setSessionCacheSize(100);
        return connectionFactory;
    }


    /**
     * The jmsListenerContainerFactory() is expected by the @JmsListener annotation . The concurrency is set between 3 and 10.
     * This means that listener container will always hold on to the minimum number of consumers and will slowly scale up to
     * the maximum number of consumers in case of increasing load.
     * <p>
     * Contrary to the JmsTemplate ideally don’t use Spring’s CachingConnectionFactory with a message listener container at all.
     * Reason for this is that it is generally preferable to let the listener container itself handle appropriate caching
     * within its lifecycle.
     * <p>
     * These comments and further info was taken from https://codenotfound.com/spring-jms-activemq-example.html
     *
     * @return
     */
    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(singleConnectionFactory());
        factory.setConcurrency(concurrency);
        return factory;
    }


    @Bean
    public JmsTemplate jmsTemplate() {
        return new JmsTemplate(cachingConnectionFactory());
    }


}