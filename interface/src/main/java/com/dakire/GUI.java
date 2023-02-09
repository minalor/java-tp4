package com.dakire;
// package interface.src.main.java.com.dakire;

/**

 * Classe qui représente l'interface graphique de l'application de jeu de Morpion en utilisant RMI.

 * Cette classe implémente l'interface {@link ActionListener} pour gérer les événements de clic sur les boutons de l'interface,

 * et l'interface {@link Callback} pour recevoir les mises à jour des coups joués par l'autre joueur.

 * Elle utilise une instance de {@link MorpionInterface} pour communiquer avec le serveur RMI et pour accéder aux méthodes de jeu.
 * (Coté client)

 * @author Camille & Guillaume
*/

import com.dakire.MorpionInterface;
import com.dakire.Callback;

import java.awt.event.*;
import java.rmi.*;
import java.rmi.server.*;

import javax.swing.*;

import java.awt.*;
import java.util.*;

public class GUI extends UnicastRemoteObject implements ActionListener, Callback {
    static JButton[] buttons = new JButton[9];
    com.dakire.MorpionInterface m;

    JFrame frame;
    static JLabel joueur, tour;

    static String nomJoueur = "";
    Map<String, String> sons;

    private String adresseIP = "localhost";

    // Pion du joueur qui sera affecté par le serveur ("X" ou "O")
    static String pion;

    // Chargement des deux images de pions
    ImageIcon cross = new ImageIcon(getClass().getClassLoader().getResource("img/cross.png"));
    ImageIcon circle = new ImageIcon(getClass().getClassLoader().getResource("img/circle.png"));

    /**
     * Constructeur de la classe GUI qui initialise l'interface graphique pour le
     * jeu de morpion. Il crée les boutons, les icônes,
     * les labels, et les panneaux nécessaires pour l'interface. Il instancie
     * également l'objet RMI MorpionInterface et enregistre
     * un callback pour recevoir les mises à jour du serveur.
     */
    GUI() throws RemoteException {
        adresseIP = JOptionPane.showInputDialog(null, "Entrez l'adresse IP (vide : localhost) :", "localhost",
                JOptionPane.QUESTION_MESSAGE);
        try {
            m = (com.dakire.MorpionInterface) Naming.lookup("rmi://" + adresseIP + ":2023/MorpionService");
        } catch (Exception e) {
            System.out.println("Client exception: " + e.toString());
            e.printStackTrace();
        }

        try {
            m.registerCallback(this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        cross = new ImageIcon(cross.getImage().getScaledInstance(128, 128, java.awt.Image.SCALE_SMOOTH));
        circle = new ImageIcon(circle.getImage().getScaledInstance(128, 128, java.awt.Image.SCALE_SMOOTH));

        while (nomJoueur.trim().isEmpty()) {
            nomJoueur = JOptionPane.showInputDialog(null, "Entrez le nom du joueur :", "Nom du joueur",
                    JOptionPane.QUESTION_MESSAGE);

            if (nomJoueur == null) {
                System.exit(0);
            } else if (nomJoueur.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vous devez entrer un nom de joueur valide.", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        // Créer une nouvelle fenêtre
        frame = new JFrame("Morpion");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension taillemax = new Dimension(300, 15);
        // Créer un label
        joueur = new JLabel("Joueur : ");
        joueur.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40)); // Ajouter une bordure vide au label
        joueur.setPreferredSize(taillemax);

        // Créer un second label
        tour = new JLabel("Au tour du joueur : ");
        tour.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40)); // Ajouter une bordure vide au label
        tour.setPreferredSize(taillemax);

        // Créer un JPanel avec un GridBagLayout
        JPanel leftPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        leftPanel.add(joueur, c);
        c.gridy = 1;
        leftPanel.add(tour, c);
        frame.add(leftPanel, BorderLayout.WEST);

        // Créer un panneau pour contenir les boutons
        JPanel boutons = new JPanel(new GridLayout(3, 3));
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JButton();
            buttons[i].setSize(400, 400);
            buttons[i].setActionCommand(String.valueOf(i));
            buttons[i].addActionListener(e -> actionPerformed(e));
            boutons.add(buttons[i]);
        }
        boutons.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Ajouter une bordure vide au panneau

