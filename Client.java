import java.rmi.*;

public class Client {
    public static void main(String[] args) {
        try {
            Interface rev = (Interface) Naming.lookup("rmi://localhost:1099/MyReverse");
            //peut etre pas la
            new GUI();

            // String result = rev.reverseString(args[0]);
            // System.out.println("L'inverse de " + args[0] + " est " + result);
        } catch (Exception e) {
            System.out.println("Erreur d'accès à l'objet distant.");
            System.out.println(e.toString());
        }
    }
}
