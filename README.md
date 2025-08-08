# Projet_file_rouge_BACK

## C2.1.1 Mettre en œuvre des environnements de déploiement et de test en y intégrant les outils de suivi de performance et de qualité afin de permettre le bon déroulement de la phase de développement du logiciel

### Environnement de Développement Détaillé

#### Outils de Développement
- **IDE Principal** : IntelliJ IDEA Ultimate / Visual Studio Code avec extensions Java
- **JDK** : OpenJDK 23 (Amazon Corretto ou Eclipse Temurin)
- **Gestionnaire de Build** : Apache Maven 3.9+
- **Contrôle de Version** : Git 2.40+
- **Base de Données Locale** : PostgreSQL 15+ via Docker
- **Conteneurisation** : Docker Desktop 4.20+ avec Docker Compose

#### Configuration IDE
- **Extensions requises** :
  - Spring Boot Tools
  - Java Extension Pack
  - Docker Extension
  - GitLens pour l'historique Git
- **Configuration JVM** : `-Xmx2G -Xms512M` pour les performances optimales
- **Profils Maven** : `dev`, `test`, `prod` configurés dans l'IDE

### Identification des Composants Techniques

#### 1. Compilateur et Build
- **Compilateur Java** : `javac` intégré au JDK 23
- **Outil de Build** : Apache Maven 3.9+
  - Plugins configurés : `spring-boot-maven-plugin`, `jacoco-maven-plugin`
  - Profils de compilation : développement, test, production
- **Gestion des Dépendances** : Maven Central Repository
- **Artifact Repository** : Stockage des JAR générés

#### 2. Serveur d'Application
- **Serveur Embarqué** : Apache Tomcat 10+ intégré à Spring Boot
- **Configuration** :
  - Port par défaut : 8080 (configurable via `server.port`)
  - Context path : `/` (racine)
  - Compression GZIP activée
  - SSL/TLS en production via Traefik

#### 3. Outils de Gestion de Sources
- **VCS** : Git avec repository GitHub
  - Branch principale : `main`
  - Strategy de branching : GitFlow simplifié
  - Hooks pre-commit pour validation du code
- **Plateforme** : GitHub avec intégrations CI/CD
- **Gestion des releases** : Tags Git sémantiques (v1.0.0, v1.1.0, etc.)

#### 4. Base de Données
- **SGBD** : PostgreSQL 15+
- **Driver JDBC** : org.postgresql:postgresql:42.6.0
- **ORM** : Hibernate 6+ via Spring Data JPA
- **Migrations** : Hibernate DDL-auto (update) + scripts SQL manuels si nécessaire

#### 5. Outils de Qualité et Tests
- **Framework de Tests** : JUnit 5 + Mockito + Spring Boot Test
- **Couverture de Code** : JaCoCo avec rapports HTML/XML
- **Analyse Statique** : SonarCloud intégré au pipeline CI/CD
- **Tests de Performance** : Spring Boot Actuator + Micrometer

### Séquences de Déploiement Définies

#### Séquence 1 : Développement Local
```bash
# 1. Préparation environnement
docker-compose -f docker-compose.local.yml up -d postgres

# 2. Compilation et tests
./mvnw clean compile test

# 3. Génération rapport couverture
./mvnw jacoco:report

# 4. Lancement application
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# 5. Vérification santé application
curl http://localhost:8080/actuator/health
```

#### Séquence 2 : Intégration Continue (CI)
```yaml
# Pipeline GitHub Actions
name: CI Pipeline
on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      # 1. Checkout code
      - uses: actions/checkout@v3
      
      # 2. Setup JDK
      - uses: actions/setup-java@v3
        with:
          java-version: '23'
          
      # 3. Cache Maven dependencies
      - uses: actions/cache@v3
        with:
          path: ~/.m2
          key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
          
      # 4. Run tests with coverage
      - run: ./mvnw clean test jacoco:report
      
      # 5. SonarCloud analysis
      - uses: SonarSource/sonarcloud-github-action@master
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
      
      # 6. Build Docker image
      - run: docker build -t schooldev:${{ github.sha }} .
```

#### Séquence 3 : Déploiement Staging
```bash
# 1. Build image de production
docker build -t schooldev:staging .

# 2. Tag et push vers registry
docker tag schooldev:staging registry/schooldev:staging
docker push registry/schooldev:staging

# 3. Déploiement staging
docker-compose -f docker-compose.staging.yml up -d

# 4. Tests de smoke sur staging
curl -f http://staging.schooldev.com/actuator/health
./scripts/smoke-tests.sh staging

# 5. Tests d'intégration complets
./mvnw test -Pintegration-staging
```

