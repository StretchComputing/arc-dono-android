package com.donomobile.fragments;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.donomobile.ArcMobileApp;
import com.donomobile.activities.DefaultLocation;
import com.donomobile.activities.Funds;
import com.donomobile.activities.Home;
import com.donomobile.activities.Support;
import com.donomobile.activities.UserProfile;
import com.donomobile.utils.ArcPreferences;
import com.donomobile.utils.Constants;
import com.donomobile.utils.Enums.ModernPicTypes;
import com.donomobile.utils.Keys;
import com.donomobile.utils.Logger;
import com.donomobile.utils.MerchantObject;
import com.donomobile.utils.Utils;
import com.donomobile.web.rskybox.AppActions;
import com.donomobile.web.rskybox.CreateClientLogTask;
import com.donomobileapp.R;

public class MenuListFragment extends ListFragment {
	
	public MenuAdapter adapter;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		try {
			super.onActivityCreated(savedInstanceState);
			adapter = new MenuAdapter(getActivity());
			adapter.add(new MenuItem(Utils.convertModernPicType(ModernPicTypes.World), "Home"));
			adapter.add(new MenuItem(Utils.convertModernPicType(ModernPicTypes.Guy), "My Profile"));
			adapter.add(new MenuItem(Utils.convertModernPicType(ModernPicTypes.Dollar), "Payment"));
			adapter.add(new MenuItem(Utils.convertModernPicType(ModernPicTypes.Question), "Settings"));
			adapter.add(new MenuItem(Utils.convertModernPicType(ModernPicTypes.Question), "All Locations"));

			//adapter.add(new MenuItem(Utils.convertModernPicType(ModernPicTypes.Girl), "Social"));
			setListAdapter(adapter);
		} catch (Exception e) {
			(new CreateClientLogTask("MenuListFragment.onActivityCreated", "Exception Caught", "error", e)).execute();

		}
	}

//	private class SampleItem {
//		public String tag;
//		public int iconRes;
//		public SampleItem(String tag, int iconRes) {
//			this.tag = tag; 
//			this.iconRes = iconRes;
//		}
//	}
	
	private class MenuItem {
		public String icon;
		public String text;
		public MenuItem(String icon, String text) {
			this.icon = icon; 
			this.text = text;
		}
	}

	public class MenuAdapter extends ArrayAdapter<MenuItem> {

		public MenuAdapter(Context context) {
			super(context, 0);
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			try {
				if (convertView == null) {
					convertView = LayoutInflater.from(getContext()).inflate(R.layout.menu_row, null);
				}
//			ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
//			icon.setImageResource(getItem(position).iconRes);
				
				//ImageView icon = (ImageView) convertView.findViewById(R.id.iconImage);
				/*
				if (getItem(position).text.equals("Home")){
					icon.setImageResource(R.drawable.menuhome);
					LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) icon.getLayoutParams();
					params.setMargins(10, 10, 25, 0);

				}else if (getItem(position).text.equals("Profile")){
					icon.setImageResource(R.drawable.profiledefault);
					LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) icon.getLayoutParams();
					params.setMargins(0, 10, 15, 0);
				}else if (getItem(position).text.equals("Payment")){
					icon.setImageResource(R.drawable.menupayment);
					LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) icon.getLayoutParams();
					params.setMargins(10, 10, 25, 0);
				}else if (getItem(position).text.equals("Support")){
					icon.setImageResource(R.drawable.menusupport);
					LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) icon.getLayoutParams();
					params.setMargins(10, 10, 25, 0);
				}
				*/
				//TextView icon = (TextView) convertView.findViewById(R.id.row_icon);
				//icon.setText(getItem(position).icon);
				//icon.setTextSize(35);
				//icon.setTypeface(ArcMobileApp.getModernPicsTypeface());
				TextView title = (TextView) convertView.findViewById(R.id.row_title);
				title.setText(getItem(position).text);
				title.setTypeface(ArcMobileApp.getLatoLightTypeface());
				convertView.setOnTouchListener(new OnTouchListener() {
					
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						onMenuTouch(position);		
						return false;  //consume the view?
					}
				});

				return convertView;
			} catch (Exception e) {
				(new CreateClientLogTask("MenuListFragment.getView", "Exception Caught", "error", e)).execute();
				return null;
			}
		}
		
		private void onMenuTouch(int position) {
			try {
				switch(position) {
				case 1:
					goToProfile();	
					break;
				case 0:
					goDefault();
					break;
				case 2:
					goToFunds();				
					break;
				case 3:
					//showInfoDialog();
					goAboutScreen();
					break;
				case 4:
					goHome();
					break;
				}
			} catch (Exception e) {
				(new CreateClientLogTask("MenuListFragment.onMenuTouch", "Exception Caught", "error", e)).execute();

			}
			
		}
		
		private void goDefault(){
			
			try{
				

				ArcPreferences myPrefs = new ArcPreferences(getContext());

				if (myPrefs.getString(Keys.DEFAULT_CHURCH_ID) != null && myPrefs.getString(Keys.DEFAULT_CHURCH_ID).length() > 0){
					
					//There is a Default
					
					ArrayList<MerchantObject> myList = new ArrayList<MerchantObject>(ArcMobileApp.getAllMerchants());
					

					if (myList.size() > 0){
						

						Boolean isFound = false;
						for (int i = 0; i < myList.size(); i++){
							

							MerchantObject tmp = myList.get(i);

							String defaultId = myPrefs.getString(Keys.DEFAULT_CHURCH_ID);
							
							if (defaultId.equalsIgnoreCase(tmp.merchantId)){
								
								goDefault(tmp);
								isFound = true;
								break;
							}
							
						}
						
						if (!isFound){
							goHome();

						}
						
						
					}else{

						goHome();
					}
					
					
				}else{

					goHome();
				}
				

			}catch (Exception e){

				goHome();
			}
		}
		
		
		private void goDefault(MerchantObject merchObject){
			
			Intent single = new Intent(getContext(), DefaultLocation.class);
			single.putExtra(Constants.VENUE, merchObject);
			single.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			startActivity(single);
		}
		private void goToProfile(){
			AppActions.add("Left Menu - Profile Clicked");
			Intent funds = (new Intent(getContext(), UserProfile.class));
			startActivity(funds);
		}
		private void goHome() {
			AppActions.add("Left Menu - Home Clicked");

			Intent home = (new Intent(getContext(), Home.class));
			home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(home);
		}
		
		private void goToFunds() {
			AppActions.add("Left Menu - Payment Clicked");

			Intent funds = (new Intent(getContext(), Funds.class));
			startActivity(funds);
		}
		
		private void goToSocial() {
			
		}
		
		private void goAboutScreen(){
			AppActions.add("Left Menu - Support Clicked");

			Intent about = (new Intent(getContext(), Support.class));
			startActivity(about);
			
		}
		private void showInfoDialog() {
			AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
			builder.setTitle(getString(R.string.app_dialog_title));
			builder.setMessage("Thanks for using Arc, the simplest way to pay your restaurant bill.");
			//builder.setIcon(R.drawable.logo);
			builder.setPositiveButton("ok",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					});
			builder.create().show();
		}

	}
	
	
}
