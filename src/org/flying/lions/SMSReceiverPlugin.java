package org.flying.lions;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.cordova.api.PluginResult;
import org.apache.cordova.api.PluginResult.Status;
import org.apache.cordova.Notification;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.telephony.SmsMessage;
import android.util.Log;

import com.phonegap.api.Plugin;



public class SMSReceiverPlugin extends Plugin {
	/** Logger-Tag. */
    public static final String TAG = "SMSReceiverPlugin";

    /** Constant for action REGISTER */
    public static final String REGISTER="register";

    /** Constant for action UNREGISTER */
	public static final String UNREGISTER="unregister";

	/** Currently active plugin instance */
	public static Plugin currentPluginInstance;

	/** save Callbackfunction-Name for later use */
	private static String callbackFunction;

	@Override
    public PluginResult execute(final String action, final JSONArray data, final String callbackId)
    {
		Log.v(TAG + ":execute", "action=" + action);

		PluginResult result = null;

		if (REGISTER.equals(action))
		{
			Log.v(TAG + ":execute", "data=" + data.toString());

			try
			{
				JSONObject json = data.getJSONObject(0);
				callbackFunction = (String) json.get("callback");
				currentPluginInstance = this;
				result = new PluginResult(Status.OK);
			}
			catch (JSONException e)
			{
				Log.e(TAG, "Got JSON Exception " + e.getMessage());
				result = new PluginResult(Status.JSON_EXCEPTION);
			}
		}
		else if (UNREGISTER.equals(action))
		{
			currentPluginInstance = null;
			result = new PluginResult(Status.OK);
		}
		else
		{
			Log.e(TAG, "Invalid action : " + action);
			result = new PluginResult(Status.INVALID_ACTION);
		}

		return result;
	}

	/**
	 * Static function to send a SMS to JS.
	 * @param sms
	 * @throws IOException 
	 */
	public static void sendMessage(final SmsMessage msg) throws IOException
	{
			Log.d(TAG, "sendMessage Called");
			// build JSON message
			JSONObject sms = new JSONObject();
			MultipleSmsHandler smsHand = new MultipleSmsHandler();
			
			try
			{
				
				sms.put("origin", msg.getOriginatingAddress());
				sms.put("body", msg.getMessageBody());
				//sms.put("id", msg.getTimestampMillis());
				
		        //String smsSimulation = " Absa: SPR 9437, Gesk, 29/06/12 DIREKTE DEBIET, DEAGOSTINI-4X000500, R-253.90, Saldo R4,093.75. Hulp 0860008600; VDWALPG043";

		        smsHand.parseSMS(msg.getMessageBody() + ";" + msg.getTimestampMillis());
		        Log.d(TAG, "SUCCESS");
		        
				/*Date dateObj = new Date(msg.getTimestampMillis());
				DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				String timeDate = df.format(dateObj);
				//Log.v(TAG + ":sendJavascript", timeDate);
				
				sms.put("time", timeDate);*/
			}
			catch (JSONException e)
			{
		 	   	Log.e(TAG + ":sendMessage", "JSON exception");
			}


			// When the Activity is not loaded, the currentPluginInstance is null
			
			if (currentPluginInstance != null)
			{
				// build code to call function
				String code =  "javascript:" + callbackFunction + "(" + sms.toString() + ");";
				
		 	   	Log.v(TAG + ":sendJavascript", code);
	
		 	   	// execute code
		 	   	//currentPluginInstance.sendJavascript(code);
		 	   	currentPluginInstance.sendJavascript("javascript:notificationCallback()");
			}
			else
			{
				/*try
				{
					if(!sms.getString("bank").equals("NOT BANK SMS"))
					{
			            FileWriter fileWriter;
			            try{
			            	 fileWriter = new FileWriter("/mnt/sdcard/receivedSMS.txt", true);
			            	 fileWriter.append(sms.toString());
			            	 fileWriter.flush();
			            	 fileWriter.close();
			             }
			            catch(Exception e){
			                      e.printStackTrace();
			             }
					}
				}
				catch (JSONException e)
				{
			 	   	Log.e(TAG + ":sendMessage", "JSON exception");
				}*/
				
				/*
				 *     	 FileWriter fileWriter;
					   	 fileWriter = new FileWriter("/mnt/sdcard/SQLStatements.txt", true);
					   	 fileWriter.append(Insert + "\r\n");
					     fileWriter.append(recon + "\r\n");
					   	 fileWriter.flush();
					   	 fileWriter.close();    	
    	
				 * 
				 * 
				 */
			}
		
	}

}
