import java.rmi.*;

public class Serveur {
    public static void main(String[] args) {
        try {
            System.out.println("Serveur : Construction de l'implementation");
            Reverse rev = new Reverse();
            System.out.println("Objet Reverse lie dans le RMIregistry");
            Naming.rebind("rmi://localhost:1099/MyReverse", rev);
            System.out.println("Attente des invocations des clients ...");
        } catch (Exception e) {
            System.out.println("Erreur de liaison de l'objet Reverse");
            System.out.println(e.toString());
        }
    } // fin du main
} // fin de la classe
