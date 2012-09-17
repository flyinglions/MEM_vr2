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



    @Override
    public PluginResult execute(String action, JSONArray data, String callbackId) {
        Log.d(TAG, "Plugin Called");
        PluginResult result = null;
        JSONObject messages = new JSONObject();
        if (action.equals("inbox")) {
            try {
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
        Log.d(TAG, Integer.toString(smsCount));
        int incVal = 100 / smsCount;
        
        while (cur.moveToNext())
        {
        	
        	
            /*
             * HTC se settings
             * sms.put("number",cur.getString(3));
             * sms.put("text",cur.getString(12));   
             *
            */        	
        	
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
            

            /*Log.d(TAG, "0->"+ cur.getString(0));
            Log.d(TAG, "1->"+ cur.getString(1));
            Log.d(TAG, "2->"+ cur.getString(2));
            Log.d(TAG, "3->"+ cur.getString(3));
            Log.d(TAG, "4->"+ cur.getString(4));
            Log.d(TAG, "5->"+ cur.getString(5));
            Log.d(TAG, "6->"+ cur.getString(6));
            Log.d(TAG, "7->"+ cur.getString(7));
            Log.d(TAG, "8->"+ cur.getString(8));
            Log.d(TAG, "9->"+ cur.getString(9));
            Log.d(TAG, "10->"+ cur.getString(10));
            Log.d(TAG, "11->"+ cur.getString(11));
            Log.d(TAG, "12->"+ cur.getString(12));
            Log.d(TAG, "13->"+ cur.getString(13));
            Log.d(TAG, "14->"+ cur.getString(14));
            Log.d(TAG, "15->"+ cur.getString(15));
            //Log.d(TAG, "16->"+ cur.getString(16));
            //Log.d(TAG, "17->"+ cur.getString(17));*/
            
            
            //progressDialog.incrementProgressBy(incVal);
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