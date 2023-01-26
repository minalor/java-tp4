
/**
 *
 * Interface qui définit les méthodes à distance pour la classe Morpion
 *
 * @author Camille & Guillaume
 * @version (une version ou la date)
 */

import java.rmi.*;

public interface MorpionInterface extends Remote {
    /**
     * Permet de jouer un coup
     *
     * @param bouton le numéro du bouton où jouer le coup
     * @return vrai si le coup est valide, faux sinon
     * @throws RemoteException
     */
    boolean coup(int bouton, String pionJoueur) throws RemoteException;

    /**
     * Récupère le pion du joueur actuel
     *
     * @return le pion du joueur actuel
     * @throws RemoteException
     */
    String getPion() throws RemoteException;

    /**
     * Récupère le pion de l'autre joueur
     *
     * @return le pion de l'autre joueur
     * @throws RemoteException
     */
    String getAutrePion() throws RemoteException;

    /**
     * Récupère le numéro du joueur gagnant, 0 s'il n'y en a pas
     *
     * @return le numéro du joueur gagnant
     * @throws RemoteException
     */
    String getWinner() throws RemoteException;

    /**
     *
     * Méthode pour recommencer une partie.
     *
     * @throws RemoteException en cas d'erreur de communication distante
     */
    void recommencer() throws RemoteException;

    /**
     *
     * Méthode pour récupérer le nom du joueur 1.
     *
     * @return le nom du joueur 1
     * @throws RemoteException en cas d'erreur de communication distante
     */
    String getJ1() throws RemoteException;

    /**
     *
     * Méthode pour enregistrer un objet Callback pour recevoir des notifications de
     * changements de jeu.
     *
     * @param callback l'objet Callback à enregistrer
     * @throws RemoteException en cas d'erreur de communication distante
     */
    void registerCallback(Callback callback) throws RemoteException;

    /**
     *
     * Méthode pour supprimer l'enregistrement d'un objet Callback.
     *
     * @param callback l'objet Callback à supprimer
     * @throws RemoteException en cas d'erreur de communication distante
     */
    void unregisterCallback(Callback callback) throws RemoteException;

    /**
     *
     * Méthode pour notifier les objets Callback d'un changement de jeu.
     *
     * @param button l'index de la case qui a été jouée
     * @throws RemoteException en cas d'erreur de communication distante
     */
    void notifierCoup(int button, String pion) throws RemoteException;

    /**
     * Attribue à chaque client son pion (X ou O).
     * Quand les deux joueurs sont connectés, la partie commence.
     *
     * @return Le pion attribué au joueur qui se connecte.
     */
    String seConnecter() throws RemoteException;

    /**
     * Methode permettant de renvoyer l'etat du jeu pour savoir s'il a commencé ou
     * non
     *
     * @return le boolean gameStarted
     *
     * @throws RemoteException Si une erreur se produit lors de la communication RMI
     */
    boolean statutJeu() throws RemoteException;

}
