
/**

 * Classe qui représente l'interface graphique de l'application de jeu de Morpion en utilisant RMI.

 * Cette classe implémente l'interface {@link ActionListener} pour gérer les événements de clic sur les boutons de l'interface,

 * et l'interface {@link Callback} pour recevoir les mises à jour des coups joués par l'autre joueur.

 * Elle utilise une instance de {@link MorpionInterface} pour communiquer avec le serveur RMI et pour accéder aux méthodes de jeu.
 * (Coté client)

 * @author Camille & Guillaume
*/

import java.awt.event.*;
import java.rmi.*;
import javax.swing.*;
import java.awt.*;

public class GUI implements ActionListener, Callback {
    static JButton[] buttons = new JButton[9];
    MorpionInterface m;
    JFrame frame;
    JLabel CourantJ;

    ImageIcon cross = new ImageIcon("IMG/cross.png");
    ImageIcon circle = new ImageIcon("IMG/circle.png");

    /**
     * Constructeur de la classe GUI qui initialise l'interface graphique pour le
     * jeu de morpion. Il crée les boutons, les icônes,
     * les labels, et les panneaux nécessaires pour l'interface. Il instancie
     * également l'objet RMI MorpionInterface et enregistre
     * un callback pour recevoir les mises à jour du serveur.
     */
    GUI() {
        try {
            m = (MorpionInterface) Naming.lookup("rmi://localhost/MorpionService");
        } catch (Exception e) {
            System.out.println("Client exception: " + e.toString());
            e.printStackTrace();
        }

        try {
            m.registerCallback(this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        //
        JTextField NomJ1 = new JTextField();
        JTextField NomJ2 = new JTextField();
        JPanel namePanel = new JPanel(new GridLayout(2, 2));
        namePanel.add(new JLabel("Player 1 name: "));
        namePanel.add(NomJ1);
        namePanel.add(new JLabel("Player 2 name: "));
        namePanel.add(NomJ2);
        int result = JOptionPane.showConfirmDialog(null, namePanel, "Enter player names", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                m.setNomJoueur(NomJ1.getText(), NomJ2.getText());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        CourantJ = new JLabel("Tour du J1");

        frame = new JFrame();
        Dimension d = new Dimension(400, 400);

        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JButton();
            buttons[i].setSize(d);
            buttons[i].setActionCommand(String.valueOf(i));
            buttons[i].addActionListener(e -> actionPerformed(e));
            frame.add(buttons[i]);
        }

        // frame.add(CourantJ);
        // définir la disposition en grille de 3 lignes et 3 colonnes
        frame.setLayout(new GridLayout(3, 3));
        frame.setSize(600, 600);
        frame.setResizable(false);
        frame.setVisible(true);

        cross = new ImageIcon(cross.getImage().getScaledInstance(128, 128, java.awt.Image.SCALE_SMOOTH));
        circle = new ImageIcon(circle.getImage().getScaledInstance(128, 128, java.awt.Image.SCALE_SMOOTH));

    }

    /**
     * Méthode pour traiter les événements de bouton de la grille de jeu. Il appelle
     * la méthode de coup du serveur RMI, met à jour l'interface graphique, et
     * notifie les observateurs enregistrés d'une mise à jour.
     *
     * @param e L'événement de bouton qui a été déclenché
     */
    public void actionPerformed(ActionEvent e) {

        int i = Integer.parseInt(e.getActionCommand());
        try {
            if (m.coup(i)) {
                buttons[i].setIcon(m.getAutrePion().equals(m.getJ1()) ? cross : circle);
                buttons[i].setDisabledIcon(m.getAutrePion().equals(m.getJ1()) ? cross : circle);
                buttons[i].setEnabled(false);
                CourantJ.setText("Tour" + m.getPion());
                // Notifier le joueur adverse du coup
                m.notifierCoup(i);
            }
            if (m.getWinner() != null && !m.getWinner().equals("nul")) {
                System.out.println("Partie gagné");
                findePartie("Le joueur " + m.getAutrePion() + " a gagné");
            } else if (m.getWinner() != null && m.getWinner().equals("nul")) {
                findePartie("Il y a match nul, aucun des joueurs n'a gagné.\n");
            }
        } catch (RemoteException ex) {
            System.out.println("Error in RMI communication: " + ex.toString());
            ex.printStackTrace();
        }
    }

    /**
     * Cette méthode permet de réinitialiser l'affichage du jeu en enlevant les
     * icônes
     * et en activant les boutons.
     */
    private void reinitialiserAffichage() {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setIcon(null);
            buttons[i].setEnabled(true);
        }
    }

    /**
     * Cette méthode affiche une boîte de dialogue pour indiquer qui a gagné la
     * partie et demande au joueur s'il veut recommencer une nouvelle partie.
     * Si le joueur choisit de recommencer une nouvelle partie, la méthode
     * {@link MorpionInterface#recommencer()} est appelée pour réinitialiser la
     * grille de jeu et l'interface graphique.
     * Sinon, le programme se termine.
     *
     * @param str Chaîne de caractères indiquant qui a gagné la partie
     */
    private void findePartie(String str) {
        int result = JOptionPane.showConfirmDialog(null, str + "\nVoulez-vous recommencer une partie ?",
                "Que voulez vous faire?", JOptionPane.YES_NO_OPTION);
        if (result == 0) {
            try {
                m.recommencer();
                reinitialiserAffichage();
            } catch (Exception e) {
                System.out.println("Error in RMI communication: " + e.toString());
                e.printStackTrace();
            }
        } else {
            System.exit(0);
        }
    }

    /**
     *
     * La méthode update permet de mettre à jour l'affichage du bouton sur
     * l'interface graphique en fonction du joueur qui a joué le coup. Elle est
     * appelée lorsque le serveur notifie les clients d'un coup joué.
     *
     * @param button l'indice du bouton sur lequel le coup a été joué
     *
     * @param player le joueur qui a joué le coup (J1 ou J2)
     *
     * @throws RemoteException Si une erreur se produit lors de la communication RMI
     */
    public void update(int button, String player) throws RemoteException {
        buttons[button].setIcon(player.equals(m.getJ1()) ? cross : circle);
        buttons[button].setDisabledIcon(player.equals(m.getJ1()) ? cross : circle);
        buttons[button].setEnabled(false);
    }

    /**
     *
     * La méthode main permet de lancer l'interface graphique du client.
     * Elle crée une instance de GUI et enregistre l'objet comme un callback auprès
     * du serveur.
     *
     * @param args Arguments passés au programme
     * @throws RemoteException Si une erreur se produit lors de la communication RMI
     */
    public static void main(String[] args) throws RemoteException {
        GUI gui = new GUI();
        gui.m.registerCallback(gui);
    }

}
