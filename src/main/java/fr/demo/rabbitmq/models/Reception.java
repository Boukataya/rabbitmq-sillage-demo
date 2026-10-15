package fr.demo.rabbitmq.models;

import java.time.Instant;

public record Reception(Instant date, String queue, String routingKey, String contenu) {}
