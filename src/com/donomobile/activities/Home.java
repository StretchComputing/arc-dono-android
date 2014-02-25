package com.donomobile.activities;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.donomobile.ArcMobileApp;
import com.donomobile.BaseActivity;
import com.donomobile.utils.ArcPreferences;
import com.donomobile.utils.Constants;
import com.donomobile.utils.DataSingleton;
import com.donomobile.utils.Keys;
import com.donomobile.utils.MerchantObject;
import com.donomobile.web.ErrorCodes;
import com.donomobile.web.GetMerchantsTask;
import com.donomobile.web.rskybox.AppActions;
import com.donomobile.web.rskybox.CreateClientLogTask;
import com.donomobileapp.R;

public class Home extends BaseActivity {

	private ListView typesListView;

	private RelativeLayout theView;
	private ArrayList<MerchantObject> merchants;
	private ArrayAdapter<MerchantObject> adapter;

	private ProgressDialog loadingDialog;
	private int currentScrollPos;
	private TextView homeTitle;
	private int currentImageWidth;
	private boolean isGoingRestaurant = false;
	private Boolean defaultBlank = false;
   // public ImageLoader imageLoader; 
	private boolean isInitial = false;
	
	private LinearLayout mCarouselContainer;

	public Home() {
		super();
	}

	public Home(int titleRes) {
		super(titleRes);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			
			
	       // imageLoader=new ImageLoader(getApplicationContext());

			ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());
			
			//If there is a guest token or customer token, go to HOME
			String customerToken = myPrefs.getString(Keys.CUSTOMER_TOKEN);
			
			if (customerToken != null && customerToken.length() > 0){
				AppActions.add("Home - On Create - Using as Guest");

			}else{
				AppActions.add("Home - On Create - Using as Customer");

			}
			
			super.onCreate(savedInstanceState);
			setContentView(R.layout.home);
			
			theView = (RelativeLayout) findViewById(R.id.home_layout);
			mCarouselContainer = (LinearLayout) findViewById(R.id.carousel);
			theView.setAnimation(AnimationUtils.loadAnimation(this,
					R.anim.login_fade_in));
			
			homeTitle = (TextView) findViewById(R.id.home_title);
			homeTitle.setTypeface(ArcMobileApp.getLatoLightTypeface());
		
			
			typesListView = (ListView) findViewById(R.id.listView1);



			 
		
			
			boolean didLogOut = getIntent().getBooleanExtra(Constants.LOGGED_OUT, false);
			
			if (didLogOut){
				toastShort("Logout Successful!  You may continue to use dono as a guest.");
			}
			
			/*
			defaultLocationCheckBox = (CheckBox) findViewById(R.id.checkBox1);
			defaultLocationCheckBox.setTypeface(ArcMobileApp.getLatoLightTypeface());
			
			if (defaultBlank){
				defaultLocationCheckBox.setChecked(false);
			}else{
				defaultLocationCheckBox.setChecked(true);

			}
			*/
			setActionBarTitle("Locations");

						
			
			try{
				String isInit =  (String) getIntent().getSerializableExtra(Constants.IS_INIT);
				if (isInit != null && isInit.equalsIgnoreCase("yes")){
					isInitial = true;
				}

			}catch(Exception e){
				
			}
			
		
			DataSingleton single = DataSingleton.getInstance();
			
