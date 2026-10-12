# RabbitMQ — mini-laboratoire inspiré de Sillage

Projet pédagogique Maven / Spring Boot 3.5.7 / Java 21, sans dépendance aux JAR internes du SIB.
Les noms de l'exchange, de la queue d'export mail et de ses cinq bindings sont repris de
`amqpcore-api.zip`, et les rôles producteur/consommateur sont inspirés de `app.zip`.
Les DTO, l'envoi d'email et la logique métier restent volontairement simplifiés.

## 1. Démarrer RabbitMQ

**Option A : Docker disponible**

```bash
docker compose up -d
```

**Option B : sans Docker**

Installer RabbitMQ Server avec le plugin Management activé, puis créer un utilisateur
`demo` avec le mot de passe `demo`, et lui donner les permissions sur un vhost dédié.
Modifier `src/main/resources/application.yml` pour correspondre à votre configuration.
Ne pas pointer cette démonstration vers un broker partagé de Sillage : elle utilise volontairement
un nom de queue identique à celui de Sillage, ce qui ferait entrer les consommateurs en concurrence.

Sur Docker, l'interface Management est : http://localhost:15672
Identifiants : `demo` / `demo`.

## 2. Démarrer Spring Boot

Java 21 et Maven requis :

```bash
mvn spring-boot:run
```

Ou ouvrir le projet Maven dans IntelliJ et lancer `Application.main()`.

Ouvrir ensuite **http://localhost:8080**.

## 3. Comprendre le flux

```text
Bouton HTML / requête HTTP
      ↓
Controleur → Documents → Publication
      ↓ RabbitTemplate.convertAndSend(EXCHANGE, routingKey, JSON)
Exchange topic : fr.sib.sillage.topic
      ├── bindings réels Sillage ──→ File export.mail ──→ Consommateurs.recevoirMail()
      └── binding pédagogique '#' → File journal   ──→ Consommateurs.recevoirJournal()
      ↓
Journal des messages → GET /api/messages → page HTML
```

| Action | Routing key | File export.mail ? | File journal ? |
|---|---|---|---|
| Créer | `fr.sib.sillage.bureautique.document.create` | Non | Oui |
| Envoyer | `fr.sib.sillage.bureautique.export.mail.demande_manuelle` | Oui | Oui |
| Annuler | `fr.sib.sillage.bureautique.document.cancel` | Oui | Oui |
| Supprimer | `fr.sib.sillage.bureautique.document.delete` | Non | Oui |

La **file mail directe** possède exactement ces cinq bindings repris des sources :

```text
fr.sib.sillage.bureautique.classement.#
fr.sib.sillage.bureautique.export.mail.demande_manuelle
fr.sib.sillage_document.bureautique.import.#
fr.sib.sillage_document.bureautique.process.import.#
fr.sib.sillage.bureautique.document.cancel
```

**Important :** plusieurs bindings sur une même queue ne produisent pas automatiquement
deux exemplaires d'un même message. Un message peut en revanche être livré à plusieurs
*queues*, comme dans cette démo (export.mail et journal).

## 4. Explorer dans RabbitMQ Management

1. `Exchanges` → `fr.sib.sillage.topic` : inspecter le type `topic` et ses bindings.
2. `Queues and Streams` → `fr.sib.sillage_document.bureautique.export.mail.queue` :
   inspecter les bindings, les consommateurs et les messages.
3. Attention : les consommateurs Spring accusent réception rapidement, ce qui fait que
   la file peut rester à **Ready = 0** même après des envois. Les messages reçus sont
   visibles dans le panneau « Messages consommés » de la page HTML et dans les logs.
4. Tester un message manuel : `Exchanges` → `fr.sib.sillage.topic` → `Publish message` :
   **Routing key :** `fr.sib.sillage.bureautique.document.cancel`,
   **Payload :** `{"numero":"DOC-42","action":"ANNULATION"}`.
   Ce message devrait être reçu dans les deux files, puis enregistré dans le journal.
5. Tester la routing key `fr.sib.sillage.bureautique.document.create` : elle ne doit
   arriver que dans la queue pédagogique de journal.

## 5. Où modifier le code ?

- `Routage.java` : exchange, files, patterns et clés.
- `ConfigurationRabbit.java` : création des queues et **bindings** Spring AMQP.
- `Publication.java` : **publisher** vers l'exchange.
- `Consommateurs.java` : deux méthodes **@RabbitListener**.
- `Documents.java` : actions métier simulées et leurs publications.
- `Controleur.java` : endpoints REST de l'interface.
- `static/index.html` : boutons et suivi des messages.

Le projet **ne contacte aucun service MSS** et n'envoie aucun email réel.

## 6. Tests

```bash
mvn test
```

Le test vérifie l'exchange et les cinq bindings du flux mail sans nécessiter
de serveur RabbitMQ.
