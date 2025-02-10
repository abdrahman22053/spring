Voici un guide complet pour déployer une application Spring Boot sur un VPS avec un domaine via un repository GitHub, en utilisant Apache2 comme proxy inverse et MySQL pour la base de données.



# 🚀 1. Pré-requis

- Un VPS (Ubuntu 22.04) avec accès root.
- Un nom de domaine configuré (par exemple spring.dobmr.net).
- Java JDK 21 installé.
- Apache2 et Certbot (Let’s Encrypt) pour SSL.
- MySQL pour la gestion des données.
- Git et Maven pour le build de l’application.


# ⚙️ 2. Installation des dépendances

**Mise à jour du système :**
```shel
sudo apt update && sudo apt upgrade -y
```
**Installer Java JDK 21 :**
```shell
sudo apt install openjdk-21-jdk -y
java -version
```

**Installer Apache2, Git, Certbot et Maven :**
```shell
sudo apt install apache2 git certbot python3-certbot-apache maven -y
```


# 🗃️ 3. Configuration de la base de données MySQL

**Se connecter à MySQL :**
```shell
sudo mysql -u root -p
```

**Créer la base de données et l’utilisateur :**
```mysql
CREATE DATABASE spring;
CREATE USER 'spring'@'localhost' IDENTIFIED BY 'password_sécurisé';
GRANT ALL PRIVILEGES ON spring.* TO 'spring'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```


# 📥 4. Clonage du projet depuis GitHub

```shell
cd /var/www
sudo git clone https://github.com/abdrahman22053/spring.git
sudo chown -R $USER:$USER /var/www/spring
```

# 🏗️ 5. Construction de l'application Spring Boot

**Accéder au dossier et construire le projet :**

```shell
cd /var/www/spring
./mvnw clean package
```

**Si ```mvnw``` n'est pas exécutable :**
```shell
chmod +x mvnw
```
**Vérifier le JAR généré :**
```shell
ls target/
```
Par exemple : app-0.0.1-SNAPSHOT.jar


# 🔧 6. Création du service Systemd pour Spring Boot

```shell
sudo nano /etc/systemd/system/spring.service
```

**Ajouter le contenu suivant :**
```shell
[Unit]
Description=Spring Boot Application
After=network.target

[Service]
User=www-data
WorkingDirectory=/var/www/spring
ExecStart=/usr/bin/java -jar /var/www/spring/target/app-0.0.1-SNAPSHOT.jar
SuccessExitStatus=143
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
```

**Démarrer et activer le service :**

```shell
sudo systemctl daemon-reload
sudo systemctl start spring
sudo systemctl enable spring
```

**Vérifier l’état du service :**

```shell
sudo systemctl status spring
```

# 🌐 7. Configuration d’Apache pour le proxy inverse

**Créer le fichier de configuration :**

```shell
sudo nano /etc/apache2/sites-available/spring.dobmr.net.conf
```

**Ajouter le contenu suivant :**

```shell
<VirtualHost *:80>
    ServerName spring.dobmr.net
    ServerAdmin webmaster@dobmr.net

    ProxyPreserveHost On
    ProxyPass / http://localhost:8080/
    ProxyPassReverse / http://localhost:8080/

    ErrorLog ${APACHE_LOG_DIR}/spring_error.log
    CustomLog ${APACHE_LOG_DIR}/spring_access.log combined
</VirtualHost>
```

**Activer le site et les modules nécessaires :**

```shell
sudo a2enmod proxy proxy_http
sudo a2ensite spring.dobmr.net.conf
sudo systemctl reload apache2
```


# 🔒 8. Sécurisation avec SSL (Let’s Encrypt)

**Générer le certificat SSL :**
```shell
sudo certbot --apache -d spring.dobmr.net
```
**Vérifier la configuration SSL :**
```shell
sudo apache2ctl configtest
sudo systemctl reload apache2
```


# 🔄 9. Automatisation de la mise à jour de l’application


**Script de mise à jour :**
```shell
sudo nano /var/www/spring/update-spring-app.sh
```

**Contenu du script :**
```shell
#!/bin/bash

APP_DIR="/var/www/spring"
REPO_URL="https://github.com/abdrahman22053/spring.git"

cd $APP_DIR

echo "Mise à jour du dépôt Git..."
git pull origin main

echo "Construction de l'application..."
./mvnw clean package

echo "Redémarrage de l'application..."
sudo systemctl restart spring

echo "Mise à jour terminée avec succès !"

```

**Rendre le script exécutable :**
```shell
chmod +x /var/www/spring/update-spring-app.sh
```

**Exécuter la mise à jour :**
```shell
/var/www/spring/update-spring-app.sh
```

# ✅ 10. Vérification finale


**Vérifiez les journaux en cas de problème :**
```shell
sudo journalctl -u spring -f
sudo tail -f /var/log/apache2/spring_error.log
```


# 🎯 Points clés :

1. Apache redirige le trafic vers l’application Spring Boot via le proxy inverse.
1. Certbot sécurise la connexion avec un certificat SSL.
1. Systemd gère l’exécution automatique de l’application au démarrage du serveur.
1. Script de mise à jour pour faciliter le déploiement des nouvelles versions.
