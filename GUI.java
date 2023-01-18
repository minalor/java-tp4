import javax.swing.*;
import java.awt.*;


public class GUI extends JFrame{
    public GUI(){
        super("Test JFrame");
        JInternalFrame jif1 = new JInternalFrame("Dessous",true);
        Dimension d = new Dimension(500, 200);
		setMinimumSize(d);
		setSize(d);
        jif1.setVisible(true);
        jif1.setBounds(50,50,150,150);
        JInternalFrame jif2 = new JInternalFrame("Dessus",true);
        jif2.setVisible(true);
        jif2.setBounds(70,70,150,150);
        JLayeredPane jdp = new JLayeredPane();
        jdp.setBackground(new Color(150,80,80));
        jdp.add(jif1);
        jdp.add(jif2);
        jdp.setLayer(jif1,JLayeredPane.DEFAULT_LAYER-10);
        this.getContentPane().add(jdp);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(500,300);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        
        new GUI();


    }

}
