# Manuel de Mise à Jour - SchoolDev API

## Table des Matières
1. [Vue d'ensemble des Mises à Jour](#vue-densemble-des-mises-à-jour)
2. [Types de Mises à Jour](#types-de-mises-à-jour)
3. [Stratégies de Mise à Jour](#stratégies-de-mise-à-jour)
4. [Procédures par Type](#procédures-par-type)
5. [Gestion des Versions](#gestion-des-versions)
6. [Sauvegarde et Rollback](#sauvegarde-et-rollback)
7. [Tests de Validation](#tests-de-validation)
8. [Maintenance des Dépendances](#maintenance-des-dépendances)

---

## Vue d'ensemble des Mises à Jour

### Composants du Système SchoolDev

#### Stack Technique Actuelle
- **Java** : 23 (Eclipse Temurin)
- **Spring Boot** : 3.4.2
- **PostgreSQL** : Latest (15+)
- **Docker** : Latest
- **Traefik** : v2.10
- **Maven** : Wrapper inclus

#### **Justifications des Choix Technologiques pour les Mises à Jour**

**Java 23 (Eclipse Temurin)** :
- **Avantages** : Performances optimisées, nouvelles fonctionnalités du langage
- **Stratégie de mise à jour** : Suivi des versions LTS et non-LTS pour bénéficier des améliorations
- **Migration path** : Java 24 prévu, puis Java 25 LTS

**Spring Boot 3.4.2** :
- **Justification** : Framework mature avec mises à jour fréquentes de sécurité
- **Politique** : Suivi des versions mineures (3.4.x) pour les patches de sécurité
- **Breaking changes** : Versions majeures (4.x) nécessitent validation approfondie

**PostgreSQL Latest** :
- **Choix** : Bénéficier des dernières optimisations et correctifs de sécurité
- **Stratégie** : Mises à jour mineures automatiques, majeures planifiées

#### Dépendances Principales
```xml
<!-- Versions actuelles avec justifications -->
<java.version>23</java.version>                    <!-- Performances et nouvelles fonctionnalités -->
<spring-boot.version>3.4.2</spring-boot.version>   <!-- Sécurité et stabilité -->
<springdoc.version>2.8.5</springdoc.version>       <!-- Documentation API automatique -->
<lombok.version>1.18.28</lombok.version>           <!-- Réduction boilerplate code -->
<jjwt.version>0.11.5</jjwt.version>                <!-- JWT standard et sécurisé -->
<mockito.version>5.2.0</mockito.version>           <!-- Tests unitaires avec mocking -->
<bucket4j.version>7.6.0</bucket4j.version>         <!-- Rate limiting performant -->
<jacoco.version>0.8.13</jacoco.version>            <!-- Couverture de code -->
```

#### **Philosophie de Mise à Jour**
- **Sécurité first** : Patches de sécurité appliqués immédiatement
- **Stabilité** : Tests complets avant mise en production
- **Performance** : Suivi des optimisations JVM et Spring
- **Compatibilité** : Validation des breaking changes avant adoption

### Fréquence des Mises à Jour Recommandée

#### Mises à Jour de Sécurité
- **Fréquence** : Immédiate (< 24h)
- **Type** : Patches de sécurité critique
- **Déclencheur** : Alertes de sécurité, CVE

#### Mises à Jour Mineures
- **Fréquence** : Mensuelle
- **Type** : Bug fixes, améliorations mineures
- **Déclencheur** : Cycle de maintenance planifié

#### Mises à Jour Majeures
- **Fréquence** : Trimestrielle
- **Type** : Nouvelles fonctionnalités, breaking changes
- **Déclencheur** : Roadmap produit, obsolescence

---

## Types de Mises à Jour

### 1. Mise à Jour de l'Application (Code Métier)

#### Scope
- Nouvelles fonctionnalités
- Corrections de bugs
- Améliorations UI/UX
- Optimisations de performance

#### Impact
- **Risque** : Moyen
- **Downtime** : Minimal avec stratégie appropriée
- **Rollback** : Simple

### 2. Mise à Jour des Dépendances

#### Spring Boot
```xml
<!-- Exemple de mise à jour -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.4.2</version> <!-- Vers 3.4.3 ou 3.5.0 -->
</parent>
```

#### Java Runtime
```dockerfile
# Dockerfile - mise à jour Java
FROM eclipse-temurin:23-jdk-alpine  # Vers 24 ou LTS
```

### 3. Mise à Jour de l'Infrastructure

#### Base de Données PostgreSQL
- Versions mineures : 15.1 → 15.2
- Versions majeures : 15 → 16

#### Containers et Orchestration
- Docker Engine
- Docker Compose
- Images de base (Alpine, etc.)

#### Reverse Proxy
- Traefik v2.10 → v2.11 ou v3.0

### 4. Mise à Jour de Sécurité

#### Certificats SSL
- Renouvellement automatique Let's Encrypt
- Rotation des clés JWT
- Mise à jour des secrets

---

## Stratégies de Mise à Jour

### **Choix des Stratégies de Déploiement**

#### **Critères de Sélection des Stratégies**
- **Architecture Stateless** : Application Spring Boot sans session serveur
- **Base de données partagée** : Contrainte pour les migrations de schéma
- **Tolérance aux pannes** : Exigence zero-downtime en production
- **Simplicité opérationnelle** : Équipe réduite, processus automatisés

### 1. Rolling Update (Recommandé)

#### Principe
- Déploiement progressif sans interruption de service
- Maintien de l'ancienne version pendant la transition
- Validation automatique avant bascule complète

#### **Justifications du Choix**
- **Applications Stateless** : Parfait pour les APIs REST sans session
- **Compatibilité descendante** : Versions N et N-1 cohabitent temporairement
- **Monitoring intégré** : Health checks automatiques avec rollback
- **Ressources optimisées** : Pas de doublement d'infrastructure

#### Avantages
- Zero downtime
- Rollback immédiat possible
- Validation en conditions réelles
- Utilisation efficace des ressources

#### Configuration Docker Compose
```yaml
# docker-compose.yml - Configuration optimisée pour Rolling Updates
services:
  spring-app:
    image: matias151/schooldev_back:latest
    deploy:
      replicas: 2                    # Minimum pour rolling update
      update_config:
        parallelism: 1               # Une instance à la fois
        delay: 30s                   # Délai pour health checks
        failure_action: rollback     # Sécurité automatique
        monitor: 60s                 # Période de surveillance
      restart_policy:
        condition: on-failure
        delay: 10s
```

### 2. Blue-Green Deployment

#### Principe
- Deux environnements identiques (Blue/Green)
- Déploiement sur l'environnement inactif
- Bascule instantanée du trafic

#### **Justifications d'Usage**
- **Migrations de schéma** : Changements incompatibles en base de données
- **Versions majeures** : Breaking changes nécessitant validation complète
- **Tests de performance** : Charge complète sur nouvel environnement
- **Rollback critique** : Bascule instantanée en cas de problème majeur

#### Utilisation
- Mises à jour majeures (Spring Boot 3.x → 4.x)
- Changements de schéma de base de données
- Migration d'infrastructure (Java 23 → 24)
- Tests de montée en charge

### 3. Canary Deployment

#### Principe
- Déploiement sur un sous-ensemble d'utilisateurs
- Surveillance des métriques
- Déploiement progressif si validation OK

#### **Avantages pour SchoolDev**
- **Validation métier** : Test avec vrais utilisateurs sur nouvelles fonctionnalités
- **Détection d'anomalies** : Monitoring des métriques business
- **Risque limité** : Exposition progressive en cas de problème

#### Configuration Traefik
```yaml
# traefik-canary.yml - Configuration canary intelligente
labels:
  - "traefik.http.services.app-canary.loadbalancer.server.port=8080"
  - "traefik.http.routers.app-canary.rule=Host(`schooldev.duckdns.org`) && Headers(`X-Canary`, `true`)"
  - "traefik.http.routers.app-canary.middlewares=canary-auth"
  # 10% du trafic vers la version canary
  - "traefik.http.services.app-weighted.loadbalancer.sticky.cookie.name=canary"
```

#### **Matrice de Décision des Stratégies**

| Type de Mise à Jour | Rolling Update | Blue-Green | Canary |
|---------------------|---------------|------------|---------|
| **Patch de sécurité** | ✅ Recommandé | ❌ Overkill | ❌ Trop lent |
| **Fonctionnalité mineure** | ✅ Recommandé | ⚠️ Possible | ✅ Idéal |
| **Version majeure Spring** | ⚠️ Risqué | ✅ Recommandé | ⚠️ Complexe |
| **Migration BD majeure** | ❌ Impossible | ✅ Obligatoire | ❌ Risqué |
| **Nouvelle API** | ✅ OK | ⚠️ Overkill | ✅ Recommandé |

---

## Procédures par Type

### Mise à Jour de l'Application

#### 1. Préparation
```bash
# 1. Backup complet
./scripts/backup-full.sh

# 2. Tests locaux complets
git pull
./mvnw clean test
./mvnw jacoco:report

# 3. Vérification des dépendances
./mvnw dependency:tree
./mvnw versions:display-dependency-updates
```

#### 2. Build et Validation
```bash
# Build de la nouvelle version
./mvnw clean package -DskipTests

# Tests d'intégration
./mvnw verify -Pintegration

# Scan de sécurité
./mvnw org.owasp:dependency-check-maven:check
```

#### 3. Déploiement Rolling Update
```bash
#!/bin/bash
# rolling-update.sh

echo "🚀 Démarrage Rolling Update SchoolDev..."

# Variables
NEW_VERSION=$1
HEALTH_URL="https://schooldev.duckdns.org/actuator/health"
MAX_RETRIES=30
RETRY_INTERVAL=10

# Validation des paramètres
if [ -z "$NEW_VERSION" ]; then
    echo "❌ Usage: $0 <version>"
    exit 1
fi

# 1. Build de la nouvelle image
echo "📦 Build de l'image $NEW_VERSION..."
docker build -t schooldev_back:$NEW_VERSION .
docker tag schooldev_back:$NEW_VERSION matias151/schooldev_back:$NEW_VERSION

# 2. Push vers le registry
echo "📤 Push vers le registry..."
docker push matias151/schooldev_back:$NEW_VERSION

# 3. Sauvegarde de l'état actuel
echo "💾 Sauvegarde avant mise à jour..."
./scripts/backup-db.sh
docker tag matias151/schooldev_back:latest matias151/schooldev_back:backup

# 4. Mise à jour progressive
echo "🔄 Déploiement progressif..."

# Scale up avec nouvelle version
docker service update --image matias151/schooldev_back:$NEW_VERSION schooldev_spring-app

# Attente de stabilisation
echo "⏳ Attente de stabilisation (30s)..."
sleep 30

# 5. Validation de santé
echo "🔍 Validation de la nouvelle version..."
for i in $(seq 1 $MAX_RETRIES); do
    if curl -f -s $HEALTH_URL > /dev/null; then
        echo "✅ Health check réussi (tentative $i)"
        break
    else
        echo "⚠️ Health check échoué (tentative $i/$MAX_RETRIES)"
        if [ $i -eq $MAX_RETRIES ]; then
            echo "❌ Échec de validation - Rollback automatique"
            docker service update --image matias151/schooldev_back:backup schooldev_spring-app
            exit 1
        fi
        sleep $RETRY_INTERVAL
    fi
done

# 6. Finalisation
docker tag matias151/schooldev_back:$NEW_VERSION matias151/schooldev_back:latest

echo "🎉 Rolling update complété avec succès!"
echo "📊 Version déployée: $NEW_VERSION"
echo "🔗 Application: $HEALTH_URL"
```

### Mise à Jour des Dépendances

#### 1. Mise à Jour Spring Boot
```bash
# 1. Vérifier les versions disponibles
./mvnw versions:display-dependency-updates

# 2. Mise à jour du parent POM
# Éditer pom.xml manuellement ou via script
sed -i 's/<version>3.4.2<\/version>/<version>3.4.3<\/version>/' pom.xml

# 3. Résoudre les dépendances
./mvnw clean compile

# 4. Tests de compatibilité
./mvnw test
./mvnw spring-boot:run

# 5. Tests d'intégration
./mvnw verify -Pintegration
```

#### Script de Mise à Jour Automatique
```bash
#!/bin/bash
# update-dependencies.sh

echo "🔧 Mise à jour des dépendances Maven..."

# Backup du pom.xml actuel
cp pom.xml pom.xml.backup

# Mise à jour des versions
./mvnw versions:use-latest-versions -DallowSnapshots=false
./mvnw versions:update-parent

# Compilation et tests
if ./mvnw clean test; then
    echo "✅ Mise à jour des dépendances réussie"
    ./mvnw versions:commit
else
    echo "❌ Échec des tests - Restauration du pom.xml"
    mv pom.xml.backup pom.xml
    exit 1
fi

# Génération du rapport de mise à jour
./mvnw versions:dependency-updates-report
echo "📊 Rapport disponible dans target/site/dependency-updates-report.html"
```

#### 2. Mise à Jour Java Runtime
```bash
# 1. Vérifier la compatibilité
java --version
./mvnw compiler:compile -Dmaven.compiler.source=24 -Dmaven.compiler.target=24

# 2. Mise à jour du Dockerfile
sed -i 's/eclipse-temurin:23-jdk-alpine/eclipse-temurin:24-jdk-alpine/' Dockerfile
sed -i 's/eclipse-temurin:23-jdk-alpine/eclipse-temurin:24-jdk-alpine/' Dockerfile.local

# 3. Mise à jour du pom.xml
sed -i 's/<java.version>23<\/java.version>/<java.version>24<\/java.version>/' pom.xml

# 4. Tests de compatibilité
./mvnw clean test
```

### Mise à Jour de la Base de Données

#### 1. PostgreSQL - Version Mineure
```bash
#!/bin/bash
# update-postgres-minor.sh

echo "📊 Mise à jour PostgreSQL mineure..."

# 1. Sauvegarde complète
docker-compose exec postgres pg_dumpall -U postgres > backup_full_$(date +%Y%m%d_%H%M%S).sql

# 2. Arrêt de l'application
docker-compose stop spring-app

# 3. Mise à jour PostgreSQL
docker-compose pull postgres
docker-compose up -d postgres

# 4. Vérification de santé BD
sleep 30
docker-compose exec postgres psql -U postgres -c "SELECT version();"

# 5. Redémarrage de l'application
docker-compose up -d spring-app

echo "✅ Mise à jour PostgreSQL mineure terminée"
```

#### 2. PostgreSQL - Version Majeure
```bash
#!/bin/bash
# update-postgres-major.sh

echo "🔄 Mise à jour PostgreSQL majeure (avec migration)..."

# 1. Sauvegarde complète
echo "💾 Sauvegarde complète..."
docker-compose exec postgres pg_dumpall -U postgres > backup_before_major_$(date +%Y%m%d_%H%M%S).sql

# 2. Arrêt complet
docker-compose down

# 3. Sauvegarde du volume de données
docker run --rm -v schooldev_postgres_data:/source -v $(pwd):/backup alpine tar czf /backup/postgres_data_backup_$(date +%Y%m%d_%H%M%S).tar.gz -C /source .

# 4. Mise à jour de l'image PostgreSQL
sed -i 's/postgres:15/postgres:16/' docker-compose.yml

# 5. Recréation avec nouvelle version
docker volume rm schooldev_postgres_data
docker-compose up -d postgres

# 6. Attente de démarrage
sleep 60

# 7. Restauration des données
docker-compose exec -T postgres psql -U postgres < backup_before_major_$(date +%Y%m%d_%H%M%S).sql

# 8. Redémarrage de l'application
docker-compose up -d

echo "✅ Migration PostgreSQL majeure terminée"
```

### Mise à Jour de l'Infrastructure

#### 1. Traefik
```bash
#!/bin/bash
# update-traefik.sh

echo "🔄 Mise à jour Traefik..."

# 1. Backup de la configuration actuelle
cp docker-compose.yml docker-compose.yml.backup

# 2. Mise à jour de la version Traefik
sed -i 's/traefik:v2.10/traefik:v2.11/' docker-compose.yml

# 3. Redéploiement
docker-compose pull traefik
docker-compose up -d traefik

# 4. Vérification
sleep 10
curl -f https://schooldev.duckdns.org/actuator/health

if [ $? -eq 0 ]; then
    echo "✅ Mise à jour Traefik réussie"
else
    echo "❌ Problème détecté - Rollback"
    mv docker-compose.yml.backup docker-compose.yml
    docker-compose up -d traefik
fi
```

#### 2. Images Docker de Base
```bash
#!/bin/bash
# update-base-images.sh

echo "🐳 Mise à jour des images Docker de base..."

# 1. Pull des nouvelles images
docker pull eclipse-temurin:23-jdk-alpine
docker pull postgres:latest
docker pull traefik:v2.10

# 2. Rebuild de l'image application
docker build -t schooldev_back:latest .

# 3. Redéploiement progressif
docker-compose up -d --force-recreate

echo "✅ Images de base mises à jour"
```

---

## Gestion des Versions

### **Stratégie de Versioning Adoptée**

#### **Choix du Semantic Versioning (SemVer)**
**Justifications** :
- **Standard industrie** : Convention largement adoptée et comprise
- **Clarté des impacts** : MAJOR/MINOR/PATCH indiquent le niveau de changement
- **Automatisation possible** : Outils CI/CD peuvent interpréter les versions
- **Rétrocompatibilité** : Gestion claire des breaking changes

#### Semantic Versioning (SemVer)
```
MAJOR.MINOR.PATCH
│     │     └── Bug fixes, patches de sécurité (1.0.1)
│     └── Nouvelles fonctionnalités rétrocompatibles (1.1.0)
└── Breaking changes, migrations majeures (2.0.0)
```

#### **Exemples de Versioning SchoolDev**
- `1.0.0` : Version initiale production
- `1.0.1` : Correctif de sécurité JWT
- `1.1.0` : Nouvelle API Challenges
- `1.2.0` : Système de badges avancé
- `2.0.0` : Migration Spring Boot 4.x (breaking changes)

#### Versioning de l'Application
```bash
# pom.xml - Application avec stratégie SNAPSHOT
<version>1.2.3-SNAPSHOT</version>    # Version en développement
<version>1.2.3</version>             # Version release

# Docker tags avec convention
matias151/schooldev_back:1.2.3       # Version spécifique
matias151/schooldev_back:1.2         # Version mineure (pour patches automatiques)
matias151/schooldev_back:latest      # Dernière version stable
matias151/schooldev_back:dev         # Version développement
```

#### **Stratégie de Tagging Git**
- `v1.2.3` : Tags releases officielles
- `v1.2.3-rc1` : Release candidates
- `v1.2.3-hotfix` : Correctifs urgents
- Branches `release/1.2.x` pour maintenance des versions

### Tags Git et Releases

#### Création d'une Release
```bash
#!/bin/bash
# create-release.sh

VERSION=$1
CHANGELOG_FILE="CHANGELOG.md"

if [ -z "$VERSION" ]; then
    echo "❌ Usage: $0 <version>"
    exit 1
fi

echo "🏷️ Création de la release $VERSION..."

# 1. Mise à jour de la version dans pom.xml
./mvnw versions:set -DnewVersion=$VERSION
./mvnw versions:commit

# 2. Tests complets
./mvnw clean test jacoco:report

# 3. Build final
./mvnw clean package -DskipTests

# 4. Commit des changements de version
git add pom.xml
git commit -m "chore: bump version to $VERSION"

# 5. Création du tag
git tag -a v$VERSION -m "Release $VERSION"

# 6. Push avec tags
git push origin main
git push origin v$VERSION

# 7. Build et push de l'image Docker
docker build -t matias151/schooldev_back:$VERSION .
docker tag matias151/schooldev_back:$VERSION matias151/schooldev_back:latest
docker push matias151/schooldev_back:$VERSION
docker push matias151/schooldev_back:latest

echo "✅ Release $VERSION créée et publiée"
echo "🐳 Image Docker: matias151/schooldev_back:$VERSION"
echo "🏷️ Tag Git: v$VERSION"
```

### Changelog Automatique

#### Génération du CHANGELOG.md
```bash
#!/bin/bash
# generate-changelog.sh

echo "📝 Génération du changelog..."

# Récupérer la dernière version
LAST_TAG=$(git describe --tags --abbrev=0 2>/dev/null || echo "")
CURRENT_DATE=$(date +%Y-%m-%d)

# Générer les changements depuis le dernier tag
if [ -z "$LAST_TAG" ]; then
    CHANGES=$(git log --oneline --pretty=format:"- %s (%h)")
else
    CHANGES=$(git log $LAST_TAG..HEAD --oneline --pretty=format:"- %s (%h)")
fi

# Préparer le nouveau changelog
{
    echo "# Changelog"
    echo ""
    echo "## [Unreleased] - $CURRENT_DATE"
    echo ""
    echo "$CHANGES"
    echo ""
    if [ -f CHANGELOG.md ]; then
        tail -n +4 CHANGELOG.md
    fi
} > CHANGELOG.new.md

mv CHANGELOG.new.md CHANGELOG.md

echo "✅ CHANGELOG.md mis à jour"
```

---

## Sauvegarde et Rollback

### Stratégie de Sauvegarde

#### Sauvegarde Automatique Pré-Mise à Jour
```bash
#!/bin/bash
# backup-full.sh

BACKUP_DIR="/var/backups/schooldev"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_NAME="full_backup_$TIMESTAMP"

echo "💾 Sauvegarde complète pré-mise à jour..."

mkdir -p $BACKUP_DIR

# 1. Sauvegarde base de données
echo "📊 Sauvegarde PostgreSQL..."
docker-compose exec postgres pg_dumpall -U postgres | gzip > $BACKUP_DIR/${BACKUP_NAME}_database.sql.gz

# 2. Sauvegarde de l'image Docker actuelle
echo "🐳 Sauvegarde image Docker..."
docker save matias151/schooldev_back:latest | gzip > $BACKUP_DIR/${BACKUP_NAME}_docker_image.tar.gz

# 3. Sauvegarde des volumes Docker
echo "📁 Sauvegarde volumes Docker..."
docker run --rm -v schooldev_postgres_data:/source -v $BACKUP_DIR:/backup alpine tar czf /backup/${BACKUP_NAME}_volumes.tar.gz -C /source .

# 4. Sauvegarde configuration
echo "⚙️ Sauvegarde configuration..."
tar czf $BACKUP_DIR/${BACKUP_NAME}_config.tar.gz docker-compose.yml docker-compose.local.yml Dockerfile* *.md

# 5. Métadonnées de sauvegarde
cat > $BACKUP_DIR/${BACKUP_NAME}_metadata.txt << EOF
Backup Date: $(date)
Git Commit: $(git rev-parse HEAD)
Git Branch: $(git branch --show-current)
Docker Image: matias151/schooldev_back:latest
Application Version: $(grep -A1 "<artifactId>schoolDev</artifactId>" pom.xml | grep version | sed 's/.*<version>\(.*\)<\/version>.*/\1/')
PostgreSQL Version: $(docker-compose exec postgres psql -U postgres -t -c "SELECT version();" | head -1)
EOF

echo "✅ Sauvegarde complète terminée: $BACKUP_DIR/$BACKUP_NAME*"
```

### Procédures de Rollback

#### 1. Rollback Application (Rapide)
```bash
#!/bin/bash
# rollback-app.sh

BACKUP_VERSION=${1:-backup}

echo "🔄 Rollback rapide de l'application..."

# 1. Revert vers la version précédente
docker tag matias151/schooldev_back:$BACKUP_VERSION matias151/schooldev_back:latest

# 2. Redéploiement
docker-compose up -d spring-app

# 3. Vérification
sleep 20
if curl -f -s https://schooldev.duckdns.org/actuator/health > /dev/null; then
    echo "✅ Rollback application réussi"
else
    echo "❌ Rollback échoué - Investigation nécessaire"
    exit 1
fi
```

#### 2. Rollback Complet (Base de Données Incluse)
```bash
#!/bin/bash
# rollback-full.sh

BACKUP_NAME=$1

if [ -z "$BACKUP_NAME" ]; then
    echo "❌ Usage: $0 <backup_name>"
    echo "Sauvegardes disponibles:"
    ls /var/backups/schooldev/ | grep full_backup
    exit 1
fi

BACKUP_DIR="/var/backups/schooldev"

echo "🔄 Rollback complet du système..."

# 1. Arrêt complet
docker-compose down

# 2. Restauration de l'image Docker
echo "🐳 Restauration image Docker..."
gunzip -c $BACKUP_DIR/${BACKUP_NAME}_docker_image.tar.gz | docker load

# 3. Restauration des volumes
echo "📁 Restauration volumes..."
docker volume rm schooldev_postgres_data
docker volume create schooldev_postgres_data
docker run --rm -v schooldev_postgres_data:/target -v $BACKUP_DIR:/backup alpine tar xzf /backup/${BACKUP_NAME}_volumes.tar.gz -C /target

# 4. Restauration configuration
echo "⚙️ Restauration configuration..."
tar xzf $BACKUP_DIR/${BACKUP_NAME}_config.tar.gz

# 5. Redémarrage
docker-compose up -d

# 6. Vérification
echo "⏳ Attente de démarrage (60s)..."
sleep 60

if curl -f -s https://schooldev.duckdns.org/actuator/health > /dev/null; then
    echo "✅ Rollback complet réussi"
    
    # Affichage des métadonnées de la sauvegarde restaurée
    echo "📊 Version restaurée:"
    cat $BACKUP_DIR/${BACKUP_NAME}_metadata.txt
else
    echo "❌ Rollback complet échoué"
    exit 1
fi
```

#### 3. Rollback Base de Données Uniquement
```bash
#!/bin/bash
# rollback-database.sh

BACKUP_FILE=$1

if [ -z "$BACKUP_FILE" ]; then
    echo "❌ Usage: $0 <backup_file.sql.gz>"
    exit 1
fi

echo "📊 Rollback base de données..."

# 1. Arrêt de l'application
docker-compose stop spring-app

# 2. Restauration BD
echo "🔄 Restauration depuis $BACKUP_FILE..."
gunzip -c $BACKUP_FILE | docker-compose exec -T postgres psql -U postgres

# 3. Redémarrage application
docker-compose up -d spring-app

echo "✅ Rollback base de données terminé"
```

---

## Tests de Validation

### Tests Pré-Mise à Jour

#### Suite de Tests Complète
```bash
#!/bin/bash
# pre-update-tests.sh

echo "🧪 Exécution des tests pré-mise à jour..."

EXIT_CODE=0

# 1. Tests unitaires
echo "🔬 Tests unitaires..."
if ./mvnw test; then
    echo "✅ Tests unitaires: OK"
else
    echo "❌ Tests unitaires: ECHEC"
    EXIT_CODE=1
fi

# 2. Tests d'intégration
echo "🔧 Tests d'intégration..."
if ./mvnw verify -Pintegration; then
    echo "✅ Tests d'intégration: OK"
else
    echo "❌ Tests d'intégration: ECHEC"
    EXIT_CODE=1
fi

# 3. Analyse de sécurité
echo "🔒 Analyse de sécurité..."
if ./mvnw org.owasp:dependency-check-maven:check; then
    echo "✅ Sécurité: OK"
else
    echo "⚠️ Sécurité: ALERTES"
    # Ne pas bloquer pour les alertes de sécurité mineures
fi

# 4. Qualité du code
echo "📊 Analyse qualité (SonarCloud)..."
./mvnw sonar:sonar -Dsonar.login=$SONAR_TOKEN

# 5. Couverture de tests
echo "🎯 Couverture de tests..."
./mvnw jacoco:report
COVERAGE=$(grep -o 'Total.*instruction.*\([0-9]\+%\)' target/site/jacoco/index.html | grep -o '[0-9]\+%' | head -1)
echo "Couverture actuelle: $COVERAGE"

if [ $EXIT_CODE -eq 0 ]; then
    echo "✅ Tous les tests pré-mise à jour sont passés"
else
    echo "❌ Certains tests ont échoué - Mise à jour non recommandée"
fi

exit $EXIT_CODE
```

### Tests Post-Mise à Jour

#### Validation Fonctionnelle
```bash
#!/bin/bash
# post-update-validation.sh

API_BASE="https://schooldev.duckdns.org"
EXIT_CODE=0

echo "✅ Validation post-mise à jour..."

# 1. Health Check
echo "🏥 Health Check..."
if curl -f -s $API_BASE/actuator/health | grep -q '"status":"UP"'; then
    echo "✅ Health Check: OK"
else
    echo "❌ Health Check: ECHEC"
    EXIT_CODE=1
fi

# 2. API Endpoints
echo "🔗 Tests des endpoints principaux..."

# Test authentification
AUTH_TOKEN=$(curl -s -X POST $API_BASE/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{"username":"test@example.com","password":"testpass"}' \
    | jq -r '.token' 2>/dev/null)

if [ "$AUTH_TOKEN" != "null" ] && [ -n "$AUTH_TOKEN" ]; then
    echo "✅ Authentification: OK"
else
    echo "❌ Authentification: ECHEC"
    EXIT_CODE=1
fi

# Test API courses
if curl -f -s $API_BASE/api/courses > /dev/null; then
    echo "✅ API Courses: OK"
else
    echo "❌ API Courses: ECHEC"
    EXIT_CODE=1
fi

# Test API exercises
if curl -f -s $API_BASE/api/exercises > /dev/null; then
    echo "✅ API Exercises: OK"
else
    echo "❌ API Exercises: ECHEC"
    EXIT_CODE=1
fi

# Test Swagger
if curl -f -s $API_BASE/swagger-ui/index.html > /dev/null; then
    echo "✅ Swagger UI: OK"
else
    echo "❌ Swagger UI: ECHEC"
    EXIT_CODE=1
fi

# 3. Base de données
echo "📊 Test connectivité base de données..."
if docker-compose exec postgres psql -U postgres -d schoolDevDatabase -c "SELECT 1;" > /dev/null 2>&1; then
    echo "✅ Base de données: OK"
else
    echo "❌ Base de données: ECHEC"
    EXIT_CODE=1
fi

# 4. Performance basique
echo "⚡ Test de performance basique..."
RESPONSE_TIME=$(curl -o /dev/null -s -w "%{time_total}" $API_BASE/actuator/health)
if (( $(echo "$RESPONSE_TIME < 2.0" | bc -l) )); then
    echo "✅ Performance: OK (${RESPONSE_TIME}s)"
else
    echo "⚠️ Performance: LENTE (${RESPONSE_TIME}s)"
fi

# 5. SSL/TLS
echo "🔒 Validation SSL/TLS..."
if curl -I -s $API_BASE | grep -q "HTTP/2 200"; then
    echo "✅ HTTPS/HTTP2: OK"
else
    echo "⚠️ HTTPS/HTTP2: Problème détecté"
fi

# Résumé
if [ $EXIT_CODE -eq 0 ]; then
    echo ""
    echo "🎉 Validation post-mise à jour réussie!"
    echo "📊 Application opérationnelle sur $API_BASE"
else
    echo ""
    echo "❌ Validation post-mise à jour échouée!"
    echo "🚨 Investigation nécessaire"
fi

exit $EXIT_CODE
```

### Tests de Régression

#### Suite de Tests End-to-End
```bash
#!/bin/bash
# e2e-regression-tests.sh

API_BASE="https://schooldev.duckdns.org"

echo "🎭 Tests de régression End-to-End..."

# 1. Scénario complet utilisateur
echo "👤 Test scénario utilisateur complet..."

# Inscription
USER_EMAIL="regression-test-$(date +%s)@example.com"
USER_PASSWORD="TestPass123!"

REGISTER_RESPONSE=$(curl -s -X POST $API_BASE/api/auth/register \
    -H "Content-Type: application/json" \
    -d "{\"email\":\"$USER_EMAIL\",\"password\":\"$USER_PASSWORD\",\"firstName\":\"Test\",\"lastName\":\"User\"}")

if echo "$REGISTER_RESPONSE" | grep -q "success\|created\|registered"; then
    echo "✅ Inscription: OK"
else
    echo "❌ Inscription: ECHEC"
    echo "Réponse: $REGISTER_RESPONSE"
fi

# Connexion
LOGIN_RESPONSE=$(curl -s -X POST $API_BASE/api/auth/login \
    -H "Content-Type: application/json" \
    -d "{\"email\":\"$USER_EMAIL\",\"password\":\"$USER_PASSWORD\"}")

TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.token' 2>/dev/null)

if [ "$TOKEN" != "null" ] && [ -n "$TOKEN" ]; then
    echo "✅ Connexion: OK"
else
    echo "❌ Connexion: ECHEC"
    echo "Réponse: $LOGIN_RESPONSE"
fi

# Tests avec authentification
if [ -n "$TOKEN" ] && [ "$TOKEN" != "null" ]; then
    # Test création de cours (si autorisé)
    COURSE_RESPONSE=$(curl -s -X POST $API_BASE/api/courses \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $TOKEN" \
        -d '{"title":"Test Course","description":"Regression test course","difficulty":"BEGINNER"}')
    
    # Test récupération de profil
    PROFILE_RESPONSE=$(curl -s -X GET $API_BASE/api/users/profile \
        -H "Authorization: Bearer $TOKEN")
    
    if echo "$PROFILE_RESPONSE" | grep -q "$USER_EMAIL"; then
        echo "✅ Profil utilisateur: OK"
    else
        echo "❌ Profil utilisateur: ECHEC"
    fi
fi

# 2. Tests de charges légères
echo "📈 Test de charge légère (10 requêtes simultanées)..."

for i in {1..10}; do
    curl -s $API_BASE/api/courses > /dev/null &
done
wait

echo "✅ Test de charge: Terminé"

echo "🏁 Tests de régression terminés"
```

---

## Maintenance des Dépendances

### Surveillance des Vulnérabilités

#### **Choix des Outils de Sécurité**

**OWASP Dependency Check** :
- **Justification** : Standard de l'industrie pour audit Maven
- **Base de données** : CVE National Vulnerability Database officielle
- **Intégration** : Plugin Maven natif, rapports automatiques

**Trivy pour Docker** :
- **Avantages** : Scanner de vulnérabilités complet (OS + applications)
- **Performance** : Base de données locale, scan rapide
- **Polyvalence** : Supporte multiples formats (Docker, Kubernetes, filesystem)

**GitHub Security Alerts** :
- **Intégration native** : Détection automatique dans le repository
- **Notifications** : Alertes en temps réel sur nouvelles vulnérabilités
- **Dependabot** : Mise à jour automatique des dépendances vulnérables

#### Audit de Sécurité Automatique
```bash
#!/bin/bash
# security-audit.sh - Audit complet multi-outils

echo "🔒 Audit de sécurité des dépendances SchoolDev..."

# 1. Audit Maven (OWASP)
echo "📦 Audit Maven (OWASP Dependency Check)..."
./mvnw org.owasp:dependency-check-maven:check
MAVEN_RESULT=$?

# 2. Audit Docker (Trivy)
echo "🐳 Audit images Docker (Trivy)..."
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
    -v $(pwd):/src \
    aquasec/trivy image --exit-code 1 --severity HIGH,CRITICAL \
    matias151/schooldev_back:latest
DOCKER_RESULT=$?

# 3. Audit du code source (Trivy filesystem)
echo "📁 Audit filesystem (Trivy)..."
docker run --rm -v $(pwd):/src \
    aquasec/trivy fs --exit-code 1 --severity HIGH,CRITICAL /src
FS_RESULT=$?

# 4. Génération du rapport consolidé
echo "📊 Génération du rapport de sécurité..."
cat > security-report.md << EOF
# Rapport de Sécurité SchoolDev - $(date)

## Résultats d'Audit

### Dépendances Maven (OWASP)
Status: $([ $MAVEN_RESULT -eq 0 ] && echo "✅ CLEAN" || echo "❌ VULNÉRABILITÉS DÉTECTÉES")
$(cat target/dependency-check-report.txt 2>/dev/null || echo "Aucune vulnérabilité critique détectée")

### Images Docker (Trivy)
Status: $([ $DOCKER_RESULT -eq 0 ] && echo "✅ CLEAN" || echo "❌ VULNÉRABILITÉS DÉTECTÉES")

### Code Source (Trivy)
Status: $([ $FS_RESULT -eq 0 ] && echo "✅ CLEAN" || echo "❌ VULNÉRABILITÉS DÉTECTÉES")

## Actions Recommandées
$([ $(($MAVEN_RESULT + $DOCKER_RESULT + $FS_RESULT)) -gt 0 ] && cat << ACTIONS
⚠️ **ACTIONS IMMÉDIATES REQUISES:**
- Mettre à jour les dépendances avec vulnérabilités HIGH/CRITICAL
- Vérifier les alertes GitHub Security
- Appliquer les patches de sécurité recommandés
- Re-exécuter l'audit après corrections
ACTIONS
[ $(($MAVEN_RESULT + $DOCKER_RESULT + $FS_RESULT)) -eq 0 ] && echo "✅ Aucune action immédiate requise")

## Planification
- Prochain audit programmé: $(date -d '+1 week' '+%Y-%m-%d')
- Surveillance continue via GitHub Security Alerts
- Mise à jour automatique via Dependabot

EOF

echo "✅ Rapport de sécurité généré: security-report.md"

# 5. Notification si vulnérabilités critiques détectées
if [ $(($MAVEN_RESULT + $DOCKER_RESULT + $FS_RESULT)) -gt 0 ]; then
    echo "🚨 ALERTE: Vulnérabilités critiques détectées!"
    # Envoyer notification (Slack, email, etc.)
    exit 1
fi

echo "🎉 Audit de sécurité terminé - Aucune vulnérabilité critique"
```

### Mise à Jour Automatique des Dépendances

#### Dependabot Configuration (.github/dependabot.yml)
```yaml
version: 2
updates:
  # Maven dependencies
  - package-ecosystem: "maven"
    directory: "/SchoolDev"
    schedule:
      interval: "weekly"
    open-pull-requests-limit: 5
    reviewers:
      - "Matias1512"
    commit-message:
      prefix: "chore"
      include: "scope"
    
  # Docker dependencies
  - package-ecosystem: "docker"
    directory: "/SchoolDev"
    schedule:
      interval: "weekly"
    reviewers:
      - "Matias1512"
    commit-message:
      prefix: "chore"
      include: "scope"

  # GitHub Actions
  - package-ecosystem: "github-actions"
    directory: "/"
    schedule:
      interval: "monthly"
    reviewers:
      - "Matias1512"
```

### Pipeline de Mise à Jour Continue

#### GitHub Actions Workflow (.github/workflows/dependency-updates.yml)
```yaml
name: Dependency Updates

on:
  schedule:
    - cron: '0 2 * * 1' # Lundi à 2h du matin
  workflow_dispatch:

jobs:
  check-updates:
    runs-on: ubuntu-latest
    
    steps:
    - name: Checkout
      uses: actions/checkout@v4
    
    - name: Set up Java
      uses: actions/setup-java@v4
      with:
        java-version: '23'
        distribution: 'temurin'
    
    - name: Check for updates
      run: |
        cd SchoolDev
        ./mvnw versions:display-dependency-updates > dependency-updates.log
        ./mvnw versions:display-plugin-updates > plugin-updates.log
    
    - name: Security audit
      run: |
        cd SchoolDev
        ./mvnw org.owasp:dependency-check-maven:check
    
    - name: Create issue if updates available
      uses: actions/github-script@v7
      with:
        script: |
          const fs = require('fs');
          
          // Lire les logs de mise à jour
          const depUpdates = fs.readFileSync('SchoolDev/dependency-updates.log', 'utf8');
          const pluginUpdates = fs.readFileSync('SchoolDev/plugin-updates.log', 'utf8');
          
          // Vérifier s'il y a des mises à jour
          if (depUpdates.includes('The following dependencies') || 
              pluginUpdates.includes('The following plugin updates')) {
            
            // Créer une issue
            await github.rest.issues.create({
              owner: context.repo.owner,
              repo: context.repo.repo,
              title: `🔄 Mises à jour de dépendances disponibles - ${new Date().toISOString().split('T')[0]}`,
              body: `
## Mises à jour disponibles

### Dépendances
\`\`\`
${depUpdates}
\`\`\`

### Plugins
\`\`\`
${pluginUpdates}
\`\`\`

### Actions recommandées
- [ ] Réviser les mises à jour
- [ ] Tester en local
- [ ] Créer une PR de mise à jour
- [ ] Valider avec les tests automatisés

/cc @Matias1512
              `,
              labels: ['dependencies', 'maintenance']
            });
          }
```

---

## Monitoring et Alertes

### Surveillance Post-Mise à Jour

#### Script de Monitoring Continu
```bash
#!/bin/bash
# monitor-post-update.sh

DURATION=${1:-3600}  # 1 heure par défaut
INTERVAL=60          # Check toutes les 60 secondes
API_BASE="https://schooldev.duckdns.org"
LOG_FILE="/var/log/schooldev-monitor.log"

echo "📊 Monitoring post-mise à jour pendant ${DURATION}s..."

START_TIME=$(date +%s)
END_TIME=$((START_TIME + DURATION))

while [ $(date +%s) -lt $END_TIME ]; do
    CURRENT_TIME=$(date '+%Y-%m-%d %H:%M:%S')
    
    # Health check
    if curl -f -s $API_BASE/actuator/health > /dev/null; then
        HEALTH="✅"
    else
        HEALTH="❌"
        echo "$CURRENT_TIME - ❌ ALERTE: Health check échoué!" | tee -a $LOG_FILE
    fi
    
    # Response time
    RESPONSE_TIME=$(curl -o /dev/null -s -w "%{time_total}" $API_BASE/actuator/health)
    
    # Memory usage
    MEMORY_USAGE=$(docker stats --no-stream --format "{{.MemUsage}}" $(docker-compose ps -q spring-app) | head -1)
    
    # Log status
    echo "$CURRENT_TIME - Health: $HEALTH, Response: ${RESPONSE_TIME}s, Memory: $MEMORY_USAGE" | tee -a $LOG_FILE
    
    # Alert if response time > 5s
    if (( $(echo "$RESPONSE_TIME > 5.0" | bc -l) )); then
        echo "$CURRENT_TIME - ⚠️ ALERTE: Temps de réponse élevé (${RESPONSE_TIME}s)!" | tee -a $LOG_FILE
    fi
    
    sleep $INTERVAL
done

echo "📊 Monitoring terminé. Logs disponibles dans $LOG_FILE"
```

### Notifications d'Alertes

#### Webhook de Notification
```bash
#!/bin/bash
# send-alert.sh

ALERT_TYPE=$1
MESSAGE=$2
WEBHOOK_URL=${SLACK_WEBHOOK_URL:-""}

if [ -z "$WEBHOOK_URL" ]; then
    echo "⚠️ Pas de webhook configuré - Alerte locale uniquement"
    echo "ALERTE [$ALERT_TYPE]: $MESSAGE"
    return
fi

# Couleurs selon le type d'alerte
case $ALERT_TYPE in
    "CRITICAL")
        COLOR="#FF0000"
        ICON=":rotating_light:"
        ;;
    "WARNING")
        COLOR="#FFA500"
        ICON=":warning:"
        ;;
    "INFO")
        COLOR="#00FF00"
        ICON=":information_source:"
        ;;
    *)
        COLOR="#808080"
        ICON=":question:"
        ;;
esac

# Payload Slack
PAYLOAD=$(cat << EOF
{
    "attachments": [
        {
            "color": "$COLOR",
            "fields": [
                {
                    "title": "$ICON SchoolDev - $ALERT_TYPE",
                    "value": "$MESSAGE",
                    "short": false
                },
                {
                    "title": "Environnement",
                    "value": "Production (schooldev.duckdns.org)",
                    "short": true
                },
                {
                    "title": "Timestamp",
                    "value": "$(date)",
                    "short": true
                }
            ]
        }
    ]
}
EOF
)

# Envoi de la notification
curl -X POST -H 'Content-type: application/json' \
    --data "$PAYLOAD" \
    $WEBHOOK_URL

echo "📨 Alerte envoyée: [$ALERT_TYPE] $MESSAGE"
```

---

## Checklist de Mise à Jour

### Pré-Mise à Jour
- [ ] Sauvegarde complète effectuée
- [ ] Tests locaux passés (unit + integration)
- [ ] Analyse de sécurité sans vulnérabilités critiques
- [ ] Documentation mise à jour
- [ ] Changelog préparé
- [ ] Équipe notifiée
- [ ] Fenêtre de maintenance planifiée

### Pendant la Mise à Jour
- [ ] Monitoring actif
- [ ] Logs surveillés en temps réel
- [ ] Health checks fonctionnels
- [ ] Performance dans les normes
- [ ] Rollback prêt si nécessaire

### Post-Mise à Jour
- [ ] Validation fonctionnelle OK
- [ ] Tests de régression passés
- [ ] Performance validée
- [ ] Monitoring étendu activé (1h minimum)
- [ ] Documentation mise à jour
- [ ] Équipe notifiée du succès
- [ ] Ancien backup nettoyé (après 7 jours)

---

*Ce manuel de mise à jour est un document vivant, maintenu et amélioré avec chaque mise à jour du système SchoolDev.*