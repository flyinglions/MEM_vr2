package org.flying.lions;

public class Rule {
    /*
    Split sms op as van een na ander.
    */

   private int ruleNumber = 0;
   private String playString = "";
   private boolean isInternet = false;
   private String location = "";

   Rule(int toDo) {
       ruleNumber = toDo;
   }

   

   /*
    * Rule list:
    * 01 - Skip till next token := *
    * 02 - Token is till ',' := <Value,,>
    * 03 - Token is till '' := <Value,; >
    * 04 - Token is till '.' := <Value,.>
    * 05 - Token is from end of  last" till ',' := <"R"Value,,>
    * 06 - Token is from end of  last" till '.' := <"R"Value,.>  
    * 07 - Token is from end of last "from" till ' ' := <Value,"from">
    * 08 - Token is from end of last" till ' ' := <"R"Value,;>
    * 09 - Token is from this point, till end of SMS := <Value,;*>
    * 10 - Token is from @ till "from" or ".". Checks first for from. := <Value,"@"
    */
   public String doRule(String getToken, int Start) {

       playString = getToken;
       playString = this.removeTo(Start);

       if (ruleNumber == 0) {
           System.out.println("Init Rule");
           return "";
       }
       switch (ruleNumber) {
           case 1: {
               return skipToken().trim();
           }
           case 2: {  
               return getToBreak().trim();
           }
           case 3: {  
               return getToSpace().trim();
           }
           case 4: {  
               return getToPoint().trim();
           }
           case 5: {  
               return getFromHashToBreak().trim();
           }
           case 6: {  
               return getFromHashToPoint().trim();
           }
           case 7:{   
               return getFromFromToSpace();
           }
           case 8:{   
               return getFromHashToSpace();
           }
           case 9:{   
               return getToEndOfSms();
           }case 10:{ 
               return getFromAtToFromOrPoint();
           }
       }
       
       return "";
   }
   //Rule 1
   private String skipToken() {
       /*int firstBreak = playString.indexOf(" ");
       SMSHandler.setStart(firstBreak);*/
       return "*";
   }

   //Rule 2;
   private String getToBreak() {
       String returnString = "";

       int breakCon = playString.indexOf(",") + 1;
       returnString = playString.substring(0, breakCon);
       SMSHandler.setStart(breakCon);
       returnString = removeFinalBreaks(returnString);
       return returnString;
   }

   //Rule 3;    
   private String getToSpace() {
       String returnString = "";
       int breakCon = playString.indexOf(" ");
       returnString = playString.substring(0, breakCon);
       SMSHandler.setStart(breakCon);
       returnString = removeFinalBreaks(returnString);
       return returnString;
   }

   //Rule 4;   
   private String getToPoint() {
       String returnString = "";
       int breakCon = playString.indexOf(".");
       returnString = playString.substring(0, breakCon);
       SMSHandler.setStart(breakCon);
       returnString = removeFinalBreaks(returnString);
       return returnString;
   }
   //Rule 5
   private String getFromHashToBreak() {
       String returnString = "";
       boolean isMinus = false;
       int breakFirst = playString.indexOf("R-") + 1;
       int breakLast = playString.indexOf(", ");

       if (breakFirst == 0) {
           breakFirst = playString.indexOf("R") + 1;
       }

       if (breakFirst < 0) {
           breakFirst = 0;
       }

       if (breakFirst > breakLast) {          
           this.handleSpecLocation(breakLast);
           
            if (playString.contains("R-")) {
               breakFirst = playString.indexOf("R-") + 1;
           } else {
               breakFirst = playString.indexOf("R") + 1;
           }
       }

       returnString = playString.substring(breakFirst, breakLast);
       String tempString = playString;

       while (!returnString.matches("^[-]?([0-9][0-9]?([,][0-9]{3}){0,4}([.][0-9]{0,4})?)$|^[$]?([0-9]{1,14})?([.][0-9]{1,4})$|^[$]?[0-9]{1,14}$")) {

           if (tempString.contains("R-")) {
               isMinus = true;
               breakFirst = tempString.indexOf("R-") + 2;
           } else {
               breakFirst = tempString.indexOf("R") + 1;
           }
           
           breakLast = tempString.indexOf(", ");
           
           //Spesiale case, as balance negatief is.       
           if(breakFirst > breakLast){
             isMinus = false;
              breakFirst = tempString.indexOf("R") + 1;
           }      

           returnString = tempString.substring(breakFirst, breakLast);
           SMSHandler.setStart(breakLast);
           tempString = tempString.substring(breakLast + 1).trim();
       }
       if (isMinus) {
           returnString = "-" + returnString;
       }
       isMinus = false;      
            if (playString.contains("reserved for purchase")                    
                   || playString.contains("withdrawn from")
                   || playString.contains("paid from")
                   || playString.contains("t/fer from")
                   || playString.contains("Scheduled Payment from")) {returnString = "-"+returnString;
               
           }
       
       return returnString.replaceAll(",", "");
   }
   //Rule 6
   private String getFromHashToPoint() {  
       String returnString = "";
       boolean isMinus = false;
       int breakFirst = playString.indexOf("R-") + 1;
       int breakLast = playString.indexOf(". ");

       if (breakFirst == 0) {
           breakFirst = playString.indexOf("R") + 1;
       }

       if (breakFirst < 0) {
           breakFirst = 0;
       }
       //BREAK RULE SYSTEM!
       if(breakLast < 0){
           String temp = playString;
           int space = temp.indexOf(" ");
           playString = playString.substring(space).trim();
           breakLast = playString.indexOf(" ");
           
       }

       if (breakFirst > breakLast && !(breakLast < 0) ) {
           this.handleSpecLocation(breakLast);
           if (playString.contains("R-")) {
               breakFirst = playString.indexOf("R-") + 1;
           } else {
               breakFirst = playString.indexOf("R") + 1;
           }
       }
           if(breakLast > playString.length()){
               breakLast = playString.indexOf(". ");
           }
       returnString = playString.substring(breakFirst, breakLast);
       String tempString = playString;

       while (!returnString.matches("^[-]?([0-9][0-9]?([,][0-9]{3}){0,4}([.][0-9]{0,4})?)$|^[$]?([0-9]{1,14})?([.][0-9]{1,4})$|^[$]?[0-9]{1,14}$")) {

           if (tempString.contains("R-")) {
               isMinus = true;
               breakFirst = tempString.indexOf("R-") + 2;
           } else {
               breakFirst = tempString.indexOf("R") + 1;
           }
           /*Nie tot punt...*/
           breakLast = tempString.indexOf(" ");
           
           
           //Spesiale case, as balance negatief is.       
           if(breakFirst > breakLast){
             isMinus = false;
             breakFirst = tempString.indexOf("R") + 1;
           } 
           
           if(breakFirst > breakLast){
             breakLast = tempString.indexOf(". ");              
           }
           returnString = tempString.substring(breakFirst, breakLast);
           SMSHandler.setStart(breakLast);
           tempString = tempString.substring(breakLast + 1).trim();
       }
       if (isMinus) {
           returnString = "-" + returnString;
       }
       
        if (playString.contains("reserved for purchase")                    
                   || playString.contains("withdrawn from")
                   || playString.contains("paid from")
                   || playString.contains("t/fer from")
                   || playString.contains("Scheduled Payment from")) {returnString = "-"+returnString;
               
         } 

        if(returnString.length() == 2){
            returnString = playString.substring(1,breakLast).replaceAll(",", "");
        }
       return returnString.replaceAll(",", "");         
   }
   