#### Séquence 4 : Déploiement Production
```bash
# 1. Validation pré-production
./scripts/pre-production-checks.sh

# 2. Backup base de données
pg_dump schooldev_prod > backup_$(date +%Y%m%d_%H%M%S).sql

# 3. Déploiement blue-green
docker-compose -f docker-compose.prod.yml up -d --scale spring-app=2

# 4. Health check et warm-up
./scripts/health-check-loop.sh production

# 5. Switch traffic via Traefik
./scripts/switch-traffic.sh

# 6. Tests post-déploiement
./scripts/post-deploy-tests.sh production

# 7. Monitoring et alerting
./scripts/setup-monitoring.sh production
```

### Critères de Qualité et Performance

#### Critères de Qualité du Code
- **Couverture de Tests** : ≥ 80% (ligne) et ≥ 70% (branche)
- **Métriques SonarCloud** :
  - Bugs : 0
  - Vulnerabilités : 0
  - Code Smells : < 10
  - Duplication de Code : < 3%
  - Maintainability Rating : A
  - Reliability Rating : A
  - Security Rating : A

#### Critères de Performance
- **Temps de Réponse** :
  - Endpoints simples (GET) : < 200ms
  - Endpoints complexes (POST/PUT) : < 500ms
  - Requêtes base de données : < 100ms
- **Débit** :
  - Minimum 100 requêtes/seconde
  - Pic supporté : 500 requêtes/seconde
- **Disponibilité** : 99.9% (objectif SLA)
- **Temps de Déploiement** : < 5 minutes (zéro downtime)

#### Critères de Sécurité
- **Scan de Vulnérabilités** : 0 vulnérabilité HIGH/CRITICAL
- **Tests de Pénétration** : Validation OWASP Top 10
- **Chiffrement** : HTTPS obligatoire, mots de passe BCrypt
- **Authentification** : JWT sécurisé avec expiration

#### Critères d'Exploitation
- **Logs** : Format JSON structuré, niveau configurable
- **Métriques** : Exposition via Micrometer/Prometheus
- **Health Checks** : Endpoints `/actuator/health` et `/actuator/info`
- **Documentation** : API Swagger à jour, README maintenu

### Pipeline de Déploiement Automatisé

#### Configuration GitFlow
```bash
# Branches et stratégie
main           # Production stable
develop        # Intégration continue
feature/*      # Nouvelles fonctionnalités
hotfix/*       # Corrections urgentes production
release/*      # Préparation releases
```

#### Déclencheurs Automatiques
- **Push sur develop** → Tests + Déploiement staging automatique
- **PR vers main** → Tests complets + Review obligatoire + SonarCloud gate
- **Tag release** → Déploiement production automatique après validation manuelle
- **Hotfix** → Pipeline accéléré avec déploiement direct production après tests

#### Rollback et Recovery
- **Stratégie Blue-Green** : Switch instantané en cas de problème
- **Database Rollback** : Scripts de rollback automatiques pour migrations
- **Monitoring Continu** : Alertes automatiques sur métriques dégradées
- **Plan de Recovery** : RTO < 15 minutes, RPO < 5 minutes

Cette approche garantit un déploiement continu fiable, traçable et conforme aux exigences de qualité et performance du projet SchoolDev.

## C2.2.1. Concevoir un prototype de l'application logicielle en tenant compte des spécificités ergonomiques et des équipements ciblés (ex : web, mobile…) afin de répondre aux fonctionnalités attendues et aux exigences en termes de sécurité.

**✅ Les bonnes pratiques de développement sont respectées** dans ce projet :

### Architecture et Structure
- **Architecture en couches** : Séparation claire entre Controller, Service, Repository et Model
- **Injection de dépendances** : Utilisation de Spring Boot avec constructeurs pour l'injection
- **Séparation des responsabilités** : Chaque classe a une responsabilité unique et bien définie

### Qualité du Code
- **Complexité cognitive maîtrisée** : Refactorisation des méthodes complexes (ex: ExerciseService.createExercise)
- **Gestion des cycles de dépendances** : Résolution appropriée des cycles JPA avec @JsonIgnore
- **Validations robustes** : Validation conditionnelle selon les types d'exercices
- **Gestion d'erreurs** : Exceptions appropriées avec messages explicites

