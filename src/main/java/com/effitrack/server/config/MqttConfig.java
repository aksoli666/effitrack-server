package com.effitrack.server.config;

import com.effitrack.server.constant.NumConst;
import com.effitrack.server.constant.StringConst;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import java.util.UUID;

@Configuration
public class MqttConfig {
    private static final String CLIENT_ID = StringConst.CLIENT_ID_PREFIX + UUID.randomUUID();

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{StringConst.MQTT_BROKER_URL});
        options.setCleanSession(true);
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                                CLIENT_ID,
                                mqttClientFactory(),
                                StringConst.EQUIPMENT_UPDATE_TOPIC
                );

        adapter.setCompletionTimeout(NumConst.COMPLETION_TIMEOUT_MS);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(NumConst.DEFAULT_QOS_LEVEL);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }
}
