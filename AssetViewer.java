import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * AssetViewer
 * Displays a window with information about a CSP asset
 */
public class AssetViewer implements ActionListener {
    private Asset asset;
    public JFrame frame;

    /**
     * Constructor
     * Creates a JFrame to display the information for a given asset
     * PRE: An asset
     * POST: A JFrame that displays the information of the given asset
     * @param asset
     */
    public AssetViewer(Asset asset){
        // initializing JFrame
        this.asset = asset;
        frame = new JFrame(asset.assetName);
        frame.setSize(1200, 600);

        // formatting name and adding information
        String assetName = asset.assetName;
        if(asset.assetName.length() > 15){
            assetName = assetName.substring(0, 15);
            if(asset.assetName.length() > 16) assetName += "...";
        }
        JLabel label = new JLabel(asset.assetName);
        JButton link = new JButton("<html><a href=\"\">" + asset.filePath + "</a><html>");
        link.addActionListener(this);
        label.setText(assetName);
        ImageIcon img = new ImageIcon(new ImageIcon(asset.thumbnail).getImage());
        label.setIcon(img);
        label.setHorizontalTextPosition(SwingConstants.CENTER);
        label.setVerticalTextPosition(SwingConstants.BOTTOM);

        // adds any subassets to the display
        if(this.asset.hasSub()){
            // creating panels
            JPanel panel = new JPanel();
            int cols = asset.getNumAssets()/3 + 1;
            panel.setLayout(new GridLayout(cols, 3));
            panel.add(label);
            frame.getContentPane().add(BorderLayout.SOUTH, link);

            // getting all subassets
            ArrayList<Asset> subAssets = asset.getAssets();
            for(int i = 0; i < subAssets.size(); i++){
                // formatting subasset information
                Asset grid = subAssets.get(i);
                ImageIcon subImg = new ImageIcon(grid.thumbnail);
                String subLabel = grid.assetName;
                if(grid.assetName.length() > 15)subLabel = subLabel.substring(0, 15);
                if(grid.assetName.length() > 16) subLabel += "...";
                JLabel b = new JLabel();
                b.setText(subLabel);
                b.setIcon(subImg);
                b.setHorizontalTextPosition(SwingConstants.CENTER);
                b.setVerticalTextPosition(SwingConstants.BOTTOM);
                panel.add(b);
            }

            // adding everything to a scrollpane
            JScrollPane scroll = new JScrollPane(panel);
            scroll.getVerticalScrollBar().setUnitIncrement(16);
            frame.add(scroll);
        }
        else{
            frame.getContentPane().add(BorderLayout.NORTH, label);
            frame.getContentPane().add(BorderLayout.SOUTH, link);
        }
        frame.setVisible(true);
    }

    @Override
    /**
     * actionPerformed
     * Displays the location of an asset if the link is clicked
     * PRE: An asset
     * POST: The location of the asset opened
     */
    public void actionPerformed(ActionEvent e){
        try {
            Runtime.getRuntime().exec("explorer.exe /select," + asset.filePath);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }
}
