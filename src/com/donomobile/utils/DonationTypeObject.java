package com.donomobile.utils;

import java.io.Serializable;
import java.util.ArrayList;

public class DonationTypeObject implements Serializable {

	private static final long serialVersionUID = 4406815055461255727L;
	
	public String donationId;
	public Boolean shouldDisplay;
	public String description;
	public double percentPaying;
	public Boolean isSelected;
	
	public DonationTypeObject() {

		donationId = "";
		description = "";
		shouldDisplay = false;
		percentPaying = 0.0;
		isSelected = false;
	}

}
