import java.util.ArrayList;

/**
 * Asset
 * A representation of a CSP asset
 */
public class Asset {
    public final String filePath; // the filepath where the asset is stored
    public final String assetName; // the name of the asset
    public final String thumbnail; // the file where the thumbnail is stored
    private ArrayList<Asset> subAssets; // the list of subassets

    /**
     * Constructor
     * Creates an asset that holds all basic information
     * PRE: The pathname of the file
     *      The name of the asset
     *      The file and path of the thumbnail
     * POST: An asset
     * @param path
     * @param name
     * @param img
     */
    Asset(String path, String name, String img){
        filePath = path;
        assetName = name;
        thumbnail = img;
        subAssets = null;
    }

    /**
     * hasSub
     * Returns true if this asset has any subassets, and false otherwise
     * PRE: None
     * POST: True/false returned based on the number of subassets
     * @return
     */
    public boolean hasSub(){
        return subAssets != null;
    }

    /**
     * addAssets
     * Adds multiple subassets to this asset
     * PRE: An ArrayList of subassets
     * POST: The given assets added registered as subassets for this asset
     * @param toAdd
     */
    public void addAssets(ArrayList<Asset> toAdd){
        if(subAssets == null) subAssets = new ArrayList<Asset>(toAdd);
        else subAssets.addAll(toAdd);
    }

    /**
     * addAsset
     * Adds a subasset to this asset
     * PRE: An Asset
     * POST: The given asset added registered as a subasset for this asset
     * @param toAdd
     */
    public void addAsset(Asset toAdd){
        if(subAssets != null && subAssets.contains(toAdd)) return;
        if(subAssets == null) subAssets = new ArrayList<Asset>();
        subAssets.add(toAdd);
    }

    /**
     * getAssets
     * Returns all of this asset's subassets
     * PRE: None
     * POST: Returns an ArrayList of all of this asset's subassets
     * @return
     */
    public ArrayList<Asset> getAssets(){
        return this.subAssets;
    }

    /**
     * getNumAssets
     * Returns the number of subassets that this asset has
     * PRE: None
     * POST: Returns the number of subassets
     * @return
     */
    public int getNumAssets(){
        return this.subAssets.size();
    }

    /**
     * toString
     * Returns a string representation of this asset
     * PRE: None
     * POST: The asset name, file path, and thumbnail returned as a string
     */
    public String toString(){
        return assetName + ": " + filePath + ", " + thumbnail;
    }
}
