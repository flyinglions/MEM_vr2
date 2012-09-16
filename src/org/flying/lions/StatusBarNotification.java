package org.flying.lions;


import org.flying.lions.R;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.apache.cordova.DroidGap;
import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.apache.cordova.api.PluginResult.Status;

public class StatusBarNotification extends Plugin {
	//	Action to execute
	public static final String NOTIFY = "notify";
	public static final String CLEAR = "clear";

	/**
	 * 	Executes the request and returns PluginResult
	 * 
	 * 	@param action		Action to execute
	 * 	@param data			JSONArray of arguments to the plugin
	 *  @param callbackId	The callback id used when calling back into JavaScript
	 *  
	 *  @return				A PluginRequest object with a status
	 * */
	@Override
	public PluginResult execute(String action, JSONArray data, String callbackId) {
		String ns = Context.NOTIFICATION_SERVICE;
		//phonegap version 2.0.0
		//mNotificationManager = (NotificationManager) cordova.getActivity().getSystemService(ns);
        //context = cordova.getActivity();
		mNotificationManager = (NotificationManager) ctx.getActivity().getSystemService(ns);
		context = ctx.getActivity();

		PluginResult result = null;
		if (NOTIFY.equals(action)) {
			try {

				String title = data.getString(0);
				String body = data.getString(1);
				Log.d("NotificationPlugin", "Notification: " + title + ", " + body);
				showNotification(title, body);
				result = new PluginResult(Status.OK);
			} catch (JSONException jsonEx) {
				Log.d("NotificationPlugin", "Got JSON Exception "
						+ jsonEx.getMessage());
				result = new PluginResult(Status.JSON_EXCEPTION);
			}
		} else if (CLEAR.equals(action)){
			clearNotification();
		} else {
			result = new PluginResult(Status.INVALID_ACTION);
			Log.d("NotificationPlugin", "Invalid action : "+action+" passed");
		}
		return result;
	}

	/**
	 * 	Displays status bar notification
	 * 
	 * 	@param contentTitle	Notification title
	 *  @param contentText	Notification text
	 * */
	public void showNotification( CharSequence contentTitle, CharSequence contentText ) {
		int icon = R.drawable.notification;
	 	
		long when = System.currentTimeMillis();
	
		Notification notification = new Notification(icon, contentTitle, when);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
  	
		Intent notificationIntent = new Intent(ctx.getActivity(), ctx.getClass());
		PendingIntent contentIntent = PendingIntent.getActivity(ctx.getActivity(), 0, notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		mNotificationManager.notify(1, notification);        
		
		//phonegap version 2.0.0
        /*Notification noti = new Notification.Builder(context)
          .setContentTitle(contentTitle)
          .setContentText(contentText)
          .setSmallIcon(icon)
          .build();
        //notification.flags |= Notification.FLAG_NO_CLEAR; //Notification cannot be clearned by user


        mNotificationManager.notify(1, noti);*/
	}

	/**
	 * Removes the Notification from status bar
	 */
	public void clearNotification() {
		mNotificationManager.cancelAll();
	}

	private NotificationManager mNotificationManager;
    private Context context;
}