package com.donomobile.fragments.anim;

import android.graphics.Canvas;
import android.view.animation.Interpolator;

import com.donomobile.utils.Logger;
import com.donomobile.web.rskybox.CreateClientLogTask;
import com.donomobileapp.R;
import com.slidingmenu.lib.SlidingMenu.CanvasTransformer;

public class CustomSlideAnimation extends CustomAnimation {
	
	private static Interpolator interp = new Interpolator() {
		@Override
		public float getInterpolation(float t) {
			try {
				Logger.d("********************************** ANIMATING2");

				t -= 1.0f;
				return t * t * t + 1.0f;
			} catch (Exception e) {
				(new CreateClientLogTask("CustomSlideAnimation.getInterpolation", "Exception Caught", "error", e)).execute();
				return 0.0f;

			}
		}		
	};

	public CustomSlideAnimation() {
		// see the class CustomAnimation for how to attach 
		// the CanvasTransformer to the SlidingMenu
		super(R.string.anim_slide, new CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {

				try {

					canvas.translate(0, canvas.getHeight()*(1-interp.getInterpolation(percentOpen)));
				} catch (Exception e) {
					(new CreateClientLogTask("CustomSlideAnimation.transformCanvas", "Exception Caught", "error", e)).execute();

				}
			}			
		});
	}

}
