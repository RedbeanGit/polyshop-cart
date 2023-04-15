package fr.dopolytech.polyshop.cart.components;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.dopolytech.polyshop.cart.events.CartCheckoutEvent;

@Service
public class QueueUtils {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public QueueUtils(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendCheckout(CartCheckoutEvent event) throws JsonProcessingException {
        rabbitTemplate.convertAndSend("cartExchange", "cart.checkout", this.stringify(event));
    }

    public String stringify(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }
}
