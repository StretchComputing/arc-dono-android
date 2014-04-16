package com.donomobile.utils;

import java.io.Serializable;


public class RecurringDonationObject implements Serializable {

	private static final long serialVersionUID = 6406810554461255727L;

	public String scheduleId;
	public int merchantId;
	public int invoiceId;
	public String type;
	public int value;
	public int xOfMonth;
	public String ccToken;
	public double amount;
	public double gratuity;
	public String nextDate;


	
	public RecurringDonationObject() {
		// TODO Auto-generated constructor stub
		

		scheduleId = "";
		merchantId = 0;
		invoiceId = 0;
		type = "";
		value = 0;
		xOfMonth = 0;
		ccToken = "";
		amount = 0.0;
		gratuity = 0.0;
		nextDate = "";
		
		
	}

}