   //Rule 8
   private String getFromHashToSpace() { 
       
       String returnString = "";

       int breakFirst = playString.indexOf("R") + 1;
       int breakLast = playString.indexOf(" ");

       if (breakFirst < 0) {
           breakFirst = 0;
       }
       
       if(breakFirst > breakLast){
           playString = playString.substring(breakLast).trim();
           breakLast = playString.indexOf(" ");
           breakFirst = playString.indexOf("R")+1;
       }
       
      if(breakFirst > breakLast){
          String tempString = playString.substring(breakFirst);
          breakLast = tempString.indexOf(" ");
          breakLast += breakFirst;
      }
       
       SMSHandler.setStart(breakLast);
      
       returnString = playString.substring(breakFirst, breakLast);
      if (playString.contains("reserved for purchase")                    
                   || playString.contains("withdrawn from")
                   || playString.contains("paid from")
                   || playString.contains("t/fer from")
                   || playString.contains("Scheduled Payment from")) 
      {returnString = "-"+returnString;
               
           } 
   
       return returnString.replaceAll(",", "");
   }  
   
   //Rule 8
   private String getFromFromToSpace(){
      
       /*Werk nie...iewers word verkeerd geset*/
       String returnString;
       String tempString;
       int breakFirst;
       int breakLast;
       int tempInt;

       
       breakFirst = playString.indexOf("from") + 4;
       /*Reset die location*/
       playString = playString.substring(breakFirst).trim();        
       tempInt = playString.indexOf(" ");
       tempString = playString.substring(tempInt).trim();
       breakLast = tempString.indexOf(" ");
       SMSHandler.setStart(breakLast + tempInt);  

       returnString = playString.substring(0, breakLast + tempInt +1);
       
       /*Special Case - Heeltemal Ander Account*/
       if(returnString.contains("card")){
           breakFirst = breakLast;
           breakLast = playString.indexOf("using");
           returnString = playString.substring(breakFirst, breakLast).trim();               
           return returnString;
       } else {
           return returnString;
       }

        
   }
   
   //rule 09
   private String getToEndOfSms() {
       int index = playString.lastIndexOf(".")+1;
       String tempStrin = playString.substring(index).trim();
       tempStrin = tempStrin.replaceAll(",", "");
       return tempStrin;
   }
   //Rule 10
   private String getFromAtToFromOrPoint() {
       String returnString = "";
       String tempString = playString;
       int breakFirst = tempString.indexOf("@") + 1;
       int breakLast = 0;
       tempString = tempString.substring(breakFirst);
       if(tempString.contains("from")){
         breakLast = tempString.indexOf("from") -1; 
       }else {
           breakLast = tempString.indexOf(".");
       }

         //Special case
         if(breakFirst + breakLast <= 0) {
           return "No location";
       }
         
         if(breakFirst > breakLast){
             breakFirst = 0;
         }
              
      returnString = tempString.substring(breakFirst,breakLast);
      
       return returnString;
   }

   @Override
   public String toString() {
       return "Rule Nr: " + ruleNumber;
   }

   private String removeTo(int where) {
       return playString.substring(where).trim();
   }

   private String removeFinalBreaks(String toTrim) {
       int leng = toTrim.length();
       if (toTrim.endsWith(",") || toTrim.endsWith(":")) {
           toTrim = toTrim.substring(0, leng - 1);
       }
       return toTrim;
   }

   private void handleSpecLocation(int to) {
       isInternet = true;
       location = playString.substring(0, to);
       playString = this.removeTo(to + 1);
      //NEEDED FOR ABSA
       SMSHandler.setPrev(location);
       SMSHandler.setStart(to);
   }

   public boolean isIsInternet() {
       return isInternet;
   }

   public String getLocation() {
       return location;
   }

   public void setIsInternet(boolean isInternet) {
       this.isInternet = isInternet;
   }
}
