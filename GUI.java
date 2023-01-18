import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GUI implements ActionListener{

    static JButton[] buttons = new JButton[9];
    // joueur1 : false -> X, joueur2 : true -> O
    static boolean player = false;

    GUI() {
        JFrame frame = new JFrame();
        Dimension d = new Dimension(400,400);

        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JButton("");
            buttons[i].setSize(d);
            buttons[i].setActionCommand(String.valueOf(i));
            buttons[i].addActionListener(e -> actionPerformed(e));
            frame.add(buttons[i]);
        }

        // définir la disposition en grille de 3 lignes et 3 colonnes
        frame.setLayout(new GridLayout(3, 3));
        frame.setSize(600, 600);
        frame.setResizable(true);
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        switch(e.getActionCommand()){

        }
        int i = Integer.parseInt(e.getActionCommand());
        // System.out.println(i);
        // System.out.println(e.getActionCommand());
        if(player == false){
            buttons[i].setText("X");
        } else buttons[i].setText("O");
        player = !player;
    }

    public static void main(String[] args) {
        new GUI();
    }
}
