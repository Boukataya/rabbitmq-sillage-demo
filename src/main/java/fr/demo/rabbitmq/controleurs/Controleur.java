package fr.demo.rabbitmq.controleurs;

import fr.demo.rabbitmq.config.Routage;
import fr.demo.rabbitmq.models.DemandeCreation;
import fr.demo.rabbitmq.models.Document;
import fr.demo.rabbitmq.models.Reception;
import fr.demo.rabbitmq.services.Documents;
import fr.demo.rabbitmq.services.Journal;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class Controleur {
    private final Documents documents;
    private final Journal journal;

    public Controleur(Documents documents, Journal journal) {
        this.documents = documents;
        this.journal = journal;
    }

    @GetMapping("/documents")
    public List<Document> documents() {
        return documents.lister();
    }

    @PostMapping("/documents")
    @ResponseStatus(HttpStatus.CREATED)
    public Document creer(@RequestBody DemandeCreation demande) {
        return documents.creer(demande);
    }

    @PostMapping("/documents/{numero}/envoi")
    public Document envoyer(@PathVariable String numero) {
        return documents.envoyer(numero);
    }

    @PostMapping("/documents/{numero}/annulation")
    public Document annuler(@PathVariable String numero) {
        return documents.annuler(numero);
    }

    @DeleteMapping("/documents/{numero}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable String numero) {
        documents.supprimer(numero);
    }

    @GetMapping("/messages")
    public List<Reception> messages() {
        return journal.lister();
    }

    @GetMapping("/topologie")
    public Map<String, Object> topologie() {
        Map<String, Object> donnees = new LinkedHashMap<>();
        donnees.put("exchange", Routage.EXCHANGE);
        donnees.put("type", "topic");
        donnees.put("fileExportMail", Routage.FILE_EXPORT_MAIL);
        donnees.put("bindingsExportMail", List.of(Routage.CLASSEMENT_PATTERN, Routage.ENVOI_MAIL,
                Routage.IMPORT_PATTERN, Routage.PROCESS_IMPORT_PATTERN, Routage.ANNULATION));
        donnees.put("fileJournalDemo", Routage.FILE_JOURNAL_DEMO);
        donnees.put("bindingsJournalDemo", List.of("#"));
        return donnees;
    }
}
