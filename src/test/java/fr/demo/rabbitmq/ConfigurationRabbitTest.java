package fr.demo.rabbitmq;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Declarables;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationRabbitTest {
    private final ConfigurationRabbit configuration = new ConfigurationRabbit();

    @Test
    void declareLesCinqBindingsDeLaFileMail() {
        Declarables declarables = configuration.liaisons(
                configuration.exchangeSillage(), configuration.fileExportMail(), configuration.fileJournalDemo());
        Set<String> bindings = declarables.getDeclarablesByType(Binding.class).stream()
                .filter(binding -> Routage.FILE_EXPORT_MAIL.equals(binding.getDestination()))
                .map(Binding::getRoutingKey).collect(Collectors.toSet());

        assertEquals(Set.of(Routage.CLASSEMENT_PATTERN, Routage.ENVOI_MAIL,
                Routage.IMPORT_PATTERN, Routage.PROCESS_IMPORT_PATTERN, Routage.ANNULATION), bindings);
        assertEquals("fr.sib.sillage.topic", configuration.exchangeSillage().getName());
    }
}
