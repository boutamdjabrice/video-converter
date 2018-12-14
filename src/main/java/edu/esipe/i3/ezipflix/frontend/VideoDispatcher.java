package edu.esipe.i3.ezipflix.frontend;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.esipe.i3.ezipflix.frontend.data.entities.VideoConversions;
import edu.esipe.i3.ezipflix.frontend.data.services.VideoConversion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.net.URI;
import java.util.UUID;

/**
 * Created by Gilles GIRAUD gil on 11/4/17.
 */

@SpringBootApplication
@RestController
public class VideoDispatcher {

    // rabbitmqadmin -H localhost -u ezip -p pize -V ezip delete queue name=video-conversion-queue
    // rabbitmqadmin -H localhost -u ezip -p pize -V ezip delete exchange name=video-conversion-exchange
    // sudo rabbitmqadmin -u ezip -p pize -V ezip declare exchange name=video-conversion-exchange type=direct
    // sudo rabbitmqadmin -u ezip -p pize -V ezip declare queue name=video-conversion-queue durable=true
    // sudo rabbitmqadmin -u ezip -p pize -V ezip declare binding source="video-conversion-exchange" destination_type="queue" destination="video-conversion-queue" routing_key="video-conversion-queue"
    // MONGO : db.video_conversions.remove({})

    //sudo rabbitmq-server start
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoDispatcher.class);

    /*@Value("${rabbitmq-server.credentials.username}") private String username;
    @Value("${rabbitmq-server.credentials.password}") private String password;
    @Value("${rabbitmq-server.credentials.vhost}") private String vhost;
    @Value("${rabbitmq-server.server}") private String host;
    @Value("${rabbitmq-server.port}") private String port;
    @Value("${conversion.messaging.rabbitmq.conversion-queue}") public  String conversionQueue;
    @Value("${conversion.messaging.rabbitmq.conversion-exchange}") public  String conversionExchange;*/

    private final VideoConversion conversionService;

    @Autowired
    public VideoDispatcher(VideoConversion conversionService) {
        this.conversionService = conversionService;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(VideoDispatcher.class, args);
    }

    // ┌───────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
    // │ REST Resources                                                                                                │
    // └───────────────────────────────────────────────────────────────────────────────────────────────────────────────┘


    @PostMapping(value = "/convert")
    public ConversionResponse convert(@RequestBody ConversionRequest request) throws Exception {

        VideoConversions videoConversion = new VideoConversions(
                UUID.randomUUID().toString(),
                request.getPath().toString(),
                new URI("").toString());
        String dbOutcome = this.conversionService.save(videoConversion);
        String messageId = this.conversionService.publish(videoConversion);

        return new ConversionResponse(videoConversion.getUuid(), messageId, dbOutcome);
    }


    /*@Bean
    ConnectionFactory connectionFactory() {
        final CachingConnectionFactory c = new CachingConnectionFactory(host, Integer.parseInt(port));
        c.setVirtualHost(vhost);
        c.setUsername(username);
        c.setPassword(password);
        return c;
    }

    @Bean
    public WebSocketHandler videoStatusHandler() {
        return new VideoStatusHandler();
    }

    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(videoStatusHandler(), "/video_status");
    }*/


//    @Bean
//    AmqpAdmin amqpAdmin() {
//        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory());
//        q = rabbitAdmin.declareQueue(new Queue(conversionQueue));
//        rabbitAdmin.declareExchange(new DirectExchange(conversionExchange));
//        Binding binding = BindingBuilder.bind(new Queue(conversionQueue)).to(new DirectExchange(conversionExchange))
//                .with(COMMANDS_QUEUE);
//        rabbitAdmin.declareBinding(binding);
//
//        rabbitAdmin.setAutoStartup(true);
//        return rabbitAdmin;
//    }

//    @Bean(name="video-conversion-template")
//    public RabbitTemplate getVideoConversionTemplate() {
//        RabbitTemplate template = new RabbitTemplate(connectionFactory());
//
//        template.setExchange(conversionExchange);
//        template.setRoutingKey(conversionQueue);
//        template.setQueue(conversionQueue);
//        return template;
//    }

}