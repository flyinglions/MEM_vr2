package org.flying.lions;

import java.io.*;
import java.util.Scanner;

import android.os.Environment;
import android.util.Log;

public class SQLGenerator {

    private String newLine = System.getProperty("line.separator");
    private String sqlFileName = "SQLStatements.txt";
    private String prevFileName = "prevValue.txt";
    private String timeStamp = "";
    private String[] tempReal = null;
    private int lengthCompare = 11;
    private String[] bankHolder = new String[10];
    private int bank = 0;
    private int bankAmount = 2;

    public void buildSQL(String[] realValue) throws IOException {
        timeStamp = SMSHandler.getTimeStamp();
        tempReal = realValue;
        this.getBanks();
        if (realValue.length == lengthCompare) {
            bank = 1;
            this.writeToFile(this.buildInsert(realValue), this.buildRecon(realValue[7], realValue[5], realValue[4]));
        } else {
            bank = 0;
            this.writeToFile(this.buildInsert(realValue), this.buildRecon(realValue[12], realValue[2], realValue[6]));
        }
    }

    private String buildInsert(String[] realValue) throws FileNotFoundException, IOException {
        String SQLStatement = "INSERT INTO sms(Date,Time,Amount,Balance,Location,Account_Num,Category) values('";
        if (realValue.length == lengthCompare) {
            SQLStatement += this.convertFromAbsa(realValue[3]) + "','" + timeStamp + "'," + realValue[5] + "," + this.getCorrectBalance(realValue[7]) + ",'" + realValue[4] + "','" + realValue[1] + "','" + this.getCategories(realValue[4]) + "')";
        } else {
            SQLStatement += this.getDateCorrect(realValue[15]) + "','" + timeStamp + "'," + realValue[2] + "," + this.getCorrectBalance(realValue[12]) + ",'" + realValue[6] + "','" + realValue[8] + "','" + this.getCategories(realValue[6]) + "')";
        }

        return SQLStatement;
    }
    
    private String getCorrectBalance(String string) throws NumberFormatException, IOException {
        float currBal = Float.parseFloat(string);
        if (tempReal.length == lengthCompare) {
            if (currBal == 0) {
                currBal = this.getPrev() + Float.parseFloat(tempReal[5]);
                return currBal + "";
            }
        } else {
            if (currBal == 0) {
                currBal = this.getPrev() + Float.parseFloat(tempReal[2]);
                return currBal + "";
            }
        }
        return string;

    }

    private String buildRecon(String currBal, String diff, String Loc) throws IOException {
        String SQLStatement = "INSERT INTO Recon(Type,Recon,Account_Num,SMS_ID) values('";
        String recon = this.getRecon(currBal, diff);
       
        if (recon.equals("0.00")) {
            return "";
        }

        String Account = "";
        String date = "";
        if (tempReal.length == lengthCompare) {
            Account = tempReal[1];
            date = this.convertFromAbsa(tempReal[3]) + ":" + timeStamp;
        } else {
            Account = tempReal[8];
            date = this.getDateCorrect(tempReal[15]) + ":" + timeStamp;
        }
        SQLStatement += this.getExspensesIncome(diff) + "'," + recon + ",'" + Account + "','" + date + "')";
        return SQLStatement;
    }

    private String removeMinus(String inValue) {
        if (inValue.startsWith("-")) {
            inValue = inValue.substring(1);
        }
        return inValue;
    }

    private String getExspensesIncome(String inCurrancy) {
        if (inCurrancy.contains("-")) {
            return "Expense";
        } else {
            return "Income";
        }
    }

    private String getCategories(String Loc) throws FileNotFoundException, IOException {
         return SMSHandler.theSorter.getCategory(Loc);
    }

    private void getBanks() throws IOException {
    	
        File sdcard = Environment.getExternalStorageDirectory();
        
        File folder = new File(sdcard + "/MEM/ORI");
        folder.mkdirs();

      //Get the text file
        File file = new File(sdcard + "/MEM/ORI", prevFileName);
        
        if(file.exists())
        {
            Scanner sc = new Scanner(file);
            String singleLine = "";
            int place = 0;
            while (sc.hasNextLine()) {
                singleLine = sc.nextLine();
                bankHolder[place] = singleLine.substring(0, singleLine.indexOf("="));
                place += 1;
            }
            bankAmount = place;
        }
        	
        //If file doesn't exist on startup
        else
        {
	   		 FileWriter fileWriter = new FileWriter("/mnt/sdcard/MEM/ORI/" + prevFileName);
		   	 fileWriter.write("cheq Acc..909429=0.0;" + "\r\n");
		   	 fileWriter.write("SPR 9437=0.0;" + "\r\n");

	
		   	 fileWriter.flush();
		   	 fileWriter.close();
        	
	            Scanner sc = new Scanner(file);
	            String singleLine = "";
	            int place = 0;
	            while (sc.hasNextLine()) {
	                singleLine = sc.nextLine();
	                bankHolder[place] = singleLine.substring(0, singleLine.indexOf("="));
	                place += 1;
	            }
	            bankAmount = place;
        }
        
    	

    }    
    
