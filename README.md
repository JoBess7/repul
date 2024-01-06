[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-24ddc0f5d75046c5622901739e7c5dd533143b0c8e959d652212380cedb1ea36.svg)](https://classroom.github.com/a/E16L_9U-)
# Rep-UL 🥡

## Qu'est-ce que c'est?

Service de boîte repas de l’Université Laval, l’éducation commence toujours par une bonne alimentation!

## Requis ✅

* Java 17 (openJDK seulement)
* Junit 5+
* Mockito 3+
* Maven

### Notes sur les technologies utilisées

* [Jetty](https://www.eclipse.org/jetty/) est un Servlet Container. Il accepte les requêtes du web et sait comment répondre.
* [Jersey](https://jersey.github.io/) est un Servlet fait pour le développement d'API REST. Il sait comment faire la correspondance entre un API REST et vos méthodes selon la norme JAX-RS.

## Setup pour le développement 🛠

_Le setup est recommandé pour IntelliJ_

* À partir du [Github](https://github.com/GLO4003UL/a23-projet-rep-ul-a23-eq-01) du projet, cloner le projet sur votre poste de travail.

* Installer la bonne version de Java. Vous pouvez installer manuellement Java 17 et mettre à jour la variable JAVA_HOME. Cependant, nous vous recommandons d'utiliser
  [SDKMAN](https://sdkman.io/) pour gérer Java et plusieurs outils et frameworks reliés à la JVM. Cet outil va vous faciliter énormément
  la gestion des versions de Java et outils annexes surtout si vous en voulez plusieurs sur votre machine.

## Démarrer le projet 🚀

### Variable d'environnement

Vous devez ajouter les informations suivantes : EMAIL_SENDER, PASSWORD_SENDER, LOCKER_API_KEY et JWT_SECRET dans le `.env`.

Afin de faire fonctionner l'encryptage des mots de passe, vous devez ajouter la variable JWT_SECRET dans le `.env`.
Vous pouvez mettre la valeur que vous voulez pour cette variable.

Afin de faire fonctionner l'envoi de courriels, vous devez ajouter les variables EMAIL_SENDER et PASSWORD_SENDER dans le `.env`.
Vous ne pouvez pas mettre n'importe quelle valeur dans ces variables. Pour obtenir les valeurs à inscrire, vous devez allez directement sur le
discord de l'équipe 01 dans le salon suivant : https://discord.com/channels/870753929317744660/1167630977959469216. (credentials-gmail-repul)

Afin d'avoir la vérification que l'envoi de courriel fonctionne correctement pour le livreur lorsqu'une cargaison est prête, il faut
ajouter votre adresse Ulaval dans le `.env` à la variable d'environnement "DELIVERY_PERSON_EMAIL". C'est obligatoire lorsqu'on lance 
l'application dans un autre contexte que celui de test.

Afin de recevoir le courriel qui affiche où le colis est livré pour le client, il faut
ajouter votre adresse Ulaval dans le `.env` à la variable d'environnement "CLIENT_EMAIL". C'est obligatoire lorsqu'on lance
l'application dans un autre contexte que celui de test.

Il est important de noter que ces deux adresses emails doivent être différentes. 

Afin de savoir quelle valeur de clé d'API est valide pour l'authentification des lockers, il faut ajouter la variable LOCKER_API_KEY
dans le `.env`. La valeur ajoutée doit être la même que dans la collection _Postman_.

### Lancer l'application
* Dans un terminal, exécutez start.sh si vous êtes sur Linux / OSX
* Dans un terminal, exécutez start.bat si vous êtes sur Windows
* Dans un IDE, exécutez la classe `Main` en tant que "Java Application"
* Une fois démarrée, vous trouverez l'API à l'URL suivante:
  * http://localhost:8080/api/
* Afin de valider votre projet, vous pouvez:
  * Exécuter toutes les vérifications (test, dependency-check, etc...) et produire un artifact pour votre application (se trouvant sous
    `target/application.jar`) avec `mvn verify` que vous pouvez juste invoquer directement avec un `java -jar target/application.jar`

### Démarrer le projet en mode test

Pour lancer l'application en mode test, exécuter start.sh ou start.bt en ajoutant l'argument de ligne de commande "--test".
Par exemple, sous Mac/Linux: `./start.sh --test`.

### Démarrer le projet en mode Demo et spécification

Comme les requis font en sorte qu'on ne peut confirmer une commande dans un délai de 48h, un contexte de démonstration a été créé afin
de pouvoir tester les parties demandant une boîte-repas confirmée.

Pour lancer l'application en mode demo, exécuter start.sh ou start.bat en ajoutant l'argument de ligne de commande "--demo".

#### Information sur les comptes déjà présents dans l'application
Ces informations sont nécessaires afin de se connecter avec chacun des comptes à l'aide de la route de _log in_.

Compte du cuisinier :
  - courriel : paul@ulaval.ca
  - mot de passe : paul123

Compte du livreur :
  - courriel : (l'adresse Ulaval que vous avez mise dans le `.env`)
  - mot de passe : roger123

**En mode démo seulement:

Compte du client :
- courriel : alexandra@ulaval.ca
- mot de passe : alexandra123

## Tests 🧪

Afin d'exécuter les tests, vous pouvez:
  * Exécuter les tests unitaires avec `mvn test`
  * Exécuter tous les tests (unitaires et d'intégrations) avec `mvn integration-test`

## Checkstyle 📝

Le checkstyle est ce qui permet de faire le formatage du code de façon uniforme.
Voici les différentes étapes pour l'activer :

```
File -> Settings -> Plugins
S'assurer d'installer le plugin CheckStyle-IDEA (Redémarrer l'IDE si demandé)

// Pour faire apparaître les erreurs dans le code
File -> Settings -> Tools -> Checkstyle
Dans le tableau Configuration File, cliquer sur Add
Sélectionner "Use a local Checkstyle file" et sélectionner le fichier checkstyle.xml à la racine du projet
Saisir une description pour le fichier et appuyer sur NEXT
Une fois la nouvelle configuration ajoutée, la sélectionner dans le tableau
Appuyer sur APPLY et ensuite sur OK

// Pour le formateur
File -> Settings -> Editor -> Code Style
Sélectionner Default copy pour le Scheme
Cliquer sur les trois petits points (Show Scheme Actions)
Appuyer sur Rename et renommer pour "Checkstyle"
Cliquer sur les trois petits points (Show Scheme Actions)
Appuyer sur "Import Scheme" et "Checkstyle configuration"
Sélectionner le fichier checkstyle.xml à la root du projet
//Pour tester le formateur, dans un fichier Java, appuyez sur Ctrl+Alt+Shift+L
```
