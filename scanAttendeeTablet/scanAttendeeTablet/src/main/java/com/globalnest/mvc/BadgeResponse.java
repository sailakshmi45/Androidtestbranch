/** 
 *GlobalNest 
 * Ajay Goyal
 */
package com.globalnest.mvc;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class BadgeResponse {
	
	@SerializedName("badges")
	public ArrayList<BadgeController> badges=new ArrayList<BadgeController>();

	@Override
	public String toString() {
		return "BadgeResponse [badges=" + badges + "]";
	} 
	
	
	
}