        // Ajouter le panneau à la fenêtre
        frame.add(boutons, BorderLayout.CENTER);

        // Emballer et afficher la fenêtre
        frame.pack();
        frame.setSize(1000, 800);
        frame.setResizable(false);
        frame.setVisible(true);

        sons = new HashMap<String, String>();
        sons.put("gagné", "audio/win.wav");
        sons.put("perdu", "audio/loose.wav");
        sons.put("X", "audio/cross.wav");
        sons.put("O", "audio/circle.wav");
    }

    /**
     * Méthode pour associer un cercle / croix a un joueur
     *
     * @param pion la chaine de caractères correspondant au joueur
     * @return Retourne l'image correspondant au pion du joueur
     */
    ImageIcon getIconPion(String pion) throws RemoteException {
        if (pion.equals(m.getJ1()))
            return this.cross;
        else
            return this.circle;
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
            if (m.coup(i, pion)) {
                Son.playSound(sons.get(pion));
                buttons[i].setIcon(getIconPion(pion));
                buttons[i].setDisabledIcon(getIconPion(pion));
                buttons[i].setEnabled(false);
                // Notifier le joueur adverse du coup
                m.notifierCoup(i, pion);
            }

            if (!m.getWinner().equals(" ") && !m.getWinner().equals("nul")) {

                Son.playSound(sons.get("gagné"));
                System.out.println("Partie gagnée");
                findePartie("Le joueur " + m.getAutrePion() + " a gagné");

            } else if (m.getWinner() != null && m.getWinner().equals("nul")) {
                Son.playSound(sons.get("perdu"));
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
    public void updateReset() {
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
                "Que voulez-vous faire?", JOptionPane.YES_NO_OPTION);
        if (result == 0) {
            try {
                m.recommencer();
                updateReset();
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
    public void updateCoup(int button, String player) throws RemoteException {
        buttons[button].setIcon(player.equals(m.getJ1()) ? cross : circle);
        buttons[button].setDisabledIcon(player.equals(m.getJ1()) ? cross : circle);
        buttons[button].setEnabled(false);
        majTexte();
    }

    /**
     * Methode pour mettre a jour le texte dans les JLabels pour savoir qui doit
     * jouer
     *
     * @throws RemoteException Si une erreur se produit lors de la communication RMI
     */
    private void majTexte() throws RemoteException {
        joueur.setText("Joueur : " + nomJoueur);
        if (m.getPion().equals(pion))
            tour.setText("A votre tour !");
        else
            tour.setText("Au tour de l'autre joueur !");
    }

    private void fenetreAttente() throws RemoteException {

        this.frame.setFocusableWindowState(false);

        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setEnabled(false);
        }

        // Create a new JFrame for the waiting window
        JFrame frameAttente = new JFrame("Attente...");
        // Create a label to display the waiting message
        JLabel messageAttente = new JLabel("Attente d'un autre joueur...");
        // Add the label to the frame
        frameAttente.add(messageAttente);
        // Set the size and location of the frame
        frameAttente.setSize(250, 100);
        frameAttente.setLocationRelativeTo(null);
        frameAttente.setAlwaysOnTop(true);
        frameAttente.setDefaultCloseOperation(0);
        // Make the frame visible
        frameAttente.setVisible(true);
        while (!m.statutJeu()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Close the waiting window when the game starts
        frameAttente.dispose();
        this.frame.setFocusableWindowState(true);

        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setEnabled(true);
        }
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
        pion = gui.m.seConnecter();
        System.out.println("Pion de ce joueur : " + pion);
        gui.m.registerCallback(gui);
        gui.majTexte();
        gui.fenetreAttente();
    }

}
