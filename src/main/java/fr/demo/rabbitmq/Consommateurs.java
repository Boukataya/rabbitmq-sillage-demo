package fr.demo.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class Consommateurs {
    private static final Logger LOG = LoggerFactory.getLogger(Consommateurs.class);
    private final Journal journal;

    public Consommateurs(Journal journal) {
        this.journal = journal;
    }

    @RabbitListener(queues = Routage.FILE_EXPORT_MAIL)
    public void recevoirMail(Message message) {
        enregistrer(Routage.FILE_EXPORT_MAIL, message);
        // Démonstration : pas d'envoi de véritable email.
    }

    @RabbitListener(queues = Routage.FILE_JOURNAL_DEMO)
    public void recevoirJournal(Message message) {
        enregistrer(Routage.FILE_JOURNAL_DEMO, message);
    }

    private void enregistrer(String queue, Message message) {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        String contenu = new String(message.getBody(), StandardCharsets.UTF_8);
        journal.ajouter(queue, routingKey, contenu);
        LOG.info("Message reçu | queue={} | routingKey={} | payload={}", queue, routingKey, contenu);
    }
}
