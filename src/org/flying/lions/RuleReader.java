package org.flying.lions;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.io.File;

import android.os.Environment;
import android.util.Log;


public class RuleReader {

    private String confFileName = "ABSARules.ini";
    private String[] tokenedRules = null;

    public RuleReader(String newConfName) {
        confFileName = newConfName;
    }

    RuleReader() {
        
    }

    public void iniRules() throws IOException {
     
    	String tempString = null;
        File sdcard = Environment.getExternalStorageDirectory();

      //Get the text file
        File folder = new File(sdcard + "/MEM/ORI");
        folder.mkdirs();
        
       	File file = new File(sdcard + "/MEM/ORI",confFileName);   
        
       	if(file.exists())
        {
            Scanner sc = new Scanner(file);
            
            tempString = sc.nextLine();

            sc.close();

        }
        	
        else
        {
	   		 FileWriter fileWriter = new FileWriter("/mnt/sdcard/MEM/ORI/" + confFileName);
	   		 
	   		 if(confFileName.equals("ABSARules.ini"))
	   			 tempString = "<Absa,;> <accName,,> <TransactionType,,> <Date,;> <Location,,> <\"R\"amount,,> * <\"R\"balance,.> * * *";
	   			 	   		 
	   		 if(confFileName.equals("FNBRules.ini"))
	   			 tempString = "<FNB,;> * <\"R\"Cost,;> * * <TransactionType,;> <location,\"@\"|\"from\"|\".\"> * <accName,\"from\"> * * * <\"R\"balance,.> * * <Time,;*>";
	   			 
	   		 fileWriter.write(tempString);
	
		   	 fileWriter.flush();
		   	 fileWriter.close();
        	
        }
        
        tokenedRules = tempString.split(" ");
       
    }



    public String getConfFileName() {
        return confFileName;
    }

    public String[] getTokenedRules() {
        return tokenedRules;
    }
}
