# TP 5 – Activité 5-2 : Service de banque avec Java RMI et JNDI

**Matière :** Développement d'Applications Réparties – LSI 3
**Faculté des Sciences de Sfax** – Année universitaire 2026 – 2027
**Type :** Individuel

---

## 1. Objectif

Développer une application répartie de gestion de comptes bancaires avec **Java RMI**, puis remplacer l'accès direct au registre RMI (`Naming`) par **JNDI** (Java Naming and Directory Interface) pour l'enregistrement (*bind*) et la recherche (*lookup*) de l'objet distant.

Un compte bancaire est défini par : un **code** (`int`), un **solde** (`double`) et une **date de création** (`Date`).

Services distants offerts par le serveur :
- **Créer un compte** avec un code et un solde initial (si le code n'existe pas déjà) ;
- **Consulter un compte** à partir de son code.

## 2. Architecture du projet

```
votreNomPrenom_LSI3_DevAppReparties/
├── RMIBanque_Server/
│   ├── src/
│   │   ├── jndi.properties
│   │   ├── metier/Compte.java            (classe sérialisable)
│   │   ├── rmiService/IBanque.java       (interface distante)
│   │   ├── rmiService/BanqueImpl.java    (implémentation)
│   │   └── rmiServer/BanqueServer.java   (serveur)
│   └── out/artifacts/banqueClient/banqueClient.jar
├── RMIBanque_Client/
│   ├── lib/banqueClient.jar
│   └── src/
│       ├── jndi.properties
│       └── rmiClient/BanqueClient.java
└── README.md
```

## 3. Travail réalisé

### Étape 2 – Développement du serveur

- **`Compte`** (package `metier`) : classe qui implémente `Serializable`, avec les attributs `code`, `solde`, `dateCreation`. Elle doit être sérialisable car les objets `Compte` sont **transmis par copie** entre le client et le serveur à travers le réseau.
- **`IBanque`** (package `rmiService`) : interface distante qui étend `Remote` et déclare :
  - `String creerCompte(Compte c) throws RemoteException`
  - `String getInfoCompte(int code) throws RemoteException`
- **`BanqueImpl`** (package `rmiService`) : implémente `IBanque` (en étendant `UnicastRemoteObject`) et stocke les comptes dans une collection (clé = code). `creerCompte` refuse la création si le code existe déjà.
- **`BanqueServer`** (package `rmiServer`) : démarre le registre RMI, crée l'objet `BanqueImpl` et le publie.

### Étape 3 – Développement du client

- Export du JAR **`banqueClient.jar`** depuis le serveur, contenant **uniquement** `metier/Compte.class` et `rmiService/IBanque.class` (sans `BanqueImpl`).
- Ajout du JAR au classpath de `RMIBanque_Client`.
- Création de `BanqueClient` (package `rmiClient`) qui recherche l'objet distant `IBanque`, crée un compte puis consulte ses informations.

### Étape 5 – Passage à JNDI

Le fichier **`jndi.properties`** (dans `src`) configure le contexte JNDI pour qu'il utilise le registre RMI :

```properties
java.naming.factory.initial=com.sun.jndi.rmi.registry.RegistryContextFactory
java.naming.provider.url=rmi://localhost:1099
```

- **Serveur :** un `InitialContext` remplace `Naming` ; l'objet `BanqueImpl` est enregistré avec `context.rebind(...)`.
- **Client :** l'objet distant est recherché avec `context.lookup(...)` via un `InitialContext`, au lieu de `Naming.lookup(...)`.

---

## 4. Captures d'écran et explications

### 4.1 Étape 4 – Exécution avec le registre RMI

**Démarrage du serveur**

<img width="669" height="196" alt="Capture d&#39;écran 2026-11-13 225915" src="https://github.com/user-attachments/assets/8d22f3f7-22a8-4c22-a0aa-413606262382" />


Le serveur affiche « Serveur Banque RMI démarré... » : le registre est actif et l'objet `BanqueImpl` est publié.

**Exécution du client**

<img width="674" height="204" alt="Capture d&#39;écran 2026-11-13 230033" src="https://github.com/user-attachments/assets/27028daf-1af9-4f4c-94ed-f942da41eca5" />


Le client affiche « Compte créé avec succès. » puis les informations du compte consulté : `Compte{code=1, solde=5000.0, dateCreation=...}`. Les deux méthodes distantes (`creerCompte` et `getInfoCompte`) fonctionnent.

### 4.2 Étape 3 – Génération et import du JAR client

**Artefact JAR `banqueClient`**

<img width="1028" height="483" alt="Capture d&#39;écran 2026-11-13 230118" src="https://github.com/user-attachments/assets/cfd70880-fc8f-491f-b03d-dfc573240fc0" />



Dans *Project Structure → Artifacts*, l'artefact `banqueClient` (type JAR) contient uniquement `metier/Compte.class` et `rmiService/IBanque.class`, conformément à la consigne (sans `BanqueImpl`).

**Dépendance dans le projet client**

<img width="1011" height="465" alt="Capture d&#39;écran 2026-11-13 230051" src="https://github.com/user-attachments/assets/008e25bd-237e-4e98-8cd1-84e7ea1ba985" />


`banqueClient.jar` est ajouté dans *Modules → Dependencies* du projet `RMIBanque_Client` avec la portée **Compile**.

### 4.3 Étape 5 – Exécution avec JNDI

**Démarrage du serveur avec JNDI**

<img width="511" height="207" alt="Capture d&#39;écran 2026-11-13 231158" src="https://github.com/user-attachments/assets/5faaadf2-10fb-4419-9d68-91fb5b144746" />


Le message « Serveur Banque RMI avec JNDI démarré... » confirme que l'objet distant est désormais enregistré via `InitialContext`.

**Client JNDI – création du compte**

<img width="657" height="209" alt="Capture d&#39;écran 2026-11-13 231253" src="https://github.com/user-attachments/assets/6c1caea9-021e-4bd9-b5bb-dfa4f2135591" />


Le client retrouve l'objet distant par `lookup` JNDI, crée le compte (« Compte créé avec succès. ») et affiche ses informations. La communication RMI fonctionne donc toujours avec JNDI.

**Client JNDI – compte déjà existant**

<img width="713" height="253" alt="Capture d&#39;écran 2026-11-13 231336" src="https://github.com/user-attachments/assets/45d74667-d418-49c7-84de-19261dfa6a59" />


En relançant le client sans redémarrer le serveur, le message « Ce compte existe déjà. » s'affiche : le serveur a conservé le compte en mémoire et refuse de créer un doublon avec le même code. On remarque que la **date de création est identique** à celle de la première exécution (le compte n'a pas été recréé).

---

## 5. Synthèse

- **RMI** permet au client d'invoquer des méthodes sur un objet situé sur le serveur comme s'il était local, grâce au **stub**.
- La classe **`Compte`** est **sérialisable** : elle est passée *par valeur* (copie) entre client et serveur, alors que `IBanque` est un objet **distant** passé *par référence* (stub).
- Le **JAR client** ne contient que le contrat (`IBanque`) et le modèle (`Compte`) : le client ne dépend pas de l'implémentation du serveur.
- **JNDI** fournit une API standard d'annuaire (`InitialContext`, `bind/rebind`, `lookup`). Le fichier **`jndi.properties`** indique la fabrique de contexte (`RegistryContextFactory`) et l'URL du registre RMI, ce qui découple le code du fournisseur d'annuaire utilisé.
- **Règle métier :** un compte n'est créé que si son code n'existe pas déjà, ce qui est vérifié par le test du second lancement du client.
- **Ordre d'exécution :** toujours démarrer le serveur avant le client.

**Résultat :** l'application fonctionne correctement avec le registre RMI puis avec JNDI.
