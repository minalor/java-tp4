import java.rmi.*;
import java.rmi.server.*;

public class Bouton extends UnicastRemoteObject implements ReverseInterface {

    String J1;
    String J2;
    public Bouton() throws RemoteException {
        super();
        tourJoueur = Math.random() < 0.5;
    }

    public String Player(String Joueur) throws RemoteException {
    }
}
