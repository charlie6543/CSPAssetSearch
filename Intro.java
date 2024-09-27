import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileSystemView;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.io.File;  // Import the File class

/**
 * Intro
 * Opens an introductory JFrame that prompts the user to pick the folder containing their
 * CSP assets.
 */
public class Intro implements ActionListener{
    private JFrame f;
    private JPanel p;

    /**
     * Constructor
     * Creates the JFrame and sets everything up
     * PRE: None
     * POST: Displays a JFrame that prompts the user to pick the folder containing their
     * CSP assets.
     */
    Intro(){
        // setting up introductory jframe
        f = new JFrame("Asset Viewer");
        f.setSize(800, 400);
        p = new JPanel();
        JLabel introLabel = new JLabel
        ("<html>Please input the folder that holds your CSP assets.<br>Usually this is under " +
        "'C:\\Users\\[your name]\\AppData\\Roaming\\CELSYSUserData\\CELSYS\\CLIPStudioCommon\\Material<br>" +
        "You can find the location in the Clip Studio App under Settings -> Location of Materials<br>" +
        "After picking, please allow some time for the program to compile the assets.</html>");
    
        // file choosing
        JButton open = new JButton("open");
        open.addActionListener(this);
    
        // adding everything to panel/frame
        p.add(introLabel);
        p.add(open);
        f.add(p);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    /**
     * main
     * Calls the constructor to set up the JFrame.
     * @param args None
     */
    public static void main(String[] args) {
      Intro test = new Intro();
    }
    
  /**
   * actionPerformed
   * Allows the user to pick a directory from their device
   * PRE: Directory must be the folder where CSP assets are stored
   * POST: AssetViewer program will run to display all CSP assets on device
   */
  public void actionPerformed(ActionEvent e){
    String com = e.getActionCommand();
    JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    int r = fileChooser.showOpenDialog(null);
    if(r == JFileChooser.APPROVE_OPTION){
      File folder = fileChooser.getSelectedFile();
      cspAssetSearch search = new cspAssetSearch(folder);
      f.setVisible(false);
    }
  }
}