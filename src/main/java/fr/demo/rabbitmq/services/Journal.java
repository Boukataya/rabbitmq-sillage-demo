package fr.demo.rabbitmq.services;

import fr.demo.rabbitmq.models.Reception;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

@Service
public class Journal {
    private final ArrayDeque<Reception> receptions = new ArrayDeque<>();

    public synchronized void ajouter(String queue, String routingKey, String contenu) {
        receptions.addFirst(new Reception(Instant.now(), queue, routingKey, contenu));
        if (receptions.size() > 100) {
            receptions.removeLast();
        }
    }

    public synchronized List<Reception> lister() {
        return new ArrayList<>(receptions);
    }
}
