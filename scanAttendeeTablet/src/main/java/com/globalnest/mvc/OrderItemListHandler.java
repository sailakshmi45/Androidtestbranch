// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc

package com.globalnest.mvc;

import com.globalnest.utils.Util;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

// Referenced classes of package com.globalnest.mvc:
//            BuyerInfoHandler

public class OrderItemListHandler implements Serializable {
	/**
	 *
	 */
	private static  long serialVersionUID = 1L;

	@SerializedName("BLN_GN_User__c")
	private String GNUserId;

	@SerializedName("Ticket_Status__c")
	public String TicketStatus;

	@SerializedName("RSVP__c")
	public String RSVPstatus;

	@SerializedName("Client_Company__c")
	public String company_id;

	@SerializedName("Client_GN_User__r")
	public BuyerInfoHandler attendeeInfo;

	@SerializedName("Badge_Label__c")
	private String badgeLabel;

	@SerializedName("Company__c")
	private String companyName;

	@SerializedName("Email__c")
	private String email;

	@SerializedName("Event__c")
	private String eventId;

	@SerializedName("First_Name__c")
	private String firstName;

	@SerializedName("User_Pic__c")
	private String UserImage;

	@SerializedName("Id")
	private String id;

	@SerializedName("Item__c")
	private String itemId;

	@SerializedName("Item_Pool__c")
	private String itemPoolId;

	@SerializedName("Item_Type__c")
	private String itemTypeId;

	@SerializedName("Last_Name__c")
	private String lastName;

	@SerializedName("Mobile__c")
	private String mobile;

	@SerializedName("Order__c")
	private String orderId;

	@SerializedName("Order_Item__c")
	private String orderItemId;

	@SerializedName("Name")
	private String ticketNumber;

	@SerializedName("Badge_ID__c")
	private String badgeId="";

	@SerializedName("LastModifiedDate")
	private String chickindate;

	//newly added tag

	@SerializedName("Note__c")
	private String note;

	@SerializedName("Tag_No__c")
	private String seatno;

	@SerializedName("TKT_Job_Title__c")
	private String designation;

	@SerializedName("badgeparentid__c")
	private String badgeparent_id;
	@SerializedName("Parent_ID__c")
	private String parent_id;
	@SerializedName("Custom_Barcode__c")
	private String custom_barcode_id;

	@SerializedName("Scan_Id__c")
	private String scan_id;

	public ItemPoor_R Item_Pool__r = new ItemPoor_R();
	public ItemType_R  Item_Type__r = new ItemType_R();
	public BadgeParentId_R badgeparentid__r = new BadgeParentId_R();
	public BadgeID_R Badge_ID__r = new BadgeID_R();
	public TStatus_R Tstatus__r = new TStatus_R();
	/*public String Id;*/

	@SerializedName("Tstatus_Id__r")
	public CheckinStatusHandler checkinhandler;

	public TicketProfileController tkt_profile__r = new TicketProfileController();

	public int totalSize=0;
	private String ticket_name="",item_type="";
	public OrderItemListHandler() {
		attendeeInfo = new BuyerInfoHandler();
		//checkinhandler = new CheckinStatusHandler();
	}


	//for Badge Lable Fields in Registration form

	public String tag="";


	public String getBadgeLabel() {
		return badgeLabel;
	}

	public String getCompanyName() {
		return companyName;
	}

	public String getEmail() {
		return email;
	}

	public String getEventId() {
		return eventId;
	}

	public String getFirstName() {
		return firstName;
	}
	public String getUserImage() {
		return UserImage;
	}
	public String getGNUserId() {
		return GNUserId;
	}

	public String getId() {
		return id;
	}

	public String getItemId() {
		return itemId;
	}

	public String getItemPoolId() {
		return itemPoolId;
	}

	public String getItemTypeId() {
		return itemTypeId;
	}

	public String getLastName() {
		return lastName;
	}

	public String getMobile() {
		return mobile;
	}

	public String getOrderId() {
		return orderId;
	}

	public String getOrderItemId() {
		return orderItemId;
	}

	public String getTicketNumber() {
		return ticketNumber;
	}

	public String getTicketStatus() {
		return TicketStatus;
	}

	public String getRSVPStatus() {
		return RSVPstatus;
	}
	public void setBadgeLabel(String s) {
		badgeLabel = s;
	}

	public void setCompanyName(String s) {
		companyName = s;
	}

	public void setEmail(String s) {
		email = s;
	}

	public void setEventId(String s) {
		eventId = s;
	}

	public void setFirstName(String s) {
		firstName = s;
	}
	public void setAttendeeImage(String s) {
		UserImage = s;
	}
	public void setGNUserId(String s) {
		GNUserId = s;
	}

