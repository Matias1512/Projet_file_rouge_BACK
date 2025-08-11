# Manuel de Déploiement - SchoolDev API

## Table des Matières
1. [Architecture du Système](#architecture-du-système)
2. [Prérequis et Environnements](#prérequis-et-environnements)
3. [Déploiement Local de Développement](#déploiement-local-de-développement)
4. [Déploiement Production](#déploiement-production)
5. [Gestion des Secrets et Sécurité](#gestion-des-secrets-et-sécurité)
6. [Monitoring et Maintenance](#monitoring-et-maintenance)
7. [Procédures de Déploiement](#procédures-de-déploiement)
8. [Résolution de Problèmes](#résolution-de-problèmes)

---

## Architecture du Système

### Vue d'ensemble
SchoolDev est une application Spring Boot 3.4.2 utilisant Java 23, containerisée avec Docker et déployée avec PostgreSQL comme base de données.

### Justifications des Choix Architecturaux

#### **Architecture en Couches (Layered Architecture)**
- **Choix** : Séparation Controller/Service/Repository/Model
- **Justification** : 
  - Maintenabilité et lisibilité du code
  - Testabilité avec isolation des couches
  - Réutilisabilité des services métier
  - Respect des principes SOLID

#### **API REST avec Spring Boot**
- **Justification** :
  - Standard de l'industrie pour les APIs web
  - Interopérabilité maximale (clients web, mobile, etc.)
  - Documentation automatique via Swagger/OpenAPI
  - Ecosystem Spring riche et mature

### Composants principaux

#### 1. Application Spring Boot
- **Framework** : Spring Boot 3.4.2 avec Spring Security
- **Java Version** : JDK 23 (Eclipse Temurin)
- **Port** : 8080 (interne au container)
- **Authentification** : JWT avec clé secrète externalisée
- **Base Image** : `eclipse-temurin:23-jdk-alpine`

**Justifications technologiques** :
- **Java 23** : Version récente avec optimisations de performance et nouvelles fonctionnalités du langage
- **Spring Boot 3.4.2** : Framework mature avec convention-over-configuration, sécurité intégrée
- **Eclipse Temurin** : Distribution OpenJDK stable et performante
- **Alpine Linux** : Image légère (réduction de la surface d'attaque et des temps de déploiement)

#### 2. Base de Données
- **SGBD** : PostgreSQL (latest)
- **Port** : 5432
- **Base de données** : `schoolDevDatabase`
- **ORM** : Hibernate avec DDL auto-update

**Justifications technologiques** :
- **PostgreSQL** : 
  - SGBD relationnel robuste avec support JSON natif
  - Conformité ACID pour la cohérence des données
  - Performances excellentes avec optimiseur de requêtes avancé
  - Écosystème Docker mature et bien documenté
- **Hibernate/JPA** : Abstraction ORM standard Java, migrations automatiques pour le développement

#### 3. Reverse Proxy (Production uniquement)
- **Reverse Proxy** : Traefik v2.10
- **HTTPS** : Let's Encrypt automatique
- **Domaine** : schooldev.duckdns.org
- **Ports** : 80/443 (HTTP/HTTPS), 8081 (dashboard)

**Justifications technologiques** :
- **Traefik** :
  - Configuration automatique via labels Docker (Infrastructure as Code)
  - HTTPS automatique avec Let's Encrypt (sécurité sans effort manuel)
  - Load balancing natif pour la scalabilité horizontale
  - Dashboard de monitoring intégré
  - Alternative plus moderne à Nginx pour les environnements containerisés

### Architecture réseau
```
Internet → Traefik (80/443) → Spring App (8080)
                            ↓
                         PostgreSQL (5432)
```

---

## Prérequis et Environnements

### Prérequis Système

#### Développement Local
- **Docker** : 20.10+ avec Docker Compose
- **Java** : JDK 23 (optionnel pour développement hors container)
- **Maven** : 3.9+ (optionnel, utilise le wrapper inclus)
- **Git** : Pour le contrôle de version

#### Production
- **Serveur Linux** : Ubuntu 20.04+ ou CentOS 8+
- **Docker** : 20.10+ avec Docker Compose
- **Domaine** : Nom de domaine pointant vers le serveur (ex: schooldev.duckdns.org)
- **Ports ouverts** : 80, 443, 8081 (optional dashboard)

### Variables d'Environnement Obligatoires

#### **Stratégie de Configuration Externalisée**
**Justification** : Conformité aux principes 12-Factor App
- Séparation code/configuration pour la sécurité
- Déploiement multi-environnements simplifié
- Rotation des secrets sans rebuild d'image

#### Pour le Développement Local
```bash
# Base de données (définies dans docker-compose.local.yml)
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/schoolDevDatabase
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=localpassword

# JWT (définie dans docker-compose.local.yml)
JWT_SECRET_KEY=your-local-256-bit-secret-key-for-development-only
JWT_EXPIRATION=3600000

# Profil Spring
SPRING_PROFILES_ACTIVE=local
```

**Choix pour le développement** :
- Credentials simplifiés pour faciliter le setup local
- JWT secret en dur acceptable (environnement non-production)
- Hot reload activé via profil `local`

#### Pour la Production
```bash
# Base de données
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/schoolDevDatabase
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=Saxomat+15

# JWT (via Docker secret)
# JWT_SECRET_KEY sera fourni via le système de secrets Docker

# Variables système
DB_USER=postgres
DB_PASSWORD=Saxomat+15
```

**Choix pour la production** :
- **Docker Secrets** pour JWT : Sécurité renforcée avec gestion native Docker
- Variables d'environnement pour BD : Équilibre simplicité/sécurité
- Séparation claire des environnements via profils Spring

---

## Déploiement Local de Développement

### Configuration

Le déploiement local utilise `docker-compose.local.yml` avec :
- PostgreSQL avec credentials simplifiés
- Application Spring Boot avec hot reload
- Port debug exposé (5005)
- Volumes mappés pour le développement

### Étapes de Déploiement

#### 1. Cloner le Repository
```bash
git clone <repository-url>
cd Projet_file_rouge_BACK/SchoolDev
```

#### 2. Démarrer l'Environnement Local
```bash
# Démarrer tous les services
docker-compose -f docker-compose.local.yml up -d

# Ou démarrer avec rebuild automatique
docker-compose -f docker-compose.local.yml up --build
```

#### 3. Vérifier le Déploiement
```bash
# Vérifier les containers
docker-compose -f docker-compose.local.yml ps

# Vérifier les logs
docker-compose -f docker-compose.local.yml logs -f spring-app

# Test de santé de l'API
curl http://localhost:8080/actuator/health
```

#### 4. Accéder aux Services
- **API REST** : http://localhost:8080
- **Documentation Swagger** : http://localhost:8080/swagger-ui/index.html
- **Base de données** : localhost:5432 (postgres/localpassword)

#### 5. Développement avec Hot Reload
```bash
# Les modifications de code sont automatiquement rechargées
# Pour forcer un rebuild :
docker-compose -f docker-compose.local.yml up --build spring-app
```

### Arrêt de l'Environnement
```bash
# Arrêter les services
docker-compose -f docker-compose.local.yml down

# Arrêter et supprimer les volumes (⚠️ perte de données)
docker-compose -f docker-compose.local.yml down -v
```

---

## Déploiement Production

### Préparation

#### 1. Configuration du Serveur
```bash
# Installer Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh

# Ajouter l'utilisateur au groupe docker
sudo usermod -aG docker $USER

# Installer Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

#### 2. Créer le Réseau Docker
```bash
# Créer le réseau externe pour Traefik
docker network create web
```

#### 3. Configuration DNS
- Pointer votre domaine vers l'IP du serveur
- Exemple : `schooldev.duckdns.org` → `123.456.789.012`

### Déploiement

#### 1. Cloner le Code
```bash
git clone <repository-url>
cd Projet_file_rouge_BACK/SchoolDev
```

#### 2. Créer le Secret JWT
```bash
# Générer une clé JWT sécurisée (256 bits)
openssl rand -base64 32 > jwt_secret.txt

# Créer le secret Docker
docker secret create jwt_secret_key jwt_secret.txt

# Supprimer le fichier temporaire
rm jwt_secret.txt
```

#### 3. Builder l'Image de Production
```bash
# Builder l'application
./mvnw clean package -DskipTests

# Builder l'image Docker
docker build -t schooldev_back .
```

#### 4. Démarrer les Services
```bash
# Démarrer tous les services
docker-compose up -d

# Vérifier le statut
docker-compose ps
```

#### 5. Vérifier le Déploiement
```bash
# Vérifier la santé de l'application
curl -k https://schooldev.duckdns.org/actuator/health

# Vérifier les certificats SSL
curl -I https://schooldev.duckdns.org

# Vérifier les logs
docker-compose logs -f spring-app
```

### Configuration Traefik

Le fichier `docker-compose.yml` configure automatiquement :
- **Certificats SSL** : Let's Encrypt automatique
- **Redirection HTTP → HTTPS**
- **Dashboard Traefik** : http://votre-serveur:8081

#### Labels Traefik pour l'Application
```yaml
labels:
  - "traefik.enable=true"
  - "traefik.http.routers.springboot.rule=Host(`schooldev.duckdns.org`)"
  - "traefik.http.routers.springboot.entrypoints=websecure"
  - "traefik.http.routers.springboot.tls.certresolver=myresolver"
  - "traefik.http.services.springboot.loadbalancer.server.port=8080"
```

---

## Gestion des Secrets et Sécurité

### **Stratégie de Sécurité Adoptée**

#### **Authentification JWT Stateless**
**Justifications** :
- **Scalabilité** : Pas de stockage session côté serveur
- **Microservices-ready** : Token portable entre services
- **Performance** : Validation locale sans appel base de données
- **Standard industrie** : RFC 7519 largement adopté

#### **Chiffrement des Mots de Passe (BCrypt)**
**Justifications** :
- **Sécurité** : Algorithme adaptatif résistant aux attaques par force brute
- **Salt automatique** : Protection contre les rainbow tables
- **Coût configurable** : Équilibre sécurité/performance (coût 12)

#### **Rate Limiting (Bucket4j)**
**Justifications** :
- **Protection DDoS** : 50 requêtes/minute par utilisateur
- **Algorithme Token Bucket** : Lissage du trafic avec bursts autorisés
- **Performance** : Implémentation in-memory efficace

### Secrets Docker (Production)

#### Création du Secret JWT
```bash
# Méthode 1 : Via fichier temporaire
echo "your-super-secure-256-bit-jwt-secret-key-here" | docker secret create jwt_secret_key -

# Méthode 2 : Via génération automatique (recommandée)
openssl rand -base64 32 | docker secret create jwt_secret_key -
```

**Avantages Docker Secrets** :
- Chiffrement au repos et en transit
- Distribution sécurisée aux containers
- Rotation sans interruption de service
- Pas de traces dans les logs ou variables d'environnement

#### Utilisation dans docker-compose.yml
```yaml
secrets:
  jwt_secret_key:
    external: true

services:
  spring-app:
    secrets:
      - jwt_secret_key
```

#### Accès dans l'Application
Le secret est monté dans `/run/secrets/jwt_secret_key` dans le container.

### Variables d'Environnement Sensibles

#### Développement (docker-compose.local.yml)
```yaml
environment:
  JWT_SECRET_KEY: your-local-256-bit-secret-key-for-development-only
  SPRING_DATASOURCE_PASSWORD: localpassword
```

#### Production (docker-compose.yml)
```yaml
environment:
  SPRING_DATASOURCE_PASSWORD: Saxomat+15
  # JWT_SECRET_KEY fourni via Docker secret
```

### Sécurité des Mots de Passe

#### Changement du Mot de Passe PostgreSQL
```bash
# 1. Arrêter les services
docker-compose down

# 2. Modifier docker-compose.yml
# Changer POSTGRES_PASSWORD et SPRING_DATASOURCE_PASSWORD

# 3. Supprimer le volume existant (⚠️ perte de données)
docker volume rm schooldev_postgres_data

# 4. Redémarrer
docker-compose up -d
```

#### Rotation du Secret JWT
```bash
# 1. Créer un nouveau secret
openssl rand -base64 32 | docker secret create jwt_secret_key_v2 -

# 2. Modifier docker-compose.yml pour utiliser jwt_secret_key_v2

# 3. Redéployer
docker-compose up -d spring-app

# 4. Supprimer l'ancien secret
docker secret rm jwt_secret_key
```

---

## Monitoring et Maintenance

### Health Checks

#### Vérification de l'État de l'Application
```bash
# Health check complet
curl https://schooldev.duckdns.org/actuator/health

# Réponse attendue :
# {"status":"UP","components":{"db":{"status":"UP"},"diskSpace":{"status":"UP"}}}
```

#### Informations de l'Application
```bash
# Informations générales
curl https://schooldev.duckdns.org/actuator/info

# Métriques (si activées)
curl https://schooldev.duckdns.org/actuator/metrics
```

### Logs et Troubleshooting

#### Consultation des Logs
```bash
# Logs de l'application Spring Boot
docker-compose logs -f spring-app

# Logs de PostgreSQL
docker-compose logs -f postgres

# Logs de Traefik
docker-compose logs -f traefik

# Logs avec timestamps
docker-compose logs -f -t spring-app
```

#### Logs Structurés
L'application produit des logs au format standard Spring Boot :
```
2024-01-15 10:30:15.123  INFO 1 --- [main] c.l.schoolDev.SchoolDevApplication : Started SchoolDevApplication in 3.456 seconds
```

### Surveillance des Ressources

#### **Choix de Monitoring avec Spring Boot Actuator**
**Justifications** :
- **Intégration native** : Pas de dépendances externes
- **Standards** : Endpoints compatibles Prometheus/Grafana
- **Léger** : Impact minimal sur les performances
- **Extensible** : Custom metrics facilement ajoutables

#### Utilisation des Ressources
```bash
# Statistiques des containers
docker stats

# Espace disque utilisé par Docker
docker system df

# Nettoyage des ressources inutilisées
docker system prune -f
```

#### Métriques Exposées par Actuator
- `/actuator/health` : État global de l'application
- `/actuator/info` : Informations de build et version
- `/actuator/metrics` : Métriques JVM et application
- `/actuator/loggers` : Configuration des logs en temps réel

#### Surveillance de la Base de Données
```bash
# Connexion à PostgreSQL
docker-compose exec postgres psql -U postgres -d schoolDevDatabase

# Taille de la base de données
\l+

# Tables et leurs tailles
\dt+

# Activité des connexions
SELECT * FROM pg_stat_activity;
```

### Sauvegarde de la Base de Données

#### Sauvegarde Manuelle
```bash
# Sauvegarde complète
docker-compose exec postgres pg_dump -U postgres schoolDevDatabase > backup_$(date +%Y%m%d_%H%M%S).sql

# Sauvegarde avec compression
docker-compose exec postgres pg_dump -U postgres schoolDevDatabase | gzip > backup_$(date +%Y%m%d_%H%M%S).sql.gz
```

#### Script de Sauvegarde Automatique
```bash
#!/bin/bash
# backup-db.sh
BACKUP_DIR="/var/backups/schooldev"
DATE=$(date +%Y%m%d_%H%M%S)

mkdir -p $BACKUP_DIR
docker-compose exec postgres pg_dump -U postgres schoolDevDatabase | gzip > $BACKUP_DIR/backup_$DATE.sql.gz

# Conserver seulement les 7 dernières sauvegardes
find $BACKUP_DIR -name "backup_*.sql.gz" -mtime +7 -delete

echo "Backup completed: $BACKUP_DIR/backup_$DATE.sql.gz"
```

#### Restauration
```bash
# Restaurer depuis une sauvegarde
gunzip -c backup_20240115_103000.sql.gz | docker-compose exec -T postgres psql -U postgres schoolDevDatabase
```

---

## Procédures de Déploiement

### Mise à Jour de l'Application

#### 1. Préparation
```bash
# Sauvegarder la base de données
./backup-db.sh

# Tester la nouvelle version localement
git pull
./mvnw clean test
```

#### 2. Build et Déploiement
```bash
# Builder la nouvelle version
./mvnw clean package -DskipTests

# Builder la nouvelle image Docker
docker build -t schooldev_back:new .

# Tagger l'ancienne image comme backup
docker tag matias151/schooldev_back:latest matias151/schooldev_back:backup

# Tagger la nouvelle image
docker tag schooldev_back:new matias151/schooldev_back:latest
```

#### 3. Déploiement avec Zéro Downtime
```bash
# Méthode 1 : Rolling update
docker-compose up -d --scale spring-app=2
sleep 30
docker-compose up -d --scale spring-app=1

# Méthode 2 : Blue-Green deployment
docker-compose -f docker-compose.yml -f docker-compose.green.yml up -d
# Tester la nouvelle version
# Basculer le trafic via Traefik
# Arrêter l'ancienne version
```

#### 4. Vérification Post-Déploiement
```bash
# Vérifier la santé
curl https://schooldev.duckdns.org/actuator/health

# Vérifier les logs
docker-compose logs -f spring-app

# Tests de smoke
curl -f https://schooldev.duckdns.org/api/courses
```

### Rollback d'Urgence

#### Rollback Rapide
```bash
# Revenir à l'image précédente
docker tag matias151/schooldev_back:backup matias151/schooldev_back:latest
docker-compose up -d spring-app

# Vérifier le rollback
curl https://schooldev.duckdns.org/actuator/health
```

#### Rollback avec Restauration de BD (si nécessaire)
```bash
# Arrêter l'application
docker-compose stop spring-app

# Restaurer la base de données
gunzip -c backup_YYYYMMDD_HHMMSS.sql.gz | docker-compose exec -T postgres psql -U postgres schoolDevDatabase

# Redémarrer avec l'ancienne version
docker tag matias151/schooldev_back:backup matias151/schooldev_back:latest
docker-compose up -d
```

### Maintenance Planifiée

#### Préparation
```bash
# Notification aux utilisateurs (si applicable)
# Page de maintenance via Traefik

# Sauvegarde complète
./backup-db.sh
docker save matias151/schooldev_back:latest > schooldev_image_backup.tar
```

#### Maintenance
```bash
# Arrêt des services
docker-compose down

# Maintenance du système (mises à jour, nettoyage, etc.)
docker system prune -f
apt update && apt upgrade

# Redémarrage
docker-compose up -d
```

---

## Résolution de Problèmes

### Problèmes Courants

#### 1. Application ne démarre pas

**Symptômes** : Container spring-app en état "Restarting" ou "Exited"

**Diagnostic** :
```bash
# Vérifier les logs
docker-compose logs spring-app

# Erreurs communes à rechercher :
# - "Connection refused" → Problème base de données
# - "Port already in use" → Conflit de port
# - "JWT secret key" → Problème de configuration JWT
```

**Solutions** :
```bash
# Problème BD : Vérifier que PostgreSQL est démarré
docker-compose logs postgres

# Problème JWT : Vérifier le secret Docker
docker secret ls
docker secret inspect jwt_secret_key

# Problème de port : Vérifier les ports utilisés
netstat -tlnp | grep :8080
```

#### 2. Erreurs de Base de Données

**Symptômes** : Erreurs 500, logs "Connection refused" ou "Authentication failed"

**Diagnostic** :
```bash
# Tester la connexion à PostgreSQL
docker-compose exec postgres psql -U postgres -d schoolDevDatabase

# Vérifier les variables d'environnement
docker-compose exec spring-app env | grep SPRING_DATASOURCE
```

**Solutions** :
```bash
# Recréer la base de données
docker-compose down
docker volume rm schooldev_postgres_data
docker-compose up -d

# Vérifier les credentials dans docker-compose.yml
```

#### 3. Problèmes HTTPS/Certificats

**Symptômes** : Erreurs SSL, certificats expirés ou invalides

**Diagnostic** :
```bash
# Vérifier les certificats
curl -I https://schooldev.duckdns.org

# Vérifier les logs Traefik
docker-compose logs traefik | grep -i cert
```

**Solutions** :
```bash
# Forcer le renouvellement des certificats
docker-compose restart traefik

# Vérifier la configuration DNS
nslookup schooldev.duckdns.org

# Supprimer les certificats corrompus
rm -rf ./letsencrypt
docker-compose restart traefik
```

#### 4. Problèmes de Performance

**Symptômes** : Réponses lentes, timeouts

**Diagnostic** :
```bash
# Vérifier les ressources
docker stats

# Vérifier les connexions BD
docker-compose exec postgres psql -U postgres -d schoolDevDatabase -c "SELECT * FROM pg_stat_activity;"

# Vérifier les logs applicatifs
docker-compose logs spring-app | grep -i "slow\|timeout\|error"
```

**Solutions** :
```bash
# Augmenter les ressources Docker
# Redémarrer les services
docker-compose restart

# Analyser les requêtes lentes en BD
docker-compose exec postgres psql -U postgres -d schoolDevDatabase -c "SELECT query, mean_time FROM pg_stat_statements ORDER BY mean_time DESC LIMIT 10;"
```

### Commandes de Debug Utiles

#### Inspection des Containers
```bash
# État détaillé des containers
docker-compose ps -a

# Inspection d'un container
docker inspect schooldev_spring_boot_1

# Connexion à un container
docker-compose exec spring-app bash
docker-compose exec postgres bash

# Variables d'environnement d'un container
docker-compose exec spring-app env
```

#### Réseaux et Connectivité
```bash
# Lister les réseaux Docker
docker network ls

# Inspecter le réseau
docker network inspect schooldev_web

# Tester la connectivité réseau
docker-compose exec spring-app ping postgres
docker-compose exec spring-app curl http://postgres:5432
```

#### Volumes et Stockage
```bash
# Lister les volumes
docker volume ls

# Inspecter un volume
docker volume inspect schooldev_postgres_data

# Espace utilisé
docker system df
```

### Points de Vérification Post-Problème

#### Checklist de Récupération
- [ ] Services démarrés : `docker-compose ps`
- [ ] Health check OK : `curl https://schooldev.duckdns.org/actuator/health`
- [ ] API accessible : `curl https://schooldev.duckdns.org/api/courses`
- [ ] Base de données accessible : Test de connexion
- [ ] HTTPS fonctionnel : `curl -I https://schooldev.duckdns.org`
- [ ] Logs sans erreurs : `docker-compose logs --tail=50`
- [ ] Ressources normales : `docker stats`

#### Tests de Validation
```bash
# Script de validation post-déploiement
#!/bin/bash
# validate-deployment.sh

echo "🔍 Validation du déploiement SchoolDev..."

# Test 1 : Health check
if curl -f -s https://schooldev.duckdns.org/actuator/health > /dev/null; then
    echo "✅ Health check : OK"
else
    echo "❌ Health check : FAILED"
    exit 1
fi

# Test 2 : API accessible
if curl -f -s https://schooldev.duckdns.org/api/courses > /dev/null; then
    echo "✅ API : OK"
else
    echo "❌ API : FAILED"
    exit 1
fi

# Test 3 : Base de données
if docker-compose exec -T postgres psql -U postgres -d schoolDevDatabase -c "SELECT 1;" > /dev/null 2>&1; then
    echo "✅ Base de données : OK"
else
    echo "❌ Base de données : FAILED"
    exit 1
fi

echo "🎉 Déploiement validé avec succès!"
```

---

## Support et Maintenance

### Contacts et Escalade
- **Développeur Principal** : Matias1512
- **Repository** : GitHub - Projet_file_rouge_BACK
- **Documentation** : CLAUDE.md, README.md

### Ressources Utiles
- **Documentation Spring Boot** : https://docs.spring.io/spring-boot/docs/current/reference/html/
- **Documentation Docker** : https://docs.docker.com/
- **Documentation Traefik** : https://doc.traefik.io/traefik/
- **Documentation PostgreSQL** : https://www.postgresql.org/docs/

### Historique des Versions
Consultez les tags Git pour l'historique des versions et les notes de release.

---

*Ce manuel de déploiement est maintenu et mis à jour avec chaque release du projet SchoolDev.*