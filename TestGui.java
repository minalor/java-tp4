import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class TestGui extends JFrame {

    private JDesktopPane desktopPane = new JDesktopPane();

    
    /* Construction de l'interface graphique */
    public TestGui() {
        super( "JDesktopPane / JInternalFrame sample" );
        this.setSize(600,400);
        this.setLocationRelativeTo( null );
        this.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        
        // Mise en place du conteneur de sous-fenêtres
        desktopPane.setBackground(Color.gray);
        JPanel contentPane = (JPanel)this.getContentPane();
        contentPane.add(desktopPane, BorderLayout.CENTER);

        // Construction de la barre de menu
        this.createMenuBar();

        // Creation d'une sous-fenêtre
        JInternalFrame firstWindow = new JInternalFrame("Première fenêtre");
        firstWindow.setSize(300,200);
        firstWindow.setVisible(true);
        firstWindow.setResizable(true);
        desktopPane.add( firstWindow );
        
        // Creation d'une autre sous-fenêtre sans les boutons
        JInternalFrame secondWindow = new JInternalFrame("Seconde fenêtre");
        secondWindow.setSize(300,200);
        secondWindow.setVisible(true);
        secondWindow.setResizable(true);
        secondWindow.setClosable( true );
        secondWindow.setIconifiable( true );
        secondWindow.setMaximizable( true );
        secondWindow.setLocation(20,20);
        desktopPane.add( secondWindow );
        
        // Pass the second frame to front
        try {
            secondWindow.moveToFront();
            secondWindow.setSelected( true );
        } catch ( PropertyVetoException exception ) {
            exception.printStackTrace();
        }

        // Autres Décorations
        contentPane.add(new JTree(), BorderLayout.WEST);
        contentPane.add(new JLabel("La barre de status"), BorderLayout.SOUTH);
    }


    /* Methode de construction de la barre de menu */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu mnuFile = new JMenu( "File" );
        JMenuItem mnuNewFile = new JMenuItem( "New File" );
        mnuFile.add(mnuNewFile);
        JMenuItem mnuOpenFile = new JMenuItem( "Open File ..." );
        mnuFile.add(mnuOpenFile);
        JMenuItem mnuSaveFile = new JMenuItem( "Save File ..." );
        mnuFile.add(mnuSaveFile);
        JMenuItem mnuSaveFileAs = new JMenuItem( "Save File As ..." );
        mnuFile.add(mnuSaveFileAs);
        menuBar.add(mnuFile);
        
        JMenu mnuEdit = new JMenu( "Edit" );
        JMenuItem mnuUndo = new JMenuItem( "Undo" );
        mnuEdit.add(mnuUndo);
        JMenuItem mnuRedo = new JMenuItem( "Redo" );
        mnuEdit.add(mnuRedo);
        mnuEdit.addSeparator();
        JMenuItem mnuCopy = new JMenuItem( "Copy" );
        mnuEdit.add(mnuCopy);
        JMenuItem mnuCut = new JMenuItem( "Cut" );
        mnuEdit.add(mnuCut);
        JMenuItem mnuPaste = new JMenuItem( "Paste" );
        mnuEdit.add(mnuPaste);
        menuBar.add(mnuEdit);

        JMenu mnuWindow = new JMenu( "Window" );
        JMenuItem mnuCascade = new JMenuItem( "Cascade" );
        mnuCascade.addActionListener( this::mnuCascadeListener );
        mnuWindow.add(  mnuCascade );
        JMenuItem mnuTileHorizontaly = new JMenuItem( "Tile horizontaly" );
        mnuTileHorizontaly.addActionListener( this::mnuTileHorizontalyListener );
        mnuWindow.add(  mnuTileHorizontaly );
        JMenuItem mnuTileVerticaly = new JMenuItem( "Tile verticaly" );
        mnuTileVerticaly.addActionListener( this::mnuTileVerticalyListener );
        mnuWindow.add(  mnuTileVerticaly );
        JMenuItem mnuIconifyAll = new JMenuItem( "Iconify all" );
        mnuIconifyAll.addActionListener( this::mnuIconifyAll );
        mnuWindow.add(  mnuIconifyAll );
        menuBar.add( mnuWindow );

        JMenu mnuHelp = new JMenu( "Help" );
        menuBar.add( mnuHelp );
        
        this.setJMenuBar( menuBar );
    }

    /* Permet de  cascader toutes les fenêtres internes */
    private void mnuCascadeListener( ActionEvent event ) {
        JInternalFrame internalFrames[] = desktopPane.getAllFrames();
        for (int i = 0; i < internalFrames.length; i++) {
          internalFrames[i].setBounds( i*20, i*20, 300, 200 );
        }
    }
    
    /* Permet d'aligner horizontalement toutes les fenêtres internes */
    private void mnuTileHorizontalyListener( ActionEvent event ) {
        JInternalFrame internalFrames[] = desktopPane.getAllFrames();
        int frameWidth = desktopPane.getWidth() / internalFrames.length;
        for (int i = 0; i < internalFrames.length; i++) {
          internalFrames[i].setBounds( i*frameWidth, 0, frameWidth, desktopPane.getHeight() );
        }
    }

    /* Permet d'aligner verticalement toutes les fenêtres internes */
    private void mnuTileVerticalyListener( ActionEvent event ) {
        JInternalFrame internalFrames[] = desktopPane.getAllFrames();
        int frameHeight = desktopPane.getHeight() / internalFrames.length;
        for (int i = 0; i < internalFrames.length; i++) {
          internalFrames[i].setBounds( 0, i*frameHeight, desktopPane.getWidth(), frameHeight );
        }
    }

    /* Permet d'iconifier toutes les fenêtres internes */
    private void mnuIconifyAll( ActionEvent event ) {
        JInternalFrame internalFrames[] = desktopPane.getAllFrames();
        for (int i = 0; i < internalFrames.length; i++) {
            try {
                internalFrames[i].setIcon( true );
            } catch ( PropertyVetoException exception ) {
                exception.printStackTrace();
            }
        }
    }
    
    
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel( new NimbusLookAndFeel() );
        TestGui frame = new TestGui();
        frame.setVisible(true);
    }
}