### Sécurité
- **Authentification JWT** : Implémentation sécurisée avec filtre personnalisé
- **Chiffrement des mots de passe** : Utilisation de BCrypt
- **Limitation de débit** : Rate limiting avec Bucket4j
- **Headers de sécurité** : CORS, HSTS, Content-Type Options

### Base de Données
- **Modélisation relationnelle** : Relations JPA correctement définies
- **Transactions** : Utilisation appropriée de @Transactional
- **Requêtes optimisées** : Repository patterns avec Spring Data JPA
- **Gestion des cascades** : Cascade types appropriés pour les relations

### Tests et Documentation
- **Couverture de tests** : Tests unitaires et d'intégration avec JaCoCo
- **Documentation API** : Swagger/OpenAPI intégré
- **Documentation technique** : CLAUDE.md avec instructions détaillées

### DevOps et Déploiement
- **Containerisation** : Docker avec multi-stage builds
- **Orchestration** : Docker Compose pour dev et production
- **Reverse Proxy** : Configuration Traefik avec HTTPS automatique
- **Variables d'environnement** : Configuration externalisée

### Standards et Conventions
- **Conventions de nommage** : Respect des conventions Java/Spring
- **Structure de packages** : Organisation logique par domaine
- **Configuration** : Propriétés externalisées et configurables
- **Logging** : Logs appropriés pour le debugging et monitoring

## Validation du Prototype Fonctionnel

**✅ Le prototype est fonctionnel et répond aux besoins identifiés** :

### Fonctionnalités Core Implémentées
- **Système d'authentification** : Inscription/connexion utilisateur avec JWT
- **Gestion des cours** : CRUD complet des cours avec niveaux de difficulté
- **Système d'exercices** : Création et soumission d'exercices avec validation automatique
- **Suivi de progression** : Tracking des progrès utilisateur par cours et exercice
- **Système de badges** : Attribution automatique des badges d'accomplissement

### API REST Complète
- **Endpoints fonctionnels** : Tous les contrôleurs exposent des endpoints testés
- **Documentation interactive** : Swagger UI accessible pour tests en temps réel
- **Validation des données** : DTOs avec validation appropriée des entrées
- **Gestion des erreurs** : Réponses HTTP cohérentes avec messages explicites

### Déploiement Production-Ready
- **Environnement conteneurisé** : Application dockerisée avec PostgreSQL
- **Configuration flexible** : Variables d'environnement pour différents déploiements
- **Reverse proxy** : Traefik configuré avec HTTPS automatique
- **Monitoring** : Logs structurés et métriques de santé applicative

### Validation par Tests
- **Tests automatisés** : Couverture complète des services et contrôleurs
- **Tests d'intégration** : Validation des flux end-to-end
- **Rapport de couverture** : JaCoCo générant des métriques de qualité

## Satisfaction des Exigences de Sécurité

**✅ Le prototype satisfait aux exigences de sécurité** :

### Authentification et Autorisation
- **JWT sécurisé** : Tokens avec clé secrète externalisée et expiration configurable
- **Chiffrement des mots de passe** : BCrypt avec salt automatique (coût 12)
- **Filtre d'authentification** : JwtFilter vérifiant chaque requête protégée
- **Gestion des rôles** : Système de rôles utilisateur avec contrôle d'accès

### Protection contre les Attaques
- **Rate Limiting** : Bucket4j limitant les requêtes par utilisateur (50 req/min)
- **CORS configuré** : Origins, méthodes et headers autorisés explicitement
- **Injection SQL** : Protection native via JPA/Hibernate avec requêtes paramétrées
- **XSS Prevention** : Headers de sécurité et validation des entrées

### Headers de Sécurité HTTP
- **HSTS** : Strict-Transport-Security pour forcer HTTPS
- **Content-Type Options** : nosniff pour éviter le type sniffing
- **Frame Options** : DENY pour prévenir le clickjacking
- **Referrer Policy** : strict-origin-when-cross-origin pour limiter les fuites

### Gestion des Secrets et Configuration
- **Variables d'environnement** : Secrets externalisés (JWT_SECRET_KEY, DB_PASSWORD)
- **Configuration sécurisée** : Aucun secret en dur dans le code
- **HTTPS en production** : Traefik avec Let's Encrypt pour certificats automatiques
- **Base de données sécurisée** : Connexions chiffrées et credentials isolés

