package fr.demo.rabbitmq.config;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class ConfigurationRabbit {
    @Bean
    public TopicExchange exchangeSillage() {
        return new TopicExchange(Routage.EXCHANGE, true, false);
    }

    @Bean
    public Queue fileExportMail() {
        return new Queue(Routage.FILE_EXPORT_MAIL, true);
    }

    @Bean
    public Queue fileJournalDemo() {
        return new Queue(Routage.FILE_JOURNAL_DEMO, true);
    }

    @Bean
    public Declarables liaisons(TopicExchange exchangeSillage,
                               @Qualifier("fileExportMail") Queue fileExportMail,
                               @Qualifier("fileJournalDemo") Queue fileJournalDemo) {
        return new Declarables(
            BindingBuilder.bind(fileExportMail).to(exchangeSillage).with(Routage.CLASSEMENT_PATTERN),
            BindingBuilder.bind(fileExportMail).to(exchangeSillage).with(Routage.ENVOI_MAIL),
            BindingBuilder.bind(fileExportMail).to(exchangeSillage).with(Routage.IMPORT_PATTERN),
            BindingBuilder.bind(fileExportMail).to(exchangeSillage).with(Routage.PROCESS_IMPORT_PATTERN),
            BindingBuilder.bind(fileExportMail).to(exchangeSillage).with(Routage.ANNULATION),
            // Ajout pédagogique pour visualiser CREATE et DELETE, absents de la file mail Sillage.
            BindingBuilder.bind(fileJournalDemo).to(exchangeSillage).with("#")
        );
    }
}
