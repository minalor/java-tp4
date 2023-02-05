
/**
 * LINUX:
 * javac --module-path $JAVAFX --add-modules javafx.controls *.java
 * java --module-path $JAVAFX --add-modules javafx.controls <nom_fich>
 * WIN:
 * javac --module-path 'C:\Program Files\Java\javafx-sdk-19.0.2.1\lib' --add-modules javafx.controls *.java
 * java --module-path 'C:\Program Files\Java\javafx-sdk-19.0.2.1\lib' --add-modules javafx.controls <nom_fich>
 */

/**

* Classe qui représente l'interface graphique de l'application de jeu de Morpion en utilisant RMI.

* Cette classe implémente l'interface {@link ActionListener} pour gérer les événements de clic sur les boutons de l'interface,

* et l'interface {@link Callback} pour recevoir les mises à jour des coups joués par l'autre joueur.

* Elle utilise une instance de {@link MorpionInterface} pour communiquer avec le serveur RMI et pour accéder aux méthodes de jeu.
* (Coté client)

* @author Camille & Guillaume
*/

import javafx.application.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.*;

import java.awt.event.*;
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

public class GUI extends Application implements Callback{
    private Button[] buttons = new Button[9];
    private Text joueur = new Text("Joueur : ");
    private Text tour = new Text("Au tour du joueur : ");
    Stage stage = new Stage();

    MorpionInterface m;

    static String nomJoueur = "";
    Map<String, String> sons;

    private String adresseIP = "localhost";

    // Pion du joueur qui sera affecté par le serveur ("X" ou "O")
    static String pion;

    // Chargement des deux images de pions
    // ImageIcon cross = new ImageIcon("IMG/cross.png");
    // ImageIcon circle = new ImageIcon("IMG/circle.png");
    Image cross = new Image("IMG/cross.png");
    Image circle = new Image("IMG/circle.png");


    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        GridPane boutons = new GridPane();
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new Button();
            buttons[i].setMinSize(200, 200);
            buttons[i].setUserData(String.valueOf(i));
            buttons[i].setOnAction(e -> actionPerformed(e));
            boutons.add(buttons[i], i % 3, i / 3);
        }
        boutons.setAlignment(Pos.CENTER);
        root.setCenter(boutons);

        VBox leftPane = new VBox(joueur, tour);
        leftPane.setAlignment(Pos.CENTER);
        leftPane.setSpacing(20);
        root.setLeft(leftPane);

        Scene scene = new Scene(root, 1000, 800);
        stage.setScene(scene);
        stage.setTitle("Morpion");
        stage.setResizable(false);
        stage.show();
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
        joueur.setText("Joueur : " + nomJoueur);
        if (m.getPion().equals(pion))
            tour.setText("A votre tour !");
        else
            tour.setText("Au tour de l'autre joueur !");
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

    public static void main(String[] args) throws RemoteException {
        launch(args);
    }
}
