package fr.dopolytech.polyshop.cart.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class QueueService {
    private final RabbitTemplate rabbitTemplate;

    public QueueService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendCheckout(String message) {
        rabbitTemplate.convertAndSend("cartExchange", "cart.checkout", message);
    }

    public String createMessage(Object object) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }
}
