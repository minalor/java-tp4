import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MorpionInterface extends Remote {
    public void recommencer() throws RemoteException;
    public boolean coup(int bouton) throws RemoteException;
    public String getWinner() throws RemoteException;
    public String getPion() throws RemoteException;
    public String getAutrePion() throws RemoteException;
    public String getVide() throws RemoteException;
    public String getJ1() throws RemoteException;
    public String getJ2() throws RemoteException;
    public void finDePartie(String str) throws RemoteException;
    
}
