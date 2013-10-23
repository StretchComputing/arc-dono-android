package com.donomobile.activities;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.donomobile.ArcMobileApp;
import com.donomobile.BaseActivity;
import com.donomobile.domain.Check;
import com.donomobile.utils.ArcPreferences;
import com.donomobile.utils.Constants;
import com.donomobile.utils.CurrencyFilter;
import com.donomobile.utils.Keys;
import com.donomobile.utils.Logger;
import com.donomobile.web.ErrorCodes;
import com.donomobile.web.GetCheckTask;
import com.donomobile.web.rskybox.AppActions;
import com.donomobile.web.rskybox.CreateClientLogTask;
import com.donomobileapp.R;

public class GetCheck extends BaseActivity {

	private TextView title;
	private ProgressBar activityBar;
	private EditText invoice;
	private String venueName;
	private String merchantId;
	private ProgressDialog loadingDialog;
	private RelativeLayout helpLayout;
	private TextView textEnter;
    private static final int DISPLAY_DATA = 1;
    private ImageView helpImage;
    private RelativeLayout helpImageLayout;
    private boolean isGettingInvoice = false;
    
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        public void run() {
        	hideHelp();
        }
    };
	
	public GetCheck() {
		super();
	}

	public GetCheck(int titleRes) {
		super(titleRes);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			

			super.onCreate(savedInstanceState);
			setContentView(R.layout.get_check);
			invoice = (EditText) findViewById(R.id.invoice);
			invoice.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			invoice.setFilters(new InputFilter[] { new InputFilter.LengthFilter(6) });

			title = (TextView) findViewById(R.id.title);
			title.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			
			//activityBar = (ProgressBar) findViewById(R.id.activityBar);
			//activityBar.setVisibility(View.INVISIBLE);
			
			venueName = getIntent().getStringExtra(Constants.VENUE);
			
			AppActions.add("Get Check - OnCreate - Merchant Name:" + venueName);

			
			
			merchantId = getIntent().getStringExtra(Constants.VENUE_ID);
			title.setText(venueName);
			textEnter = (TextView) findViewById(R.id.check_enter);
			textEnter.setTextColor(Color.rgb(190,190,190));
			textEnter.setTypeface(ArcMobileApp.getLatoLightTypeface());

			
			helpImage = (ImageView) findViewById(R.id.help_image);
			helpImageLayout = (RelativeLayout) findViewById(R.id.help_image_layout);
			helpImageLayout.setVisibility(View.INVISIBLE);

			helpImageLayout.setOnTouchListener(new View.OnTouchListener(){
				 
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					
					showCheckHelp();
					return false;
				}
		 
			});
			
			
			helpLayout = (RelativeLayout) findViewById(R.id.top_layout);
			
			ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());
			
			Boolean hasSeenCheckHelp = myPrefs.getBoolean(Keys.SEEN_CHECKNUMBER_HELP);
			
			if (hasSeenCheckHelp){
				helpLayout.setVisibility(View.GONE);
			}else{
				

		        handler.postDelayed(runnable, 6000);

				myPrefs.putAndCommitBoolean(Keys.SEEN_CHECKNUMBER_HELP, true);
				
				helpLayout.setOnTouchListener(new View.OnTouchListener(){
					 
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						
						helpLayout.setVisibility(View.GONE);
						return false;
					}
			 
				});
	
			}
			
			setHelpImage();

			

		} catch (Exception e) {

			//(new CreateClientLogTask("GetCheck.onCreate", "Exception Caught", "error", e)).execute();

		}
		
		
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		loadingDialog = new ProgressDialog(GetCheck.this);
		loadingDialog.setTitle("Getting Invoice");
		loadingDialog.setMessage("Please Wait...");
		loadingDialog.setCancelable(false);
	}
	 public void hideHelp()
     {
		helpLayout.setVisibility(View.GONE);

     }
	 
	 
	public void onViewBillClick(View v) {
		try {
			String checkNum = invoice.getText().toString();
			
			AppActions.add("Get Check - View Bill Clicked - Check Number:" + checkNum + ", Merchant:" + venueName);

			
			if(checkNum == null || checkNum.trim().length() == 0) {
				toastLong("Please enter your check number");
				return;
			}
			
		
			loadingDialog.show();
			
			if (!isGettingInvoice){
				isGettingInvoice = true;
				getInvoice();
			}
		} catch (Exception e) {
			(new CreateClientLogTask("GetCheck.onViewBillClick", "Exception Caught", "error", e)).execute();

		}

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.check_number_menu, menu);
		return true;
	}
	
	protected void getInvoice() {
		try {
			String token = getToken();
			if (token != null) {
				GetCheckTask getInvoiceTask = new GetCheckTask(token, merchantId, invoice.getText().toString(), getApplicationContext()) {
					@Override
					protected void onPostExecute(Void result) {
						try {
							
							super.onPostExecute(result);
							

							loadingDialog.hide();
							int errorCode = getErrorCode();
							isGettingInvoice = false;
							
							if (getFinalSuccess() && errorCode == 0) {

								AppActions.add("Get Check - Get Invoice Successful");

								
								Check theBill = getTheBill();

								if (theBill == null || theBill.getItems().size() == 0) {
									toastShort("Could not locate your check");
									//.setVisibility(View.INVISIBLE);
									return;
								}else{
								
									String checkNum = invoice.getText().toString();
									loadingDialog.dismiss();
									
									Logger.d("*******************GET CHECK START ACTIVITY 1");


									Intent viewCheck = new Intent(getApplicationContext(), ViewCheck.class);
									viewCheck.putExtra(Constants.INVOICE, theBill);
									viewCheck.putExtra(Constants.VENUE, venueName);
									viewCheck.putExtra(Constants.CHECK_NUM, checkNum);
									startActivity(viewCheck);

									
								}

							} else {
								//Not Succes
								
								AppActions.add("Get Check - Get Invoice Failed - Error Code:" + errorCode);

								if (errorCode != 0){
									
									String errorMsg = "";
									
									if(errorCode == ErrorCodes.INVOICE_NOT_FOUND) {
						                errorMsg = "Can not find invoice.";
						            } else if(errorCode == ErrorCodes.INVOICE_CLOSED) {
						                errorMsg = "Invoice closed.";
						            }else if (errorCode == ErrorCodes.CHECK_IS_LOCKED){
						                errorMsg = "Invoice being accessed by your waiter.  Try again in a few minutes.";
						            } else if (errorCode == ErrorCodes.NETWORK_ERROR){
						                errorMsg = "Arc is having problems connecting to the internet.  Please check your connection and try again.  Thank you!";
						                
						            } else {
						                errorMsg = ErrorCodes.ARC_ERROR_MSG;
						            }
									
									
									
									toastShort(errorMsg);
									
								}else{
									toastShort("Error retreiving invoice");

								}
								//.setVisibility(View.INVISIBLE);

							}
						} catch (Exception e) {

							(new CreateClientLogTask("GetCheck.getInvoice.onPostExecute", "Exception Caught", "error", e)).execute();

						}
					}
				};
				getInvoiceTask.execute();
			} else {

			}
		} catch (Exception e) {
			(new CreateClientLogTask("GetCheck.getInvoice", "Exception Caught", "error", e)).execute();

		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int itemId = item.getItemId();
		if (itemId == R.id.showCheckHelp) {
			showCheckHelp();
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void showCheckHelp(){
		helpLayout.setVisibility(View.GONE);
		
		if (helpImageLayout.getVisibility() == View.VISIBLE){
			AppActions.add("Get Check - Hiding Check Help");

			helpImageLayout.setVisibility(View.INVISIBLE);
			
			InputMethodManager imm = (InputMethodManager)getSystemService(
				      Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(invoice, 0);
		}else{
			
			AppActions.add("Get Check - Showing Check Help");

			InputMethodManager imm = (InputMethodManager)getSystemService(
				      Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(invoice.getWindowToken(), 0);
				
				helpImageLayout.setVisibility(View.VISIBLE);

		}

	}
	
	public void setHelpImage(){
		
		try{
			
			AppActions.add("Get Check - Setting Help Image");

			
			String url = "http://arc.dagher.mobi/Images/App/Receipts/"+merchantId+ ".jpg";
			
			GetXMLTask task = new GetXMLTask();
	        // //Execute the task
	        task.execute(new String[] { url });
			
		}catch (Exception e){
            Logger.d("Exception1 " + e.getMessage());
		}
		
	}
	
	

	private class GetXMLTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
        	try{
        		Bitmap map = null;
                for (String url : urls) {
                    map = downloadImage(url);
                }
                return map;
        	}catch(Exception e){
        		return null;
        	}
            
        }
 
        // Sets the Bitmap returned by doInBackground
        @Override
        protected void onPostExecute(Bitmap result) {
        	try{
        		if (result != null){
                    helpImage.setImageBitmap(result);
            	}
        	}catch(Exception e){
        		
        	}
        }
 
        // Creates Bitmap from InputStream and returns it
        private Bitmap downloadImage(String url) {
           try{
        	   Bitmap bitmap = null;
               InputStream stream = null;
               BitmapFactory.Options bmOptions = new BitmapFactory.Options();
               bmOptions.inSampleSize = 1;
    
               try {
                   stream = getHttpConnection(url);
                   bitmap = BitmapFactory.
                           decodeStream(stream, null, bmOptions);
                   stream.close();
               } catch (IOException e1) {
                   e1.printStackTrace();
               }
               return bitmap;
           }catch(Exception e){
        	   return null;
           }
        }
 
        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
           try{
        	   InputStream stream = null;
               URL url = new URL(urlString);
               URLConnection connection = url.openConnection();
    
               try {
                   HttpURLConnection httpConnection = (HttpURLConnection) connection;
                   httpConnection.setRequestMethod("GET");
                   httpConnection.connect();
    
                   if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                       stream = httpConnection.getInputStream();
                   }
               } catch (Exception ex) {
                   ex.printStackTrace();
               }
               return stream;
           }catch(Exception e){
        	   return null;
           }
        }
    }
	
	
}
