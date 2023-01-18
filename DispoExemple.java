import java.awt.*;
import javax.swing.*;

public class DispoExemple {
    DispoExemple() {
        JFrame frame = new JFrame();
        

        JButton btn1 = new JButton("");
        JButton btn2 = new JButton("");
        JButton btn3 = new JButton("");
        JButton btn4 = new JButton("");
        JButton btn5 = new JButton("");
        JButton btn6 = new JButton("");
        JButton btn7 = new JButton("");
        JButton btn8 = new JButton("");
        JButton btn9 = new JButton("");

        frame.add(btn1);
        frame.add(btn2);
        frame.add(btn3);
        frame.add(btn4);
        frame.add(btn5);
        frame.add(btn6);
        frame.add(btn7);
        frame.add(btn8);
        frame.add(btn9);

        // définir la disposition en grille de 3 lignes et 3 colonnes
        frame.setLayout(new GridLayout(3, 3));

        frame.setSize(300, 300);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new DispoExemple();
    }
}