package com.donomobile.utils;

import java.util.ArrayList;


public class DataSingleton {
	private static volatile DataSingleton instance = null;
 
	public ArrayList<MerchantObject> merchantsArray;
	
	private DataSingleton() {	
		merchantsArray = new ArrayList<MerchantObject>();
	}
 
	public static DataSingleton getInstance() {
		if (instance == null) {
                        synchronized (DataSingleton .class){
			        if (instance == null) {
                                        instance = new DataSingleton ();
                                }
                        }
		}
		return instance;
	}
}

