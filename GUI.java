import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame{
    public GUI(){
        super("Le premier exemple");
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JPanel jp = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jp.setBackground(Color.lightGray);
        jp.add(new JLabel("TextField"));
        JTextField tf = new JTextField(15);
        jp.add(tf);

        this.pack();
        this.setVisible(true) ;
    }

    public static void main(String[] args) {
        
        new GUI();
    }
}