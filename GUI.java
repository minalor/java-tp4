import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.plaf.synth.SynthScrollBarUI;

import java.rmi.*;

public class GUI {

    static JButton[] buttons = new JButton[9];
    JFrame frame;
    MorpionInterface m;
    



    ImageIcon cross = new ImageIcon("IMG/cross.png");

    ImageIcon circle = new ImageIcon("IMG/circle.png");

    GUI() throws RemoteException {

        frame = new JFrame();
        Dimension d = new Dimension(400, 400);

        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JButton();
            buttons[i].setSize(d);
            buttons[i].setActionCommand(String.valueOf(i));
            buttons[i].addActionListener(e -> {
                try { 
                    actionPerformed(e);
                } catch (RemoteException err) {
                    System.out.println("Erreur de client vers serveur : " + err.getMessage());
                } 
            });

            frame.add(buttons[i]);
        }

        // définir la disposition en grille de 3 lignes et 3 colonnes
        frame.setLayout(new GridLayout(3, 3));
        frame.setSize(600, 600);
        frame.setResizable(false);
        frame.setVisible(true);

        cross = new ImageIcon( cross.getImage().getScaledInstance( 128, 128,  java.awt.Image.SCALE_SMOOTH ));
        circle = new ImageIcon( circle.getImage().getScaledInstance( 128, 128,  java.awt.Image.SCALE_SMOOTH ));


        try {
            m = (MorpionInterface) Naming.lookup("rmi://localhost:1099/MorpionCamille");
            // String result = rev.reverseString(args[0]);
            // System.out.println("L'inverse de " + args[0] + " est " + result);
        } catch (Exception e) {
            System.out.println("Erreur d'accès à l'objet distant.");
            System.out.println(e.toString());
        }

    }


    public JButton[] getListeBoutons(){
        return buttons;
    }

    public void actionPerformed(ActionEvent e)  throws RemoteException{

        // Récupération du numéro du bouton
        int numeroBouton = Integer.parseInt(e.getActionCommand());

        //Actualisation de l'image sur le bouton, si le placement du pion a marché
        if (m.coup(numeroBouton)) {
            buttons[numeroBouton].setIcon(m.getAutrePion() == m.getJ1() ? cross : circle);
            buttons[numeroBouton].setDisabledIcon(m.getAutrePion() == m.getJ1() ? cross : circle);
            buttons[numeroBouton].setEnabled(false);
        }

        // Affichage d'un message de victoire, puis popup pour recommencer une partie
        if (m.getWinner() == m.getJ1() || m.getWinner() == m.getJ2()) {
            System.out.println("Partie gagnée");
            demanderFin("Le joueur " + m.getAutrePion() + " a gagné");
        } 

        // Affichage d'un message de match nul, puis popup pour recommencer une partie
        else if (m.getWinner() == "nul") {
            demanderFin("Il y a match nul, aucun des joueurs n'a gagné.\n");
        }
        // System.out.println(m);
        // System.out.println(m.grille.contains(m.getVide()));

    }

    /**
     * Demande à l'utilisateur s'il veut recommencer une partie ou non.
     * Pour l'instant, si l'un des deux joueurs souhaite recommencer, la partie se relance
     * @param str Issue de la dernière partie (victoire de J1, match nul... etc.)
     */
    public void demanderFin(String str){

        int result = JOptionPane.showConfirmDialog(null, str + "\nVoulez-vous recommencer une partie ?", "Que voulez vous faire?", JOptionPane.YES_NO_OPTION);
        if (result == 0) {
            try {
                m.recommencer();
            } catch (Exception e) {
                System.out.println("Erreur" + e);
            }
            
            reinitialiserAffichage();
        } 
        else 
            System.exit(0);
    }

    /**
     * Reinitialise l'affichage de la grille
     */
    private void reinitialiserAffichage() {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setIcon(null);
        }
    }


    public static void main(String[] args) throws RemoteException{
        new GUI();

        
    }
}
