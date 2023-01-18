import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GUI {

    static JButton[] buttons = new JButton[9];
    Morpion m;
    JFrame frame;



    ImageIcon cross = new ImageIcon("IMG/cross.png");

    ImageIcon circle = new ImageIcon("IMG/circle.png");

    GUI() {
        m = new Morpion();
        frame = new JFrame();
        Dimension d = new Dimension(400, 400);

        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JButton();
            buttons[i].setSize(d);
            buttons[i].setActionCommand(String.valueOf(i));
            buttons[i].addActionListener(e -> actionPerformed(e));
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
    public void actionPerformed(ActionEvent e) {

        int i = Integer.parseInt(e.getActionCommand());
        if (m.coup(i)) {
            buttons[i].setIcon(m.getAutrePion() == m.getJ1() ? cross : circle);
        }
        if (m.getWinner() != m.getVide() && m.getWinner() != "nul") {
            System.out.println("Partie gagné");
            findePartie("Le joueur " + m.getAutrePion() + " a gagné");
        } 
        else if (m.getWinner() == "nul") {
            findePartie("Il y a match nul, aucun des joueurs n'a gagné.\n");
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

    private void findePartie(String str) {
        int result = JOptionPane.showConfirmDialog(null, str + "\nVoulez-vous recommencer une partie ?", "Que voulez vous faire?", JOptionPane.YES_NO_OPTION);
        if (result == 0) {
            m.recommencer();
            reinitialiserAffichage();
        } 
        else 
            System.exit(0);
    }
    public static void main(String[] args) {
        new GUI();
    }
}
