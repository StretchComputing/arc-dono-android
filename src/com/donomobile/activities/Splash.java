package com.donomobile.activities;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.donomobile.utils.ArcPreferences;
import com.donomobile.utils.Constants;
import com.donomobile.utils.Keys;
import com.donomobile.utils.Logger;
import com.donomobile.web.rskybox.AppActions;
import com.donomobile.web.rskybox.CreateClientLogTask;
import com.donomobileapp.R;

public class Splash extends Activity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {

			super.onCreate(savedInstanceState);
			setContentView(R.layout.splash);
			    
			Logger.d("ENTERING SPLASH AREA1");

			
			final ImageView logo = (ImageView) findViewById(R.id.logo);
			logo.setAnimation(AnimationUtils.loadAnimation(this, R.anim.logo_animation));
			logo.getAnimation().setAnimationListener(new AnimationListener() {
				
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub
					
				}
				
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
										
				}

				public void onAnimationEnd(Animation animation) {
					
					try {
						logo.setVisibility(View.GONE);
						Logger.d("ENTERING SPLASH AREA");

						ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());
						
						//If there is a guest token or customer token, go to HOME
						String guestToken = myPrefs.getString(Keys.GUEST_TOKEN);
						String customerToken = myPrefs.getString(Keys.CUSTOMER_TOKEN);
						Boolean hasAgreed = myPrefs.getBoolean(Keys.AGREED_TERMS);

						if(customerToken == null || customerToken.length() == 0){
							//Go to initPage
							Logger.d("TOKEN IS NULL");

							AppActions.add("Splash - No Tokens Found - Going to Init");

							if (guestToken != null && guestToken.length() > 0){
								toast("Dono no longer supports using the app as a guest.  Please log in or register to continue.  We apologize for any inconvenience", Toast.LENGTH_LONG);
								myPrefs.putAndCommitString(Keys.GUEST_TOKEN, "");
							}
							startActivity(new Intent(getApplicationContext(), InitActivity.class));

						}else{
							//Go Home
							Logger.d("Customer Token: " + customerToken);
							
							AppActions.add("Splash - Token Found - Going Home");

							Intent home = new Intent(getApplicationContext(), Home.class);
							home.putExtra(Constants.IS_INIT, "yes");
							startActivity(home);
							

						}
						overridePendingTransition(0, 0);
						finish();
					} catch (Exception e) {
						(new CreateClientLogTask("Splash.onAnimationEnd", "Exception Caught", "error", e)).execute();

					}
				}
			});
			
			
		} catch (NotFoundException e) {
			(new CreateClientLogTask("Splash.onCreate", "Exception Caught", "error", e)).execute();

		}
	}

	
	private void toast(String message, int duration) {
		Toast.makeText(getApplicationContext(), message, duration).show();
		Toast.makeText(getApplicationContext(), message, duration).show();

	}
	
}



