# Trombinoscope API

## Description
Cette application est une API REST développée en **Spring Boot** permettant la gestion d'un trombinoscope. Elle gère les étudiants, leurs photos et les utilisateurs avec des rôles définis.

## Technologies utilisées
- **Spring Boot** (Spring Security, Spring Data JPA, Spring Web)
- **MySQL** (Base de données)
- **Hibernate** (ORM)
- **JWT** (Authentification sécurisée)
- **Maven** (Gestion des dépendances)
- **Postman** (Tests API)

## Fonctionnalités implémentées

### 1. Gestion des utilisateurs 👥
- **Deux types d’utilisateurs** : `admin`, `normal`
- **Authentification sécurisée** avec Spring Security et JWT
- **CRUD Utilisateurs** (Seul l’admin peut voir/modifier la liste des utilisateurs)
- **Modification de son propre profil** (email, username, mot de passe)
- **Forcer un utilisateur à changer son mot de passe à la première connexion**

### 2. Gestion des étudiants 🎓
- Ajout, modification, suppression et récupération des étudiants
- Filtrage des étudiants par `promotion`, `parcours`, `spécialité`

### 3. Gestion des photos 🖼️
- Upload d’une photo de profil pour chaque étudiant
- Affichage des photos via une URL accessible
- Suppression et mise à jour d’une photo
- Optimisation de la gestion des fichiers (taille limitée, formats acceptés)

### 4. Sécurité 🔒
- **JWT Token** pour sécuriser les endpoints
- **Accès restreint** :
  - `admin` : Accès total
  - `normal` : Accès aux étudiants et aux photos, mais pas aux utilisateurs

## Endpoints disponibles
### 🔑 **Authentification**
| Méthode | Endpoint           | Description |
|---------|-------------------|-------------|
| POST    | `/api/auth/login` | Connexion et obtention du token JWT |
| POST    | `/api/auth/register` | Inscription d'un utilisateur (admin uniquement) |

### 👥 **Utilisateurs**
| Méthode | Endpoint                   | Description |
|---------|---------------------------|-------------|
| GET     | `/api/utilisateurs`        | Liste des utilisateurs (admin uniquement) |
| GET     | `/api/utilisateurs/{id}`   | Récupérer un utilisateur (admin uniquement) |
| PUT     | `/api/utilisateurs/{id}`   | Modifier son profil |
| DELETE  | `/api/utilisateurs/{id}`   | Supprimer un utilisateur (admin uniquement) |

### 🎓 **Étudiants**
| Méthode | Endpoint                  | Description |
|---------|--------------------------|-------------|
| GET     | `/api/etudiants`          | Liste des étudiants |
| GET     | `/api/etudiants/{id}`     | Récupérer un étudiant |
| POST    | `/api/etudiants`          | Ajouter un étudiant |
| PUT     | `/api/etudiants/{id}`     | Modifier un étudiant |
| DELETE  | `/api/etudiants/{id}`     | Supprimer un étudiant |

### 📸 **Photos**
| Méthode | Endpoint                 | Description |
|---------|-------------------------|-------------|
| POST    | `/api/photos/upload`    | Upload d'une photo |
| GET     | `/api/photos/{filename}` | Récupérer une photo |
| DELETE  | `/api/photos/{filename}` | Supprimer une photo |

## Configuration 🔧

1. **Configurer MySQL** dans `application.properties` :
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/trombinoscope
   spring.datasource.username=root
   spring.datasource.password=
   spring.jpa.hibernate.ddl-auto=update
   ```

2. **Lancer l’application** avec Maven :
   ```sh
   mvn spring-boot:run
   ```

3. **Tester avec Postman** en ajoutant le token JWT dans l’en-tête `Authorization`.

## Prochaines améliorations 🚀
- Ajout de rôles plus précis (ex: gestionnaire, modérateur...)
- Implémentation d’un système de récupération de mot de passe
- Ajout d’un stockage Cloud pour les images

---
📌 **Projet en cours d'amélioration !**

