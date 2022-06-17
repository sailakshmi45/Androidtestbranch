package com.globalnest.mvc;

import com.globalnest.objects.CountriesObj;
import com.globalnest.objects.EventObjects;
import com.globalnest.objects.ItemType;
import com.globalnest.objects.PaymentType;
import com.globalnest.objects.UserObjects;

import java.util.ArrayList;
import java.util.List;

public class EventsRefreshObject {
	
	
	 public UserObjects Profile = new UserObjects();
	  public List<EventObjects> Events = new ArrayList<EventObjects>();
	  public List<EventObjects> UpcomingEvents = new ArrayList<EventObjects>();
	  public List<CountriesObj> CountriesList = new ArrayList<CountriesObj>();
	  public List<ItemType> ItemTypes = new ArrayList<ItemType>();
	  public List<PaymentType> PayGateways = new ArrayList<PaymentType>();
	  public String StripeEventdexClientID ="",StripeEventdexSecretKey="";
	  /* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EventsRefreshObject [Profile=" + Profile + ", Events=" + Events
				+ ", UpcomingEvents=" + UpcomingEvents + ", CountriesList="
				+ CountriesList + ", ItemTypes=" + ItemTypes + ",PayGateways="+PayGateways
				+",StripeEventdexClientID="+StripeEventdexClientID+",StripeEventdexSecretKey="+StripeEventdexSecretKey+"]";
	}
	  
	  
	  

}
