package fr.demo.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class Publication {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public Publication(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void publier(Document document, String action, String routingKey) {
        EvenementDocument evenement = new EvenementDocument(
                document.numero(), action, document.destinataire(), Instant.now());
        try {
            String json = objectMapper.writeValueAsString(evenement);
            rabbitTemplate.convertAndSend(Routage.EXCHANGE, routingKey, json);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Impossible de sérialiser le document", e);
        }
    }
}
