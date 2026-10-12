package fr.demo.rabbitmq;

import java.time.Instant;

/** DTO volontairement simplifié, distinct des DTO métier de Sillage. */
public record EvenementDocument(String numero, String action, String destinataire, Instant date) {}
