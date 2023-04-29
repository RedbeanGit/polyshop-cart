package fr.dopolytech.polyshop.cart.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.dopolytech.polyshop.cart.models.PolyshopEvent;

@Service
public class QueueService {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public QueueService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendCartCheckout(PolyshopEvent event) throws JsonProcessingException {
        rabbitTemplate.convertAndSend("cartExchange", "cart.checkout", this.stringify(event));
    }

    public String stringify(PolyshopEvent object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }
}
