public class Morpion{

    static int VIDE = 0, J1 = 1, J2 = 2;
    Integer[] grille;

    Morpion(){
        this.grille = new Integer[] {
            VIDE, VIDE, VIDE,
            VIDE, VIDE, VIDE,
            VIDE, VIDE, VIDE,
        };
    }
    
    public String toString() {
        String text = "";
        for(Integer i : this.grille){
            text += i.toString();
        }    
        return text;
    }


    /**
     * Place un pion sur le plateau
     * @param joueur Numéro du joueur (1 ou 2)
     * @param bouton Numéro du bouton pressé sur la grille de 3x3
    */
    public void coup(int joueur, int bouton){
        this.grille[bouton] = joueur;
    }



    /**
     * @return le numéro du joueur gagnant, et 0 s'il n'y en a pas.
     */
    Integer getWinner(){

        // Vérification des lignes
        for(int i = 0; i < 3; i++){
            if(grille[i*3] != 0 && grille[i*3] == grille[i*3 + 1] && grille[i*3] == grille[i*3 + 2])
                return grille[i*3];
        }


        // Vérification des colonnes
        for(int i = 0; i < 3; i++){
            if(grille[i] != 0 &&grille[i] == grille[i + 3] && grille[i] == grille[i + 6])
                return grille[i];
        }


        // Vérification des diagonales
        for(int i = 0; i < 2; i++){
            if(grille[i*2] != 0 && grille[i*2] == grille[4] && grille[i] == grille[8 - 2*i])
                return grille[i];
        }


        return 0;
    }





    public static void main(String[] args) {


        Morpion m = new Morpion();
        System.out.println(m);

        m.coup(J2, 5);
        m.coup(J2, 2);
        m.coup(J2, 8);

        System.out.println(m);
        System.out.println(m.getWinner());

    }




}