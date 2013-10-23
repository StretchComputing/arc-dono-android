package com.donomobile.fragments.anim;

import android.os.Bundle;

import com.donomobile.BaseActivity;
import com.donomobile.fragments.MenuListFragment;
import com.donomobile.web.rskybox.CreateClientLogTask;
import com.donomobileapp.R;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.CanvasTransformer;

public abstract class CustomAnimation extends BaseActivity {
	
	private CanvasTransformer mTransformer;
	
	public CustomAnimation(int titleRes, CanvasTransformer transformer) {
		super(titleRes);
		mTransformer = transformer;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			// set the Above View
			setContentView(R.layout.content_frame);
			getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.content_frame, new MenuListFragment())
			.commit();
			
			SlidingMenu sm = getSlidingMenu();
			setSlidingActionBarEnabled(true);
			sm.setBehindScrollScale(0.0f);
			sm.setBehindCanvasTransformer(mTransformer);
		} catch (Exception e) {
			(new CreateClientLogTask("CustomAnimation.onCreate", "Exception Caught", "error", e)).execute();

		}
	}

}
