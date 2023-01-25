
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
     * @throws RemoteException
     */
    void updateCoup(int button, String player) throws RemoteException;
    void updateReset() throws RemoteException;
}
