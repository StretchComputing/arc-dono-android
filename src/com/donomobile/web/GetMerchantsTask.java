package com.donomobile.web;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.donomobile.utils.ArcPreferences;
import com.donomobile.utils.DonationTypeObject;
import com.donomobile.utils.Logger;
import com.donomobile.utils.MerchantObject;
import com.donomobile.web.rskybox.CreateClientLogTask;

public class GetMerchantsTask extends AsyncTask<Void, Void, Void> {
	
	private String mResponse;
	private Boolean mSuccess;
	private Context mContext;
	private ArrayList<MerchantObject> mMerchantList;
	private int mErrorCode;
	
	public GetMerchantsTask(Context context) {
		super();
		mResponse = null;
		mSuccess = false;
		mContext = context;
		mErrorCode = 0;
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
		mResponse = webService.getMerchants();
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
			(new CreateClientLogTask("GetMerchantsTask.performTask", "JSON Exception Caught", "error", e)).execute();

			Logger.e("Error retrieving merchants, JSON Exception");
		} catch (Exception e){
			(new CreateClientLogTask("GetMerchantsTask.performTask", "Exception Caught", "error", e)).execute();

		}
	}
	
	private void parseJSON(JSONObject json) throws JSONException {
		try {
			// GET MERCHANTS RESP = {"Success":true,"Results":[{"Id":12,"Name":"Isis Lab","Street":"111 Kidzie St.","City":"Chicago","State":"IL","Zipcode":"60654","Latitude":41.889456,"Longitude":-87.6317749999,"PaymentAccepted":"VNMADZ","TwitterHandler":"@IsisLab","GeoDistance":-1.0,"Status":"A","Accounts":[],"Cards":[]}],"ErrorCodes":[]}
			
			if (json.isNull(WebKeys.RESULTS)){
				return;
			}
			
			JSONArray results = json.getJSONArray(WebKeys.RESULTS);  // get an array of returned results
			//Logger.d("Results: " + results);
			mMerchantList = new ArrayList<MerchantObject>();

			for(int i = 0; i < results.length(); i++) {
				
				MerchantObject myMerchant = new MerchantObject();
				
				JSONObject result = results.getJSONObject(i);
				
				String name = result.getString(WebKeys.NAME);
				String street = result.getString(WebKeys.STREET);
				String city = result.getString(WebKeys.CITY);
				String state = result.getString(WebKeys.STATE);
				String zip = result.getString(WebKeys.ZIPCODE);
				double lat = result.getDouble(WebKeys.LATITUDE);
				double lon = result.getDouble(WebKeys.LONGITUDE);
				
				String paymentsAccepted = "";
				if (result.has(WebKeys.PAYMENT_ACCEPTED)){
					 paymentsAccepted = result.getString(WebKeys.PAYMENT_ACCEPTED);
				}
				
				String twitterHandle = "";
				
				if (result.has(WebKeys.TWITTER_HANDLE)){
					twitterHandle = result.getString(WebKeys.TWITTER_HANDLE);
				}
			
				String merchantId = "";
				if (result.has(WebKeys.GET_MERCHANT_ID)){
					merchantId = result.getString(WebKeys.GET_MERCHANT_ID);				}
				
				double geoDistance = 0.0;
				if (result.has(WebKeys.GEO_DISTANCE)){
					geoDistance = result.getDouble(WebKeys.GEO_DISTANCE);
				}
				
				String invoiceId = "";
				if (result.has(WebKeys.INVOICE_ID)){
					invoiceId = result.getString(WebKeys.INVOICE_ID);
				}

				myMerchant.invoiceId = invoiceId;
				myMerchant.merchantName = name;
				myMerchant.merchantId = merchantId;
				
				myMerchant.merchantAddress = "";
				
				try{
					myMerchant.merchantAddress = street;
				}catch(Exception e){
					
				}
				
				if (result.has(WebKeys.CHARGE_CONVENIENCE_FEE)){
					
					myMerchant.chargeFee = result.getBoolean(WebKeys.CHARGE_CONVENIENCE_FEE);
					myMerchant.convenienceFee = result.getDouble(WebKeys.CONVENIENCE_FEE);
					myMerchant.convenienceFeeCap = result.getDouble(WebKeys.CONVENIENCE_FEE_CAP);

				}
				
				
				if (result.has(WebKeys.QUICKPAY)){
					
					JSONArray quickPayDetails = result.getJSONArray(WebKeys.QUICKPAY);  
					
					 for (int j = 0; j < quickPayDetails.length(); j++){
		                    
		                    JSONObject quickPayObject = quickPayDetails.getJSONObject(j);
		                    
		                    if (j == 0) {
		                    	myMerchant.quickDonateOne = quickPayObject.getDouble(WebKeys.VALUE);
		                    }else if (j == 1){
		                    	myMerchant.quickDonateTwo = quickPayObject.getDouble(WebKeys.VALUE);

		                    }else if (j == 2){
		                    	myMerchant.quickDonateThree = quickPayObject.getDouble(WebKeys.VALUE);

		                    }else if (j == 3){
		                    	myMerchant.quickDonateFour = quickPayObject.getDouble(WebKeys.VALUE);

		                    }
		                }

				}
				if (result.has(WebKeys.INVOICE_DETAILS)){
					
					JSONArray invoiceDetails = result.getJSONArray(WebKeys.INVOICE_DETAILS);  
					
					for(int j = 0; j < invoiceDetails.length(); j++) {

						DonationTypeObject myDonationType = new DonationTypeObject();

						JSONObject donationType =  invoiceDetails.getJSONObject(j);
												
						myDonationType.donationId = donationType.getString(WebKeys.ID);
						myDonationType.description = donationType.getString(WebKeys.DESCRIPTION);
						myDonationType.shouldDisplay = donationType.getBoolean(WebKeys.DISPLAY);

						myMerchant.donationTypes.add(myDonationType);

					}

				}

				String status = result.getString(WebKeys.STATUS);
				if (status != null && status.equalsIgnoreCase("A")){
					mMerchantList.add(myMerchant);

				}
				
				
			}
		} catch (Exception e) {
			(new CreateClientLogTask("GetMerchantsTask.parseJson", "Exception Caught", "error", e)).execute();

		}
	}

	public String getResponse() {
		return mResponse;
	}	
	
	public ArrayList<MerchantObject> getMerchants(){
		return mMerchantList;
	}
	
	public Context getContext() {
		return mContext;
	}	
	
	public int getErrorCode(){
		return mErrorCode;
	}
}