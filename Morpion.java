
/**

 * Classe implémentant l'interface MorpionInterface pour jouer à un jeu de morpion en mode distribué avec RMI.

 * Il gère les différentes fonctionnalités liées au jeu de morpion.
 * (Coté serveur)
 * @author Camille & Guillaume
*/

import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

public class Morpion extends UnicastRemoteObject implements MorpionInterface {
    /**
     *
     * Représente le pion du joueur 1
     */
    private static String J1 = "X";
    /**
     *
     * Représente le pion du joueur 2
     */
    private static String J2 = "O";
    /**
     *
     * Représente un emplacement vide sur la grille
     */
    private static final String VIDE = " ";


    /**
     * Est initialisé à true, et passe à false si il y a déjà eu une première connexion
     */
    private boolean firstConnection = true;


    /**
     * Est initialisée à false, et sera true quand les deux clients seront connectés.
     * Tant que gameStarted est false, personne ne peux jouer.
     */
    private boolean gameStarted = false;

    /**
     *
     * La grille de jeu de Morpion
     */
    private ArrayList<String> grille;
    /**
     *
     * String qui indique quel pion doit être placé
     */
    private String tourJoueur;
    /**
     *
     * Liste des callbacks (observateurs) enregistrés pour être notifiés des mises à
     * jour de la grille de jeu
     */
    private ArrayList<Callback> callbacks;

    /**
     *
     * Constructeur de la classe Morpion qui initialise les différents éléments
     * nécessaires au jeu de morpion.
     *
     * @throws RemoteException en cas d'erreur de communication distante
     */
    Morpion() throws RemoteException {
        this.grille = new ArrayList<String>();
        for (int i = 0; i < 9; i++) {
            grille.add(VIDE);
        }
        this.callbacks = new ArrayList<Callback>();
        tourJoueur = Math.random() < 0.5 ? J1 : J2;
        System.out.println("Joueur qui commence : " + tourJoueur);
    }

    /**
     *
     * Méthode pour définir les noms des joueurs.
     *
     * @param player1 le nom du joueur 1
     * @param player2 le nom du joueur 2
     * @throws RemoteException en cas d'erreur de communication distante
     */
    // public void setNomJoueur(String player) throws RemoteException {
    //     if(J1 == null)
    //         J1 = player;
    //     else J2 = player;
    // }

    /**
     *
     * Méthode pour récupérer la valeur de la case vide.
     *
     * @return la valeur de la case vide
     * @throws RemoteException en cas d'erreur de communication distante
     */
    public String getVide() throws RemoteException {
        return VIDE;
    }

    /**
     *
     * Méthode pour récupérer le symbole du joueur 1.
     *
     * @return le symbole du joueur 1
     * @throws RemoteException en cas d'erreur de communication distante
     */
    public String getJ1() throws RemoteException {
        return J1;
    }

    /**
     *
     * Méthode pour récupérer le symbole du joueur 2.
     *
     * @return le symbole du joueur 2
     * @throws RemoteException en cas d'erreur de communication distante
     */
    public String getJ2() throws RemoteException {
        return J2;
    }

    /**
     *
     * Affiche le plateau de jeu en texte.
     * @return une chaine de caractères contenant le plateau de jeu
     */

    public String toString() {
        String text = "";
        for (int i = 0; i < grille.size(); i++) {
            text += grille.get(i);
            if (i % 3 == 2)
                text += "\n";
        }
        return text;
    }

    /**
     *
     * Méthode pour recommencer une partie de morpion.
     *
     * @throws RemoteException en cas d'erreur de communication distante
     */
    public void recommencer() throws RemoteException {


        for (int i = 0; i < grille.size(); i++) {
            grille.set(i, VIDE);
        }

        // Un joueur aléatoire commence
        tourJoueur = Math.random() < 0.5 ? J1 : J2;

        for (Callback c : callbacks)
            c.updateReset();

    }


    /**
     * Change le pion du joueur courant de J1 à J2 et de J2 à J1.
     */
    private void pionSuivant(){
        if(tourJoueur == J1)
            tourJoueur = J2;
        else
            tourJoueur = J1;
    }

    /**
     *
     * Méthode pour jouer un coup sur la grille de jeu.
     *
     * @param bouton L'emplacement sur la grille où le joueur veut jouer son coup
     * @param pionJoueur Le pion du joueur qui demande à placer un pion
     * @return true si le coup a réussi, false sinon
     * @throws RemoteException Si une erreur se produit lors de la communication RMI
     */
    public boolean coup(int bouton, String pionJoueur) throws RemoteException {

        System.out.println("Coup / Pion du joueur : " + pionJoueur + "/ au tour de : " + tourJoueur);

        if (tourJoueur.equals(pionJoueur) && gameStarted == true) {
            this.grille.set(bouton, tourJoueur);
            pionSuivant();
            System.out.println("Succès du coup");
            notifierCoup(bouton, pionJoueur);
            return true;
        }

        System.out.println("Echec du coup");
        return false;
    }

