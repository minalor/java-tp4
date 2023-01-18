import java.rmi.*;

public class MorpionServeur{

    public static void main(String[] args) {
        
    
        try {
            System.out.println("Serveur : Construction de l'implementation");
            Morpion m = new Morpion();
            System.out.println("Objet Reverse lie dans le RMIregistry");
            Naming.rebind("rmi://localhost:1099/MorpionCamille", m);
            System.out.println("Attente des invocations des clients ...");
        } catch (Exception e) {
            System.out.println("Erreur de liaison de l'objet Morpion");
            System.out.println(e.toString());
        }
    }

}