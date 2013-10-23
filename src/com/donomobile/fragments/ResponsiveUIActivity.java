package com.donomobile.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;

import com.actionbarsherlock.view.MenuItem;
import com.donomobile.BaseActivity;
import com.donomobile.web.rskybox.CreateClientLogTask;
import com.donomobileapp.R;
import com.slidingmenu.lib.SlidingMenu;

/**
 * This activity is an example of a responsive Android UI.
 * On phones, the SlidingMenu will be enabled only in portrait mode.
 * In landscape mode, it will present itself as a dual pane layout.
 * On tablets, it will will do the same general thing. In portrait
 * mode, it will enable the SlidingMenu, and in landscape mode, it
 * will be a dual pane layout.
 */

public class ResponsiveUIActivity extends BaseActivity {

	public ResponsiveUIActivity(int titleRes) {
		super(titleRes);
	}

	private Fragment mContent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setTitle(R.string.responsive_ui);

			setContentView(R.layout.responsive_content_frame);

			// check if the content frame contains the menu frame
			if (findViewById(R.id.menu_frame) == null) {
				setBehindContentView(R.layout.menu_frame);
				getSlidingMenu().setSlidingEnabled(true);
				getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
				// show home as up so we can toggle
				getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			} else {
				// add a dummy view
				View v = new View(this);
				setBehindContentView(v);
				getSlidingMenu().setSlidingEnabled(false);
				getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
			}

			// set the Above View Fragment
			if (savedInstanceState != null)
				mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
			if (mContent == null) {
				mContent = new ColorFragment();
				Bundle args = new Bundle();
				args.putInt(ColorFragment.COLOR_KEY, R.color.red);
				mContent.setArguments(args);
			}	
			getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.content_frame, mContent)
			.commit();

			// set the Behind View Fragment
			getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.menu_frame, new ColorFragment())
			.commit();

			// customize the SlidingMenu
			SlidingMenu sm = getSlidingMenu();
			sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
			sm.setShadowWidthRes(R.dimen.shadow_width);
			sm.setShadowDrawable(R.drawable.shadow);
			sm.setBehindScrollScale(0.25f);
			sm.setFadeDegree(0.25f);

			// show the explanation dialog
//		if (savedInstanceState == null)
//			new AlertDialog.Builder(this)
//			.setTitle(R.string.what_is_this)
//			.setMessage(R.string.responsive_explanation)
//			.show();
		} catch (Exception e) {
			(new CreateClientLogTask("ResponsiveUIActivity.onCreate", "Exception Caught", "error", e)).execute();

		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
	}

	public void switchContent(final Fragment fragment) {
		try {
			mContent = fragment;
			getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.content_frame, fragment)
			.commit();
			Handler h = new Handler();
			h.postDelayed(new Runnable() {
				public void run() {
					getSlidingMenu().showContent();
				}
			}, 50);
		} catch (Exception e) {
			(new CreateClientLogTask("ResponsiveUIActivity.switchContent", "Exception Caught", "error", e)).execute();

		}
	}	

//	public void onBirdPressed(int pos) {
//		Intent intent = BirdActivity.newInstance(this, pos);
//		startActivity(intent);
//	}

}
