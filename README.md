# Projet Gobelins 2ème année
## (11/10/2016 - 23/10/2016)

Projet en binôme.

Réalisation d'une application Android d'échange de messages chiffrés.

# Zebra

## Fonctionnalités

- Création / Connexion de compte via mail et mot de passe
- Envoie de message chiffrés (à 1 seul contact à la fois)
    - Chiffrement symétrique AES pour les messages
    - Chiffrement asymétrique RSA pour les passphrases des messages
- Envoie de messages au format lien (URL)
- Envoie de messages au format l33t
- Envoie d'images (depuis l'appareil photo ou la galerie)
- Disparition des messages lorsque le nombre de personnes en face du téléphone est différent de 1
- Sauvegarde local SQLite

## Vues

- Splash screen
- Connexion
- Inscription
- Liste des messages de l'utilisateur
- Nouvelle conversation
- Message
- Liste des contacts
- Fiche d'un contact
- Mon compte

## Améliorations possibles

- Rendre les vues Connexion et Inscription responsive
- Améliorer les temps de chargement ou les loaders (mieux informer l'utilisateur)
- Possibilité d'ajouter ou supprimer des contacts
- Possibilité d'avoir des conversations à plusieurs (infrastructure en place, manque légères corrections)
