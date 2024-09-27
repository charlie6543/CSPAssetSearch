import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner; // Import the Scanner class to read text files
import java.util.ArrayList;

/**
 * cspAssetSearch
 * Displays all CSP assets in a grid, allowing the user to click any assets to view
 * their thumbnail, location, and any subassets.
 */
public class cspAssetSearch{

  private ArrayList<Asset> assets; // list of all assets
  private JFrame f;
  private JPanel panel;
  private JScrollPane scroll;
  
  /**
   * Constructor
   * Creates a JFrame that shows all assets in a grid.
   * PRE: The file that holds all CSP assets, formatted in the standard
   *      way that CSP organizes files
   * POST: A JFrame that shows all assets in a grid, allowing the user to
   *       click on any asset to view more information.
   * @param folder
   */
  public cspAssetSearch(File folder){
    // initializing array to hold assets before compiling
    assets = new ArrayList<Asset>();
    searchFolder(folder, assets); // getting all assets

    // initializing frame and panel
    f = new JFrame("Asset Viewer");
    f.setSize(1200, 800);
    panel = new JPanel();
    int cols = assets.size()/5 + 1;
    panel.setLayout(new GridLayout(cols, 5));

    // adding images
    for(int i = 0; i < assets.size(); i++){ 
      // getting all information from the asset
      Asset grid = assets.get(i);
      ImageIcon img = new ImageIcon(new ImageIcon(grid.thumbnail).getImage().getScaledInstance(150, 150, Image.SCALE_DEFAULT));
      String label = grid.assetName;

      // formatting names
      if(grid.assetName.length() > 15)label = label.substring(0, 15);
      if(grid.assetName.length() > 16) label += "...";

      // creating panel
      JButton b = new JButton(label, img);
      b.setHorizontalTextPosition(SwingConstants.CENTER);
      b.setVerticalTextPosition(SwingConstants.BOTTOM);
      b.addActionListener(
        new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              AssetViewer viewer = new AssetViewer(grid);
            }
        }
      );
      panel.add(b);
    }

    // formatting JFrame and setting visible
    scroll = new JScrollPane(panel);
    scroll.getVerticalScrollBar().setUnitIncrement(16);
    f.add(scroll);
    f.setVisible(true);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  /**
   * searchFolder
   * Finds all CSP assets and their relevant information given a folder
   * PRE: A folder containing properly formatted CSP assets
   *      An ArrayList to add the asset information to
   * POST: All CSP assets inside the given folder compiled into the given ArrayList
   * @param file
   * @param assets
   */
  public static void searchFolder(File file, ArrayList<Asset> assets){
    try{
      String folderName = file.getAbsolutePath();
      if(file.isDirectory()){ // finding all files to search through
        File[] dirs = file.listFiles();
        for(int i = 0; i < dirs.length; i++){
          File newFile = dirs[i];
          searchFolder(newFile, assets); // recursing to get all assets
        }
      }

      // TODO: optimize, do not recurse for each file
      // check for catalog.xml and thumbnail folder
      else if(file.getName().equals("catalog.xml")){ 
        String targetFolder = folderName.replace("\\catalog.xml", "");

        // when catalog found, parse for name
        Scanner scanner = new Scanner(file);
        String nameInfo = "";
        ArrayList<String> subAssets = new ArrayList<String>();
        while(scanner.hasNextLine()){
          String data = scanner.nextLine();
          if(data.contains("<name>")){ // look for keyword for asset name
            // format to get name alone
            String name = data;
            name = name.trim();
            name = name.replace("<name>", "");
            name = name.replace("</name>", "");
            nameInfo += name + " ";
          }
          // subassets listed in catalog
          if(data.contains("<path>") && data.contains("thumbnail/thumbnail.png")){ 
            // format to isolate subasset path
            String subAsset = data;
            subAsset = subAsset.trim();
            subAsset = subAsset.replace("<path>", "");
            subAsset = subAsset.replace("</path>", "");
            subAsset = subAsset.replace("/", "\\");
            if(subAsset.length() <= 23) break; // filtering out all solo thumbnails (no preceeding folder)
            subAssets.add(subAsset);
          }
        }
        // inputting all relevant information
        Asset newAsset = new Asset(targetFolder, nameInfo.substring(0, nameInfo.length() - 1), getThumbnail(targetFolder));
        assets.add(newAsset); // add name to asset list
        scanner.close();

        // if there are subassets, get information
        if(!subAssets.isEmpty()) getSubAsset(subAssets, newAsset);
      }
      
    }
    catch(FileNotFoundException e){
      System.out.println("File not found");
    }
  }

  /**
   * getThumbnail
   * Finds the thumbnail image for an asset
   * PRE: The folder of the asset to get the thumbnail for
   * POST: The filename of the thumbnail returned
   * @param folder
   * @return
   */
  public static String getThumbnail(String folder){
    String thumbnail = folder + "\\thumbnail\\thumbnail.png";
    File file = new File(thumbnail);
    if(file.isFile()) return thumbnail;
    else{
      // if named something else, finding thumbnail folder then returning the contents of folder
      thumbnail = folder + "\\thumbnail";
      File thFolder = new File(thumbnail);
      return thumbnail + "\\" + thFolder.list()[0];
    }
  }

  /*
   * getSubAsset
   * Given a parent asset and an ArrayList of the parent's subassets, adds all subassets
   * to parent.
   * PRE: 1. An String ArrayList of a parent's subassets. The uuid's of the subassets should be
   *         used.
   *      2. A parent Asset. This should be the parent to the ArrayList of subassets.
   * POST: All subassets created and added to the parent Asset's information
   */
  public static void getSubAsset(ArrayList<String> subs, Asset parent){
    // getting contents of catalog to parse for names of subassets
    File catalog = new File(parent.filePath + "\\catalog.xml");
    String content = "";
    try{
      content = Files.readString(catalog.toPath());
    }
    catch(IOException e){
      Asset errorAsset = new Asset(null, "catalog could not be read", null);
      parent.addAsset(errorAsset);
      return;
    }

    // running through subasset list to get information and add to parent
    for(int i = 0; i < subs.size(); i++){
      // isolating uuid of subasset
      String uuid = subs.get(i);
      uuid = uuid.substring(0, uuid.length() - 24);

      // using uuid to find names of corresponding assets
      String name = "";
      try{ // error handling if uuid can't be found
        name = content.substring(content.indexOf("uuid=\"" + uuid));
        name = name.substring(name.indexOf("<name>"), name.indexOf("</name>"));
        name = name.substring(6);
      }
      catch(StringIndexOutOfBoundsException e){
        Asset errorAsset = new Asset(null, "subasset not found", null);
        parent.addAsset(errorAsset);
        continue;
      }

      // creating asset and adding to parent
      Asset subAsset = new Asset(parent.filePath + "\\" + uuid, name, parent.filePath + "\\" + subs.get(i));
      parent.addAsset(subAsset);
    }
  }
}
