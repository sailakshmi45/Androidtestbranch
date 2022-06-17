package com.globalnest.mvc;

import android.graphics.Bitmap;
import android.widget.FrameLayout;

import com.globalnest.utils.SFDCDetails;

import java.util.ArrayList;

/**
 * Created by saila_000 on 23-05-2017.
 */

public class PrintDetails {

    public String attendeeId="",order_id="",checked_in_eventId="";
    public SFDCDetails sfdcddetails;
    public FrameLayout print_badge,frame_transparentbadge;
    public boolean isselfCheckinbool=false;
    public String attendeeWhereClause="";
    public boolean isOrderScaneed=false;
    public String qrCode="";
    public String reason="";
    public Bitmap bitmap;
    public ArrayList<Bitmap> bitmapArrayList;

}
