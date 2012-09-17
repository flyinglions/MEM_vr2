package org.flying.lions;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Looper;
import android.util.Log;
import com.phonegap.api.Plugin;
import com.phonegap.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SMSReader extends Plugin {
	
	public static final String TAG = "SMSReadPlugin";
	
	/** Currently active plugin instance */
	public static Plugin currentPluginInstance;



    @Override
    public PluginResult execute(String action, JSONArray data, String callbackId) {
        Log.d(TAG, "Plugin Called");
        PluginResult result = null;
        JSONObject messages = new JSONObject();
        if (action.equals("inbox")) {
            try {
            	currentPluginInstance = this;
                messages = readSMS("inbox");
                
                Log.d(TAG, "Returning " + messages.toString());
                result = new PluginResult(PluginResult.Status.OK, messages);
            } catch (JSONException jsonEx) {
                Log.d(TAG, "Got JSON Exception "+ jsonEx.getMessage());
                result = new PluginResult(PluginResult.Status.JSON_EXCEPTION);
            }
        }
        else {
            result = new PluginResult(PluginResult.Status.INVALID_ACTION);
            Log.d(TAG, "Invalid action : "+action+" passed");
        }
        return result;
    }


    //Read messages from inbox/or sent box.
    private JSONObject readSMS(String folder) throws JSONException {
    	
    	//currentPluginInstance.sendJavascript("javascript:alert(\"" + android.os.Build.DEVICE + "\")");
    	//currentPluginInstance.sendJavascript("javascript:alert(\"" + android.os.Build.MANUFACTURER + "\")");

    	if(android.os.Build.DEVICE.equals("GT-I9100"))
    	{
	    	try
	    	{
		        String[] smsArray = new String[12];
		
		     
		        smsArray[0] = "Absa: SPR 9437, Bet, 20/06/12 SETTLEMENT/C - INTERNET BETALING DEBIET, ABSA BANK GK betaling, R-900.00, Saldo R1,671.33. Hulp 0860008600; VDWALPG043;1111";
		        smsArray[1] = "Absa: SPR 9437, Dep, 28/06/12 DIREKTE KREDIET, PUNIV SAL UNIV VAN PRETORIA, R2,954.26, Saldo R4,625.59 Hulp 0860008600; VDWALPG043;123163849613;22222";
		        smsArray[2] = "Absa: SPR 9437, Aank, 29/06/12 SETTLEMENT/C - POS AANKOPE, P4ft0N131263 SPAR ZAMBESI, R-163.19, Saldo R4,459.75. Hulp 0860008600; VDWALPG043;33333";
		        smsArray[3] = "Absa: SPR 9437, Onttrek, 29/06/12 PTA RASC - OTM ONTTREKKING, R-100.00, Saldo R4,354.80. Hulp 0860008600; VDWALPG043;123163849613;44444";
		        smsArray[4] = "Bla bla ek is besig om te toets sdf sa";
		        smsArray[5] = "Absa: SPR 9437, Gesk, 29/06/12 DIREKTE DEBIET, DEAGOSTINI-4X000500, R-253.90, Saldo R4,093.75. Hulp 0860008600; VDWALPG043;123163849613;55555";
		        smsArray[6] = "Absa: SPR 9437, Dep, 01/07/12 SETTLEMENT/C - INTERNET BETALING KREDIET, ABSA BANK sakgeld, R400.00, Saldo R4,464.65 Hulp 0860008600; VDWALPG043;666666";
		        smsArray[7] = "Absa: SPR 9437, Aank, 02/07/12 SETTLEMENT/C - POS AANKOPE, P0BFY9510670 EXTREME WARG, R-255.00, Saldo R4,207.00. Hulp 0860008600; VDWALPG043;777777";
		        smsArray[8] = "Absa: SPR 9437, Aank, 03/07/12 SETTLEMENT/C - POS AANKOPE, C32665220001 BUILDERS WAR, R-385.90, Saldo R3,807.45. Hulp 0860008600; VDWALPG043;88888";
		        smsArray[9] = "Absa: SPR 9437, Onttrek, 04/07/12 PTA RASC - OTM ONTTREKKING, R-100.00, Saldo R3,657.90. Hulp 0860008600; VDWALPG043;999999";
		        smsArray[10] = "Absa: Die is nonsense sms om die try catch te toets.";
		    	
		    	MultipleSmsHandler smsHand2 = new MultipleSmsHandler();
		    	
		        for (int y = 0; y < 11; y++) {
		            smsHand2.parseSMS(smsArray[y]);
		        }
	    	}
	    	catch(Exception ex)
	    	{
	    		currentPluginInstance.sendJavascript("javascript:alert(\"EXCEPTION\")");
	    	}
    	}
    	
    	
    	
        
    	/*	@param data 
    			JSON object sent to javascript
    		@param smsList
    			JSON array that is in data
    			
    			makes it easier to read data in javascript => data.messages[0].id
    		
    		*/
    	
    	JSONObject data = new JSONObject();
        Uri uriSMSURI = Uri.parse("");
        if(folder.equals("inbox")){
            uriSMSURI = Uri.parse("content://sms/inbox");
        }

        Cursor cur = getContentResolver().query(uriSMSURI, null, null, null,null);
        JSONArray smsList = new JSONArray();
        data.put("messages", smsList);
        
        
        /*ProgressDialog progressDialog = new ProgressDialog(super.ctx.getActivity());
        
        progressDialog.setMessage("Importing...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0); // set percentage completed to 0%
        //progressDialog.show();*/

        int smsCount = cur.getCount();
        //Log.d(TAG, Integer.toString(smsCount));
        int incVal = 0;
        int counter = 0;
        
        while (cur.moveToNext())
        {
        	counter+=1;
        	
  	
        	
			try
			{
				MultipleSmsHandler smsHand = new MultipleSmsHandler();
		        smsHand.parseSMS(cur.getString(cur.getColumnIndex("body")) + ";" + cur.getString(cur.getColumnIndex("date")));
		        Log.d(TAG, "SUCCESS");
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
        	
        	//SOLVED: Works for both devices Samsung and HTC
        	JSONObject sms = new JSONObject();
            sms.put("number",cur.getString(cur.getColumnIndex("address")));
            sms.put("text",cur.getString(cur.getColumnIndex("body")));
            sms.put("id",cur.getString(cur.getColumnIndex("date")));
            

       
            incVal = counter * 100 / smsCount;
            //progressDialog.incrementProgressBy(incVal);
            
			if (currentPluginInstance != null)
			{
		 	   	currentPluginInstance.sendJavascript("javascript:upProgress("+ Integer.toString(incVal) +")");
			}
            
            smsList.put(sms);
        }
        
        //progressDialog.dismiss();
        
        
        /*
         * Using the JSON object data sent to decoder //Use without the Log.d obviously
         * 
         * Log.d(TAG, Integer.toString(data.getJSONArray("messages").length()));
         * 		How many smses is in object
         * 
         * Log.d(TAG, data.getJSONArray("messages").getJSONObject(0).get("body").toString());
         * 		String value of "body" value of the first SMS - index 0 
        */
        
        
        return data;
    }

   
    private ContentResolver getContentResolver(){
       return this.ctx.getActivity().getContentResolver();
    }



}