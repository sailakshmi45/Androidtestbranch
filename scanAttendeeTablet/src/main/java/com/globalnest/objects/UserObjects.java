package com.globalnest.objects;

public class UserObjects {
	public String Userid = "", profilestate = "", profileimage = "",
			profilecountry = "",profileCity="",designation="";

	public UserProfile Profile = new UserProfile();
	public String ispastservicecalled="false";
	@Override
	public String toString() {
		return "User [Userid=" + Userid + ",profileimage=" + profileimage
				+ ",profilestate=" + profilestate + ",profilecountry="
				+ profilecountry + ",Profile=" + Profile + "]";
	}

}
