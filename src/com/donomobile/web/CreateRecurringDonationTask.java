package com.donomobile.web;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.donomobile.domain.Cards;
import com.donomobile.utils.ArcPreferences;
import com.donomobile.utils.Logger;
import com.donomobile.utils.MerchantObject;
import com.donomobile.utils.RecurringDonationObject;
import com.donomobile.web.rskybox.CreateClientLogTask;

public class CreateRecurringDonationTask extends AsyncTask<Void, Void, Void> {
	
	private String mResponse;
	private String mToken;
	private String mCustomerId;

	private Boolean mSuccess;
	private Context mContext;
	private ArrayList<RecurringDonationObject> mRecurringList;
	private int mErrorCode;
	
	private MerchantObject mMerchant;
	private Cards mSelectedCard;
	private RecurringDonationObject mRecurringObject;
	
	private String mNumber;
	private String mExpiration;
	private String mCCV;

	
	public CreateRecurringDonationTask(Context context, String token, String customerId, MerchantObject myMerchant, RecurringDonationObject myRecurringObject, String cardNumber, String expiration, String pin) {
		super();
		mResponse = null;
		mSuccess = false;
		mContext = context;
		mErrorCode = 0;
		mToken = token;
		mCustomerId = customerId;
		mMerchant = myMerchant;
		mRecurringObject = myRecurringObject;
		mNumber = cardNumber;
		mExpiration = expiration;
		mCCV = pin;

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
		mResponse = webService.createRecurringDonations(mToken, mCustomerId, mMerchant, mRecurringObject, mNumber, mExpiration, mCCV);
	}
	
	protected void performPostExec() {
		if(mResponse == null) {
			return;
		}
	
		try {
			JSONObject json =  new JSONObject(mResponse);
			mSuccess = json.getBoolean(WebKeys.SUCCESS);
			if(mSuccess) {
				//parseJSON(json);
			}else{
				JSONArray errorArray = json.getJSONArray(WebKeys.ERROR_CODES);  // get an array of returned results
				if (errorArray != null && errorArray.length() > 0){
					//Error
					JSONObject error = errorArray.getJSONObject(0);
					mErrorCode = error.getInt(WebKeys.CODE);

				}
			}
		} catch (JSONException e) {
			(new CreateClientLogTask("CreateRecurringDonationTask.performTask", "JSON Exception Caught", "error", e)).execute();

			Logger.e("Error retrieving payments, JSON Exception");
		} catch (Exception e){
			(new CreateClientLogTask("CreateRecurringDonationTask.performTask", "Exception Caught", "error", e)).execute();

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
	public Boolean getSuccess(){
		return mSuccess;
	}
	
	public int getErrorCode(){
		return mErrorCode;
	}
}