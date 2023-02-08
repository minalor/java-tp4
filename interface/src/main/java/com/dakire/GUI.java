package com.dakire;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.*;

import javafx.application.*;
import javafx.event.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;

public class GUI extends Application implements Callback {
    private Button[] buttons = new Button[9];
    private Label label_Joueur;
    private Label label_Tour;
    Stage stage = new Stage();

    MorpionInterface m;

    static String nomJoueur = "";
    Map<String, String> sons;

    private String adresseIP = "localhost";

    // Pion du joueur qui sera affecté par le serveur ("X" ou "O")
    static String pion;

    // Chargement des deux images de pions
    Image cross = new Image(getClass().getClassLoader().getResource("cross.png").toString());
    Image circle = new Image(getClass().getClassLoader().getResource("circle.png").toString());

    String playerPiece;

    public GUI() throws RemoteException {
        try {
            Registry registry = LocateRegistry.getRegistry(adresseIP, 2023);
            m = (com.dakire.MorpionInterface) registry.lookup("MorpionService");
            m.registerCallback(this);
        } catch (Exception e) {
            System.out.println("Client exception: " + e.toString());
            e.printStackTrace();
        }

        try {
            m.registerCallback(this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        while (nomJoueur.trim().isEmpty()) {
            nomJoueur = javafx.scene.control.TextInputDialog.showTextInput(stage, "Enter player name:","Player name").orElse("");

            if (nomJoueur.trim().isEmpty()) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("You must enter a valid player name.");
                alert.showAndWait();
            }
        }

        stage = new Stage();
        stage.setTitle("Tic Tac Toe");

        label_Joueur = new Label("Player: ");
        label_Tour = new Label("Turn: ");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        for (int i = 0; i < 9; i++) {
            buttons[i] = new Button();
            buttons[i].setMinSize(128, 128);
            buttons[i].setOnAction((EventHandler<ActionEvent>) this);
            grid.add(buttons[i], i % 3, i / 3);
        }

        grid.add(label_Joueur, 0, 3);
        grid.add(label_Tour, 1, 3);

        Scene scene = new Scene(grid, 512, 512);
        stage.setScene(scene);
        stage.show();

        sons = new HashMap<String, String>();
        sons.put("gagné", "win.wav");
        sons.put("perdu", "loose.wav");
        sons.put("X", "cross.wav");
        sons.put("O", "circle.wav");
    }

    /**
     * Méthode pour associer un cercle / croix a un joueur
     *
     * @param pion la chaine de caractères correspondant au joueur
     * @return Retourne l'image correspondant au pion du joueur
     */
    Image getIconPion(String pion) throws RemoteException {
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
    public void actionPerformed(javafx.event.ActionEvent e) {

        int i = 0;
        i = Integer.parseInt(buttons[i].getUserData().toString());

        try {
            if (m.coup(i, pion)) {
                Son.playSound(sons.get(pion));
                // buttons[i].setIcon(getIconPion(pion));
                // buttons[i].setDisabledIcon(getIconPion(pion));
                // buttons[i].setEnabled(false);
                Image image = new Image(getClass().getResourceAsStream(getIconPion(pion).getUrl()));
                ImageView imageView = new ImageView(image);
                buttons[i].setGraphic(imageView);
                buttons[i].setDisable(true);
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
            Image image = new Image(getClass().getResourceAsStream(null));
            ImageView imageView = new ImageView(image);
            buttons[i].setGraphic(imageView);
            buttons[i].setDisable(false);
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
        ButtonType yesButton = new ButtonType("Yes", ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonData.NO);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, str + "\nVoulez-vous recommencer une partie ?", yesButton,
                noButton);
        alert.setTitle("Que voulez-vous faire?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == yesButton) {
            try {
                m.recommencer();
                updateReset();
            } catch (Exception e) {
                System.out.println("Error in RMI communication: " + e.toString());
                e.printStackTrace();
            }
        } else {
            Platform.exit();
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
        // buttons[button].setIcon(player.equals(m.getJ1()) ? cross : circle);
        // buttons[button].setDisabledIcon(player.equals(m.getJ1()) ? cross : circle);
        // buttons[button].setEnabled(false);

        Image image = new Image(getClass().getResourceAsStream(getIconPion(pion).getUrl()));
        ImageView imageView = new ImageView(image);
        buttons[button].setGraphic(imageView);
        buttons[button].setDisable(true);
        majTexte();
    }

    /**
     * Methode pour mettre a jour le texte dans les JLabels pour savoir qui doit
     * jouer
     *
     * @throws RemoteException Si une erreur se produit lors de la communication RMI
     */
    private void majTexte() throws RemoteException {
        label_Tour.setText("Joueur : " + nomJoueur);
        if (m.getPion().equals(pion))
            label_Tour.setText("A votre tour !");
        else
            label_Tour.setText("Au tour de l'autre joueur !");
    }

    private void fenetreAttente() throws RemoteException {

        stage.toBack();
        for (Button button : buttons) {
            button.setDisable(true);
        }

        // Create a new Stage for the waiting window
        Stage stageAttente = new Stage();
        stageAttente.initModality(Modality.APPLICATION_MODAL);
        // Create a label to display the waiting message
        Label messageAttente = new Label("Attente d'un autre joueur...");
        // Create a VBox to hold the label
        VBox vBox = new VBox(messageAttente);
        vBox.setAlignment(Pos.CENTER);
        // Create a Scene with the VBox as its root node
        Scene scene = new Scene(vBox, 250, 100);
        // Set the scene on the stage
        stageAttente.setScene(scene);
        // Center the stage on the screen
        stageAttente.centerOnScreen();
        stageAttente.setAlwaysOnTop(true);
        // Make the stage visible
        stageAttente.show();

        while (!m.statutJeu()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Close the waiting window when the game starts
        stageAttente.close();
        stage.toFront();
        for (Button button : buttons) {
            button.setDisable(false);
        }
    }

    @Override
    public void start(Stage primaryStage) throws RemoteException {
        GUI gui = new GUI();
        pion = gui.m.seConnecter();
        System.out.println("Pion de ce joueur : " + pion);
        gui.m.registerCallback(gui);
        gui.majTexte();
        gui.fenetreAttente();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
