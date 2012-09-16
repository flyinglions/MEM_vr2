package org.flying.lions;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MultipleSmsHandler {
    SMSHandler absaHandler = null;
    SMSHandler fnbHandler = null;
    private String textFileName = "settings.ini";
    private  CategoriesSorter theSorter = null;
    
public MultipleSmsHandler() throws FileNotFoundException, IOException {
    try
    {
	absaHandler = new SMSHandler("ABSARules.ini");
    fnbHandler = new SMSHandler("FNBRules.ini");
    
    theSorter = new CategoriesSorter(textFileName);
    SMSHandler.setTheSorter(theSorter);       
    }
    catch(Exception ex)
    {
    	ex.printStackTrace();
    }
}

public void parseSMS(String inSms) throws IOException{
    try{
                String tempCheck = inSms.substring(0,5);        
    if(tempCheck.contains("Absa")){
        absaHandler.recieveSMS(inSms);
    }else {
        fnbHandler.recieveSMS(inSms); 
    }}catch(Exception e){}
}


}