	public void setId(String s) {
		id = s;
	}

	public void setItemId(String s) {
		itemId = s;
	}

	public void setItemPoolId(String s) {
		itemPoolId = s;
	}

	public void setItemTypeId(String s) {
		itemTypeId = s;
	}

	public void setLastName(String s) {
		lastName = s;
	}

	public void setMobile(String s) {
		mobile = s;
	}

	public void setOrderId(String s) {
		orderId = s;
	}

	public void setOrderItemId(String s) {
		orderItemId = s;
	}

	public void setTicketNumber(String s) {
		ticketNumber = s;
	}

	public void setTicketStatus(String s) {
		TicketStatus = s;
	}
	public void setRSVPStatus(String s) {
		RSVPstatus = s;
	}
	public String getChickindate() {
		return chickindate;
	}

	public void setChickindate(String chickindate) {
		this.chickindate = chickindate;
	}

	public String getBadgeId() {
		return badgeId;
	}

	public void setBadgeId(String badgeId) {
		this.badgeId = badgeId;
	}
	public void setPackageTicketName(String ticketname){
		this.ticket_name = ticketname;
	}
	public String getPackageTicketName(){
		return ticket_name;
	}
	public void setItemTypeName(String item_type){
		this.item_type = item_type;
	}
	public String getItemTypeName(){
		return item_type;
	}


	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * @return the designation
	 */
	public String getDesignation() {
		return designation;
	}

	/**
	 * @param designation the designation to set
	 */
	public void setDesignation(String designation) {
		this.designation = designation;
	}

	/**
	 * @return the seatno
	 */
	public String getSeatno() {
		return seatno;
	}

	/**
	 * @param seatno the seatno to set
	 */
	public void setSeatno(String seatno) {
		this.seatno = seatno;
	}



	/**
	 * @return the company_id
	 */
	public String getCompany_id() {
		return company_id;
	}

