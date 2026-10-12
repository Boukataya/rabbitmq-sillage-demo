package fr.demo.rabbitmq;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class Documents {
    private final ConcurrentMap<String, Document> documents = new ConcurrentHashMap<>();
    private final Publication publication;

    public Documents(Publication publication) {
        this.publication = publication;
    }

    public List<Document> lister() {
        return documents.values().stream().sorted(Comparator.comparing(Document::numero)).toList();
    }

    public Document creer(DemandeCreation demande) {
        if (demande == null || demande.titre() == null || demande.titre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le titre est obligatoire");
        }
        String numero = "DOC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Document document = new Document(numero, demande.titre().trim(),
                demande.destinataire() == null ? "" : demande.destinataire().trim(), "CREE");
        publication.publier(document, "CREATE", Routage.CREATION);
        documents.put(numero, document);
        return document;
    }

    public Document envoyer(String numero) {
        Document precedent = trouver(numero);
        if ("ANNULE".equals(precedent.etat())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Document déjà annulé");
        }
        Document suivant = new Document(precedent.numero(), precedent.titre(), precedent.destinataire(), "ENVOYE");
        publication.publier(suivant, "MAIL_ENVOI", Routage.ENVOI_MAIL);
        documents.put(numero, suivant);
        return suivant;
    }

    public Document annuler(String numero) {
        Document precedent = trouver(numero);
        Document suivant = new Document(precedent.numero(), precedent.titre(), precedent.destinataire(), "ANNULE");
        publication.publier(suivant, "ANNULATION", Routage.ANNULATION);
        documents.put(numero, suivant);
        return suivant;
    }

    public void supprimer(String numero) {
        Document document = trouver(numero);
        publication.publier(document, "DELETE", Routage.SUPPRESSION);
        documents.remove(numero);
    }

    private Document trouver(String numero) {
        Document document = documents.get(numero);
        if (document == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document introuvable : " + numero);
        }
        return document;
    }
}
