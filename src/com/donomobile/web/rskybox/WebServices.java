package com.donomobile.web.rskybox;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.pm.ApplicationInfo;
import android.util.Base64;

import com.donomobile.ArcMobileApp;
import com.donomobile.utils.ArcPreferences;
import com.donomobile.utils.Keys;
import com.donomobile.utils.Logger;
import com.donomobile.web.URLs;

public class WebServices {
	
	private DefaultHttpClient httpClient;
	private HttpPost httpPost;
	private HttpResponse httpResponse;
	private HttpEntity httpEntity;
	private String errorMsg = "";
	
	// rSkybox Credentials
	private static final String BASIC_AUTH_TOKEN = "ekokq167k46gbrmr6hvbht9lab";
	private static final String APPLICATION_ID = "ahRzfnJza3lib3gtc3RyZXRjaGNvbXITCxILQXBwbGljYXRpb24YgPYvDA";
	
	// URLs
	private static final String HTTPS_BASE_URL = "https://rskybox-stretchcom.appspot.com/";
	private static final String REST_BASE_URL = HTTPS_BASE_URL + "rest/v1/";
	private static final String APPLICATION_RESOURCE_URI = "applications";
	private static final String FEEDBACK_RESOURCE_URI = "feedback";
	private static final String CRASH_DETECT_RESOURCE_URI = "crashDetects";
	private static final String CLIENT_LOG_RESOURCE_URI = "clientLogs";
	
	public WebServices() {
		this.httpClient = new DefaultHttpClient();
	}
	
	public String getAuthenticationHeader() {
		String authPair = "token:" + BASIC_AUTH_TOKEN;
		byte[] tokenBytes = authPair.getBytes();
		String encodedToken = Base64.encodeToString(tokenBytes, Base64.NO_WRAP);
		String authHeader = "Basic " + encodedToken;
		Logger.d("|rskybox-web-services|", "CREATE CLIENTLOG auth header  = " + authHeader);
		return authHeader;
	}
	
	private String getResponse(String url, String json) {
		StringBuilder reply = null;
		try {
			this.httpClient = new DefaultHttpClient();
			httpPost = new HttpPost(url);

			if (json != null && json != "") {
				httpPost.setEntity(new ByteArrayEntity(json.getBytes("UTF8")));
				httpPost.setHeader("Content-type", "application/json");
				httpPost.setHeader("Authorization", getAuthenticationHeader());
			}

			httpResponse = httpClient.execute(httpPost);
			httpEntity = httpResponse.getEntity();

			BufferedReader in = new BufferedReader(new InputStreamReader(
					httpEntity.getContent()));
			String inputLine;

			reply = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				reply.append(inputLine);
			}
			in.close();

			httpEntity.consumeContent();
			httpClient.getConnectionManager().shutdown();
		} catch (IOException e) {
			Logger.e("|POST RESONSE ERROR| " + e.getMessage());
			setError(e.getMessage());
			return null;
		}

		return reply.toString();
	}


	public String getError() {
		if (this.errorMsg == null || this.errorMsg == "")
			this.errorMsg = "No Error.";

		return this.errorMsg;
	}

	private void setError(String error) {
		this.errorMsg = error;
	}
	
	
	public String createClientLog(String theLogName, String theLogMessage, String theLogLevel, Exception theException) {
		String resp = "";
		try {
	        String url = REST_BASE_URL + "applications/" + APPLICATION_ID + "/" + CLIENT_LOG_RESOURCE_URI;
			Logger.d("|rskybox-web-services|", "CREATE CLIENTLOG URL  = " + url);
			
			JSONObject json = new JSONObject();

			//Add to the summary
			String summaryString = "";
			try{
				String apiLevel = "" + android.os.Build.VERSION.SDK_INT;
				String osVersion = "" + android.os.Build.VERSION.RELEASE;

				String device = android.os.Build.MANUFACTURER + " " + android.os.Build.DEVICE + " - " + android.os.Build.BRAND;
			     
				
				//Summary String holds Android OS Version: , Android API Version: , Device: , App Version
		        summaryString = "Android OS Version: " + osVersion + ", Android API Level: " + apiLevel + ", Device: " + device + ", App Version: " + ArcMobileApp.getVersion();
			}catch(Exception e){
				summaryString = "EXCEPTION finding summaryString";
			}
			
			json.put(WebKeys.SUMMARY, summaryString);

			
			
			json.put(WebKeys.VERSION, ArcMobileApp.getVersion());
			json.put(WebKeys.LOG_LEVEL, theLogLevel);
			json.put(WebKeys.LOG_NAME, theLogName);
			
			if(theException != null) {
				theLogMessage += " - " + theException.getMessage();
			}
			json.put(WebKeys.MESSAGE, theLogMessage);
			
			String emailAddress = "<not_available>";
			ArcPreferences myPrefs = new ArcPreferences(ArcMobileApp.getAppContext());
			emailAddress = myPrefs.getString(Keys.CUSTOMER_EMAIL);
			json.put(WebKeys.USER_ID, emailAddress);
			
			ApplicationInfo applicationInfo = ArcMobileApp.getContext().getApplicationInfo();
			boolean isDebuggable = (0 != (applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE));
			String localEndpoint = "dono.android";
			if(isDebuggable) {localEndpoint = "dev." + localEndpoint;}
			else             {localEndpoint = "prd." + localEndpoint;}
			json.put(WebKeys.LOCAL_ENDPOINT, localEndpoint);
			
			// once env can be chosen in UI by admin, set remoteEndpoint a different way
			String remoteEndpoint = URLs.getHost(new ArcPreferences(ArcMobileApp.getAppContext()).getServer());
			
			json.put(WebKeys.REMOTE_ENDPOINT, remoteEndpoint);
			
			List<String> appActions = new ArrayList<String>();
			List<String> timestamps = new ArrayList<String>();
			AppActions.getAll(appActions, timestamps);
			JSONArray appActionsJsonArray = new JSONArray();
			for(int i=0; i<appActions.size(); i++) {
				JSONObject appActionJsonObject = new JSONObject();
				appActionJsonObject.put(WebKeys.DESCRIPTION, appActions.get(i));
				appActionJsonObject.put(WebKeys.TIMESTAMP, timestamps.get(i));
				// TODO -- add duration
				appActionJsonObject.put(WebKeys.DURATION, "0");
				appActionsJsonArray.put(appActionJsonObject);
			}
			json.put(WebKeys.APP_ACTIONS, appActionsJsonArray);
			
			if(theException != null) {
				JSONArray steJsonArray = new JSONArray();
				for(StackTraceElement ste : theException.getStackTrace()) {
					Logger.d("Stack TRACE****** " + ste.toString());
					steJsonArray.put(ste.toString());
				}
				json.put(WebKeys.STACK_BACK_TRACE, steJsonArray);
			}
			
			resp = this.getResponse(url, json.toString());
			Logger.d("|rskybox-web-services|", "CREATE CLIENT JSON INPUT = " + json.toString());
			Logger.d("|rskybox-web-services|", "CREATE CLIENT LOG RESP = " + resp);
			return resp;
		} catch (Exception e) {
			setError(e.getMessage());
			return resp;
		}
	}
}
