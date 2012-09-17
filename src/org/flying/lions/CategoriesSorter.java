package org.flying.lions;
import INI.INISystem;
import INI.Key;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.os.Environment;
import android.util.Log;


public class CategoriesSorter {
	 private INISystem catGetter = null;
	    private ArrayList<Key> categories = null;

	    public CategoriesSorter(String textFileName) throws FileNotFoundException, IOException {
	        //File locationFile = new File("");
	        //String fileLocation = locationFile.getCanonicalPath().toString() + "\\" + textFileName;
	        
	        File sdcard = Environment.getExternalStorageDirectory();
	        File file;
	        
	        /*if(android.os.Build.DEVICE.contains("Samsung") || android.os.Build.MANUFACTURER.contains("Samsung")){
	        	file = new File(sdcard + "/external_sd/","settings.ini");
	        }
	        else
	        {
	        	file = new File(sdcard,"settings.ini");
	        }*/

	      //Get the text file
	        
	        file = new File(sdcard,"settings.ini");
	        
	        String fileLocation = file.getPath();
	        
	        catGetter = new INISystem(fileLocation);
	        categories = catGetter.getKeys("categories");

	    }

	    public String getCategory(String location) {
	        int size = categories.size();
	        for (int y = 0; y < size; y++) {
	            String catArray[] = categories.get(y).getValue().split(",");
	            int catSize = catArray.length;
	            for (int s = 0; s < catSize; s++) {
	                if (location.toLowerCase().contains(catArray[s].trim().toLowerCase())) {
	                    return categories.get(y).getKey();
	                }
	            }
	        }
	        return "Other";
	    }

}
