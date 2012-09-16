package org.flying.lions;

import java.io.IOException;


import android.util.Log;

import java.io.FileNotFoundException;

public class SMSHandler {

    private RuleBuilder ruleBuild = new RuleBuilder();
    private static int currentLocation = 0;
    private Rule[] ruleList = null;
    private String[] valueArray = null;
    private static String[] realValue = null;
    private int messageSize = 0;
    private String smsString = "";
    private static boolean isValid = true;
    private SQLGenerator SQLGen = new SQLGenerator();
    private static int place = 0;
    private static String timeStamp = "";
    public static CategoriesSorter theSorter = null;
    
    public SMSHandler() throws FileNotFoundException, IOException {
        
    }
    
    public SMSHandler(String ruleNameFile) throws FileNotFoundException, IOException {
        ruleBuild = new RuleBuilder(ruleNameFile);
    }
    

    public void recieveSMS(String inSms) throws IOException {

        smsString = inSms.replaceAll("\"", " ");
      
        try{
        timeStamp = this.getTimeStampSMS();
        }catch(Exception e){}
        ruleBuild.ruleParser();
        messageSize = ruleBuild.getRuleLeng();
        ruleList = ruleBuild.getRuleList();
        valueArray = ruleBuild.getValueArray();

        realValue = new String[messageSize];
        try {
            String tempCheck = inSms.substring(0,5); 
            if (!smsString.contains("reserved") && !tempCheck.contains("Absa") && !smsString.contains("fraud")) {
                hardCodedFNB(inSms);
                SQLGen.buildSQL(realValue);                
                System.out.println("Sms valid1");
                currentLocation = 0;
                isValid = true;
                return;
            }
        } catch (Exception e) {
            //e.printStackTrace();
            System.err.println("SMS is not valid: '" + inSms + "'");
            return;
        }

        for (int y = 0; y < messageSize; y++) {
            try {
                if (isValid) {
                    this.parseSMS(y);
                    place = y;
                } else {

                    System.err.println("SMS is not valid: '" + inSms + "'");
                    currentLocation = 0;
                    isValid = true;
                    return;
                }

            } catch (Exception e) {
                //e.printStackTrace();
                System.err.println("SMS is not valid: '" + inSms + "'");
                currentLocation = 0;
                isValid = true;
                return;
            }
        }
        System.out.println("Sms valid2");
        Log.d("SMSHandler","Call smshandler");
        try
        {
        	SQLGen.buildSQL(realValue);
        	Log.d("SMSHANDLER", "SUCCESS");
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        }
        currentLocation = 0;
        isValid = true;

    }

    public static void setTheSorter(CategoriesSorter theSorter) {
        SMSHandler.theSorter = theSorter;
    }    
    

    private void parseSMS(int where) {
        realValue[where] = ruleList[where].doRule(smsString, currentLocation);
    }

    public static void setStart(int start) {
        start += 1;
        currentLocation += start;
    }

    public static void setValid(boolean toChange) {
        isValid = toChange;
    }

    @Override
    public String toString() {
        StringBuilder returnString = new StringBuilder();

        for (int y = 0; y < messageSize; y++) {
            returnString.append(y).append(": ").append(realValue[y]).append(" -- ").append(valueArray[y]).append("\n");
        }
        return returnString.toString();
    }

    private String getTimeStampSMS() {
        String returnString = "";
        int beginIndex = smsString.lastIndexOf(";");
        returnString = smsString.substring(beginIndex + 1).trim();
        smsString = smsString.substring(0, beginIndex);
        return returnString;
    }

    public static String getTimeStamp() {
        return timeStamp;
    }

    public static void setPrev(String location) {
        realValue[place] = location;
    }

    private void hardCodedFNB(String inSms) {
        int lastDot = inSms.lastIndexOf(".");
        //Case as stuff na die laaste dot kom 
        if ((lastDot + 2) >= inSms.length()) {
            lastDot = inSms.lastIndexOf(":") - 10;
        }

        int atPlace = inSms.indexOf("@");

        if (atPlace <= 0) {
            atPlace = inSms.indexOf("To");
        }
        int accPlace = inSms.indexOf("cheq");
        int rplace = inSms.indexOf("R");
        int avail;
        int secondSpace;
        String tempString = inSms;
        if (inSms.contains("Avail")) {
            avail = inSms.indexOf("Avail");
            realValue[6] = inSms.substring(atPlace + 2, avail - 2);
            tempString = inSms.substring(avail + 7);
            secondSpace = tempString.indexOf(". ");
            realValue[12] = tempString.substring(0, secondSpace);

        } else {
            realValue[6] = inSms.substring(atPlace + 2, lastDot).trim();
            realValue[12] = "0.00";
        }
        realValue[0] = "FNB";
        realValue[1] = "*";
        realValue[4] = "*";
        realValue[3] = "*";
        realValue[7] = "*";
        realValue[9] = "*";
        realValue[10] = "*";
        realValue[11] = "*";
        realValue[13] = "*";
        realValue[14] = "*";

        realValue[15] = inSms.substring(lastDot + 2, lastDot + 13);

        realValue[8] = inSms.substring(accPlace, atPlace - 1);
        inSms = inSms.substring(rplace);
        int spacePlace = inSms.indexOf(" ");
        realValue[2] = inSms.substring(1, spacePlace);
        inSms = inSms.substring(spacePlace + 1);
        spacePlace = inSms.indexOf(" ");
        realValue[5] = inSms.substring(0, spacePlace);
    }
    
}