    private String getRecon(String stringVal, String diff) throws IOException {
        float prevSaldo = this.getPrev();
        System.out.println("R "+stringVal + " r " + diff + " prev " + prevSaldo );
        stringVal = stringVal.replaceAll(",", "");
        diff = diff.replaceAll(",", "");
        float currBal = Float.parseFloat(stringVal);
        float currDiff = Float.parseFloat(diff);
        float recon = 0;
        
        if(currBal == 0){
            if(prevSaldo == 0){
            prevSaldo = currBal;
            this.writePrev(prevSaldo);
            return "0.00";
            }
            prevSaldo = prevSaldo + currDiff;
            this.writePrev(prevSaldo);            
            return "0.00";
        }

        if (prevSaldo == 0) {
            prevSaldo = currBal;
            this.writePrev(prevSaldo);
            return "0.00";
        }
        float newBal = 0;

        newBal = prevSaldo + currDiff;

        if (currBal == newBal) {
            prevSaldo = currBal;
            this.writePrev(prevSaldo);
            return "0.00";
        } else {
            recon = newBal - currBal;
            prevSaldo = currBal;
        }
        this.writePrev(prevSaldo);
        recon = (float) (Math.round(recon * 100.00) / 100.00);
        String reconString = String.valueOf(recon);
        return reconString;
    }

    private void writeToFile(String Insert, String recon) throws IOException {
    	
    	 Log.d("SQLWriter","Writing SQLStatements");
    	
		 FileWriter fileWriter = new FileWriter("/mnt/sdcard/MEM/SQLStatements.txt", true);
	   	 fileWriter.append(Insert + "\r\n");
	     if (!recon.equals("")) {
	    	 fileWriter.append(recon + "\r\n");
	     }

	   	 fileWriter.flush();
	   	 fileWriter.close();
	   	 
	   	 
    }

    private void writePrev(float prev) throws IOException {
    	
    	Log.d("SQLWriter","writePrev");

    	String[] lines = new String[2];
    	
        File sdcard = Environment.getExternalStorageDirectory();
        
        File folder = new File(sdcard + "/MEM/ORI");
        folder.mkdirs();

      //Get the text file
        File file = new File(sdcard + "/MEM/ORI", prevFileName); 
        
        Scanner sc = new Scanner(file);

        int place = 0;
        while (sc.hasNextLine()) {
            lines[place] = sc.nextLine();
            place += 1;
        }
        for (int y = 0; y < bankAmount; y++) {
            if (lines[y].contains(bankHolder[bank])) {
                lines[y] = bankHolder[bank] + "=" + prev + ";";

            }
        }
    	
    	
		FileWriter fileWriter = new FileWriter("/mnt/sdcard/MEM/ORI/" + prevFileName);

        for (int y = 0; y < bankAmount; y++) {
            fileWriter.write(lines[y] + newLine);
            fileWriter.flush();
        }
        fileWriter.close();
    }

    private float getPrev() throws IOException {
    	Log.d("SQLWriter","getPrev");
    	
    	String singleLine = "";
        File sdcard = Environment.getExternalStorageDirectory();
        
        File folder = new File(sdcard + "/MEM/ORI");
        folder.mkdirs();

      //Get the text file
        File file = new File(sdcard + "/MEM/ORI", prevFileName);
        
        if(file.exists())
        {
            Scanner sc = new Scanner(file);
            if (bank == 0) {
                singleLine = sc.nextLine();
            } else if (bank == 1) {
                singleLine = sc.nextLine();
                singleLine = sc.nextLine();
            }
            singleLine = singleLine.substring(singleLine.indexOf("=") + 1, singleLine.indexOf(";"));
            sc.close();
            return Float.parseFloat(singleLine);
        }
        	
        //If file doesn't exist on startup
        else
        {
	   		 FileWriter fileWriter = new FileWriter("/mnt/sdcard/MEM/ORI/" + prevFileName);
		   	 fileWriter.write("cheq Acc..909429=0.0;" + "\r\n");
		   	 fileWriter.write("SPR 9437=0.0;" + "\r\n");

	
		   	 fileWriter.flush();
		   	 fileWriter.close();
        	
        	 return (float) 0.0;
        }

        
    }


    private String getDateCorrect(String string) {
        /*Moet huidige jaar kry*/
        String day = "";
        
        String year = "2012/";
        if(string.length()  <= 10 ){
        day = "/0" + string.substring(0, 1);
        } else{
           day = "/" + string.substring(0, 2); 
        }
            
        String month = "";

        if (string.contains("Jan")) {
            month = "01";
        } else if (string.contains("Feb")) {
            month = "02";
        } else if (string.contains("Mar")) {
            month = "03";
        } else if (string.contains("Apr")) {
            month = "04";
        } else if (string.contains("May")) {
            month = "05";
        } else if (string.contains("Jun")) {
            month = "06";
        } else if (string.contains("Jul")) {
            month = "07";
        } else if (string.contains("Aug")) {
            month = "08";
        } else if (string.contains("Sep")) {
            month = "09";
        } else if (string.contains("Oct")) {
            month = "10";
        } else if (string.contains("Nov")) {
            month = "11";
        } else if (string.contains("Dec")) {
            month = "12";
        } else {
            month = "00";
        }

        return year + month + day;
    }

    private String convertFromAbsa(String string) {
        String year = "20" + string.substring(string.length() - 2, string.length()) + "/";
        String day = "/" + string.substring(0, 2);
        String month = string.substring(3, 5);
        return year + month + day;
    }
}
