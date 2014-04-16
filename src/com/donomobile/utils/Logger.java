package com.donomobile.utils;

import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.donomobile.ArcMobileApp;

public final class Logger {
	
	private static final String DEFAULT_TAG = "ARC_MOBILE";
	private static final Boolean ShouldDebug = true;
	
	// PRINT DEBUG MESSAGES TO LOGCAT
	public static final void d(String tag, String message) {
		
		ApplicationInfo applicationInfo = ArcMobileApp.getContext().getApplicationInfo();
		boolean isDebuggable = (0 != (applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE));

		if (ShouldDebug && isDebuggable){
			Log.d(tag, message);

		}
	}
	
	public static final void d(String message) {
		if (ShouldDebug){
			d(DEFAULT_TAG, message);
		}
	}
	
	// PRINT ERRORS TO LOGCAT
	public static final void e(String tag, String message) {
		if (ShouldDebug){
			Log.e(tag, message);
		}
	}
	
	public static final void e(String message) {
		if (ShouldDebug){
			e(DEFAULT_TAG, message);
		}
	}
	
	// PRINT INFO TO LOGCAT
	public static final void i(String tag, String message) {
		if (ShouldDebug){
			Log.i(tag, message);
		}
	}
	
	public static final void i(String message) {
		if (ShouldDebug){
			i(DEFAULT_TAG, message);
		}
	}
	
}
