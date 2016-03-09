/*
 *  Copyright (c) 2011 by Twilio, Inc., all rights reserved.
 *
 *  Use of this software is subject to the terms and conditions of 
 *  the Twilio Terms of Service located at http://www.twilio.com/legal/tos
 */

package com.twilio.example.callingapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.twilio.client.ConnectionListener;
import com.twilio.client.Device;
import com.twilio.client.DeviceListener;
import com.twilio.client.PresenceEvent;
import com.twilio.client.Twilio;
import com.twilio.client.Connection;

import java.util.HashMap;
import java.util.Map;

public class Phone implements Twilio.InitListener,DeviceListener,ConnectionListener
{
    private static final String TAG = "Phone";
    private Connection connection = null;
    private Context context = null;
    private Device device = null;

    public Phone(Context context)
    {
        this.context = context;
        Twilio.initialize(context, this /* Twilio.InitListener */);
    }

    public void connect(String phoneNumber )
    {
        Map<String, String> parameters = new HashMap<String, String>();
         //parameters.put("From", "+18563515180");
         parameters.put("To", "+14844859149");
        connection = device.connect(parameters, null /* ConnectionListener */);
        if (connection == null){
            //Log.v(TAG,"Establishing connection....");
            Log.w(TAG, "Failed to create new connection");
            //connection = device.connect(parameters, null /* ConnectionListener */);
         }/*else{
            Log.w(TAG, "Already Connected.");
            Log.v(TAG, "Disconnecting...");
            connection.disconnect();
            connection = null;
            Log.v(TAG,"Connection Disconnected.");
        }*/
    }

    public void disconnect()
    {
        if (connection != null) {
            connection.disconnect();
            connection = null;
        }
    }

    /* Twilio.InitListener method */
    @Override
    public void onInitialized()
    {
        Log.d(TAG, "Twilio SDK is ready");
        new RetrieveCapabilityToken().execute();


    }

    public void handleIncomingConnection(Device inDevice, Connection inConnection)
    {
        Log.i(TAG, "Device received incoming connection");
        if (connection != null)
            connection.disconnect();
        connection = inConnection;
        connection.accept();
    }
    @Override
    public void onStartListening(Device device) {
        Log.i(TAG, "Device is now listening for incoming connections");
    }

    @Override
    public void onStopListening(Device device) {
        Log.i(TAG, "Device is no longer listening for incoming connections");
    }

    @Override
    public void onStopListening(Device device, int inErrorCode, String inErrorMessage) {
        Log.i(TAG, "Device is no longer listening for incoming connections due to error " +
                inErrorCode + ": " + inErrorMessage);
    }

    @Override
    public boolean receivePresenceEvents(Device device) {
        return false;
    }

    @Override
    public void onPresenceChanged(Device device, PresenceEvent presenceEvent) {

    }

    @Override
    public void onConnecting(Connection connection) {
      Log.i(TAG, "Device is now getting connected.");
    }

    @Override
    public void onConnected(Connection connection) {
        Log.i(TAG, "Device is now connected.");
    }

    @Override
    public void onDisconnected(Connection connection) {
        Log.i(TAG, "Device is now disconnected.");
    }

    @Override
    public void onDisconnected(Connection connection, int inErrorCode, String inErrorMessage) {
        Log.i(TAG, "Device is disconnected due to error " +
                inErrorCode + ": " + inErrorMessage);
    }

    /*private void UpdateUI(final String status, final String buttonText){

    }*/

    private class RetrieveCapabilityToken extends AsyncTask<String, Void, String>{
        private final String CAPABILITY_TOKEN_URL = "http://callingapp.ignatiuz.com/Home/AndroidCapToken";
		@Override
		protected String doInBackground(String... params) {
			try{ 
				String capabilityToken = HttpHelper.httpGet(CAPABILITY_TOKEN_URL);
                Log.d(TAG + " capabilityToken: ",capabilityToken);
				return capabilityToken;
			} catch( Exception e ){
				 Log.e(TAG, "Failed to obtain capability token: " + e.getLocalizedMessage());
				 return null;
			}
		}
    	
		@Override 
		protected void onPostExecute(String capabilityToken ){
            Log.d(TAG, " onPostExecute ");
            Log.d(TAG + " capabilityToken: ", capabilityToken);
			Phone.this.setCapabilityToken(capabilityToken);
		}
    }

    protected void setCapabilityToken(String capabilityToken){
        Log.d(TAG,"Inside setCapabilityToken");
        device = Twilio.createDevice(capabilityToken, null /* DeviceListener */);
        Log.d(TAG,"Device Object Created.");
        Intent intent = new Intent(context, CallingActivity.class);
        Log.d(TAG,"Intent Created");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d(TAG, "PendingIntent Created.");
        if(device!=null) {
            Log.d(TAG,"Initiate pendingIntent.");
            device.setIncomingIntent(pendingIntent);
        }else{
            Log.e(TAG,"Device Object is Null.");
        }
    }
    
    /* Twilio.InitListener method */
    @Override
    public void onError(Exception e)
    {
        Log.e(TAG, "Twilio SDK couldn't start: " + e.getLocalizedMessage());
    }

    @Override
    protected void finalize()
    {
        if (device != null)
            device.release();
    }
}
