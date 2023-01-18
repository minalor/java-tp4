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

    public static void main(String[] args) {


        Morpion m = new Morpion();
        System.out.println(m);

        m.coup(J2, 5);

        System.out.println(m);

    }

}