# java-tp5
TP5 du module POO en Java

Pas besoin de lancer un serveur rmi dans la console avec la commande rmiregistry
Le programme le lance automatiquement sur le port 2023 avec le nom "MorpionService"

Pour compiler:
mvn install clean compile package

Pour executer le serveur:
 java -jar ./serveur/target/serveur-1.0.jar

Pour execcuter un client:
 java -jar ./interface/target/interface-1.0.jar

