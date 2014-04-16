package com.donomobile.web;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.donomobile.utils.ArcPreferences;
import com.donomobile.utils.Keys;
import com.donomobile.utils.Logger;
import com.donomobile.web.rskybox.CreateClientLogTask;

public class UpdateCustomerTask extends AsyncTask<Void, Void, Void> {
	
	private String mLogin;
	private String mPassword;
	private boolean mIsGuest;
	private String mNewCustomerToken;
	private String mDevResponse;
	private boolean mSuccess;
	private boolean finalSuccess;
	private Context mContext;
	private int mErrorCode;
	
	private String mFirstName;
	private String mLastName;

	public UpdateCustomerTask(String firstName, String lastName, Context context) {
		super();
		mFirstName = firstName;
		mLastName = lastName;
		mIsGuest = false;
		mNewCustomerToken = "";
		mContext = context;
		mErrorCode = 0;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		performTask();
		return null;
	}
	
	@Override
	protected void onPostExecute(Void param) {
		super.onPostExecute(param);
		performPostExec();
	}
	
	protected boolean performTask() {
		// get a token for the dev server
		
		
		WebServices webService = new WebServices(new ArcPreferences(mContext).getServer());
		
		ArcPreferences myPrefs = new ArcPreferences(mContext);
		String guestToken = myPrefs.getString(Keys.CUSTOMER_TOKEN);
		
		
		mDevResponse = webService.updateCustomer(mFirstName, mLastName, guestToken);
		
		if (mDevResponse == null){

			return false;
		}
		try {
			
			JSONObject json =  new JSONObject(mDevResponse);
			mSuccess = json.getBoolean(WebKeys.SUCCESS);
			if(mSuccess) {
			
				mNewCustomerToken="";
				
				if (!json.isNull(WebKeys.RESULTS)){
					mNewCustomerToken = json.getString(WebKeys.RESULTS);
				}

				if (mNewCustomerToken.length() == 0){

					return false;
				}else{
					
					
					finalSuccess = true;
		

					return true;
				}
				
			}else{
				JSONArray errorArray = json.getJSONArray(WebKeys.ERROR_CODES);  // get an array of returned results
				if (errorArray != null && errorArray.length() > 0){
					//Error
					JSONObject error = errorArray.getJSONObject(0);
					mErrorCode = error.getInt(WebKeys.CODE);

				}
			}
			
			
		} catch (JSONException e) {
			(new CreateClientLogTask("UpdateCustomerTask.performTask", "JSON Exception Caught", "error", e)).execute();

			Logger.e("Error retrieving token, JSON Exception");
		} catch (Exception e){
			(new CreateClientLogTask("UpdateCustomerTask.performTask", "Exception Caught", "error", e)).execute();

		}
		// get a token for the prod server
		//webService = new WebServices(URLs.PROD_SERVER);
		//mProdResponse = webService.getToken(mLogin, mPassword, mIsGuest);
		return true;
	}
	
	protected void performPostExec() {
		if(mDevResponse == null) { // || mProdResponse == null) {
			return;
		}
		
	
	}
	
	
	
	
	
	public String getDevResponse() {
		return mDevResponse;
	}
	
	
	public String getNewCustomerToken() {
		return mNewCustomerToken;
	}
	
	
	public Boolean getSuccess() {
		return mSuccess;
	}
	
	public Boolean getFinalSuccess(){
		return finalSuccess;
	}
	public Context getContext() {
		return mContext;
	}
	public int getErrorCode(){
		return mErrorCode;
	}
}