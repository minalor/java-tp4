import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.plaf.synth.SynthScrollBarUI;

import java.rmi.*;

public class GUI {

    static JButton[] buttons = new JButton[9];
    Morpion m;
    JFrame frame;



    ImageIcon cross = new ImageIcon("IMG/cross.png");

    ImageIcon circle = new ImageIcon("IMG/circle.png");

    GUI() throws RemoteException {
        m = new Morpion();
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

    }

    /**
     * @param
     */
    public void actionPerformed(ActionEvent e)  throws RemoteException{

        int i = Integer.parseInt(e.getActionCommand());
        if (m.coup(i)) {
            buttons[i].setIcon(m.getAutrePion() == m.getJ1() ? cross : circle);
        }
        if (m.getWinner() != m.getVide() && m.getWinner() != "nul") {
            System.out.println("Partie gagné");
            finDePartie("Le joueur " + m.getAutrePion() + " a gagné");
        } 
        else if (m.getWinner() == "nul") {
            finDePartie("Il y a match nul, aucun des joueurs n'a gagné.\n");
        }
        // System.out.println(m);
        // System.out.println(m.grille.contains(m.getVide()));
    }

    /**
     * Reinitialise l'affichage de la grille
     */
    private void reinitialiserAffichage() {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setIcon(null);
        }
    }

    private void finDePartie(String str) throws RemoteException{
        int result = JOptionPane.showConfirmDialog(null, str + "\nVoulez-vous recommencer une partie ?", "Que voulez vous faire?", JOptionPane.YES_NO_OPTION);
        if (result == 0) {
            m.recommencer();
            reinitialiserAffichage();
        } 
        else 
            System.exit(0);
    }
    public static void main(String[] args) throws RemoteException{
        new GUI();

        try {
            Interface rev = (Interface) Naming.lookup("rmi://localhost:1099/MorpionCamille");
            // String result = rev.reverseString(args[0]);
            // System.out.println("L'inverse de " + args[0] + " est " + result);
        } catch (Exception e) {
            System.out.println("Erreur d'accès à l'objet distant.");
            System.out.println(e.toString());
        }
    }
}
