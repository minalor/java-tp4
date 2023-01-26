
/**
 *
 * L'interface Callback définit les méthodes pour mettre à jour l'interface
 * graphique lorsqu'un coup est joué.
 *
 * @author Camille & Guillaume
 */

import java.rmi.*;

public interface Callback extends Remote {

    /**
     * Mettre à jour l'interface graphique en fonction du bouton cliqué et du joueur
     * qui a joué le coup.
     *
     * @param button Le bouton sur lequel le coup a été joué
     * @param player Le joueur qui a joué le coup
     * @throws RemoteException Si une erreur se produit lors de la communication RMI
     */
    void updateCoup(int button, String player) throws RemoteException;

    /**
     * Cette méthode permet de réinitialiser l'affichage du jeu en enlevant les
     * icônes
     * et en activant les boutons.
     *
     * @throws RemoteException Si une erreur se produit lors de la communication RMI
     */
    void updateReset() throws RemoteException;
}