	/**
	 * @param company_id the company_id to set
	 */
	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}



	/**
	 * @return the company_id
	 */
	public String getnote() {
		return note;
	}

	public void setBadgeParentId(String badegparent_id){
		this.badgeparent_id = badegparent_id;
	}
	public String getBadgeParentId(){
		return badgeparent_id;
	}
	public void setTicketParentId(String parent_id){
		this.parent_id = parent_id;
	}
	public String getTicketParentId(){
		return parent_id;
	}
	/**
	 * @param //company_id the company_id to set
	 */
	public void setnote(String note) {
		this.note = note;
	}

	public void setCustomBarCode(String barcode){
		this.custom_barcode_id = Util.NullChecker(barcode);
	}
	public String getCustomBarCode(){
		return Util.NullChecker(custom_barcode_id);
	}
	public void setScanID(String scan_id){
		this.scan_id = scan_id;
	}
	public String getScanID(){
		return scan_id;
	}

	@Override
	public String toString() {
		return "OrderItemListHandler [GNUserId=" + GNUserId + ", TicketStatus="
				+ TicketStatus + ", RSVPstatus=" 													+ RSVPstatus+ ", attendeeInfo=" + attendeeInfo
				+ ", badgeLabel=" + badgeLabel + ", companyName=" + companyName
				+ ", email=" + email + ", eventId=" + eventId + ", firstName="
				+ firstName + ", id=" + id + ", itemId=" + itemId
				+ ", itemPoolId=" + itemPoolId + ", itemTypeId=" + itemTypeId
				+ ", lastName=" + lastName + ", mobile=" + mobile
				+ ", orderId=" + orderId + ", orderItemId=" + orderItemId
				+ ", ticketNumber=" + ticketNumber + ", checkinhandler="
				+ checkinhandler + ", chickindate=" + chickindate +", totalSize="+totalSize
				+ ", badgeId=" + badgeId +", company_id=" + company_id
				+", badgeparent_id="+badgeparent_id+", parent_id="+parent_id
				+", designation=" + designation +", seatno=" + seatno
				+", note=" + note+",Item_Pool__r="+Item_Pool__r
				+",Item_Type__r="+Item_Type__r+",badgeparentid__r"+badgeparentid__r+ "]";


	}

	/*public   String ATTENDEE_TKTPROFILEID = "Tktprofile_Id";
	public  String ATTENDEE_PREFIX = "";
	public   String ATTENDEE_FIRST_NAME = "";
	public   String ATTENDEE_LAST_NAME = "";
	public   String ATTENDEE_SUFFIX = "";
	public   String ATTENDEE_JOB_TILE = "";
	public   String ATTENDEE_MOBILE = "";
	public   String ATTENDEE_PHONE = "";
	public   String ATTENDEE_AGE="";
	public   String ATTENDEE_GENDER="";
	public   String ATTENDEE_DOB="";
	public   String ATTENDEE_PRIMARY_BUSINESS_CATEGORY="";
	public   String ATTENDEE_SECONDARY_BUSINESS_CATEGORY="";
	public   String ATTENDEE_NUMBER_OF_EMPLOYEES="";
	public   String ATTENDEE_ESTABLISHED_DATE="";
	public   String ATTENDEE_REVENUE="";
	public   String ATTENDEE_TAX_ID="";
	public   String ATTENDEE_WEBSITE_URL="";//reg table column name
	public   String ATTENDEE_DUNS_NUMBER="";
	public   String ATTENDEE_BLOG_URL="";
	public   String ATTENDEE_DESCRIPTION="";//reg table column name
	public   String ATTENDEE_KEYWORDS="";
	public   String ATTENDEE_EXCEPTIONAL_KEYWORDS="";
	public   String ATTENDEE_DBA="";
	public   String ATTENDEE_BBB_NUMBER="";
	public   String ATTENDEE_GSA_SCHEDULE="";
	public   String ATTENDEE_GEOGRAPHICAL_REGION="";
	public   String ATTENDEE_ETHNICITY="";
	public   String ATTENDEE_BUSINESS_STRUCTURE="";
	public   String ATTENDEE_CAGECODE="";
	public   String ATTENDEE_DISTRIBUTION_COUNTRY="";
	public   String ATTENDEE_FAXNUMBER="";
	public   String ATTENDEE_MANUFACTURES_COUNTRY="";
	public   String ATTENDEE_OUTSIDE_FACILITIES="";
	public   String ATTENDEE_REFERENCES1="";
	public   String ATTENDEE_REFERENCES2="";
	public   String ATTENDEE_SCOPEOFWORK1="";
	public   String ATTENDEE_SCOPEOFWORK2="";
	public   String ATTENDEE_SECONDARY_EMAIL="";
	public   String ATTENDEE_YEAR_IN_BUSINESS="";
	public   String ATTENDEE_TABLE_NUMBER="";
	public   String ATTENDEE_LOCATION_ROOM="";
	public   String ATTENDEE_COMPANY_LOGO = "";
	public   String ATTENDEE_SPEAKERLINKEDINID="";
	public   String ATTENDEE_SPEAKERFACEBOOKID="";
	public   String ATTENDEE_NUMBER_1__C="";
	public   String ATTENDEE_NUMBER_2__C="";
	public   String ATTENDEE_NUMBER_3__C="";
	public   String ATTENDEE_NUMBER_4__C="";
	public   String ATTENDEE_NUMBER_5__C="";
	public   String ATTENDEE_TEXT_1__C="";
	public   String ATTENDEE_TEXT_2__C="";
	public   String ATTENDEE_TEXT_3__C="";
	public   String ATTENDEE_TEXT_4__C="";
	public   String ATTENDEE_TEXT_5__C="";
	public   String ATTENDEE_SPEAKERTWITTERID="";
	public   String ATTENDEE_SPEAKERBLOGGER="";
	public   String ATTENDEE_BIOGRAPHY="";
	public   String ATTENDEE_SPEAKERVIDEO="";
	public   String ATTENDEE_WHATSAPP="";
	public   String ATTENDEE_WECHAT="";
	public   String ATTENDEE_SKYPE="";
	public   String ATTENDEE_SNAPCHAT="";
	public   String ATTENDEE_INSTAGRAM="";


	public   String ATTENDEE_NAICS="";
	public   String ATTENDEE_COMMODITIES="";
	public   String ATTENDEE_SUBCOMMODITIES="";
	public   String ATTENDEE_DIVERSITIES="";













	public static  String ATTENDEE_IMAGE = "User_Pic__c";



	public static  String ATTENDEE_EMAIL_ID = "Email__c";
	public static  String ATTENDEE_COMPANY_ID = "Attendee_Company_Id";
	public static  String ATTENDEE_COMPANY = "TKT_Company__c";//"Attendee_Company";
	public static  String ATTENDEE_TICKET_STATUS = "Ticket_Status__c";
	public static  String ATTENDEE_RSVP_STATUS = "RSVP__c";
	public static  String ATTENDEE_VERIFY_EMAIL = "verify_email__c";




	public static  String ATTENDEE_WORK_PHONE = "Work_Phone__c";
	public static  String ATTENDEE_WORK_ADDRESS_1 = "Address1__c";
	public static  String ATTENDEE_WORK_ADDRESS_2 = "Address2__c";
	public static  String ATTENDEE_WORK_CITY = "Attendee_City";
	public static  String ATTENDEE_WORK_STATE = "Attendee_State";
	public static  String ATTENDEE_WORK_COUNTRY = "Attendee_Country";
	public static  String ATTENDEE_WORK_ZIPCODE = "Attendee_Zipcode";

	public static  String ATTENDEE_HOME_PHONE = "Home_Phone__c";
	public static  String ATTENDEE_HOME_ADDRESS_1 = "Attendee_Home_Address_1";
	public static  String ATTENDEE_HOME_ADDRESS_2 = "Attendee_Home_Address_2";
	public static  String ATTENDEE_HOME_CITY = "Attendee_Home_City";
	public static  String ATTENDEE_HOME_STATE = "Attendee_Home_State";
	public static  String ATTENDEE_HOME_COUNTRY = "Attendee_Home_Country";
	public static  String ATTENDEE_HOME_ZIPCODE = "Attendee_Home_Zipcode";

	public static  String ATTENDEE_BILLING_PHONE = "Billing_Phone__c";
	public static  String ATTENDEE_BILLING_ADDRESS_1 = "Attendee_Billing_Address_1";
	public static  String ATTENDEE_BILLING_ADDRESS_2 = "Attendee_Billing_Address_2";
	public static  String ATTENDEE_BILLING_CITY = "Attendee_Billing_City";
	public static  String ATTENDEE_BILLING_STATE = "Attendee_Billing_State";
	public static  String ATTENDEE_BILLING_COUNTRY = "Attendee_Billing_Country";
	public static  String ATTENDEE_BILLING_ZIPCODE = "Attendee_Billing_Zipcode";

	public static  String ATTENDEE_CHECKEDINDATE = "CheckedInDate";
	public static  String ATTENDEE_EVENT_ID = "Event_Id";
	public static  String ATTENDEE_BADGE_NAME = "Attendee_Badge_Name";
	public static  String ATTENDEE_BADGEID = "BadgeId";
	public static  String ATTENDEE_BADGE_PARENT_ID = "Badge_Parent_Id";
	public static  String ATTENDEE_REASON = "Reason";
	public static  String ATTENDEE_ITEM_ID = "Item_Id";
	public static  String ATTENDEE_ITEM_NAME = "Item_Name";
	public static  String ATTENDEE_ITEM_TYPE_ID = "Item_Type_Id";
	public static  String ATTENDEE_ITEM_POOL_ID = "Item_Pool_Id";
	public static  String ATTENDEE_TIKCET_NUMBER = "Tikcet_Number";
	public static  String ATTENDEE_ORDER_NUMBER= "Order_Id_Number";

	// public static  String ATTENDEE_ISCHECKIN = "isCheckin";
	public static  String ATTENDEE_TICKET_SEAT_NUMBER = "Ticket_Seat_Number";
	public static  String ATTENDEE_ORDER_ID = "Order_Id";
	public static  String ATTENDEE_ORDER_ITEM_ID = "Order_Item_Id";
	public static  String ATTENDEE_BUYER_ID = "buyer_id";
	public static  String ATTENDEE_TAG = "Tag_No__c";
	public static  String ATTENDEE_NOTE = "note";
	// public static  String ATTENDEE_DESIGNATION = "designation";
	public static  String ATTENDEE_BADGE_LABLE = "Badge_Label__c";
	public static  String ATTENDEE_ITEM_PARENT_ID = "Parent_Id";
	public static  String ATTENDEE_ITEMPOOL_NAME = "item_pool_name";
	public static  String ATTENDEE_ITEMTYPE_NAME = "item_type_name";
	public static  String ATTENDEE_BADGABLE = "item_badgable";
	public static  String ATTENDEE_CUSTOM_BARCODE = "custom_barcode";
	public static  String ATTENDEE_UNIQUE_NUMBER = "unique_number";
	public static  String ATTENDEE_BADGE_PRINTSTATUS = "print_status";
	public static  String ATTENDEE_SCANID = "scan_id";*/

	public class ItemPoor_R implements Serializable {

		private final static  long serialVersionUID = 1L;
		public String Id = "", Item_Pool_Name__c = "", Item_Type__c = "",Ticket_Settings__c="",Badgable__c="";
		public ItemType_R Item_Type__r = new ItemType_R();
	}

	public class ItemType_R implements Serializable{

		private final static  long serialVersionUID = 1L;
		public String Id="",Name ="",Badgable__c="";
	}

	public class BadgeParentId_R implements Serializable{

		/**
		 *
		 */
		private final static  long serialVersionUID = 1L;
		public String Badge_ID__c = "",Id="";

	}

	public class BadgeID_R implements Serializable{
		/**
		 *
		 */
		private final static  long serialVersionUID = 1L;
		public String Name="",Print_status__c="";
	}


}
