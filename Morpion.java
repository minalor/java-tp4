import java.util.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
public class Morpion extends UnicastRemoteObject implements MorpionInterface{

    static String VIDE = " ", J1 = "X", J2 = "O";
    ArrayList<String> grille;
    boolean tourJoueur;

    public Morpion() throws RemoteException{
        this.grille = new ArrayList<String>();
        for(int i = 0; i < 9; i++){
            grille.add(VIDE);
        }


        tourJoueur = Math.random() < 0.5;
    }



    public String getVide() throws RemoteException{
        return VIDE;
    }

    public String getJ1() throws RemoteException{
        return J1;
    }

    public String getJ2() throws RemoteException{
        return J2;
    }


    public String toString() {
        String text = "";
        for(int i = 0; i < grille.size(); i++){
            text += grille.get(i);
            if(i % 3 == 2)
                text += "\n";
        }
        return text;
    }


    /**
     * Réinitialise la grille.
     */
    public void recommencer() throws RemoteException{
       this.grille = new ArrayList<String>();
        for(int i = 0; i < 9; i++){
            grille.add(VIDE);
        }
        tourJoueur = !tourJoueur;
    }



    public void finDePartie(String str) throws RemoteException{

    }

    /**
     * Place un pion sur le plateau
     * @param joueur Numéro du joueur (1 ou 2)
     * @param bouton Numéro du bouton pressé sur la grille de 3x3
     * @return vrai si le coup a été placé
    */
    public boolean coup(int bouton) throws RemoteException{

        System.out.println("Coup du joueur " + this.getPion());

        if(grille.get(bouton) == VIDE){
            this.grille.set(bouton, tourJoueur ? J1 : J2);
            tourJoueur = !tourJoueur;
            System.out.println("Succès du coup");
            return true;
        }

        System.out.println("Echec du coup");
        return false;
    }

    public String getPion(){
        return tourJoueur ? "X" : "O";
    }

    public String getAutrePion(){
        return tourJoueur ? "O" : "X";
    }



    /**
     * @return le numéro du joueur gagnant, et 0 s'il n'y en a pas.
     */
    public String getWinner() throws RemoteException{

        // Vérification des lignes
        for(int i = 0; i < 3; i++){
            if(grille.get(i*3) != VIDE && grille.get(i*3) == grille.get(i*3 + 1) && grille.get(i*3) == grille.get(i*3 + 2))
                return grille.get(i*3);
        }


        // Vérification des colonnes
        for(int i = 0; i < 3; i++){
            if(grille.get(i) != VIDE && grille.get(i) == grille.get(i + 3) && grille.get(i) == grille.get(i + 6))
                return grille.get(i);
        }


        // Vérification des diagonales
        for(int i = 0; i < 2; i++){
            if(grille.get(i*2) != VIDE && grille.get(i*2) == grille.get(4) && grille.get(i*2) == grille.get(8 - 2*i))
                return grille.get(i*2);
        }


        if(grille.contains(VIDE) == false){
            System.out.println("nul");
            return "nul";
        }
        return VIDE;
    }


}




