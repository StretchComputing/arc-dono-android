package com.donomobile.utils;

import java.io.Serializable;
import java.util.ArrayList;

public class MerchantObject implements Serializable {

	private static final long serialVersionUID = 5406815055461255727L;

	
	public String merchantName;
	public String merchantId;
	public String merchantAddress;
	public String merchantCity;
	public String merchantState;

	public ArrayList<DonationTypeObject> donationTypes;
	public double donationAmount;
	public String invoiceId;
	
	public double quickDonateOne;
	public double quickDonateTwo;
	public double quickDonateThree;
	public double quickDonateFour;
	
	public Boolean chargeFee;
	public double convenienceFee;
	public double convenienceFeeCap;
	
	public MerchantObject() {
		// TODO Auto-generated constructor stub
		merchantName = "";
		merchantId = "";
		merchantAddress = "";
		merchantCity = "";
		merchantState = "";

		donationTypes = new ArrayList<DonationTypeObject>();
		donationAmount = 0.0;
		invoiceId = "";
		
		quickDonateOne = 0.0;
		quickDonateTwo = 0.0;
		quickDonateThree = 0.0;
		quickDonateFour = 0.0;
		
		chargeFee = false;
		convenienceFee = 0.0;
		convenienceFeeCap = 0.0;
		
		

	}

}
