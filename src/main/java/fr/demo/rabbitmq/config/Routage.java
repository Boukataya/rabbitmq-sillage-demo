package fr.demo.rabbitmq.config;

/** Constantes reprises de amqpcore-api (Sillage), avec une queue d'observation supplémentaire. */
public final class Routage {
    private Routage() {}

    public static final String EXCHANGE = "fr.sib.sillage.topic";
    public static final String FILE_EXPORT_MAIL = "fr.sib.sillage_document.bureautique.export.mail.queue";
    public static final String FILE_JOURNAL_DEMO = "demo.documents.journal.queue";

    public static final String CLASSEMENT_PATTERN = "fr.sib.sillage.bureautique.classement.#";
    public static final String ENVOI_MAIL = "fr.sib.sillage.bureautique.export.mail.demande_manuelle";
    public static final String IMPORT_PATTERN = "fr.sib.sillage_document.bureautique.import.#";
    public static final String PROCESS_IMPORT_PATTERN = "fr.sib.sillage_document.bureautique.process.import.#";
    public static final String ANNULATION = "fr.sib.sillage.bureautique.document.cancel";

    public static final String CREATION = "fr.sib.sillage.bureautique.document.create";
    public static final String SUPPRESSION = "fr.sib.sillage.bureautique.document.delete";
}
