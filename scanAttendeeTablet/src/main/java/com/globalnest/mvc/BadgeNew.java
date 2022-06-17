//  ScanAttendee Android
//  Created by Ajay on Oct 13, 2015
//  This class is used to get the all the orders and tickets information from backend.
//  Copyright (c) 2014 Globalnest. All rights reserved

/**
 * 
 */
package com.globalnest.mvc;

import java.io.Serializable;

/**
 * @author laxmanamurthy
 *
 */
public class BadgeNew implements Serializable{
 
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String Name="",Data__c="",Description__c="",Id="",Module__c="",event_id="",Double_sided_badge_Option__c="";
	public boolean is_selected=false,Double_sided_badge__c=false;
	public String ScanAttendeeDefaultBadge__c="";
}
