package com.donomobile.fragments.anim;

import android.graphics.Canvas;

import com.donomobile.utils.Logger;
import com.donomobile.web.rskybox.CreateClientLogTask;
import com.donomobileapp.R;
import com.slidingmenu.lib.SlidingMenu.CanvasTransformer;

public class CustomScaleAnimation extends CustomAnimation {

	public CustomScaleAnimation() {
		super(R.string.anim_scale, new CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				try {
					canvas.scale(percentOpen, 1, 0, 0);
				} catch (Exception e) {
					(new CreateClientLogTask("CustomScaleAnimation.transformCanvas", "Exception Caught", "error", e)).execute();

				}
			}			
		});
	}

}
