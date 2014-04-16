package com.donomobile.web;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.donomobile.utils.ArcPreferences;
import com.donomobile.utils.Logger;
import com.donomobile.utils.PaymentHistoryObject;
import com.donomobile.utils.RecurringDonationObject;
import com.donomobile.web.rskybox.CreateClientLogTask;

public class GetRecurringDonationsTask extends AsyncTask<Void, Void, Void> {
	
	private String mResponse;
	private String mToken;
	private String mCustomerId;

	private Boolean mSuccess;
	private Context mContext;
	private ArrayList<RecurringDonationObject> mRecurringList;
	private int mErrorCode;
	
	public GetRecurringDonationsTask(Context context, String token, String customerId) {
		super();
		mResponse = null;
		mSuccess = false;
		mContext = context;
		mErrorCode = 0;
		mToken = token;
		mCustomerId = customerId;

	}
	
	@Override
	protected Void doInBackground(Void... params) {
		performTask();
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		performPostExec();
	}
	
	protected void performTask() {
		WebServices webService = new WebServices(new ArcPreferences(mContext).getServer());
		mResponse = webService.getRecurringDonations(mToken, mCustomerId);
	}
	
	protected void performPostExec() {
		if(mResponse == null) {
			return;
		}
	
		try {
			JSONObject json =  new JSONObject(mResponse);
			mSuccess = json.getBoolean(WebKeys.SUCCESS);
			if(mSuccess) {
				parseJSON(json);
			}else{
				JSONArray errorArray = json.getJSONArray(WebKeys.ERROR_CODES);  // get an array of returned results
				if (errorArray != null && errorArray.length() > 0){
					//Error
					JSONObject error = errorArray.getJSONObject(0);
					mErrorCode = error.getInt(WebKeys.CODE);

				}
			}
		} catch (JSONException e) {
			(new CreateClientLogTask("GetRecuringDonationsTask.performTask", "JSON Exception Caught", "error", e)).execute();

			Logger.e("Error retrieving payments, JSON Exception");
		} catch (Exception e){
			(new CreateClientLogTask("GetRecuringDonationsTask.performTask", "Exception Caught", "error", e)).execute();

		}
	}
	
	private void parseJSON(JSONObject json) throws JSONException {
		try {
			
			if (json.isNull(WebKeys.RESULTS)){
				return;
			}
			
			JSONArray results = json.getJSONArray(WebKeys.RESULTS);  // get an array of returned results
			Logger.d("Recurring Donation RESULTS****: " + results);
			mRecurringList = new ArrayList<RecurringDonationObject>();

			for(int i = 0; i < results.length(); i++) {
				
				RecurringDonationObject myDonation = new RecurringDonationObject();
				
				JSONObject result = results.getJSONObject(i);
				
				myDonation.amount = result.getDouble("Amount");
				myDonation.value = result.getInt("Value");
				myDonation.gratuity = result.getDouble("Gratuity");
				myDonation.xOfMonth = result.getInt("xOfMonth");
				myDonation.type = result.getString("Type");
				myDonation.merchantId = result.getInt("MerchantId");
				myDonation.scheduleId = result.getString("Id");
				myDonation.ccToken = result.getString("CCToken");
				

				
				mRecurringList.add(myDonation);

				
			}
			
		} catch (Exception e) {
			(new CreateClientLogTask("GetRecuringDonationsTask.parseJson", "Exception Caught", "error", e)).execute();

		}
	}

	public String getResponse() {
		return mResponse;
	}	
	
	public ArrayList<RecurringDonationObject> getDonationList(){
		return mRecurringList;
	}
	
	public Context getContext() {
		return mContext;
	}	
	
	public int getErrorCode(){
		return mErrorCode;
	}
}