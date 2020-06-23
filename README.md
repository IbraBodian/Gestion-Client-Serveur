# Gestion-Client-Serveur
Application de vente entre particulier par petite annonce à l'aide d'un gestionnaire 

Un utilisateur doit pouvoir poster une annoce (le domaine de l'annonce , le prix et un descriptif) et connaitre les petites annonces postés 
par  les autres utilisateurs. Si il est interresé par une annonce, il doit pouvoir correspondre directement avec celui qui a posté cette annonce.
Un utilisateur doit pouvoir retirer son annonce. Le rôle du gestionnaire est de collecter les annonces, de les diffuser aux clients, de les mettre 
à juour et de donner aux utilisateurs les informations pour correspondre avec les autres utilisateurs.

La classe Gestionnaire est le ciment qui sert à la gestion des annonces, dans cette classe on a un constructeur qui sert à initiliser les courants de communication 
(entrante et sortante) et les listes associés aux annonces ainsi que les clients. Cette classe Gestionnaire qui est une sous classe de la classe thread 
contient une redefinition de la fonction run() qui va nous permettre de traiter les différentes commandes indiquer par l'utilisateur à l'aide d'un
switch case. Chaque case traite une commande spécifique à l'aide d'une méthode unique ( par ex la commande s'inscrire ,ajouter un  article , enlever un article , 
communiquer avec un autre client ect..).
La méthode main(String[] args) permet de lancer le serveur Gestionnaitre sur le port 1027 et d'attendre que des clients se connectent sur ce port 
La classe Gestionnaire manipule deux classe objets : La classe client et la classe Annonce 
La classe client à comme attribut un identifiant, un mot de passe ( qui va servir pour l'inscription et la connection) , une adresse IP
( qui va servir à la communication) , un booléen qui va dire si le client est connecté ou pas et enfin un clé public qui va servir à sécuriser
la communication avec un autre client.
La classe Annonce à comme attribut le domaine( Voiture, ustensile ect ..) , le prix , un descriptif et l'id du client qui à poster cette ladite annonce 