### Validation et Sanitisation
- **Validation des DTOs** : Annotations Bean Validation sur tous les endpoints
- **Gestion des erreurs** : Messages d'erreur sans exposition d'informations sensibles
- **Logs sécurisés** : Aucun log de mots de passe ou tokens
- **Transactions atomiques** : @Transactional pour la cohérence des données

## C2.2.2 Développer un harnais de test unitaire en tenant compte des fonctionnalités demandées afin de prévenir les régressions et de s’assurer du bon fonctionnement du logiciel 

**✅ Les tests unitaires couvrent la majorité du code développé** :

### Couverture Complète par Couches
- **Controllers** : Tests unitaires de tous les endpoints REST avec MockMvc
- **Services** : Tests de la logique métier avec mocks des dépendances
- **Models** : Tests des entités JPA et de leurs relations
- **Configuration** : Tests de sécurité, CORS, JWT et rate limiting
- **DTOs** : Tests de validation et sérialisation/désérialisation

### Types de Tests Implémentés
- **Tests unitaires** : Isolation complète avec Mockito pour les dépendances
- **Tests d'intégration** : Validation des flux complets avec @SpringBootTest
- **Tests de sécurité** : Validation des configurations JWT et authentification
- **Tests de contrôleurs** : Simulation des requêtes HTTP avec assertions complètes

### Métriques de Couverture
- **Rapport JaCoCo** : Génération automatique avec `./mvnw jacoco:report`
- **Couverture par classe** : Toutes les classes métier testées individuellement
- **Couverture des branches** : Tests des cas nominaux et d'erreur
- **Assertions complètes** : Vérification des retours, exceptions et états

### Exemples de Tests Couverts
- **AuthController** : Inscription, connexion, validation des tokens
- **CourseService** : CRUD complet avec gestion d'erreurs
- **ExerciseService** : Création d'exercices QCM et code avec validations
- **ProgressController** : Suivi de progression et soumissions
- **BadgeService** : Attribution automatique et gestion des badges
- **SecurityConfig** : Configuration JWT et filtres de sécurité

### Outils et Frameworks de Test
- **JUnit 5** : Framework de test principal avec annotations modernes
- **Mockito** : Mocking des dépendances pour isolation des tests
- **Spring Boot Test** : Intégration complète avec contexte Spring
- **JaCoCo** : Mesure et rapport de couverture de code

## C2.2.3. Développer le logiciel en veillant à l'évolutivité et à la sécurisation du code source, aux exigences d’accessibilité et aux spécifications techniques et fonctionnelles définies, pour garantir une exécution conforme aux exigences du client. 

**✅ Les mesures prises couvrent les 10 failles de sécurité principales OWASP** :

### 1. A01 - Broken Access Control
- **Authentification JWT** : Contrôle d'accès basé sur les tokens
- **Filtre de sécurité** : JwtFilter vérifiant l'autorisation sur chaque requête
- **Gestion des rôles** : Attribution et vérification des permissions utilisateur

### 2. A02 - Cryptographic Failures
- **Chiffrement des mots de passe** : BCrypt avec salt et coût élevé (12)
- **JWT sécurisé** : Signature avec clé secrète forte externalisée
- **HTTPS obligatoire** : Traefik avec certificats Let's Encrypt
- **Variables d'environnement** : Secrets chiffrés et externalisés

### 3. A03 - Injection
- **Protection SQL** : JPA/Hibernate avec requêtes paramétrées natives
- **Validation des entrées** : Bean Validation sur tous les DTOs
- **Sanitisation** : Échappement automatique des données utilisateur

### 4. A04 - Insecure Design
- **Architecture sécurisée** : Séparation des couches et responsabilités
- **Principe du moindre privilège** : Accès restreint par rôle
- **Fail secure** : Comportement sécurisé par défaut en cas d'erreur

### 5. A05 - Security Misconfiguration
- **Configuration externalisée** : Variables d'environnement pour tous les secrets
- **Headers de sécurité** : HSTS, nosniff, frame-options configurés
- **CORS restrictif** : Origins et méthodes explicitement autorisées
- **Profils Spring** : Configuration différenciée dev/prod

### 6. A06 - Vulnerable Components
- **Dépendances à jour** : Maven avec versions récentes et sécurisées
- **Spring Boot récent** : Framework maintenu avec patches de sécurité
- **Containers sécurisés** : Images Alpine Linux minimalistes

### 7. A07 - Identification and Authentication Failures
- **JWT robuste** : Expiration configurable et révocation possible
- **Mots de passe forts** : Validation côté serveur avec regex
- **Rate limiting** : Protection contre le brute force (50 req/min)
- **Pas de credentials par défaut** : Tous les mots de passe externalisés

