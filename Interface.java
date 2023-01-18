import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Interface extends Remote {
    String reverseString(String chaine) throws RemoteException;
}