			if (single.merchantsArray.size() > 0){
				
				merchants = new ArrayList<MerchantObject>(single.merchantsArray);
				
				populateListView();
				registerClickCallback();
				
			}else{
				
				loadingDialog = new ProgressDialog(Home.this);
				loadingDialog.setTitle("Finding Nearby Locations");
				loadingDialog.setMessage("Please Wait...");
				loadingDialog.setCancelable(false);
				loadingDialog.show();
				
				
				getMerchantsFromWeb();
			}
			
				
				
				
		} catch (NotFoundException e) {
			(new CreateClientLogTask("Home.onCreate", "Exception Caught", "error", e)).execute();

		}

		
	}
	

	@Override
	public void onBackPressed() {
		
		DataSingleton single = DataSingleton.getInstance();
		single.merchantsArray = new ArrayList<MerchantObject>();
		
		super.onBackPressed();
	}
	
	
	
	protected void getMerchantsFromWeb() {
		try {
			GetMerchantsTask getMerchantsTask = new GetMerchantsTask(getApplicationContext()) {

				@Override
				protected void onPostExecute(Void result) {
					try {
						super.onPostExecute(result);
						
						int errorCode = getErrorCode();

						merchants = new ArrayList<MerchantObject>();
						
						merchants = getMerchants();
						
						
						loadingDialog.hide();
						if (merchants != null && merchants.size() > 0){
						
							
							DataSingleton singleton = DataSingleton.getInstance();
							singleton.merchantsArray = new ArrayList<MerchantObject>(merchants);
							
							ArcMobileApp.setAllMerchants(merchants);
							
							AppActions.add("Home - Get Merchants Succeeded - Number Of Merchants:" + merchants.size());

							MerchantObject merchant = merchants.get(0);
							
						
							populateListView();
							registerClickCallback();
							
							
							if (isInitial){
								isInitial = false;
								 ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());
									if (myPrefs.getString(Keys.DEFAULT_CHURCH_ID) != null && myPrefs.getString(Keys.DEFAULT_CHURCH_ID).length() > 0){
										
										for (int i = 0; i < merchants.size(); i++){
											
											MerchantObject tmpObject = merchants.get(i);
											
											if (tmpObject.merchantId.equalsIgnoreCase(myPrefs.getString(Keys.DEFAULT_CHURCH_ID))){
												
												Intent single = new Intent(getContext(), DefaultLocation.class);
												single.putExtra(Constants.VENUE, tmpObject);
												startActivity(single);
												
												break;
											}

										}
										
									}
									
							}
					 		
							
							
							
										        
						}else{
							AppActions.add("Home - Get Merchants Failed - Error Code:" + errorCode);

				

							
							if (errorCode != 0){
								
								String errorMsg = "";
								
								if(errorCode == 999) {
					                errorMsg = "Can not find nearby locations.";
					            } else {
					                errorMsg = ErrorCodes.ARC_ERROR_MSG;
					            }
								
								
								
								toastShort(errorMsg);
								
							}else{
								toastShort("Error retrieving nearby locations.");

							}


						}
					} catch (Exception e) {
						
				
						
						toastShort("Error retrieving nearby locations.");

						(new CreateClientLogTask("Home.getMerchantsFromWeb.onPostExecute", "Exception Caught", "error", e)).execute();

					}

				}
				
			};
			getMerchantsTask.execute();
		} catch (Exception e) {
			(new CreateClientLogTask("Home.getMerchantsFromWeb", "Exception Caught", "error", e)).execute();

		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//getTokensFromWeb();
		isGoingRestaurant = false;		
		
		
	
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}
	

	

	
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
	    try {
			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
			    bitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);
 
			//final int color = 0xff424242;
			final int color = Color.BLACK;
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			final RectF rectF = new RectF(rect);
			final float roundPx = 5;
 
			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
 
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect, paint);
 
			return output;
		} catch (Exception e) {
			(new CreateClientLogTask("Home.getRoundedCornerBitimap", "Exception Caught", "error", e)).execute();
			return null;

		}
	  }
	
	


	


	
	
	private void getImageForIndex(int index, String merchantId){
		
		try{
			
			AppActions.add("Home - Get Image For Index: + " + index + " and MerchantId: " + merchantId);

			
			
			HomeXMLTask imageTask = new HomeXMLTask(index) {
				@Override
		        protected void onPostExecute(Bitmap result) {
					
					try{
						if (result != null){
							int theIndex = getCurrentIndex();
							
							View myView = adapter.getView(getCurrentIndex(), null, null);
							
							ImageView itemImage = (ImageView) myView.findViewById(R.id.imageView1);
														
							itemImage.setImageBitmap(result);
						}
					}catch(Exception e){
						(new CreateClientLogTask("Home.getImageForIndex.onPostExecute", "Exception Caught", "error", e)).execute();

					}
					
					

				}
			};
			
			String url = "http://arc.dagher.mobi/Images/App/Logos/"+merchantId+ ".jpg";
			imageTask.execute(new String[] { url });
		}catch(Exception e){
			(new CreateClientLogTask("Home.getImageForIndex", "Exception Caught", "error", e)).execute();

		}
	
	}
	
	private class HomeXMLTask extends AsyncTask<String, Void, Bitmap> {
		
		private int mcurrentIndex;
		
		public int getCurrentIndex(){
			return mcurrentIndex;
		}
		
		public HomeXMLTask(int index) {
			super();
			mcurrentIndex = index;
		}
		
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
                    //helpImage.setImageBitmap(result);
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
	
	
	private void populateListView() {
		adapter = new MyListAdapter();
		ListView list = (ListView) findViewById(R.id.listView1);
		list.setAdapter(adapter);
	}
	
	
	
	private void registerClickCallback() {
		
		
		try {
			
			typesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View viewClicked,
						int position, long id) {
					
					try {

						String name = "";
						String theId = "";
						
						name = merchants.get(position).merchantName;
						theId = merchants.get(position).merchantId;

						AppActions.add("Home -  Item Clicked - Index:" + position + ", Merchant Name:" + name);

						if (loadingDialog != null){
							loadingDialog.dismiss();
						}

						
						MerchantObject myMerchant = merchants.get(position);
						
						
						if (!isGoingRestaurant){
							isGoingRestaurant = true;
						
							
							if (myMerchant.donationTypes.size() > 1){
								
								Intent multiple = new Intent(getApplicationContext(), ChurchDonationSelector.class);
								multiple.putExtra(Constants.VENUE, myMerchant);
								startActivity(multiple);
								
							}else{
								
								Intent single = new Intent(getApplicationContext(), ChurchDonationTypeSingle.class);
								single.putExtra(Constants.VENUE, myMerchant);
								startActivity(single);
								
							}
							
						}
	
					
					} catch (Exception e) {

						(new CreateClientLogTask("Home.onRegisterClickCallBack.onItemClick", "Exception Caught", "error", e)).execute();

					}
					
				
				}
			});
			
		
		} catch (Exception e) {

			(new CreateClientLogTask("Home.registerClickCallback", "Exception Caught", "error", e)).execute();

		}
	}


	private class MyListAdapter extends ArrayAdapter<MerchantObject> {
		public MyListAdapter() {
			super(Home.this, R.layout.merchant_row, merchants);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Make sure we have a view to work with (may have been given null)
			try {
				
			
			View itemView = convertView;
			if (itemView == null) {
				itemView = getLayoutInflater().inflate(R.layout.merchant_row, parent, false);
			}
			
			// Find the car to work with.
			MerchantObject currentItem = merchants.get(position);
			
			
			// Amount
			
			TextView name = (TextView) itemView.findViewById(R.id.donation_description);
			name.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			name.setText(currentItem.merchantName);
		
			TextView address = (TextView) itemView.findViewById(R.id.p_date);
			address.setTypeface(ArcMobileApp.getLatoLightTypeface());
			address.setText(currentItem.merchantAddress);

			//Logger.d("Current Id: " + currentItem.merchantId);
			
			ImageView image = (ImageView) itemView.findViewById(R.id.imageView1);
			
			if (currentItem.merchantId.equalsIgnoreCase("16")){
				 
				image.setImageResource(R.drawable.sixteen);
	            
			} else if (currentItem.merchantId.equalsIgnoreCase("17")){
			 
				image.setImageResource(R.drawable.seventeen);

			} else if (currentItem.merchantId.equalsIgnoreCase("15")){
			 
				image.setImageResource(R.drawable.fifteen);

			} else if (currentItem.merchantId.equalsIgnoreCase("21")){
			 
				image.setImageResource(R.drawable.twentyone);

			} else if (currentItem.merchantId.equalsIgnoreCase("20")){
			 
				image.setImageResource(R.drawable.twenty);

			}else{
			 
				String url = "http://arc.dagher.mobi/Images/App/Promo/"+currentItem.merchantId+ ".png";
		        ArcMobileApp.imageLoader.DisplayImage(url, image);
			}
			
			
			
			
			

			//getImageForIndex(position, currentItem.merchantId);
	
			
			return itemView;
			
			
		} catch (Exception e) {
		
			(new CreateClientLogTask("Home.MyListAdapter.getView", "Exception Caught", "error", e)).execute();
			return convertView;

		}
	}				
}




}