### 8. A08 - Software and Data Integrity Failures
- **Validation des données** : Contrôles stricts sur tous les inputs
- **Transactions atomiques** : @Transactional garantissant la cohérence
- **Logs d'audit** : Traçabilité des actions utilisateur

### 9. A09 - Security Logging Failures
- **Logs sécurisés** : Aucune exposition de mots de passe ou tokens
- **Logs structurés** : Format cohérent pour monitoring
- **Événements tracés** : Connexions, erreurs et actions sensibles

### 10. A10 - Server-Side Request Forgery (SSRF)
- **Validation des URLs** : Pas d'endpoints acceptant des URLs externes
- **Isolation réseau** : Containers avec réseau Docker isolé
- **Principe de moindre privilège** : Accès réseau minimal

## C2.2.4. Déployer le logiciel à chaque modification de code et de façon progressive en vérifiant la performance fonctionnelle et technique auprès des utilisateurs afin de présenter une solution stable et conforme à l’attendu. 

**✅ Le logiciel est fonctionnel et manipulable en autonomie par un utilisateur** :

### Interface API Intuitive
- **Documentation interactive** : Swagger UI accessible permettant de tester tous les endpoints
- **Réponses standardisées** : Format JSON cohérent avec codes HTTP appropriés
- **Messages d'erreur explicites** : Descriptions claires des erreurs avec suggestions de correction
- **Validation en temps réel** : Feedback immédiat sur la validité des données saisies

### Parcours Utilisateur Autonome
- **Inscription simple** : Création de compte avec validation email et mot de passe
- **Connexion sécurisée** : Authentification JWT avec gestion automatique des sessions
- **Navigation intuitive** : API REST suivant les conventions RESTful standards
- **Gestion des erreurs** : Récupération gracieuse avec messages utilisateur compréhensibles

### Fonctionnalités Complètes et Accessibles
- **Gestion de profil** : L'utilisateur peut modifier ses informations personnelles
- **Exploration des cours** : Consultation autonome du catalogue avec filtres par difficulté
- **Réalisation d'exercices** : Soumission et évaluation automatique des solutions
- **Suivi de progression** : Visualisation en temps réel de l'avancement dans les cours

### Documentation et Support
- **API documentée** : Spécifications OpenAPI complètes avec exemples
- **Instructions d'installation** : Guide pas-à-pas pour déploiement local et production
- **Exemples d'utilisation** : Cas d'usage concrets dans la documentation Swagger
- **Configuration flexible** : Variables d'environnement documentées pour personnalisation

### Fiabilité et Disponibilité
- **Déploiement containerisé** : Installation reproductible avec Docker
- **Configuration automatisée** : Docker Compose pour environnement complet
- **Monitoring intégré** : Logs structurés pour diagnostic et maintenance
- **Sauvegarde des données** : Persistance PostgreSQL avec volumes Docker

### Expérience Utilisateur Optimisée
- **Performance** : Réponses API rapides avec optimisations base de données
- **Sécurité transparente** : Protection automatique sans friction utilisateur
- **Compatibilité** : API REST standard compatible avec tous types de clients
- **Évolutivité** : Architecture modulaire permettant l'ajout de nouvelles fonctionnalités

## C2.3.1 Élaborer le cahier de recettes en rédigeant les scénarios de tests et les résultats attendus afin de détecter les anomalies de fonctionnement et les régressions éventuelles

**Voir document CAHIER_DE_RECETTES.md**

## C2.3.2 Élaborer un plan de correction des bogues à partir de l’analyse des anomalies et des régressions détectées au cours de la recette afin de garantir le fonctionnement du logiciel conformément à l’attendu.

**✅ Les bogues sont détectés, qualifiés et traités avec analyse d'amélioration** :

### Détection Automatisée des Bogues
- **Tests unitaires continus** : Exécution automatique avec `./mvnw test` détectant les régressions
- **Couverture de code JaCoCo** : Identification des zones non testées potentiellement buggées
- **Tests d'intégration** : Validation des flux complets pour détecter les bugs système
- **Analyse statique** : Détection des anti-patterns et code smells

### Qualification et Priorisation
- **Classification par criticité** :
  - **CRITIQUE** : Bug bloquant l'authentification ou corrompant les données
  - **MAJEUR** : Fonctionnalité principale indisponible mais contournement possible
  - **MINEUR** : Problème d'ergonomie ou performance dégradée
  - **TRIVIAL** : Problème cosmétique ou amélioration suggérée

