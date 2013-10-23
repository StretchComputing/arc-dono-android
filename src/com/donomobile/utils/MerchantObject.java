package com.donomobile.utils;

import java.io.Serializable;
import java.util.ArrayList;

public class MerchantObject implements Serializable {

	private static final long serialVersionUID = 5406815055461255727L;

	
	public String merchantName;
	public String merchantId;
	public String merchantAddress;
	public ArrayList<DonationTypeObject> donationTypes;
	public double donationAmount;
	public String invoiceId;
	
	public MerchantObject() {
		// TODO Auto-generated constructor stub
		merchantName = "";
		merchantId = "";
		merchantAddress = "";
		donationTypes = new ArrayList<DonationTypeObject>();
		donationAmount = 0.0;
		invoiceId = "";
	}

}