    /**
     *
     * Méthode pour obtenir le pion du joueur actuel.
     *
     * @return Le pion du joueur actuel (J1 ou J2)
     * @throws RemoteException Si une erreur se produit lors de la communication RMI
     */
    public String getPion() throws RemoteException {
        return tourJoueur;
    }

    /**
     *
     * Méthode pour obtenir le pion du joueur qui ne joue pas actuellement.
     *
     * @return Le pion du l'autre joueur (J1 ou J2)
     * @throws RemoteException Si une erreur se produit lors de la communication RMI
     */
    public String getAutrePion() throws RemoteException {
        return tourJoueur == J1 ? J2 : J1;
    }

    /**
     * Méthode pour obtenir le joueur gagnant de la partie.
     *
     * @return Le pion du joueur gagnant, "nul" si la partie est finie sans gagnant,
     *         et VIDE s'il n'y a pas encore de gagnant
     * @throws RemoteException Si une erreur se produit lors de la communication RMI
     */
    public String getWinner() throws RemoteException {

        // Vérification des lignes
        for (int i = 0; i < 3; i++) {
            if (grille.get(i * 3) != VIDE && grille.get(i * 3) == grille.get(i * 3 + 1)
                    && grille.get(i * 3) == grille.get(i * 3 + 2))
                return grille.get(i * 3);
        }

        // Vérification des colonnes
        for (int i = 0; i < 3; i++) {
            if (grille.get(i) != VIDE && grille.get(i) == grille.get(i + 3) && grille.get(i) == grille.get(i + 6))
                return grille.get(i);
        }

        // Vérification des diagonales
        for (int i = 0; i < 2; i++) {
            if (grille.get(i * 2) != VIDE && grille.get(i * 2) == grille.get(4)
                    && grille.get(i * 2) == grille.get(8 - 2 * i))
                return grille.get(i * 2);
        }

        if (grille.contains(VIDE) == false) {
            System.out.println("nul");
            return "nul";
        }
        return VIDE;
    }

    /**
     * Méthode pour enregistrer un callback (observateur) pour être notifié des
     * mises à jour de la grille de jeu.
     *
     * @param callback L'objet callback à enregistrer
     * @throws RemoteException Si une erreur se produit lors de la communication RMI
     */
    @Override
    public void registerCallback(Callback callback) throws RemoteException {
        callbacks.add(callback);

    }

    /**
     * Méthode pour désinscrire un callback (observateur) de la liste des callbacks
     * enregistrés pour ne plus recevoir de notifications de mises à jour de la
     * grille de jeu.
     *
     * @param callback L'objet callback à désinscrire
     * @throws RemoteException Si une erreur se produit lors de la communication RMI
     */
    @Override
    public void unregisterCallback(Callback callback) throws RemoteException {
        callbacks.remove(callback);

    }

    /**
     * Méthode pour notifier tous les callbacks (observateurs) enregistrés d'une
     * mise à jour de la grille de jeu.
     *
     * @param button L'emplacement sur la grille qui a été mis à jour
     * @throws RemoteException Si une erreur se produit lors de la communication RMI
     */
    @Override
    public void notifierCoup(int button, String pion) throws RemoteException {
        for (Callback callback : callbacks) {
            try {
                callback.updateCoup(button, pion);

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Attribue à chaque client son pion (X ou O).
     * Quand les deux joueurs sont connectés, la partie commence.
     * @return Le pion attribué au joueur qui se connecte.
     */
    public String seConnecter(){
        if(firstConnection){
            firstConnection = false;
            return tourJoueur;
        }
        else{
            // On lance la partie
            gameStarted = true;
            return tourJoueur == J1 ? J2 : J1;
        }
    }

    @Override
    public boolean statutJeu() throws RemoteException{
        return gameStarted;
    }

    /**
     * Méthode principale pour lancer le serveur RMI de Morpion.
     *
     * @param args Arguments passés au programme
     */
    public static void main(String[] args) {
        try {
            Morpion morpion = new Morpion();
            Naming.rebind("rmi://localhost:1099/MorpionService", morpion);
            System.out.println("Morpion RMI server is ready.");
        } catch (Exception e) {
            System.out.println("Error in RMI communication: " + e.toString());
            e.printStackTrace();
        }
    }

}