- **Classification par composant** :
  - **Sécurité** : Failles de sécurité, authentification, autorisation
  - **API** : Endpoints, validation, réponses HTTP
  - **Base de données** : Intégrité, performance, transactions
  - **Configuration** : Docker, variables d'environnement, déploiement

### Processus de Traitement des Bogues

#### 1. Identification et Documentation
- **Reproduction systématique** : Étapes précises pour reproduire le bug
- **Logs détaillés** : Analyse des traces d'exécution et stack traces
- **Contexte environnemental** : Version, configuration, données de test
- **Impact utilisateur** : Évaluation de l'impact sur l'expérience utilisateur

#### 2. Analyse des Points d'Amélioration
- **Analyse de cause racine** : Identification de la source du problème
- **Évaluation des solutions** : Comparaison des approches de correction
- **Impact sur l'existant** : Vérification des effets de bord potentiels
- **Couverture de tests** : Identification des tests manquants ayant permis le bug

#### 3. Corrections et Améliorations

##### Exemples de Corrections Réalisées

**Bug AUTH-001 : Token JWT expiré non détecté**
- **Problème** : Utilisateurs restaient connectés avec des tokens expirés
- **Cause racine** : Vérification d'expiration manquante dans JwtFilter
- **Solution** : Ajout de validation d'expiration avec exception appropriée
- **Amélioration** : Tests unitaires pour tous les cas de tokens invalides
- **Code corrigé** :
```java
// Avant (vulnérable)
if (jwtUtils.validateToken(token)) {
    // Process request
}

// Après (sécurisé)
if (jwtUtils.validateToken(token) && !jwtUtils.isTokenExpired(token)) {
    // Process request
} else {
    throw new JwtAuthenticationException("Token expired or invalid");
}
```

**Bug DATA-002 : Cycle de dépendance JPA**
- **Problème** : Erreurs de sérialisation JSON lors de récupération d'exercices
- **Cause racine** : Relations bidirectionnelles sans @JsonIgnore
- **Solution** : Ajout d'annotations @JsonIgnore sur les références inverses
- **Amélioration** : Documentation des patterns de relations JPA
- **Tests ajoutés** : Sérialisation/désérialisation de toutes les entités

**Bug VALID-003 : Validation QCM insuffisante**
- **Problème** : Création d'exercices QCM sans propositions acceptée
- **Cause racine** : Validation conditionnelle manquante selon le type d'exercice
- **Solution** : Logique de validation spécifique par type d'exercice
- **Amélioration** : Factory pattern pour la création d'exercices
- **Refactoring** : Séparation claire entre ExerciseService.createQcmExercise() et createCodeExercise()

### Garanties de Conformité

#### Processus de Validation des Corrections
1. **Tests de régression** : Vérification que le bug est corrigé sans introduire de régressions
2. **Tests d'intégration étendus** : Validation des flux complets impactés
3. **Review de code** : Analyse par pairs de la qualité de la correction
4. **Tests de performance** : Vérification que la correction n'impacte pas les performances

#### Métriques de Qualité
- **Temps de résolution** : 
  - CRITIQUE : < 4h
  - MAJEUR : < 24h  
  - MINEUR : < 1 semaine
- **Taux de régression** : < 2% (bugs réintroduits par les corrections)
- **Couverture de tests post-correction** : +5% minimum sur les zones corrigées
- **Documentation mise à jour** : 100% des corrections documentées

#### Traçabilité et Amélioration Continue
- **Historique des bugs** : Documentation complète dans les commits Git
- **Analyse des tendances** : Identification des composants les plus problématiques
- **Formation équipe** : Partage des bonnes pratiques issues des corrections
- **Amélioration des tests** : Enrichissement de la suite de tests après chaque bug

### Outils de Détection et Monitoring
- **SonarCloud** : Analyse statique et détection de code smells - [Tableau de bord du projet](https://sonarcloud.io/project/overview?id=Matias1512_Projet_file_rouge_BACK)
- **JaCoCo** : Couverture de code pour identifier les zones à risque
- **Spring Boot Actuator** : Métriques applicatives et health checks
- **Logs structurés** : Traçabilité complète des erreurs en production

Cette approche garantit que chaque bogue détecté fait l'objet d'une analyse approfondie, d'une correction appropriée et d'améliorations préventives pour éviter sa réapparition.