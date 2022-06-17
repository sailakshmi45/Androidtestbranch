package com.globalnest.scanattendee;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.globalnest.cropimage.CropImage;
import com.globalnest.cropimage.CropUtil;
import com.globalnest.database.DBFeilds;
import com.globalnest.mvc.BadgeResponseNew;
import com.globalnest.mvc.OrderDetailsHandler;
import com.globalnest.mvc.PrintDetails;
import com.globalnest.mvc.TotalOrderListHandler;
import com.globalnest.network.HttpPostData;
import com.globalnest.network.WebServiceUrls;
import com.globalnest.objects.AttendeeFeildObj;
import com.globalnest.objects.RegistrationSettingsController;
import com.globalnest.printer.PrinterDetails;
import com.globalnest.utils.AppUtils;
import com.globalnest.utils.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hootsuite.nachos.NachoTextView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by sailakshmi on 29-06-2017.
 */


public class SelfCheckinAttendeeDetailActivity extends BaseActivity implements OnClickListener {
    ScrollView selfcheckedinlayout;
    FrameLayout print_badge, frame_transparentbadge,frame_barcode,frame_attendeeimg;
    EditText s_firstname,s_lastname,s_company,s_emailid,s_jobtitle,s_phonenumber;
    String requestType="",whereClause = "";
    Cursor payment_cursor;
    public static Cursor selfcheckin_payment_cursor;
    String attendee_id = "", event_id = "", order_id = "", reason = "",Tktprofile_Id = "";
    String att_fname ="",att_lname = "", att_email = "",att_mobile="",att_badge_lable = "",
            att_work_mobile = "",att_job_title = "",att_company="";
    boolean selfcheckinSaveandPrint =false,isReasonEmpty=false;
    private AlertDialog.Builder print_dialog;
    ImageView img_attendee;
    Bitmap attendee_photo;
    TextView txt_image;
    int i=0;
    ProgressDialog progressDialog;
    private TotalOrderListHandler totalorderlisthandler;
    public AttendeeFeildObj fields = new AttendeeFeildObj();
    private String workaddress1, workaddress2, workcity, workzipcode, workcountry, workstate;
    private String homeaddress1, homeaddress2, homecity, homezipcode, homecountry, homestate;
    private String billingaddress1, billingaddress2, billingcity, billingzipcode, billingcountry, billingstate;
    public HashMap<String, AttendeeFeildObj> parentarray = new HashMap<>();
    EditText edt_Email__c,edt_First_Name__c,edt_Last_Name__c;
    public Cursor c;
    List<String> tempnaics, tempcommodities, tempdiversities;
    LinearLayout lay_SpeakerImage,lay_Company_Logo;
    TextView SpeakerImage,Company_Logo;
    ImageView img_SpeakerImage,img_Company_Logo;
    Bitmap company_logo;
    boolean isuserpicclicked=true;
    String whereclause = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomContentView(R.layout.selfcheckin_attendeedetail);
        setCustumViewData();
        setPanel_basic();
        setPanel_workinfo();
        setPanel_workaddress();
        setPanel_billingaddress();
        setPanel_homeaddress();
        setPanel_naicsinfo();
        setPanel_commodities();
        setPanel_diversities();
        setPanel_socialinfo();
        txtprint_selfcheckin.setEnabled(true);
        selfcheckinonlysave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (parentarray != null && parentarray.values().size() > 0) {
                          /*  if (Util.db.getAttendeeswithTKTProfile_ID(Tktprofile_Id, checked_in_eventId) > 1&&isEdited()) {
                                setCustomAlertDialog(SelfCheckinAttendeeDetailActivity.this);
                                alert_dialog.show();
                            }else {
                                istransfer=false;*/
                            requestType =Util.ATTENDEE_DETAIL;
                            doRequest();
                            //}
                        } else {
                            showCustomToast(SelfCheckinAttendeeDetailActivity.this, "No changes Founded! ",
                                    R.drawable.img_like, R.drawable.toast_greenroundededge, true);
                        }

                    }
                });
            }
        });
       /* selfcheckinonlysave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOnline()){
                    if (isSelfcheckinColumsEdited()) {
                        if (isValidAttendeeRecord()) {
                            saveAttendeeRequest();
                        }
                    }
                    else{
                        showCustomToast(SelfCheckinAttendeeDetailActivity.this,"No changes Updated!",R.drawable.img_like,R.drawable.toast_greenroundededge,false);
                    }

                }else{
                    startErrorAnimation(getResources().getString(R.string.network_error),txt_error_msg);
                }
            }
        });*/
        txtprint_selfcheckin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOnline()){
                   /* if(Util.getselfcheckinbools(Util.ISPRINTALLOWED)&&(Util.getselfcheckinbools(Util.ISDATAEDITABLE))&&!isBadgeSelected()) {
                        if(isSelfcheckinColumsEdited()) {
                            if(isValidAttendeeRecord()) {
                                saveAttendeeRequest();
                            }
                        }else{
                            showCustomToast(SelfCheckinAttendeeDetailActivity.this,"No changes Updated!",R.drawable.img_like,R.drawable.toast_greenroundededge,false);
                        }
                    }else */

                    /*BaseActivity.baseDialog.setMessage("Please wait...");
                    BaseActivity.baseDialog.show();
                    BaseActivity.baseDialog.setCancelable(true);*/
                    if(Util.getselfcheckinbools(Util.ISPRINTALLOWED)) {
                        if (AppUtils.isStoragePermissionGranted(SelfCheckinAttendeeDetailActivity.this)&&isSelfcheckinColumsEdited()) {
                            printAttendeeRequest();
                        }else if (isSelfcheckinColumsEdited()) {
                            if(isValidAttendeeRecord()) {
                                saveAttendeeRequest();
                            }
                            else{
                                showCustomToast(SelfCheckinAttendeeDetailActivity.this,"No changes Updated!",R.drawable.img_like,R.drawable.toast_greenroundededge,false);
                            }
                        }
                        else if(!AppUtils.isStoragePermissionGranted(SelfCheckinAttendeeDetailActivity.this)){
                            AppUtils.giveStoragermission(SelfCheckinAttendeeDetailActivity.this);
                        }else{
                            printAttendeeRequest();
                        }
                    }
                    else{
                        if(isSelfcheckinColumsEdited()) {
                            if(isValidAttendeeRecord()) {
                                saveAttendeeRequest();
                            }
                        }else{
                            showCustomToast(SelfCheckinAttendeeDetailActivity.this,"No changes Updated!",R.drawable.img_like,R.drawable.toast_greenroundededge,false);
                        }
                    }
                }else{
                    startErrorAnimation(getResources().getString(R.string.network_error),txt_error_msg);
                }
            }
        });
        txtcheckin_selfcheckin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String sessionid = Util.db.getSwitchedONGroupId(checked_in_eventId);
                String status=Util.db.getTStatusBasedOnGroup(payment_cursor.
                                getString(payment_cursor.getColumnIndex(DBFeilds.ATTENDEE_ID)),
                        payment_cursor.getString(payment_cursor.getColumnIndex(DBFeilds.ATTENDEE_ITEM_POOL_ID)), checked_in_eventId);
                boolean ischeckin = Boolean.valueOf(NullChecker(status));
               /* boolean ischeckinforfreesession = Util.db.SessionCheckInStatus(
                        payment_cursor.getString(payment_cursor.getColumnIndex("Attendee_Id")),
                        Util.db.getSwitchedONGroupId(checked_in_eventId));*/
                if(ischeckin){
                    showCustomToast(SelfCheckinAttendeeDetailActivity.this,
                            " Already Checked In",
                            R.drawable.img_like,R.drawable.toast_redrounded,false);
                }else if(!isOnline()){
                    startErrorAnimation(getResources().getString(R.string.network_error),txt_error_msg);
                }else{
                    checkinbutton_clicked=true;
                    ticketCheckin(SelfCheckinAttendeeDetailActivity.this,payment_cursor,sessionid);
                }
            }
        });
    }

    @Override
    public void setCustomContentView(int layout) {
        activity = this;
        View v = inflater.inflate(layout, null);
        linearview.addView(v);
        txt_title.setText("Attendee");
        img_menu.setImageResource(R.drawable.back_button);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        event_layout.setVisibility(View.GONE);
        button_layout.setVisibility(View.GONE);
        event_layout.setVisibility(View.VISIBLE);
        back_layout.setOnClickListener(this);
        selfcheckedinlayout = (ScrollView) linearview.findViewById(R.id.selfcheckedinlayout);
        print_badge = (FrameLayout) linearview.findViewById(R.id.frame_attdetailqrcodebadge);
        frame_transparentbadge = (FrameLayout) linearview.findViewById(R.id.frame_transparentbadge);
        frame_attendeeimg =(FrameLayout) linearview.findViewById(R.id.frm_attendeeimg);
        img_attendee = (ImageView) linearview.findViewById(R.id.attendeedetailpic);
        txt_image  = (TextView) linearview.findViewById(R.id.txt_image);
        s_firstname = (EditText) linearview.findViewById(R.id.s_firstname);
        s_lastname = (EditText) linearview.findViewById(R.id.s_lastname);
        s_emailid = (EditText) linearview.findViewById(R.id.s_emailid);
        s_phonenumber = (EditText) linearview.findViewById(R.id.s_phonenumber);
        s_company = (EditText) linearview.findViewById(R.id.s_company);
        //  s_badgelabel = (EditText) linearview.findViewById(R.id.s_badgelabel);
        //s_workphno = (EditText) linearview.findViewById(R.id.s_workphno);
        s_jobtitle = (EditText) linearview.findViewById(R.id.s_jobtitle);
        if(Util.getselfcheckinbools(Util.ISCHECKINALLOWED))
        {
            txtcheckin_selfcheckin.setVisibility(View.VISIBLE);
        }

        if(Util.getselfcheckinbools(Util.ISPRINTALLOWED)) {
            txtprint_selfcheckin.setVisibility(View.VISIBLE);
            txtprint_selfcheckin.setText("Print Badge");
            if(Util.getselfcheckinbools(Util.ISDATAEDITABLE)){
                selfcheckinonlysave.setVisibility(View.VISIBLE);
            }
        }else if(Util.getselfcheckinbools(Util.ISDATAEDITABLE)){
            txtprint_selfcheckin.setVisibility(View.VISIBLE);
            txtprint_selfcheckin.setText(" Save ");
        }else {
            txtprint_selfcheckin.setVisibility(View.GONE);
        }
        img_socket_scanner.setVisibility(View.GONE);
        img_scanner_base.setVisibility(View.GONE);
        if(Util.getselfcheckinbools(Util.ISAUTOCHECKIN)){
            requestType = "Check in";
            selfcheckin_payment_cursor=null;
            selfcheckin_payment_cursor =payment_cursor;
        }
        if (Util.getselfcheckinbools(Util.ISDATAEDITABLE)) {
            //s_workphno.setFocusable(true);
            frame_attendeeimg.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    openTakeFromDialg(SelfCheckinAttendeeDetailActivity.this);
                }
            });
            s_emailid.setFocusable(true);
            s_firstname.setFocusable(true);
            s_lastname.setFocusable(true);
            s_phonenumber.setFocusable(true);
            //  s_badgelabel.setFocusable(true);
            s_jobtitle.setFocusable(true);
            s_company.setFocusable(true);
        } else {
            // s_workphno.setFocusable(false);
            frame_attendeeimg.setEnabled(false);
            s_emailid.setFocusable(false);
            s_firstname.setFocusable(false);
            s_lastname.setFocusable(false);
            s_phonenumber.setFocusable(false);
            // s_badgelabel.setFocusable(false);
            s_jobtitle.setFocusable(false);
            s_company.setFocusable(false);
        }
    }
    public void setCustumViewData() {
        try{
            Intent attendee_intent = getIntent();
            attendee_id = attendee_intent.getStringExtra("ATTENDEE_ID");
            event_id = attendee_intent.getStringExtra("EVENT_ID");
            order_id = attendee_intent.getStringExtra("ORDER_ID");
            Tktprofile_Id = attendee_intent.getStringExtra("Tktprofile_Id");
            //badge_id= attendee_intent.getStringExtra("BADGE_ID");
            whereClause = " where " + DBFeilds.ATTENDEE_EVENT_ID + " = '"
                    + checked_in_eventId + "'" + " AND " + DBFeilds.ATTENDEE_ID
                    + " = " + "'" + attendee_id + "'" + " AND "
                    + DBFeilds.ATTENDEE_ORDER_ID + " = " + "'" + order_id + "'";
            whereclause = " AND  OrderTicketDetails.Event_Id = '" + checked_in_eventId + "' AND " + DBFeilds.TABLE_ATTENDEE_DETAILS + "." + DBFeilds.ATTENDEE_ID + " = '" + attendee_id + "' AND " + DBFeilds.TABLE_ATTENDEE_DETAILS + "." + "Order_Id" + " = '" + order_id + "'";

            payment_cursor = Util.db.getAttendeeDetails(whereClause);
            payment_cursor.moveToFirst();
            s_firstname.setTypeface(Util.sanfrancisco_iphonefont);
            s_lastname.setTypeface(Util.sanfrancisco_iphonefont);
            s_company.setTypeface(Util.sanfrancisco_iphonefont);
            s_emailid.setTypeface(Util.sanfrancisco_iphonefont);
            s_jobtitle.setTypeface(Util.sanfrancisco_iphonefont);
            s_phonenumber.setTypeface(Util.sanfrancisco_iphonefont);
            if (NullChecker(payment_cursor.getString(payment_cursor.getColumnIndex(DBFeilds.ATTENDEE_IMAGE))).isEmpty()) {
                if (!getDbValue(DBFeilds.ATTENDEE_FIRST_NAME).isEmpty()
                        &&!getDbValue(DBFeilds.ATTENDEE_LAST_NAME).isEmpty()){
                    img_attendee.setVisibility(View.GONE);
                    txt_image.setVisibility(View.VISIBLE);
                    txt_image.setText(getDbValue(DBFeilds.ATTENDEE_FIRST_NAME).substring(0, 1).toUpperCase()
                            +getDbValue(DBFeilds.ATTENDEE_LAST_NAME).substring(0, 1).toUpperCase());
                }else {
                    img_attendee.setImageResource(R.drawable.default_image);
                }
            }else{
                img_attendee.setVisibility(View.VISIBLE);
                String[] fullurl = checkedin_event_record.image.split("&id=");
                String url = fullurl[0];
                Picasso.with(SelfCheckinAttendeeDetailActivity.this).load(url + "&id=" + payment_cursor.getString(payment_cursor.getColumnIndex(DBFeilds.ATTENDEE_IMAGE)))
                        .placeholder(R.drawable.default_image)
                        .error(R.drawable.default_image).into(img_attendee);

            }
            s_firstname.setText(getDbValue(DBFeilds.ATTENDEE_FIRST_NAME).substring(0,1).toUpperCase()+getDbValue(DBFeilds.ATTENDEE_FIRST_NAME).substring(1));
            s_lastname.setText(getDbValue(DBFeilds.ATTENDEE_LAST_NAME).substring(0,1).toUpperCase()+getDbValue(DBFeilds.ATTENDEE_LAST_NAME).substring(1));
            s_emailid.setText(getDbValue(DBFeilds.ATTENDEE_EMAIL_ID));
            if(NullChecker(getDbValue(DBFeilds.ATTENDEE_COMPANY)).isEmpty()){
                s_company.setText(getDbValue(DBFeilds.ATTENDEE_COMPANY));
            }else{
                s_company.setText(getDbValue(DBFeilds.ATTENDEE_COMPANY).substring(0, 1).toUpperCase() + getDbValue(DBFeilds.ATTENDEE_COMPANY).substring(1));
            }
            // s_company.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
            //  s_badgelabel.setText(getDbValue(DBFeilds.ATTENDEE_BADGE_LABLE));
            s_phonenumber.setText(getDbValue(DBFeilds.ATTENDEE_MOBILE));
            if(NullChecker(getDbValue(DBFeilds.ATTENDEE_JOB_TILE)).isEmpty()){
                s_jobtitle.setText(getDbValue(DBFeilds.ATTENDEE_JOB_TILE));
            }else {
                s_jobtitle.setText(getDbValue(DBFeilds.ATTENDEE_JOB_TILE).substring(0, 1).toUpperCase() + getDbValue(DBFeilds.ATTENDEE_JOB_TILE).substring(1));
            }
            //  s_workphno.setText(getDbValue(DBFeilds.ATTENDEE_WORK_PHONE));


        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public boolean isSelfcheckinColumsEdited(){
        boolean isedited=false;
        att_fname = s_firstname.getText().toString().trim();
        att_lname = s_lastname.getText().toString().trim();
        att_email = s_emailid.getText().toString().toLowerCase().trim();
        att_company = s_company.getText().toString().trim();
        att_mobile = s_phonenumber.getText().toString().trim();
        // att_work_mobile = s_workphno.getText().toString().trim();
        att_job_title = s_jobtitle.getText().toString().trim();
        // att_badge_lable = s_badgelabel.getText().toString().trim();
        String image=NullChecker(Util.db.getimagedata(Util.db.getByteArray(attendee_photo)));
        String dbMobile=getDbValue(DBFeilds.ATTENDEE_MOBILE).trim().replaceAll("\\p{P}","").replaceAll("-","").replaceAll(" ","");
        String dbworknumber=getDbValue(DBFeilds.ATTENDEE_WORK_PHONE).
                trim().replaceAll("\\p{P}","").replaceAll("-","").replaceAll(" ","");

        if (!att_fname.equalsIgnoreCase(getDbValue(DBFeilds.ATTENDEE_FIRST_NAME))) {
            return true;
        } else if (!att_lname.equalsIgnoreCase(getDbValue(DBFeilds.ATTENDEE_LAST_NAME))) {
            return true;
        } else if (!att_email.equalsIgnoreCase(getDbValue(DBFeilds.ATTENDEE_EMAIL_ID))) {
            return true;
        } else if (!att_company.equalsIgnoreCase(getDbValue(DBFeilds.ATTENDEE_COMPANY))) {
            return true;
        } else if (!att_mobile.replaceAll("\\p{P}","").replaceAll("-","").replaceAll(" ","").trim().equalsIgnoreCase(dbMobile.trim())) {
            return true;
        } else if (!att_job_title.equalsIgnoreCase(getDbValue(DBFeilds.ATTENDEE_JOB_TILE))) {
            return true;
        }else if(!image.isEmpty()){
            return true;
        }/* else if (!att_badge_lable.equalsIgnoreCase(getDbValue(DBFeilds.ATTENDEE_BADGE_LABLE))) {
            return true;
        }
        else if (!att_work_mobile.replaceAll("\\p{P}","").replaceAll("-","").replaceAll(" ","").trim().equalsIgnoreCase(dbworknumber.trim())) {
            return true;
        }*/

        return isedited;
    }
    public boolean isValidAttendeeRecord() {
        if (att_fname.equals("")) {
            s_firstname.setError(getResources().getString(R.string.fname_alert));
            s_firstname.requestFocus();
            return false;
        } else if (att_lname.equals("")) {
            s_lastname.setError(getResources().getString(R.string.lname_alert));
            s_lastname.requestFocus();
            return false;
        } else if (!Pattern.matches(Validation.EMAIL_REGEX, att_email)) {
            s_emailid.setError(getResources().getString(R.string.email_alert));
            s_emailid.requestFocus();
            return false;
        } else {
            return true;
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//		Log.i("---------------onActivity Result------------", ":" + requestCode + " : " + data.getStringExtra(Util.INTENT_KEY_1));

        if((requestCode == REQUEST_CODE_CROP_IMAGE)&& (data!=null)){

            String path = data.getStringExtra(CropImage.IMAGE_PATH);

            if ((path == null)||(TextUtils.isEmpty(path))) {
                return;
            }
            Bitmap bitmap = BitmapFactory.decodeFile(path);
				/*if(bitmapArrayList.size()<formPosition)
					bitmapArrayList.add(bitmap);
				else
					bitmapArrayList.set(formPosition,bitmap);*/
            img_attendee.setVisibility(View.VISIBLE);
            img_attendee.setImageBitmap(bitmap);
            attendee_photo = bitmap;

            if(mFileTemp!=null){
                mFileTemp.delete();
            }
        }else if ((requestCode == PICK_FROM_CAMERA) && (resultCode == RESULT_OK)) {
            if (data != null) {

                //doCrop();
            } else {
                File mediaStorageDir = new File(
                        Environment.getExternalStorageDirectory(),
                        "ScanAttendee");
                if(!mediaStorageDir.exists()){
                    mediaStorageDir.mkdir();
                }
                mediaFile = new File(mediaStorageDir.getPath() + File.separator
                        + "IMG_1.jpg");
                mImageCaptureUri = Uri.fromFile(mediaFile);


                if (mImageCaptureUri != null) {
                    try {
                        startCropImage(SelfCheckinAttendeeDetailActivity.this);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

        } else if (requestCode == PICK_FROM_FILE && data != null
                && data.getData() != null) {

            mImageCaptureUri = data.getData();
            //mediaFile = new File(getRealPathFromURI(mImageCaptureUri));
            try {
                File mediaStorageDir = new File(
                        Environment.getExternalStorageDirectory(),
                        "ScanAttendee");
                if(!mediaStorageDir.exists()){
                    mediaStorageDir.mkdir();
                }
                mFileTemp = new File(mediaStorageDir.getPath() + File.separator
                        + "IMG_1.jpg");
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
                CropUtil.copyStream(inputStream, fileOutputStream);
                mediaFile = mFileTemp;
                startCropImage(SelfCheckinAttendeeDetailActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == CROP_FROM_CAMERA) {

            Bundle extras = data.getExtras();

            if (extras != null) {
                attendee_photo = extras.getParcelable("data");
                img_attendee.setVisibility(View.VISIBLE);
                img_attendee.setImageBitmap(attendee_photo);

            }
        }else if (requestCode == FINISH_RESULT) {
            startActivity(new Intent(SelfCheckinAttendeeDetailActivity.this, SplashActivity.class));
            finish();
        }

    }

    public void printAttendeeRequest() {
        txtprint_selfcheckin.setEnabled(false);
        if(Util.getselfcheckinbools(Util.ISDATAEDITABLE)&&isSelfcheckinColumsEdited()){
            selfcheckinSaveandPrint=true;
        }else{
            selfcheckinSaveandPrint=false;
        }
        whereClause = " where " + DBFeilds.ATTENDEE_EVENT_ID + " = '"
                + checked_in_eventId + "'" + " AND " + DBFeilds.ATTENDEE_ID
                + " = " + "'" + attendee_id + "'" + " AND "
                + DBFeilds.ATTENDEE_ORDER_ID + " = " + "'" + order_id + "'";
        payment_cursor = Util.db.getAttendeeDetails(whereClause);
        payment_cursor.moveToFirst();

        if (isBadgeSelected()) {
            if(Util.getselfcheckinbools(Util.ISSELFCHECKIN)){
                if(!Util.getselfcheckinbools(Util.ISREPRINTALLOWED)&& !Util.getselfcheckinbools(Util.ISDATAEDITABLE)) {
                    if(!getDbValue(DBFeilds.ATTENDEE_BADGEID).isEmpty()&&getDbValue(DBFeilds.ATTENDEE_BADGE_PRINTSTATUS).equalsIgnoreCase("Printed")){
                        showBadgeAlreadyPrinted();
                    }else {//if(getDbValue(DBFeilds.ATTENDEE_BADGEID).isEmpty())
                        callBadgeid();
                    }
                }
                else if (Util.getselfcheckinbools(Util.ISREPRINTALLOWED) && !Util.getselfcheckinbools(Util.ISDATAEDITABLE)) {
                    if (getDbValue(DBFeilds.ATTENDEE_BADGEID).isEmpty()) {
                        callBadgeid();
                    } else if (!getDbValue(DBFeilds.ATTENDEE_BADGEID)
                            .isEmpty()) {
                        isprinterconnectedopendialog();
                    }
                } else if(!Util.getselfcheckinbools(Util.ISREPRINTALLOWED)&& Util.getselfcheckinbools(Util.ISDATAEDITABLE)){
                    if (!getDbValue(DBFeilds.ATTENDEE_BADGEID).isEmpty()&&getDbValue(DBFeilds.ATTENDEE_BADGE_PRINTSTATUS).equalsIgnoreCase("Printed")) {
                        if (selfcheckinSaveandPrint) {
                            if(isValidAttendeeRecord()){
                                saveAttendeeRequest();
                            }
                        }else{
                            showCustomToast(SelfCheckinAttendeeDetailActivity.this,"No changes Updated!",R.drawable.img_like,R.drawable.toast_greenroundededge,false);
                            //Toast.makeText(SelfCheckinAttendeeDetailActivity.this, "No changes Updated", Toast.LENGTH_LONG).show();
                            showBadgeAlreadyPrinted();
                        }
                    } else  {
                        if (selfcheckinSaveandPrint) {
                            if(isValidAttendeeRecord()){
                                saveAttendeeRequest();
                            }
                        }else{
                            callBadgeid();
                        }
                    }
                }else if (Util.getselfcheckinbools(Util.ISREPRINTALLOWED)&&Util.getselfcheckinbools(Util.ISDATAEDITABLE)) {
                    if (getDbValue(DBFeilds.ATTENDEE_BADGEID).isEmpty()) {
                        if (selfcheckinSaveandPrint) {
                            if(isValidAttendeeRecord()) {
                                saveAttendeeRequest();
                            }
                        }else {
                            callBadgeid();
                        }
                    } else if (!getDbValue(DBFeilds.ATTENDEE_BADGEID).isEmpty()) {
                        if (selfcheckinSaveandPrint) {
                            if(isValidAttendeeRecord()){
                                saveAttendeeRequest();
                            }
                        }else {
                            isprinterconnectedopendialog();
                        }
                    }
                }
            }
        }else {
            if (selfcheckinSaveandPrint) {
                if(isValidAttendeeRecord()) {
                    saveAttendeeRequest();
                }
            }else {
                if (isOnline()) {
                    requestType = Util.LOAD_BADGE;
                    doRequest();
                } else {
                    startErrorAnimation(getResources().getString(R.string.network_error), txt_error_msg);
                }
            }
        }
    }

    public void openprintDialog() {
        try {
            txtprint_selfcheckin.setEnabled(true);
            print_dialog = new AlertDialog.Builder(SelfCheckinAttendeeDetailActivity.this);
            LayoutInflater li = LayoutInflater
                    .from(SelfCheckinAttendeeDetailActivity.this);
            View promptsView = li.inflate(R.layout.print_dialog_layout, null);
            print_dialog.setView(promptsView);
            final EditText edit_reason = (EditText) promptsView.findViewById(R.id.edit_reason);
            final TextView txt_message=(TextView) promptsView.findViewById(R.id.txt_message);
            final TextView txt_top=(TextView) promptsView.findViewById(R.id.textView1);
            edit_reason.setText("");
            if(getDbValue(DBFeilds.ATTENDEE_BADGEID).isEmpty()){
                txt_message.setVisibility(View.VISIBLE);
                edit_reason.setVisibility(View.GONE);
            }else{
                txt_top.setText("Badge is already printed. Do you want to reprint ?\n" +
                        "The previous badge will become invalid.");
                txt_message.setVisibility(View.GONE);
                edit_reason.setVisibility(View.VISIBLE);
            }
            print_dialog
                    .setCancelable(false)
                    .setPositiveButton("Reprint",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    reason = edit_reason.getText().toString();
                                    if(getDbValue(DBFeilds.ATTENDEE_BADGEID).isEmpty()) {
                                        callBadgeid();
                                    }else if(!getDbValue(DBFeilds.ATTENDEE_BADGEID).isEmpty()){
                                        if (!reason.equalsIgnoreCase("") ) {
                                            callBadgeid();
                                        }else if(reason.trim().isEmpty()){
                                            isReasonEmpty=true;
                                            edit_reason.setFocusable(true);
                                            edit_reason.setError("Reason should not be empty");

                                        }
                                    }
                                }})
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isReasonEmpty=false;
                            hideSoftKeyboard(SelfCheckinAttendeeDetailActivity.this);
                            dialog.dismiss();

                        }
                    });


            // create alert dialog
            final AlertDialog alertDialog = print_dialog.create();

            alertDialog.show();

            alertDialog
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            hideSoftKeyboard(SelfCheckinAttendeeDetailActivity.this);
                            if (isReasonEmpty) {
                                alertDialog.dismiss();
                                alertDialog.show();
                                edit_reason.setError("Reason should not be empty");
                                edit_reason.requestFocus();
                            } else {
                                return;
                            }

                        }
                    });
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
            txt_message.setText("Do you want to print the badge?");




        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void saveAttendeeRequest() {
        if (isOnline()) {
            requestType = Util.ATTENDEE_DETAIL;
            doRequest();
        } else {
            startErrorAnimation(getResources().getString(R.string.network_error),
                    txt_error_msg);
        }
        txtprint_selfcheckin.setEnabled(true);
    }
    public void  callBadgeid(){
        /*if(BaseActivity.baseDialog!=null) {
            if(BaseActivity.baseDialog.isShowing())
                BaseActivity.baseDialog.dismiss();
        }*/
        if(Util.getselfcheckinbools(Util.ISPRINTALLOWED)) {
            if (!PrinterDetails.selectedPrinterPrefrences.getString("printer", "").isEmpty()) {
                //if(isprinterconnected()){
                if (isOnline()) {
                    try {
                        if(isBadgeSelected()) {
                            if(Util.getselfcheckinbools(Util.ISPRINTALLOWED)&&AppUtils.isStoragePermissionGranted(SelfCheckinAttendeeDetailActivity.this)){
                                txtprint_selfcheckin.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_orange));
                                txtprint_selfcheckin.setText(" Printing...");
                            }
                            doSaveAndPrint(attendee_id);
                        }else{
                            BaseActivity.showSingleButtonDialog("Alert",
                                    "No Badge Selected, Please contact your Event Organizer!",this);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    /*requestType = Util.GET_BADGE_ID;
                    doRequest();*/
                } else {
                    startErrorAnimation(getResources().getString(R.string.network_error), txt_error_msg);
                }
            } else {
                String msg = "";
                if (Util.getselfcheckinbools(Util.ISSELFCHECKIN)) {
                    if (PrinterDetails.selectedPrinterPrefrences.getString("printer", "").isEmpty()) {
                        msg = "Printer is not connected.Please contact event Organizer!";
                    } else {
                        msg = "Printer is disconnected.Please contact event Organizer!";
                    }
                    txtprint_selfcheckin.setEnabled(true);
                    Util.setCustomAlertDialog(SelfCheckinAttendeeDetailActivity.this);
                    Util.openCustomDialog("Alert", msg);
                    Util.txt_okey.setText("OK");
                    Util.alert_dialog.setCancelable(false);
                    Util.txt_dismiss.setVisibility(View.GONE);
                    Util.txt_okey.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            Util.alert_dialog.dismiss();
                            finish();
                        }
                    });
                }
            }
        }
    }
    private JSONObject setjsonbody() {
        JSONObject parent = new JSONObject();
        JSONArray fields2 = new JSONArray();
        try {
            for (String key : parentarray.keySet()) {
                JSONObject fieldobj = new JSONObject();
                fieldobj.put("field_api_name", key);
                fieldobj.put("group_display_name", parentarray.get(key).group_display_name);
                fieldobj.put("field_data", parentarray.get(key).field_data);
                fieldobj.put("field_type", parentarray.get(key).field_type);
                if(key.equalsIgnoreCase("user_pic__c")||key.equalsIgnoreCase("company_logo__c"))
                    fieldobj.put("attachmentorimagename", parentarray.get(key).field_name);
                fields2.put(fieldobj);
            }
            parent.put("fields", fields2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parent;
    }

    @Override
    public void doRequest() {

        String access_token = sfdcddetails.token_type + " "
                + sfdcddetails.access_token;
        String url = sfdcddetails.instance_url
                + WebServiceUrls.SA_ATTENDEE_DETAIL + "eventId="
                + checked_in_eventId;
        if (requestType.equalsIgnoreCase(Util.LOAD_BADGE)) {
            String _url = sfdcddetails.instance_url + WebServiceUrls.SA_GET_BADGE_TEMPLATE_NEW + "Event_Id=" + checked_in_eventId;
            postMethod = new HttpPostData("Loading Badges...", _url, null, access_token, SelfCheckinAttendeeDetailActivity.this);
            postMethod.execute();
        } else if (requestType.equalsIgnoreCase(Util.ATTENDEE_DETAIL)){
            postMethod = new HttpPostData("Saving Attendee Info", sfdcddetails.instance_url + WebServiceUrls.SA_BLN_MM_SAVETKTPROFILEDATA + "&eventid=" + checkedin_event_record.Events.Id + "&tktprofileid=" + Tktprofile_Id + "&ticketid=" + attendee_id + "&transfer=false", setjsonbody().toString(), sfdcddetails.token_type + " " + sfdcddetails.access_token, this);
            postMethod.execute();
        } /*{
            progressDialog =new ProgressDialog(SelfCheckinAttendeeDetailActivity.this);
            progressDialog.setMessage("Saving Attendee Info...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            ApiInterface apiService = ApiClient.getClient(sfdcddetails.instance_url).create(ApiInterface.class);
            // Call<Void> jsonbody= apiService.setSurveys(setSellJsonBody());
            Call<TotalOrderListHandler> call = apiService.getAttendeeDetailvalues(checked_in_eventId,setAttendeeJsonBody().toString(),sfdcddetails.token_type + " "+ sfdcddetails.access_token);
            if(AppUtils.isLogEnabled) {
                AppUtils.displayLog(call + "------ Url-------", url);
                AppUtils.displayLog(call + "------JSON Retrofit-------", setAttendeeJsonBody().toString());
            }
            call.enqueue(new Callback<TotalOrderListHandler>() {
                @Override
                public void onResponse(Call<TotalOrderListHandler> call, Response<TotalOrderListHandler> response) {
                    Log.e(call+"------success-------", "------response started-------");
                    if(AppUtils.isLogEnabled){AppUtils.displayLog(call+"------JSON Response-------", response.toString());}
                    try {
                        if (!isValidResponse(response.toString())) {
                            dismissProgressDialog();
                            openSessionExpireAlert(errorMessage(response.toString()));
                        } else if (response.code() == 200) {
                            totalorderlisthandler = response.body();
                            if (!NullChecker(totalorderlisthandler.errorMsg).isEmpty()) {
                                AlertDialogCustom dialog = new AlertDialogCustom(
                                        SelfCheckinAttendeeDetailActivity.this);
                                dialog.setParamenters("Alert",
                                        AppUtils.NullChecker(totalorderlisthandler.errorMsg), null, null,
                                        1, false);
                                dialog.show();
                            } else {
                                if (totalorderlisthandler.TotalLists.size() > 0) {
                                    Util.db.upadteOrderList(
                                            totalorderlisthandler.TotalLists,
                                            checked_in_eventId);
                                }
                                if(Util.getselfcheckinbools(Util.ISSELFCHECKIN)){
                                    if (!selfcheckinSaveandPrint) {
                                        showCustomToast(SelfCheckinAttendeeDetailActivity.this,"Saved Successfully!",R.drawable.img_like,R.drawable.toast_greenroundededge,true);
                                        finish();
                                        // Toast.makeText(SelfCheckinAttendeeDetailActivity.this, "Saved Successfully", Toast.LENGTH_LONG).show();
                                    } else if(Util.getselfcheckinbools(Util.ISREPRINTALLOWED)&&selfcheckinSaveandPrint){
                                        showCustomToast(SelfCheckinAttendeeDetailActivity.this,"Saved Successfully!",R.drawable.img_like,R.drawable.toast_greenroundededge,true);
                                        if(!getDbValue(DBFeilds.ATTENDEE_BADGEID).isEmpty()){
                                            selfcheckinSaveandPrint = false;
                                            isprinterconnectedopendialog();
                                        } else {
                                            selfcheckinSaveandPrint = false;
                                            callBadgeid();
                                        }
                                    }else if(!Util.getselfcheckinbools(Util.ISREPRINTALLOWED)&&selfcheckinSaveandPrint){
                                        selfcheckinSaveandPrint = false;
                                        showCustomToast(SelfCheckinAttendeeDetailActivity.this,"Saved Successfully!",R.drawable.img_like,R.drawable.toast_greenroundededge,true);

                                        // Toast.makeText(SelfCheckinAttendeeDetailActivity.this, "Saved Successfully", Toast.LENGTH_LONG).show();
                                        if(!getDbValue(DBFeilds.ATTENDEE_BADGEID).isEmpty()&&getDbValue(DBFeilds.ATTENDEE_BADGE_PRINTSTATUS).equalsIgnoreCase("Printed")){
                                            showBadgeAlreadyPrinted();
                                        }else {
                                            callBadgeid();
                                        }
                                    }
                                }
                            }
                            dismissProgressDialog();
                        }
                        txtprint_selfcheckin.setEnabled(true);
                    }catch (Exception e) {
                        e.printStackTrace();
                        startErrorAnimation(
                                getResources().getString(R.string.network_error1),
                                txt_error_msg);
                    }
                }




                @Override
                public void onFailure(Call<TotalOrderListHandler> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("------failure-------", t.toString());
                    dismissProgressDialog();
                    txtprint_selfcheckin.setEnabled(true);
                }
            });
			*//*String url = sfdcddetails.instance_url
					+ WebServiceUrls.SA_ATTENDEE_DETAIL + "eventId="
					+ checked_in_eventId;
			postMethod = new HttpPostData("Saving Attendee Info...", url,
					setAttendeeJsonBody().toString(), access_token,
					AttendeeDetailActivity.this);
			postMethod.execute();*//*
        }*/
        /*else if(requestType.equalsIgnoreCase(Util.GET_BADGE_ID)){
            postMethod = new HttpPostData("Getting Badge Id...",
                    sfdcddetails.instance_url + WebServiceUrls.SA_BADGE_PRINT, setPrintBadgeBody().toString(),
                    access_token, SelfCheckinAttendeeDetailActivity.this);
            postMethod.execute();
        }*/

    }
    private JSONArray setPrintBadgeBody() {
        /*String where_att = " Where EventID = '" + event_id
                + "' AND isBadgeSelected = 'Yes'";
        Cursor updated_badge1 = Util.db.getBadgeTemplate(where_att);
        updated_badge1.moveToFirst();*/
        JSONArray badgearray = new JSONArray();
        JSONObject obj = new JSONObject();
        try {
            obj.put("TicketId", payment_cursor.getString(payment_cursor
                    .getColumnIndex(DBFeilds.ATTENDEE_ID)));
            obj.put("BadgeLabel", NullChecker(payment_cursor.getString(payment_cursor
                    .getColumnIndex(DBFeilds.ATTENDEE_BADGE_LABLE))));
            obj.put("Reason", NullChecker(reason));
            badgearray.put(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return badgearray;
    }
    private void dismissProgressDialog() {
        if(progressDialog!=null) {
            if(progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }
    private String getDbValue(String Dbcolumn){
        if(payment_cursor==null){
            getAttendeeCursor();
        }
        String DbValue=Util.NullChecker(payment_cursor.getString(payment_cursor
                .getColumnIndex(Dbcolumn))).trim();
        return DbValue;
    }

    public void getAttendeeCursor(){
        //badge_id= attendee_intent.getStringExtra("BADGE_ID");
        whereClause = " where " + DBFeilds.ATTENDEE_EVENT_ID + " = '"
                + checked_in_eventId + "'" + " AND " + DBFeilds.ATTENDEE_ID
                + " = " + "'" + attendee_id + "'" + " AND "
                + DBFeilds.ATTENDEE_ORDER_ID + " = " + "'" + order_id + "'";
        payment_cursor = Util.db.getAttendeeDetails(whereClause);
        payment_cursor.moveToFirst();
    }
    private JSONObject setAttendeeJsonBody() {
        JSONObject json = null;
        try {
            json = new JSONObject();
            json.put("fn", att_fname);
            json.put("ln", att_lname);
            json.put("email", att_email);
            json.put("comp", att_company);
            if (att_company.equals(getDbValue(DBFeilds.ATTENDEE_COMPANY)))
                json.put("compid", getDbValue(DBFeilds.ATTENDEE_COMPANY_ID));
            else
                json.put("compid", "");
            json.put("mobile", att_mobile);
            json.put("BPhone", getDbValue(DBFeilds.ATTENDEE_WORK_PHONE));
            json.put("phone", getDbValue(DBFeilds.ATTENDEE_HOME_PHONE));
            json.put("title", att_job_title);
            json.put("badgelabel", getDbValue(DBFeilds.ATTENDEE_BADGE_LABLE));
            json.put("CustomBarcode", getDbValue(DBFeilds.ATTENDEE_CUSTOM_BARCODE));
            String image="";
            if(attendee_photo!=null)
                image=NullChecker(Util.db.getimagedata(Util.db.getByteArray(attendee_photo)));
            if(payment_cursor.getString(payment_cursor.getColumnIndex(DBFeilds.ATTENDEE_IMAGE)).length()>0&&NullChecker(image).length()==0){
                json.put("UserPic",payment_cursor.getString(payment_cursor.getColumnIndex(DBFeilds.ATTENDEE_IMAGE)));
            }
            else if(NullChecker(image).length()>0){
                json.put("AttendeeImage",image);
            }
            else {
                json.put("AttendeeImage","");
            }
           /* //String image=Util.db.getimagedata(Util.db.getByteArray(attendee_photo));
            if(getDbValue(DBFeilds.ATTENDEE_IMAGE).length()>0){
                json.put("UserPic",getDbValue(DBFeilds.ATTENDEE_IMAGE));
            }
            *//*else if(NullChecker(image).length()>0){
                json.put("AttendeeImage",image);
            }*//*
            else {
                json.put("AttendeeImage","");
            }*/
            json.put("tag", "");
            json.put("seatno", getDbValue(DBFeilds.ATTENDEE_TICKET_SEAT_NUMBER));
            json.put("note", getDbValue(DBFeilds.ATTENDEE_NOTE));
            String item_id = getDbValue(DBFeilds.ATTENDEE_ITEM_ID);
            ArrayList<RegistrationSettingsController> reg_setting_list = Util.db.getRegSettingsList("where "+DBFeilds.REG_ITEM_ID+" = '"+item_id+"'");
            for(RegistrationSettingsController each_setting : reg_setting_list){
                if(each_setting.Column_Name__c.equalsIgnoreCase(getString(R.string.work_address)) && Boolean.valueOf(each_setting.Included__c)){
                    json.put("BAddress1", getDbValue(DBFeilds.ATTENDEE_WORK_ADDRESS_1));
                    json.put("BAddress2", getDbValue(DBFeilds.ATTENDEE_WORK_ADDRESS_2));
                    json.put("BCity", getDbValue(DBFeilds.ATTENDEE_WORK_CITY));
                    json.put("BZipcode", getDbValue(DBFeilds.ATTENDEE_WORK_ZIPCODE));

                    if (!Util.db.getStateId(getDbValue(DBFeilds.ATTENDEE_WORK_STATE).trim()).isEmpty())
                        json.put("BState", Util.db.getStateId(getDbValue(DBFeilds.ATTENDEE_WORK_STATE).trim()));
                    if (!Util.db.getCountryId(getDbValue(DBFeilds.ATTENDEE_WORK_COUNTRY).trim()).isEmpty())
                        json.put("BCountry", Util.db.getCountryId(NullChecker(payment_cursor.getString(payment_cursor.getColumnIndex(DBFeilds.ATTENDEE_WORK_COUNTRY))).trim()));
                }else if(each_setting.Column_Name__c.equalsIgnoreCase(getString(R.string.home_address)) && Boolean.valueOf(each_setting.Included__c)){
                    json.put("add1", getDbValue(DBFeilds.ATTENDEE_HOME_ADDRESS_1));
                    json.put("add2", getDbValue(DBFeilds.ATTENDEE_HOME_ADDRESS_2));
                    json.put("city", getDbValue(DBFeilds.ATTENDEE_HOME_CITY));
                    if (!Util.db.getStateId(getDbValue(DBFeilds.ATTENDEE_HOME_STATE).trim()).isEmpty())
                        json.put("state", Util.db.getStateId(getDbValue(DBFeilds.ATTENDEE_HOME_STATE).trim()));
                    if (!Util.db.getCountryId(getDbValue(DBFeilds.ATTENDEE_HOME_COUNTRY).trim()).isEmpty())
                        json.put("country", Util.db.getCountryId(getDbValue(DBFeilds.ATTENDEE_HOME_COUNTRY).trim()));
                    json.put("zipcode", getDbValue(DBFeilds.ATTENDEE_HOME_ZIPCODE));
                }
            }

            json.put("tid", getDbValue(DBFeilds.ATTENDEE_ID));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        try {
            if(txtprint_selfcheckin!=null)
                txtprint_selfcheckin.setEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public void parseJsonResponse(String response) {
        txtprint_selfcheckin.setEnabled(true);
        if(!isValidResponse(response)){
            openSessionExpireAlert(errorMessage(response));
        }
        if(requestType.equalsIgnoreCase(Util.LOAD_BADGE)){
            Type listType = new TypeToken<List<BadgeResponseNew>>() {}.getType();
            List<BadgeResponseNew> badges =  new Gson().fromJson(response, listType);
            AppUtils.displayLog("---------------- parseJsonResponse Badge Size----------", ":"+checkedin_event_record.Events.Mobile_Default_Badge__c+" : " + response);
            Util.db.deleteBadges(checked_in_eventId);
            sharedPreferences.edit().clear().commit(); //TODO
            for(BadgeResponseNew badge : badges){
                badge.badge.event_id = checked_in_eventId;
                Util.db.InsertAndUpdateBadgeTemplateNew(badge);
            }

            if(isBadgeSelected()){
                // printAttendeeRequest();
            }else{
                showSingleButtonDialog("Alert",
                        "No Badge Selected, Please contact your Event Organizer!",this);

            }

        }
        else if (requestType.equals(Util.ATTENDEE_DETAIL)) {
            try {
                gson = new Gson();
                OrderDetailsHandler orderDetailsHandler = (OrderDetailsHandler) gson.fromJson(response, OrderDetailsHandler.class);
                if (orderDetailsHandler.ticketsInn.size() > 0) {
                    Util.db.InsertAndUpdateAttendee(orderDetailsHandler.ticketsInn, "");
                }
                if (orderDetailsHandler.listValues.size() > 0) {
                    Util.db.InsertAndUpdateAttendeeListValues(orderDetailsHandler.listValues);
                }

                if(Util.getselfcheckinbools(Util.ISSELFCHECKIN)){
                    if (!selfcheckinSaveandPrint) {
                        showCustomToast(SelfCheckinAttendeeDetailActivity.this,"Saved Successfully!",R.drawable.img_like,R.drawable.toast_greenroundededge,true);
                        finish();
                        // Toast.makeText(SelfCheckinAttendeeDetailActivity.this, "Saved Successfully", Toast.LENGTH_LONG).show();
                    } else if(Util.getselfcheckinbools(Util.ISREPRINTALLOWED)&&selfcheckinSaveandPrint){
                        showCustomToast(SelfCheckinAttendeeDetailActivity.this,"Saved Successfully!",R.drawable.img_like,R.drawable.toast_greenroundededge,true);
                        if(!getDbValue(DBFeilds.ATTENDEE_BADGEID).isEmpty()){
                            selfcheckinSaveandPrint = false;
                            isprinterconnectedopendialog();
                        } else {
                            selfcheckinSaveandPrint = false;
                            callBadgeid();
                        }
                    }else if(!Util.getselfcheckinbools(Util.ISREPRINTALLOWED)&&selfcheckinSaveandPrint){
                        selfcheckinSaveandPrint = false;
                        showCustomToast(SelfCheckinAttendeeDetailActivity.this,"Saved Successfully!",R.drawable.img_like,R.drawable.toast_greenroundededge,true);

                        // Toast.makeText(SelfCheckinAttendeeDetailActivity.this, "Saved Successfully", Toast.LENGTH_LONG).show();
                        if(!getDbValue(DBFeilds.ATTENDEE_BADGEID).isEmpty()&&getDbValue(DBFeilds.ATTENDEE_BADGE_PRINTSTATUS).equalsIgnoreCase("Printed")){
                            showBadgeAlreadyPrinted();
                        }else {
                            callBadgeid();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                startErrorAnimation(
                        getResources().getString(R.string.network_error1),
                        txt_error_msg);
            }
        }
    }

    @Override
    public void insertDB() {

    }

    @Override
    public void onClick(View v) {
        if(v == back_layout){
            print_badge.setVisibility(View.GONE);
            frame_transparentbadge.setVisibility(View.GONE);
            Intent i = new Intent();
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            setResult(2017,i);
            finish();
        }
    }
    public void isprinterconnectedopendialog(){
        if(isBadgeSelected()) {
            if(!PrinterDetails.selectedPrinterPrefrences.getString("printer", "").isEmpty()) {
                if(isValidate_badge_reg_settings&&getDbValue(DBFeilds.ATTENDEE_BADGE_PRINTSTATUS).equalsIgnoreCase("Printed")){
                    openprintDialog();
                }
                else{
                    callBadgeid();
                }

            }
            else{
                txtprint_selfcheckin.setEnabled(true);
                Util.setCustomAlertDialog(SelfCheckinAttendeeDetailActivity.this);
                Util.openCustomDialog("Alert", "Printer is not connected.Please contact event Organizer!");
                Util.txt_okey.setText("OK");
                Util.alert_dialog.setCancelable(false);
                Util.txt_dismiss.setVisibility(View.GONE);
                Util.txt_okey.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        Util.alert_dialog.dismiss();
                        finish();
                    }
                });
            }
        }else{
            BaseActivity.showSingleButtonDialog("Alert",
                    "No Badge Selected, Please contact your Event Organizer!",this);
        }
    }
    private void doSaveAndPrint(String attendeeid) throws SQLException {

        PrintAndCheckin printT= new PrintAndCheckin();
        PrintDetails printDetails=new PrintDetails();
        printDetails.attendeeId=attendeeid;
        printDetails.checked_in_eventId=checked_in_eventId;
        printDetails.frame_transparentbadge=frame_transparentbadge;
        printDetails.order_id=order_id;
        printDetails.print_badge=print_badge;
        printDetails.sfdcddetails=sfdcddetails;
        printDetails.reason=reason;
        printDetails.isselfCheckinbool=Util.getselfcheckinbools(Util.ISSELFCHECKIN);
        requestType = "Check in";
        printT.doSaveAndPrint(SelfCheckinAttendeeDetailActivity.this,printDetails);


    }
    private void  showBadgeAlreadyPrinted(){
        Util.setCustomAlertDialog(SelfCheckinAttendeeDetailActivity.this);
        Util.alert_dialog.setCancelable(false);
        Util.openCustomDialog("Alert",
                "Your Badge is Already Printed.Please contact Event Organizer!");
        Util.txt_okey.setText("Ok");
        Util.txt_dismiss.setVisibility(View.GONE);
        Util.alert_dialog.setCancelable(false);
        Util.txt_okey.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Util.alert_dialog.dismiss();
                finish();
            }
        });
        txtprint_selfcheckin.setEnabled(true);
    }

    void setDataAndEditTouchListener(LinearLayout linearLayout, TextView textView, final EditText editText, String labelname, final String tagname, final String value, final String groupname, final String feildtype) {
        try {
            linearLayout.setVisibility(View.VISIBLE);
            textView.setText(labelname);
            editText.setText(value);
            editText.setTag(tagname);//.toLowerCase()
            editText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        final String beforetext = editText.getText().toString();
                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if (!beforetext.equalsIgnoreCase(editText.getText().toString())) {
                                    fields = new AttendeeFeildObj();
                                    fields.group_display_name = groupname;
                                    fields.field_api_name = editText.getTag().toString();
                                    fields.field_data = editText.getText().toString();
                                    fields.field_type = feildtype;
                                    addobject(fields.field_api_name, fields);
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                            }
                        });
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setDataAndEditTouchListenerforAddressinfo(LinearLayout linearLayout, TextView textView, final EditText editText, String labelname, final String tagname, final String value, final String groupname) {
        try {
            linearLayout.setVisibility(View.VISIBLE);
            textView.setText(labelname);
            editText.setText(value);
            editText.setTag(tagname);
            editText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        final String beforetext = editText.getText().toString();
                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if (!beforetext.equalsIgnoreCase(editText.getText().toString())) {
                                    String tagname = editText.getTag().toString();
                                    String editvalue = NullChecker(editText.getText().toString());
                                    Log.i("", "---------editfeilds----" + tagname);
                                    String data = "{\"attributes\":{\"type\":\"BLN_Address__c\",\"url\":\"/services/data/v46.0/sobjects/BLN_Address__c/\"},\"Id\":\"\",\"Address1__c\":\"\",\"Address2__c\":\"\",\"City__c\":\"\",\"Country__c\":\"\",\"State__c\":\"\",\"ZipCode__c\":\"\"}";
                                    fields = new AttendeeFeildObj();
                                    //fields.group_display_name = groupname;
                                    if (groupname.equalsIgnoreCase(Util.WORK_ADDRESS)) {
                                        fields.field_api_name = "work_address__c";
                                        if (tagname.equalsIgnoreCase("Address1__c")) {
                                            workaddress1 = editvalue;
                                        } else if (tagname.equalsIgnoreCase("Address2__c")) {
                                            workaddress2 = editvalue;
                                        } else if (tagname.equalsIgnoreCase("City__c")) {
                                            workcity = editvalue;
                                        } else if (tagname.equalsIgnoreCase("Zipcode__c")) {
                                            workzipcode = editvalue;
                                        }
                                        data = "{\"attributes\":{\"type\":\"BLN_Address__c\",\"url\":\"/services/data/v46.0/sobjects/BLN_Address__c/\"},\"Id\":\"\",\"Address1__c\":\"" + workaddress1 + "\",\"Address2__c\":\"" + workaddress2 + "\",\"City__c\":\"" + workcity + "\",\"Country__c\":\"" + Util.db.getCountryId(workcountry) + "\",\"State__c\":\"" + Util.db.getStateId(workstate) + "\",\"ZipCode__c\":\"" + workzipcode + "\"}";
                                    } else if (groupname.equalsIgnoreCase(Util.BILLING_ADDRESS)) {
                                        fields.field_api_name = "billing_address__c";
                                        if (tagname.equalsIgnoreCase("Address1__c")) {
                                            billingaddress1 = editvalue;
                                        } else if (tagname.equalsIgnoreCase("Address2__c")) {
                                            billingaddress2 = editvalue;
                                        } else if (tagname.equalsIgnoreCase("City__c")) {
                                            billingcity = editvalue;
                                        } else if (tagname.equalsIgnoreCase("Zipcode__c")) {
                                            billingzipcode = editvalue;
                                        }
                                        data = "{\"attributes\":{\"type\":\"BLN_Address__c\",\"url\":\"/services/data/v46.0/sobjects/BLN_Address__c/\"},\"Id\":\"\",\"Address1__c\":\"" + billingaddress1 + "\",\"Address2__c\":\"" + billingaddress2 + "\",\"City__c\":\"" + billingcity + "\",\"Country__c\":\"" + Util.db.getCountryId(billingcountry) + "\",\"State__c\":\"" + Util.db.getStateId(billingstate) + "\",\"ZipCode__c\":\"" + billingzipcode + "\"}";

                                    } else if (groupname.equalsIgnoreCase(Util.HOME_ADDRESS)) {
                                        fields.field_api_name = "home_address__c";
                                        if (tagname.equalsIgnoreCase("Address1__c")) {
                                            homeaddress1 = editvalue;
                                        } else if (tagname.equalsIgnoreCase("Address2__c")) {
                                            homeaddress2 = editvalue;
                                        } else if (tagname.equalsIgnoreCase("City__c")) {
                                            homecity = editvalue;
                                        } else if (tagname.equalsIgnoreCase("Zipcode__c")) {
                                            homezipcode = editvalue;
                                        }
                                        data = "{\"attributes\":{\"type\":\"BLN_Address__c\",\"url\":\"/services/data/v46.0/sobjects/BLN_Address__c/\"},\"Id\":\"\",\"Address1__c\":\"" + homeaddress1 + "\",\"Address2__c\":\"" + homeaddress2 + "\",\"City__c\":\"" + homecity + "\",\"Country__c\":\"" + Util.db.getCountryId(homecountry) + "\",\"State__c\":\"" + Util.db.getStateId(homestate) + "\",\"ZipCode__c\":\"" + homezipcode + "\"}";

                                    }


                                    fields.field_data = data;
                                    fields.group_display_name = "Address Information";
                                    //"{\"attributes\":{\"type\":\"BLN_Address__c\"},\"Address1__c\":\"Patterson Village Court\"}"
                                    addobject(fields.field_api_name, fields);
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                            }
                        });
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void setCountryAdapterAndStateItemSelectedListener(final Spinner spin_item, final Spinner spn_stateitem, final String feildname, final String value, final String statevalue) {
        try {
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(SelfCheckinAttendeeDetailActivity.this, android.R.layout.simple_spinner_item, Util.db.getDefaultCountryListforDetail(checked_in_eventId));
            spin_item.setAdapter(spinnerArrayAdapter);
            spin_item.setTag(feildname);
            spn_stateitem.setTag(feildname);
            int k = 0;
            if(!value.isEmpty()) {
                for (String key : Util.db.getDefaultCountryListforDetail(checked_in_eventId)) {
                    if (value.equalsIgnoreCase(key)) {
                        spin_item.setSelection(k, false);
                        ArrayAdapter<String> stateArrayAdapter = new ArrayAdapter<String>(SelfCheckinAttendeeDetailActivity.this, android.R.layout.simple_spinner_item, Util.db.getStateList(Util.db.getCountryId(value)));
                        spn_stateitem.setAdapter(stateArrayAdapter);
                        setStateAdapterItemSelectedListener(spn_stateitem, statevalue, feildname, value);
                        break;
                    }
                    k++;
                }
            }/*else {
                for (String key : Util.db.getDefaultCountryListforDetail(checked_in_eventId)) {
                    if (Util.db.getDefaultCountry(checked_in_eventId).equalsIgnoreCase(key)) {
                        spin_item.setSelection(k, false);
                        ArrayAdapter<String> stateArrayAdapter = new ArrayAdapter<String>(SelfCheckinAttendeeDetailActivity.this, android.R.layout.simple_spinner_item, Util.db.getStateList(Util.db.getCountryId(Util.db.getDefaultCountry(checked_in_eventId))));
                        spn_stateitem.setAdapter(stateArrayAdapter);
                        setStateAdapterItemSelectedListener(spn_stateitem, statevalue, "", Util.db.getDefaultCountry(checked_in_eventId));
                    }
                    k++;
                }
            }*/
            spin_item.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    spin_item.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                   int arg2, long arg3) {
                            if (!value.isEmpty()&&spin_item.getSelectedItem().toString().equalsIgnoreCase("--Select--")||value.isEmpty()&&spin_item.getSelectedItem().toString().equalsIgnoreCase("--Select--")) {
                                ArrayAdapter<String> stateArrayAdapter = new ArrayAdapter<String>(SelfCheckinAttendeeDetailActivity.this, android.R.layout.simple_spinner_item, Util.db.getStateList(Util.db.getCountryId(spin_item.getSelectedItem().toString())));
                                spn_stateitem.setAdapter(stateArrayAdapter);
                                String data = "{\"attributes\":{\"type\":\"BLN_Address__c\",\"url\":\"/services/data/v46.0/sobjects/BLN_Address__c/\"},\"Id\":\"\",\"Address1__c\":\"\",\"Address2__c\":\"\",\"City__c\":\"\",\"Country__c\":\"" +""+ "\",\"State__c\":\"\",\"ZipCode__c\":\"\"}";
                                if (feildname.equalsIgnoreCase("work_address__c")) {
                                    workcountry = "";
                                    data = "{\"attributes\":{\"type\":\"BLN_Address__c\",\"url\":\"/services/data/v46.0/sobjects/BLN_Address__c/\"},\"Id\":\"\",\"Address1__c\":\"" + workaddress1 + "\",\"Address2__c\":\"" + workaddress2 + "\",\"City__c\":\"" + workcity + "\",\"Country__c\":\"" + "" + "\",\"State__c\":\"" + "" + "\",\"ZipCode__c\":\"" + workzipcode + "\"}";
                                }
                                else if (feildname.equalsIgnoreCase("billing_address__c")) {
                                    billingcountry = "";
                                    data = "{\"attributes\":{\"type\":\"BLN_Address__c\",\"url\":\"/services/data/v46.0/sobjects/BLN_Address__c/\"},\"Id\":\"\",\"Address1__c\":\"" + billingaddress1 + "\",\"Address2__c\":\"" + billingaddress2 + "\",\"City__c\":\"" + billingcity + "\",\"Country__c\":\"" + "" + "\",\"State__c\":\"" + "" + "\",\"ZipCode__c\":\"" + billingzipcode + "\"}";
                                }
                                else if (feildname.equalsIgnoreCase("home_address__c")) {
                                    homecountry = "";
                                    data = "{\"attributes\":{\"type\":\"BLN_Address__c\",\"url\":\"/services/data/v46.0/sobjects/BLN_Address__c/\"},\"Id\":\"\",\"Address1__c\":\"" + homeaddress1 + "\",\"Address2__c\":\"" + homeaddress2 + "\",\"City__c\":\"" + homecity + "\",\"Country__c\":\"" + "" + "\",\"State__c\":\"" +""+ "\",\"ZipCode__c\":\"" + homezipcode + "\"}";
                                }
                                fields.field_api_name = spin_item.getTag().toString();
                                fields.field_data = data;
                                //fields.field_type = "String";
                                fields.group_display_name = "Address Information";
                                addobject(fields.field_api_name, fields);
                            } else if ((!value.isEmpty() && !spin_item.getSelectedItem().toString().equalsIgnoreCase(value)) || ((value.isEmpty() && !spin_item.getSelectedItem().toString().equalsIgnoreCase("--Select--"))))
                            {
                                //&&!value.isEmpty(
                                //if (!value.isEmpty()&&!spin_item.getSelectedItem().toString().equalsIgnoreCase(value)) {//&&!value.isEmpty(
                                ArrayAdapter<String> stateArrayAdapter = new ArrayAdapter<String>(SelfCheckinAttendeeDetailActivity.this, android.R.layout.simple_spinner_item, Util.db.getStateList(Util.db.getCountryId(spin_item.getSelectedItem().toString())));
                                spn_stateitem.setAdapter(stateArrayAdapter);
                                //workcountry= spin_item.getSelectedItem().toString();
                                String data = "{\"attributes\":{\"type\":\"BLN_Address__c\",\"url\":\"/services/data/v46.0/sobjects/BLN_Address__c/\"},\"Id\":\"\",\"Address1__c\":\"\",\"Address2__c\":\"\",\"City__c\":\"\",\"Country__c\":\"" + Util.db.getCountryId(spin_item.getSelectedItem().toString()) + "\",\"State__c\":\"\",\"ZipCode__c\":\"\"}";
                                Log.i("---------country----", "---------country----" + feildname + "----selected value------" + spin_item.getSelectedItem().toString());
                                fields = new AttendeeFeildObj();
                                if (feildname.equalsIgnoreCase("work_address__c")) {
                                    fields.field_api_name = "work_address__c";
                                    workcountry = spin_item.getSelectedItem().toString();
                                    data = "{\"attributes\":{\"type\":\"BLN_Address__c\",\"url\":\"/services/data/v46.0/sobjects/BLN_Address__c/\"},\"Id\":\"\",\"Address1__c\":\"" + workaddress1 + "\",\"Address2__c\":\"" + workaddress2 + "\",\"City__c\":\"" + workcity + "\",\"Country__c\":\"" + Util.db.getCountryId(workcountry) + "\",\"State__c\":\"" + Util.db.getStateId(workstate) + "\",\"ZipCode__c\":\"" + workzipcode + "\"}";
                                    setStateAdapterItemSelectedListener(spn_stateitem,"","work_address__c",workcountry);

                                } else if (feildname.equalsIgnoreCase("home_address__c")) {
                                    fields.field_api_name = "home_address__c";
                                    homecountry = spin_item.getSelectedItem().toString();
                                    data = "{\"attributes\":{\"type\":\"BLN_Address__c\",\"url\":\"/services/data/v46.0/sobjects/BLN_Address__c/\"},\"Id\":\"\",\"Address1__c\":\"" + homeaddress1 + "\",\"Address2__c\":\"" + homeaddress2 + "\",\"City__c\":\"" + homecity + "\",\"Country__c\":\"" + Util.db.getCountryId(homecountry) + "\",\"State__c\":\"" + Util.db.getStateId(homestate) + "\",\"ZipCode__c\":\"" + homezipcode + "\"}";
                                    setStateAdapterItemSelectedListener(spn_stateitem,"","home_address__c",homecountry);
                                } else if (feildname.equalsIgnoreCase("billing_address__c")) {
                                    fields.field_api_name = "billing_address__c";
                                    billingcountry = spin_item.getSelectedItem().toString();
                                    data = "{\"attributes\":{\"type\":\"BLN_Address__c\",\"url\":\"/services/data/v46.0/sobjects/BLN_Address__c/\"},\"Id\":\"\",\"Address1__c\":\"" + billingaddress1 + "\",\"Address2__c\":\"" + billingaddress2 + "\",\"City__c\":\"" + billingcity + "\",\"Country__c\":\"" + Util.db.getCountryId(billingcountry) + "\",\"State__c\":\"" + Util.db.getStateId(billingstate) + "\",\"ZipCode__c\":\"" + billingzipcode + "\"}";
                                    setStateAdapterItemSelectedListener(spn_stateitem,"","billing_address__c",billingcountry);
                                }

                                //fields.group_display_name = groupname;
                                fields.field_api_name = spin_item.getTag().toString();
                                fields.field_data = data;
                                //fields.field_type = "String";
                                fields.group_display_name = "Address Information";
                                addobject(fields.field_api_name, fields);
                       /* if(Util.db.getCountryId(spin_item.getSelectedItem().toString()).isEmpty()||spin_item.getSelectedItem().toString().equalsIgnoreCase("--Select--"))
                        {
                            ArrayAdapter<String> stateArrayAdapter = new ArrayAdapter<String>(SelfCheckinAttendeeDetailActivity.this, android.R.layout.simple_spinner_item, Util.db.getStateList(Util.db.getCountryId(spin_item.getSelectedItem().toString())));
                            spn_stateitem.setAdapter(stateArrayAdapter);
                            if (feildname.equalsIgnoreCase("work_address__c"))
                                workcountry="";
                            else if (feildname.equalsIgnoreCase("billing_address__c"))
                                billingcountry="";
                            else if (feildname.equalsIgnoreCase("home_address__c"))
                                homecountry="";

                        }*/
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                        }
                    });
                    return false;
                }
            });

         /*   int j = 0;
            for (String key : Util.db.getStateList(Util.db.getCountryId(value))) {
                if (statevalue.equalsIgnoreCase(key)) {
                    spn_stateitem.setSelection(j, false);
                    break;
                }
                j++;
            }
            spn_stateitem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                    if (!spn_stateitem.getSelectedItem().toString().equalsIgnoreCase("--Select--") ) {//&&!value.isEmpty(
                        if(!workcountry.isEmpty())
                            workstate=Util.db.getStateId(spn_stateitem.getSelectedItem().toString());
                        String data ="{\"attributes\":{\"type\":\"BLN_Address__c\",\"url\":\"/services/data/v46.0/sobjects/BLN_Address__c/\"},\"Id\":\"\",\"Address1__c\":\""+workaddress1+"\",\"Address2__c\":\""+workaddress2+"\",\"City__c\":\""+workcity+"\",\"Country__c\":\""+Util.db.getCountryId(workcountry)+"\",\"State__c\":\""+Util.db.getStateId(workstate)+"\",\"ZipCode__c\":\""+workzipcode+"\"}";
                        fields = new AttendeeFeildObj();
                        fields.field_api_name=spn_stateitem.getTag().toString();
                        //fields.group_display_name = groupname;
                        *//*if (groupname.equalsIgnoreCase(Util.WORK_ADDRESS))
                            fields.field_api_name = "work_address__c";
                        else if (groupname.equalsIgnoreCase(Util.BILLING_ADDRESS))
                            fields.field_api_name = "billing_address__c";
                        else if (groupname.equalsIgnoreCase(Util.HOME_ADDRESS))
                            fields.field_api_name = "home_address__c";*//*
                        fields.field_data = data;
                        //fields.field_type = "String";
                        addobject(fields.field_api_name, fields);
                    }
                }  @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });*/
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void setStateAdapterItemSelectedListener(final Spinner spn_stateitem, final String statevalue, final String feildname, final String countryvalue) {
        try {

            ArrayAdapter<String> stateArrayAdapter = new ArrayAdapter<String>(SelfCheckinAttendeeDetailActivity.this, android.R.layout.simple_spinner_item, Util.db.getStateList(Util.db.getCountryId(countryvalue)));
            /*if(countryvalue.isEmpty()&&!countryvalue.equalsIgnoreCase("--select--"))
            {
                stateArrayAdapter = new ArrayAdapter<String>(SelfCheckinAttendeeDetailActivity.this, android.R.layout.simple_spinner_item, Util.db.getStateList(Util.db.getCountryId(countryvalue)));
            }else{

            }*/
            spn_stateitem.setAdapter(stateArrayAdapter);
            spn_stateitem.setTag(feildname);
            int j = 0;
            for (String key : Util.db.getStateList(Util.db.getCountryId(countryvalue))) {
                if (statevalue.equalsIgnoreCase(key)) {
                    spn_stateitem.setSelection(j, false);
                    break;
                }
                j++;
            }
            spn_stateitem.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    spn_stateitem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                   int arg2, long arg3) {

                            if (!statevalue.isEmpty()&&spn_stateitem.getSelectedItem().toString().equalsIgnoreCase("--Select--")||statevalue.isEmpty()&&spn_stateitem.getSelectedItem().toString().equalsIgnoreCase("--Select--")) {
                                String data = "{\"attributes\":{\"type\":\"BLN_Address__c\",\"url\":\"/services/data/v46.0/sobjects/BLN_Address__c/\"},\"Id\":\"\",\"Address1__c\":\"\",\"Address2__c\":\"\",\"City__c\":\"\",\"Country__c\":\"" +""+ "\",\"State__c\":\"\",\"ZipCode__c\":\"\"}";
                                if (feildname.equalsIgnoreCase("work_address__c"))
                                    workstate = "";
                                else if (feildname.equalsIgnoreCase("billing_address__c"))
                                    billingstate = "";
                                else if (feildname.equalsIgnoreCase("home_address__c"))
                                    homestate = "";
                                AppUtils.displayLog("---------state----", "---------state----" + feildname + "--------selected value ------" + spn_stateitem.getSelectedItem().toString());
                                fields = new AttendeeFeildObj();
                                fields.field_api_name = spn_stateitem.getTag().toString();
                                if (feildname.equalsIgnoreCase("work_address__c")) {
                                    workstate = spn_stateitem.getSelectedItem().toString();
                                    data = "{\"attributes\":{\"type\":\"BLN_Address__c\",\"url\":\"/services/data/v46.0/sobjects/BLN_Address__c/\"},\"Id\":\"\",\"Address1__c\":\"" + workaddress1 + "\",\"Address2__c\":\"" + workaddress2 + "\",\"City__c\":\"" + workcity + "\",\"Country__c\":\"" + Util.db.getCountryId(workcountry) + "\",\"State__c\":\"" + Util.db.getStateId(workstate) + "\",\"ZipCode__c\":\"" + workzipcode + "\"}";
                                } else if (feildname.equalsIgnoreCase("home_address__c")) {
                                    homestate = spn_stateitem.getSelectedItem().toString();
                                    data = "{\"attributes\":{\"type\":\"BLN_Address__c\",\"url\":\"/services/data/v46.0/sobjects/BLN_Address__c/\"},\"Id\":\"\",\"Address1__c\":\"" + homeaddress1 + "\",\"Address2__c\":\"" + homeaddress2 + "\",\"City__c\":\"" + homecity + "\",\"Country__c\":\"" + Util.db.getCountryId(homecountry) + "\",\"State__c\":\"" + Util.db.getStateId(homestate) + "\",\"ZipCode__c\":\"" + homezipcode + "\"}";
                                } else if (feildname.equalsIgnoreCase("billing_address__c")) {
                                    billingstate = spn_stateitem.getSelectedItem().toString();
                                    data = "{\"attributes\":{\"type\":\"BLN_Address__c\",\"url\":\"/services/data/v46.0/sobjects/BLN_Address__c/\"},\"Id\":\"\",\"Address1__c\":\"" + billingaddress1 + "\",\"Address2__c\":\"" + billingaddress2 + "\",\"City__c\":\"" + billingcity + "\",\"Country__c\":\"" + Util.db.getCountryId(billingcountry) + "\",\"State__c\":\"" + Util.db.getStateId(billingstate) + "\",\"ZipCode__c\":\"" + billingzipcode + "\"}";
                                }
                                fields.field_data = data;
                                fields.group_display_name = "Address Information";
                                addobject(fields.field_api_name, fields);
                            } else if ((!statevalue.isEmpty() && !spn_stateitem.getSelectedItem().toString().equalsIgnoreCase(statevalue)) || (statevalue.isEmpty() && !spn_stateitem.getSelectedItem().toString().equalsIgnoreCase("--Select--"))) {//&&!value.isEmpty(

                                String data = "{\"attributes\":{\"type\":\"BLN_Address__c\",\"url\":\"/services/data/v46.0/sobjects/BLN_Address__c/\"},\"Id\":\"\",\"Address1__c\":\"\",\"Address2__c\":\"\",\"City__c\":\"\",\"Country__c\":\"\",\"State__c\":\"\",\"ZipCode__c\":\"\"}";
                                Log.i("---------state----", "---------state----" + feildname + "--------selected value ------" + spn_stateitem.getSelectedItem().toString());
                                fields = new AttendeeFeildObj();
                                fields.field_api_name = spn_stateitem.getTag().toString();
                                if (feildname.equalsIgnoreCase("work_address__c")) {
                                    workstate = spn_stateitem.getSelectedItem().toString();
                                    data = "{\"attributes\":{\"type\":\"BLN_Address__c\",\"url\":\"/services/data/v46.0/sobjects/BLN_Address__c/\"},\"Id\":\"\",\"Address1__c\":\"" + workaddress1 + "\",\"Address2__c\":\"" + workaddress2 + "\",\"City__c\":\"" + workcity + "\",\"Country__c\":\"" + Util.db.getCountryId(workcountry) + "\",\"State__c\":\"" + Util.db.getStateId(workstate) + "\",\"ZipCode__c\":\"" + workzipcode + "\"}";
                                } else if (feildname.equalsIgnoreCase("home_address__c")) {
                                    homestate = spn_stateitem.getSelectedItem().toString();
                                    data = "{\"attributes\":{\"type\":\"BLN_Address__c\",\"url\":\"/services/data/v46.0/sobjects/BLN_Address__c/\"},\"Id\":\"\",\"Address1__c\":\"" + homeaddress1 + "\",\"Address2__c\":\"" + homeaddress2 + "\",\"City__c\":\"" + homecity + "\",\"Country__c\":\"" + Util.db.getCountryId(homecountry) + "\",\"State__c\":\"" + Util.db.getStateId(homestate) + "\",\"ZipCode__c\":\"" + homezipcode + "\"}";
                                } else if (feildname.equalsIgnoreCase("billing_address__c")) {
                                    billingstate = spn_stateitem.getSelectedItem().toString();
                                    data = "{\"attributes\":{\"type\":\"BLN_Address__c\",\"url\":\"/services/data/v46.0/sobjects/BLN_Address__c/\"},\"Id\":\"\",\"Address1__c\":\"" + billingaddress1 + "\",\"Address2__c\":\"" + billingaddress2 + "\",\"City__c\":\"" + billingcity + "\",\"Country__c\":\"" + Util.db.getCountryId(billingcountry) + "\",\"State__c\":\"" + Util.db.getStateId(billingstate) + "\",\"ZipCode__c\":\"" + billingzipcode + "\"}";
                                }
                                fields.field_data = data;
                                fields.group_display_name = "Address Information";
                                addobject(fields.field_api_name, fields);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                        }
                    });
                    return false;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void setAdapterAndItemSelectedListener(final Spinner spin_item, final String feildname, final String value, final String groupname,final String feildapiname, final String feildtype) {
        try {
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(SelfCheckinAttendeeDetailActivity.this, android.R.layout.simple_spinner_item, Util.db.getFiterItems(feildname));
            spin_item.setAdapter(spinnerArrayAdapter);
            int k = 0;
            for (String key : Util.db.getFiterItems(feildname)) {
                if (value.equalsIgnoreCase(key)) {
                    spin_item.setSelection(k, false);
                    break;
                }
                k++;
            }
            spin_item.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                    if ((!value.isEmpty() && spin_item.getSelectedItem().toString().equalsIgnoreCase("--None--"))) {
                        fields = new AttendeeFeildObj();
                        fields.group_display_name = groupname;
                        fields.field_api_name = feildapiname;
                        fields.field_data = "";
                        addobject(fields.field_api_name, fields);
                    } else if ((!value.isEmpty() && !spin_item.getSelectedItem().toString().equalsIgnoreCase(value)) || (value.isEmpty() && !spin_item.getSelectedItem().toString().equalsIgnoreCase("--None--"))) {//&&!value.isEmpty(
                        // if (!spin_item.getSelectedItem().toString().isEmpty()&&!value.isEmpty()) {//&&!value.isEmpty()
                        fields = new AttendeeFeildObj();
                        fields.group_display_name = groupname;
                        fields.field_data = Util.db.getPicklistItemId(feildname, spin_item.getSelectedItem().toString());
                        fields.field_api_name = feildapiname;
                        fields.field_type = feildtype;

                        addobject(fields.field_api_name, fields);
                    }else if(value.isEmpty()&&spin_item.getSelectedItem().toString().equalsIgnoreCase("--None--")){
                        removeobject(fields.field_api_name, fields);
                     /*  fields = new AttendeeFeildObj();
                        fields.group_display_name = groupname;
                        fields.field_data = "";
                        fields.field_api_name = feildapiname;
                        fields.field_type = feildtype;
                        addobject(fields.field_api_name, fields);*/

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void addobject(String key, AttendeeFeildObj ee) {
        parentarray.put(key, ee);
    }
    public void removeobject(String key, AttendeeFeildObj ee) {
        try {
            if( parentarray.containsKey(key))
                parentarray.remove(key, ee);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void setPanel_basic() {

        String groupname = Util.BASIC_INFORMATION;
        String labelgroupname = Util.db.getRegGroupLabelNamewithGroupName("where reg_eventid='" + checked_in_eventId + "' AND " + DBFeilds.REG_GROUP_NAME + "= '" + groupname + "'", groupname);
        

        if (groupname.equalsIgnoreCase(Util.BASIC_INFORMATION)) {
           // String whereclause = " OrderTicketDetails.Event_Id = '" + checked_in_eventId + "' AND  OrderTicketDetails." + DBFeilds.ATTENDEE_ID + " = '" + attendee_id + "' AND " + "OrderTicketDetails.Order_Id" + " = '" + order_id + "'";
            // att_ordercursor = Util.db.getAttendeeDetails(whereclause);
            try {
                LinearLayout lay_Prefix__c = linearview.findViewById(R.id.lay_Prefix__c);
                TextView Prefix__c = linearview.findViewById(R.id.Prefix__c);
                Spinner spn_Prefix__c = linearview.findViewById(R.id.spn_Prefix__c);

                LinearLayout lay_Suffix__c = linearview.findViewById(R.id.lay_Suffix__c);
                TextView Suffix__c = linearview.findViewById(R.id.Suffix__c);
                Spinner spn_Suffix__c = linearview.findViewById(R.id.spn_Suffix__c);

                LinearLayout lay_First_Name__c = linearview.findViewById(R.id.lay_First_Name__c);
                TextView First_Name__c = linearview.findViewById(R.id.First_Name__c);
                edt_First_Name__c = linearview.findViewById(R.id.edt_First_Name__c);

                LinearLayout lay_Last_Name__c = linearview.findViewById(R.id.lay_Last_Name__c);
                TextView Last_Name__c = linearview.findViewById(R.id.Last_Name__c);
                edt_Last_Name__c = linearview.findViewById(R.id.edt_Last_Name__c);

                LinearLayout lay_Email__c = linearview.findViewById(R.id.lay_Email__c);
                TextView Email__c = linearview.findViewById(R.id.Email__c);
                edt_Email__c = linearview.findViewById(R.id.edt_Email__c);

                LinearLayout lay_Company_Name = linearview.findViewById(R.id.lay_Company_Name);
                TextView Company_Name = linearview.findViewById(R.id.Company_Name);
                EditText edt_Company_Name = linearview.findViewById(R.id.edt_Company_Name);

                LinearLayout lay_Title__c = linearview.findViewById(R.id.lay_Title__c);
                TextView Title__c = linearview.findViewById(R.id.Title__c);
                EditText edt_Title__c = linearview.findViewById(R.id.edt_Title__c);

                LinearLayout lay_Phone__c = linearview.findViewById(R.id.lay_Phone__c);
                TextView Phone__c = linearview.findViewById(R.id.Phone__c);
                EditText edt_Phone__c = linearview.findViewById(R.id.edt_Phone__c);

                LinearLayout lay_Work_Phone__c = linearview.findViewById(R.id.lay_Work_Phone__c);
                TextView Work_Phone__c = linearview.findViewById(R.id.Work_Phone__c);
                EditText edt_Work_Phone__c = linearview.findViewById(R.id.edt_Work_Phone__c);

                LinearLayout lay_Home_Phone__c = linearview.findViewById(R.id.lay_Home_Phone__c);
                TextView Home_Phone__c = linearview.findViewById(R.id.Home_Phone__c);
                EditText edt_Home_Phone__c = linearview.findViewById(R.id.edt_Home_Phone__c);

                LinearLayout lay_Gender__c = linearview.findViewById(R.id.lay_Gender__c);
                TextView Gender__c = linearview.findViewById(R.id.Gender__c);
                Spinner spn_Gender__c = linearview.findViewById(R.id.spn_Gender__c);

                LinearLayout lay_DOB__c = linearview.findViewById(R.id.lay_DOB__c);
                TextView DOB__c = linearview.findViewById(R.id.DOB__c);
                EditText edt_DOB__c = linearview.findViewById(R.id.edt_DOB__c);

                LinearLayout lay_Age__c = linearview.findViewById(R.id.lay_Age__c);
                TextView Age__c = linearview.findViewById(R.id.Age__c);
                EditText edt_Age__c = linearview.findViewById(R.id.edt_Age__c);

                LinearLayout lay_Badge_Label__c = linearview.findViewById(R.id.lay_Badge_Label__c);
                TextView Badge_Label__c = linearview.findViewById(R.id.Badge_Label__c);
                EditText edt_Badge_Label__c = linearview.findViewById(R.id.edt_Badge_Label__c);

                LinearLayout lay_Tag_No__c = linearview.findViewById(R.id.lay_Tag_No__c);
                TextView Tag_No__c = linearview.findViewById(R.id.Tag_No__c);
                EditText edt_Tag_No__c = linearview.findViewById(R.id.edt_Tag_No__c);

                LinearLayout lay_Note__c = linearview.findViewById(R.id.lay_Note__c);
                TextView Note__c = linearview.findViewById(R.id.Note__c);
                EditText edt_Note__c = linearview.findViewById(R.id.edt_Note__c);

                c = Util.db.getRegChildswithGroupName("where Item_Reg_Settings.reg_group_name__c = '" + groupname + "' AND  Item_Reg_Settings.reg_eventid = '" + checked_in_eventId + "' AND Item_Reg_Settings."+DBFeilds.REG_ITEM_ID+"='true'" + whereclause);
                if (c != null && c.getCount() > 0) {
                    for (int i = 0; i < c.getCount(); i++) {
                        c.moveToPosition(i);
                        String columnname = c.getString(c.getColumnIndex(DBFeilds.REG_COLUMN_NAME));
                        String labelname = c.getString(c.getColumnIndex(DBFeilds.REG_LABEL_NAME));
                        String feildtype = NullChecker(c.getString(c.getColumnIndex(DBFeilds.REG_FIELDTYPE)));
                        String feildname = "";
                        if (columnname.equals(DBFeilds.ATTENDEE_PREFIX)) {
                            feildname = DBFeilds.ATTENDEE_PREFIX;
                            columnname = DBFeilds.ATTENDEE_PREFIX;
                            lay_Prefix__c.setVisibility(View.VISIBLE);
                            Prefix__c.setText(labelname);
                            String value = NullChecker(c.getString(c.getColumnIndex(columnname)));
                            setAdapterAndItemSelectedListener(spn_Prefix__c, feildname, value, groupname,feildname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_SUFFIX)) {
                            feildname = DBFeilds.ATTENDEE_SUFFIX;
                            columnname = DBFeilds.ATTENDEE_SUFFIX;
                            lay_Suffix__c.setVisibility(View.VISIBLE);
                            Suffix__c.setText(labelname);
                            String value = NullChecker(c.getString(c.getColumnIndex(columnname)));
                            setAdapterAndItemSelectedListener(spn_Suffix__c, feildname, value, groupname,feildname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_GENDER)) {
                            feildname = Util.ATTENDEE_GENDER;
                            columnname = DBFeilds.ATTENDEE_GENDER;
                            lay_Gender__c.setVisibility(View.VISIBLE);
                            Gender__c.setText(labelname);
                            String value = NullChecker(c.getString(c.getColumnIndex(columnname)));
                            setAdapterAndItemSelectedListener(spn_Gender__c, feildname, value, groupname,feildname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_FIRST_NAME)) {
                            att_fname=c.getString(c.getColumnIndex(columnname));
                            setDataAndEditTouchListener(lay_First_Name__c, First_Name__c, edt_First_Name__c, labelname, columnname, att_fname, groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_LAST_NAME)) {
                            att_lname=c.getString(c.getColumnIndex(columnname));
                            setDataAndEditTouchListener(lay_Last_Name__c, Last_Name__c, edt_Last_Name__c, labelname, columnname, att_lname, groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_EMAIL_ID)) {
                            att_email=c.getString(c.getColumnIndex(columnname));
                            setDataAndEditTouchListener(lay_Email__c, Email__c, edt_Email__c, labelname, columnname, att_email, groupname, feildtype);
                        } else if (columnname.equals(Util.ATTENDEECOMPANY)) {
                            columnname = DBFeilds.ATTENDEE_COMPANY;
                            setDataAndEditTouchListener(lay_Company_Name, Company_Name, edt_Company_Name, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_JOB_TILE)) {
                            feildname = "tkt_job_title__c";
                            setDataAndEditTouchListener(lay_Title__c, Title__c, edt_Title__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_PHONE)) {
                            setDataAndEditTouchListener(lay_Phone__c, Phone__c, edt_Phone__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_WORK_PHONE)) {
                            setDataAndEditTouchListener(lay_Work_Phone__c, Work_Phone__c, edt_Work_Phone__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_HOME_PHONE)) {
                            setDataAndEditTouchListener(lay_Home_Phone__c, Home_Phone__c, edt_Home_Phone__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_DOB)) {
                            setDataAndEditTouchListener(lay_DOB__c, DOB__c, edt_DOB__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                            //edt_DOB__c.addTextChangedListener(new addListenerOnTextChange(this, edt_DOB__c));

                        } else if (columnname.equals(DBFeilds.ATTENDEE_AGE)) {
                            setDataAndEditTouchListener(lay_Age__c, Age__c, edt_Age__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_TAG)) {
                            setDataAndEditTouchListener(lay_Tag_No__c, Tag_No__c, edt_Tag_No__c, labelname, columnname, c.getString(c.getColumnIndex(DBFeilds.ATTENDEE_TICKET_SEAT_NUMBER)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_BADGE_LABLE)) {
                            setDataAndEditTouchListener(lay_Badge_Label__c, Badge_Label__c, edt_Badge_Label__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals("Note__c")) {
                            setDataAndEditTouchListener(lay_Note__c, Note__c, edt_Note__c, labelname, "Note__c", c.getString(c.getColumnIndex("note")), groupname, feildtype);
                        }

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void setPanel_workaddress() {
        String groupname = Util.WORK_ADDRESS;
        String labelgroupname = Util.db.getRegGroupLabelNamewithGroupName("where reg_eventid='" + checked_in_eventId + "' AND " + DBFeilds.REG_GROUP_NAME + "= '" + groupname + "'", groupname);
        if (groupname.equalsIgnoreCase(Util.WORK_ADDRESS)) {
            try {
                LinearLayout lay_WorkAddress1__c = linearview.findViewById(R.id.lay_WorkAddress1__c);
                TextView WorkAddress1__c = linearview.findViewById(R.id.WorkAddress1__c);
                EditText edt_WorkAddress1__c = linearview.findViewById(R.id.edt_WorkAddress1__c);

                LinearLayout lay_WorkAddress2__c = linearview.findViewById(R.id.lay_WorkAddress2__c);
                TextView WorkAddress2__c = linearview.findViewById(R.id.WorkAddress2__c);
                EditText edt_WorkAddress2__c = linearview.findViewById(R.id.edt_WorkAddress2__c);

                LinearLayout lay_Work_City = linearview.findViewById(R.id.lay_Work_City);
                TextView Work_City = linearview.findViewById(R.id.Work_City);
                EditText edt_Work_City = linearview.findViewById(R.id.edt_Work_City);

                LinearLayout lay_Work_Country = linearview.findViewById(R.id.lay_Work_Country);
                TextView Work_Country = linearview.findViewById(R.id.Work_Country);
                Spinner spn_Work_Country = linearview.findViewById(R.id.spn_Work_Country);

                LinearLayout lay_Work_State = linearview.findViewById(R.id.lay_Work_State);
                TextView Work_State = linearview.findViewById(R.id.Work_State);
                Spinner spn_Work_State = linearview.findViewById(R.id.spn_Work_State);

                LinearLayout lay_Work_ZipCode = linearview.findViewById(R.id.lay_Work_ZipCode);
                TextView Work_ZipCode = linearview.findViewById(R.id.Work_ZipCode);
                EditText edt_Work_ZipCode = linearview.findViewById(R.id.edt_Work_ZipCode);


                c = Util.db.getRegChildswithGroupName("where Item_Reg_Settings.reg_group_name__c = '" + groupname + "' AND  Item_Reg_Settings.reg_eventid = '" + checked_in_eventId + "' AND Item_Reg_Settings."+DBFeilds.REG_ITEM_ID+"='true'" + whereclause);
                if (c != null && c.getCount() > 0) {
                    for (int i = 0; i < c.getCount(); i++) {
                        c.moveToPosition(i);
                        String columnname = c.getString(c.getColumnIndex(DBFeilds.REG_COLUMN_NAME));
                        String labelname = c.getString(c.getColumnIndex(DBFeilds.REG_LABEL_NAME));
                        String feildtype = NullChecker(c.getString(c.getColumnIndex(DBFeilds.REG_FIELDTYPE)));
                        String feildname = "";
                        if (columnname.equals(Util.ATTENDEE_WORK_ADDRESS_1)) {
                            columnname = DBFeilds.ATTENDEE_WORK_ADDRESS_1;
                            workaddress1 = c.getString(c.getColumnIndex(columnname));
                            setDataAndEditTouchListenerforAddressinfo(lay_WorkAddress1__c, WorkAddress1__c, edt_WorkAddress1__c, labelname, "Address1__c", workaddress1, groupname);
                        } else if (columnname.equals(Util.ATTENDEE_WORK_ADDRESS_2)) {
                            columnname = DBFeilds.ATTENDEE_WORK_ADDRESS_2;
                            workaddress2 = c.getString(c.getColumnIndex(columnname));
                            setDataAndEditTouchListenerforAddressinfo(lay_WorkAddress2__c, WorkAddress2__c, edt_WorkAddress2__c, labelname, "Address2__c", workaddress2, groupname);
                        } else if (columnname.equals(Util.ATTENDEE_WORK_CITY)) {
                            columnname = DBFeilds.ATTENDEE_WORK_CITY;
                            workcity = c.getString(c.getColumnIndex(columnname));
                            setDataAndEditTouchListenerforAddressinfo(lay_Work_City, Work_City, edt_Work_City, labelname, "City__c", workcity, groupname);
                        } else if (columnname.equals(Util.ATTENDEE_WORK_COUNTRY)) {
                            feildname = "work_address__c";
                            columnname = DBFeilds.ATTENDEE_WORK_COUNTRY;
                            lay_Work_Country.setVisibility(View.VISIBLE);
                            Work_Country.setText(labelname);
                            workcountry = NullChecker(c.getString(c.getColumnIndex(columnname)));
                            setCountryAdapterAndStateItemSelectedListener(spn_Work_Country, spn_Work_State, feildname, workcountry,NullChecker(c.getString(c.getColumnIndex(DBFeilds.ATTENDEE_WORK_STATE))));
                        } else if (columnname.equals(Util.ATTENDEE_WORK_STATE)) {
                            feildname = "work_address__c";
                            columnname = DBFeilds.ATTENDEE_WORK_STATE;
                            lay_Work_State.setVisibility(View.VISIBLE);
                            Work_State.setText(labelname);
                            workstate = NullChecker(c.getString(c.getColumnIndex(columnname)));
                            setStateAdapterItemSelectedListener(spn_Work_State, workstate, feildname, workcountry);

                            //setDataAndEditTouchListener(lay_Work_State,Work_State,edt_Work_State,labelname,columnname,c.getString(c.getColumnIndex(columnname)),groupname);
                        } else if (columnname.equals(Util.ATTENDEE_WORK_ZIPCODE)) {
                            columnname = DBFeilds.ATTENDEE_WORK_ZIPCODE;
                            workzipcode = c.getString(c.getColumnIndex(columnname));
                            setDataAndEditTouchListenerforAddressinfo(lay_Work_ZipCode, Work_ZipCode, edt_Work_ZipCode, labelname, "Zipcode__c", workzipcode, groupname);
                        }

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setPanel_homeaddress() {
        String groupname = Util.HOME_ADDRESS;
        String labelgroupname = Util.db.getRegGroupLabelNamewithGroupName("where reg_eventid='" + checked_in_eventId + "' AND " + DBFeilds.REG_GROUP_NAME + "= '" + groupname + "'", groupname);
        if (groupname.equalsIgnoreCase(Util.HOME_ADDRESS)) {
            try {
                LinearLayout lay_HomeAddress1__c = linearview.findViewById(R.id.lay_HomeAddress1__c);
                TextView HomeAddress1__c = linearview.findViewById(R.id.HomeAddress1__c);
                EditText edt_HomeAddress1__c = linearview.findViewById(R.id.edt_HomeAddress1__c);

                LinearLayout lay_HomeAddress2__c = linearview.findViewById(R.id.lay_HomeAddress2__c);
                TextView HomeAddress2__c = linearview.findViewById(R.id.HomeAddress2__c);
                EditText edt_HomeAddress2__c = linearview.findViewById(R.id.edt_HomeAddress2__c);

                LinearLayout lay_Home_City = linearview.findViewById(R.id.lay_Home_City);
                TextView Home_City = linearview.findViewById(R.id.Home_City);
                EditText edt_Home_City = linearview.findViewById(R.id.edt_Home_City);

                LinearLayout lay_Home_Country = linearview.findViewById(R.id.lay_Home_Country);
                TextView Home_Country = linearview.findViewById(R.id.Home_Country);
                Spinner spn_Home_Country = linearview.findViewById(R.id.spn_Home_Country);

                LinearLayout lay_Home_State = linearview.findViewById(R.id.lay_Home_State);
                TextView Home_State = linearview.findViewById(R.id.Home_State);
                Spinner spn_Home_State = linearview.findViewById(R.id.spn_Home_State);

                LinearLayout lay_Home_ZipCode = linearview.findViewById(R.id.lay_Home_ZipCode);
                TextView Home_ZipCode = linearview.findViewById(R.id.Home_ZipCode);
                EditText edt_Home_ZipCode = linearview.findViewById(R.id.edt_Home_ZipCode);


                c = Util.db.getRegChildswithGroupName("where Item_Reg_Settings.reg_group_name__c = '" + groupname + "' AND  Item_Reg_Settings.reg_eventid = '" + checked_in_eventId + "' AND Item_Reg_Settings."+DBFeilds.REG_ITEM_ID+"='true'" + whereclause);
                if (c != null && c.getCount() > 0) {
                    for (int i = 0; i < c.getCount(); i++) {
                        c.moveToPosition(i);
                        String columnname = c.getString(c.getColumnIndex(DBFeilds.REG_COLUMN_NAME));
                        String labelname = c.getString(c.getColumnIndex(DBFeilds.REG_LABEL_NAME));
                        String feildtype = NullChecker(c.getString(c.getColumnIndex(DBFeilds.REG_FIELDTYPE)));
                        String feildname = "";
                        if (columnname.equals(Util.ATTENDEE_HOME_ADDRESS_1)) {
                            columnname = DBFeilds.ATTENDEE_HOME_ADDRESS_1;
                            homeaddress1 = c.getString(c.getColumnIndex(columnname));
                            setDataAndEditTouchListenerforAddressinfo(lay_HomeAddress1__c, HomeAddress1__c, edt_HomeAddress1__c, labelname, "Address1__c", homeaddress1, groupname);
                        } else if (columnname.equals(Util.ATTENDEE_HOME_ADDRESS_2)) {
                            columnname = DBFeilds.ATTENDEE_HOME_ADDRESS_2;
                            homeaddress2 = c.getString(c.getColumnIndex(columnname));
                            setDataAndEditTouchListenerforAddressinfo(lay_HomeAddress2__c, HomeAddress2__c, edt_HomeAddress2__c, labelname, "Address2__c", homeaddress2, groupname);
                        } else if (columnname.equals(Util.ATTENDEE_HOME_CITY)) {
                            columnname = DBFeilds.ATTENDEE_HOME_CITY;
                            homecity = c.getString(c.getColumnIndex(columnname));
                            setDataAndEditTouchListenerforAddressinfo(lay_Home_City, Home_City, edt_Home_City, labelname, "City__c", homecity, groupname);
                        } else if (columnname.equals(Util.ATTENDEE_HOME_COUNTRY)) {
                            feildname = "home_address__c";
                            columnname = DBFeilds.ATTENDEE_HOME_COUNTRY;
                            lay_Home_Country.setVisibility(View.VISIBLE);
                            Home_Country.setText(labelname);
                            homecountry = NullChecker(c.getString(c.getColumnIndex(columnname)));
                            setCountryAdapterAndStateItemSelectedListener(spn_Home_Country, spn_Home_State, feildname, homecountry,NullChecker(c.getString(c.getColumnIndex(DBFeilds.ATTENDEE_HOME_STATE))));
                        } else if (columnname.equals(Util.ATTENDEE_HOME_STATE)) {
                            feildname = "home_address__c";
                            columnname = DBFeilds.ATTENDEE_HOME_STATE;
                            lay_Home_State.setVisibility(View.VISIBLE);
                            Home_State.setText(labelname);
                            homestate = NullChecker(c.getString(c.getColumnIndex(columnname)));
                            setStateAdapterItemSelectedListener(spn_Home_State, homestate, feildname, homecountry);

                            //setDataAndEditTouchListener(lay_Home_State,Home_State,edt_Home_State,labelname,columnname,c.getString(c.getColumnIndex(columnname)),groupname);
                        } else if (columnname.equals(Util.ATTENDEE_HOME_ZIPCODE)) {
                            columnname = DBFeilds.ATTENDEE_HOME_ZIPCODE;
                            homezipcode = c.getString(c.getColumnIndex(columnname));
                            setDataAndEditTouchListenerforAddressinfo(lay_Home_ZipCode, Home_ZipCode, edt_Home_ZipCode, labelname, "Zipcode__c", homezipcode, groupname);
                        }

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setPanel_billingaddress() {
        String groupname = Util.BILLING_ADDRESS;
        String labelgroupname = Util.db.getRegGroupLabelNamewithGroupName("where reg_eventid='" + checked_in_eventId + "' AND " + DBFeilds.REG_GROUP_NAME + "= '" + groupname + "'", groupname);
        if (groupname.equalsIgnoreCase(Util.BILLING_ADDRESS)) {
            try {
                LinearLayout lay_BillingAddress1__c = linearview.findViewById(R.id.lay_BillingAddress1__c);
                TextView BillingAddress1__c = linearview.findViewById(R.id.BillingAddress1__c);
                EditText edt_BillingAddress1__c = linearview.findViewById(R.id.edt_BillingAddress1__c);

                LinearLayout lay_BillingAddress2__c = linearview.findViewById(R.id.lay_BillingAddress2__c);
                TextView BillingAddress2__c = linearview.findViewById(R.id.BillingAddress2__c);
                EditText edt_BillingAddress2__c = linearview.findViewById(R.id.edt_BillingAddress2__c);

                LinearLayout lay_Billing_City = linearview.findViewById(R.id.lay_Billing_City);
                TextView Billing_City = linearview.findViewById(R.id.Billing_City);
                EditText edt_Billing_City = linearview.findViewById(R.id.edt_Billing_City);

                LinearLayout lay_Billing_Country = linearview.findViewById(R.id.lay_Billing_Country);
                TextView Billing_Country = linearview.findViewById(R.id.Billing_Country);
                Spinner spn_Billing_Country = linearview.findViewById(R.id.spn_Billing_Country);

                LinearLayout lay_Billing_State = linearview.findViewById(R.id.lay_Billing_State);
                TextView Billing_State = linearview.findViewById(R.id.Billing_State);
                Spinner spn_Billing_State = linearview.findViewById(R.id.spn_Billing_State);

                LinearLayout lay_Billing_ZipCode = linearview.findViewById(R.id.lay_Billing_ZipCode);
                TextView Billing_ZipCode = linearview.findViewById(R.id.Billing_ZipCode);
                EditText edt_Billing_ZipCode = linearview.findViewById(R.id.edt_Billing_ZipCode);


                c = Util.db.getRegChildswithGroupName("where Item_Reg_Settings.reg_group_name__c = '" + groupname + "' AND  Item_Reg_Settings.reg_eventid = '" + checked_in_eventId + "' AND Item_Reg_Settings."+DBFeilds.REG_ITEM_ID+"='true'" + whereclause);
                if (c != null && c.getCount() > 0) {
                    for (int i = 0; i < c.getCount(); i++) {
                        c.moveToPosition(i);
                        String columnname = c.getString(c.getColumnIndex(DBFeilds.REG_COLUMN_NAME));
                        String labelname = c.getString(c.getColumnIndex(DBFeilds.REG_LABEL_NAME));
                        String feildtype = NullChecker(c.getString(c.getColumnIndex(DBFeilds.REG_FIELDTYPE)));

                        String feildname = "";
                        if (columnname.equals(Util.ATTENDEE_BILLING_ADDRESS_1)) {
                            columnname = DBFeilds.ATTENDEE_BILLING_ADDRESS_1;
                            billingaddress1 = c.getString(c.getColumnIndex(columnname));
                            setDataAndEditTouchListenerforAddressinfo(lay_BillingAddress1__c, BillingAddress1__c, edt_BillingAddress1__c, labelname, "Address1__c", billingaddress1, groupname);
                        } else if (columnname.equals(Util.ATTENDEE_BILLING_ADDRESS_2)) {
                            columnname = DBFeilds.ATTENDEE_BILLING_ADDRESS_2;
                            billingaddress2 = c.getString(c.getColumnIndex(columnname));
                            setDataAndEditTouchListenerforAddressinfo(lay_BillingAddress2__c, BillingAddress2__c, edt_BillingAddress2__c, labelname, "Address2__c", billingaddress2, groupname);
                        } else if (columnname.equals(Util.ATTENDEE_BILLING_CITY)) {
                            columnname = DBFeilds.ATTENDEE_BILLING_CITY;
                            billingcity = c.getString(c.getColumnIndex(columnname));
                            setDataAndEditTouchListenerforAddressinfo(lay_Billing_City, Billing_City, edt_Billing_City, labelname, "City__c", billingcity, groupname);
                        } else if (columnname.equals(Util.ATTENDEE_BILLING_COUNTRY)) {
                            feildname = "billing_address__c";
                            columnname = DBFeilds.ATTENDEE_BILLING_COUNTRY;
                            lay_Billing_Country.setVisibility(View.VISIBLE);
                            Billing_Country.setText(labelname);
                            billingcountry = NullChecker(c.getString(c.getColumnIndex(columnname)));
                            setCountryAdapterAndStateItemSelectedListener(spn_Billing_Country, spn_Billing_State, feildname, billingcountry,NullChecker(c.getString(c.getColumnIndex(DBFeilds.ATTENDEE_BILLING_STATE))));
                        } else if (columnname.equals(Util.ATTENDEE_BILLING_STATE)) {
                            feildname = "billing_address__c";
                            columnname = DBFeilds.ATTENDEE_BILLING_STATE;
                            lay_Billing_State.setVisibility(View.VISIBLE);
                            Billing_State.setText(labelname);
                            billingstate = NullChecker(c.getString(c.getColumnIndex(columnname)));
                            setStateAdapterItemSelectedListener(spn_Billing_State, billingstate, feildname, billingcountry);

                            //setDataAndEditTouchListener(lay_Billing_State,Billing_State,edt_Billing_State,labelname,columnname,c.getString(c.getColumnIndex(columnname)),groupname,feildtype);
                        } else if (columnname.equals(Util.ATTENDEE_BILLING_ZIPCODE)) {
                            feildname = "billing_address__c";
                            columnname = DBFeilds.ATTENDEE_BILLING_ZIPCODE;
                            billingzipcode = c.getString(c.getColumnIndex(columnname));
                            setDataAndEditTouchListenerforAddressinfo(lay_Billing_ZipCode, Billing_ZipCode, edt_Billing_ZipCode, labelname, "Zipcode__c", billingzipcode, groupname);
                        }

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setPanel_workinfo() {
        String groupname = Util.WORK_INFORMATION;
        String labelgroupname = Util.db.getRegGroupLabelNamewithGroupName("where reg_eventid='" + checked_in_eventId + "' AND " + DBFeilds.REG_GROUP_NAME + "= '" + groupname + "'", groupname);
        if (groupname.equalsIgnoreCase(Util.WORK_INFORMATION)) {
            // att_ordercursor = Util.db.getAttendeeDetails(whereclause);
            try {
                LinearLayout lay_Primary_Business_Category__c = linearview.findViewById(R.id.lay_Primary_Business_Category__c);
                TextView Primary_Business_Category__c = linearview.findViewById(R.id.Primary_Business_Category__c);
                Spinner spn_Primary_Business_Category__c = linearview.findViewById(R.id.spn_Primary_Business_Category__c);

                LinearLayout lay_Secondary_Business_Category__c = linearview.findViewById(R.id.lay_Secondary_Business_Category__c);
                TextView Secondary_Business_Category__c = linearview.findViewById(R.id.Secondary_Business_Category__c);
                Spinner spn_Secondary_Business_Category__c = linearview.findViewById(R.id.spn_Secondary_Business_Category__c);

                LinearLayout lay_Number_Of_Employees = linearview.findViewById(R.id.lay_Number_Of_Employees);
                TextView Number_Of_Employees = linearview.findViewById(R.id.Number_Of_Employees);
                Spinner spn_Number_Of_Employees = linearview.findViewById(R.id.spn_Number_Of_Employees);

                LinearLayout lay_Established_Date__c = linearview.findViewById(R.id.lay_Established_Date__c);
                TextView Established_Date__c = linearview.findViewById(R.id.Established_Date__c);
                EditText edt_Established_Date__c = linearview.findViewById(R.id.edt_Established_Date__c);

                LinearLayout lay_Keywords__c = linearview.findViewById(R.id.lay_Keywords__c);
                TextView Keywords__c = linearview.findViewById(R.id.Keywords__c);
                EditText edt_Keywords__c = linearview.findViewById(R.id.edt_Keywords__c);

                LinearLayout lay_Exceptional_Keywords__c = linearview.findViewById(R.id.lay_Exceptional_Keywords__c);
                TextView Exceptional_Keywords__c = linearview.findViewById(R.id.Exceptional_Keywords__c);
                EditText edt_Exceptional_Keywords__c = linearview.findViewById(R.id.edt_Exceptional_Keywords__c);


                LinearLayout lay_Business_Revenue = linearview.findViewById(R.id.lay_Business_Revenue);
                TextView Business_Revenue = linearview.findViewById(R.id.Business_Revenue);
                Spinner spn_Business_Revenue = linearview.findViewById(R.id.spn_Business_Revenue);

                LinearLayout lay_Tax_Id__c = linearview.findViewById(R.id.lay_Tax_Id__c);
                TextView Tax_Id__c = linearview.findViewById(R.id.Tax_Id__c);
                EditText edt_Tax_Id__c = linearview.findViewById(R.id.edt_Tax_Id__c);

                LinearLayout lay_Website_URL__c = linearview.findViewById(R.id.lay_Website_URL__c);
                TextView Website_URL__c = linearview.findViewById(R.id.Website_URL__c);
                EditText edt_Website_URL__c = linearview.findViewById(R.id.edt_Website_URL__c);

                LinearLayout lay_Duns_Number__c = linearview.findViewById(R.id.lay_Duns_Number__c);
                TextView Duns_Number__c = linearview.findViewById(R.id.Duns_Number__c);
                EditText edt_Duns_Number__c = linearview.findViewById(R.id.edt_Duns_Number__c);

                LinearLayout lay_Blog_URL__c = linearview.findViewById(R.id.lay_Blog_URL__c);
                TextView Blog_URL__c = linearview.findViewById(R.id.Blog_URL__c);
                EditText edt_Blog_URL__c = linearview.findViewById(R.id.edt_Blog_URL__c);


                LinearLayout lay_Description__c = linearview.findViewById(R.id.lay_Description__c);
                TextView Description__c = linearview.findViewById(R.id.Description__c);
                EditText edt_Description__c = linearview.findViewById(R.id.edt_Description__c);

                LinearLayout lay_FaxNumber__c = linearview.findViewById(R.id.lay_FaxNumber__c);
                TextView FaxNumber__c = linearview.findViewById(R.id.FaxNumber__c);
                EditText edt_FaxNumber__c = linearview.findViewById(R.id.edt_FaxNumber__c);

                LinearLayout lay_Geographical_Region = linearview.findViewById(R.id.lay_Geographical_Region);
                TextView Geographical_Region = linearview.findViewById(R.id.Geographical_Region);
                Spinner spn_Geographical_Region = linearview.findViewById(R.id.spn_Geographical_Region);

                LinearLayout lay_Ethnicity = linearview.findViewById(R.id.lay_Ethnicity);
                TextView Ethnicity = linearview.findViewById(R.id.Ethnicity);
                Spinner spn_Ethnicity = linearview.findViewById(R.id.spn_Ethnicity);

                LinearLayout lay_Business_Structure = linearview.findViewById(R.id.lay_Business_Structure);
                TextView Business_Structure = linearview.findViewById(R.id.Business_Structure);
                Spinner spn_Business_Structure = linearview.findViewById(R.id.spn_Business_Structure);

                LinearLayout lay_Year_in_business__c = linearview.findViewById(R.id.lay_Year_in_business__c);
                TextView Year_in_business__c = linearview.findViewById(R.id.Year_in_business__c);
                Spinner spn_Year_in_business__c = linearview.findViewById(R.id.spn_Year_in_business__c);

                LinearLayout lay_DBA__c = linearview.findViewById(R.id.lay_DBA__c);
                TextView DBA__c = linearview.findViewById(R.id.DBA__c);
                EditText edt_DBA__c = linearview.findViewById(R.id.edt_DBA__c);

                LinearLayout lay_BBB_Number__c = linearview.findViewById(R.id.lay_BBB_Number__c);
                TextView BBB_Number__c = linearview.findViewById(R.id.BBB_Number__c);
                EditText edt_BBB_Number__c = linearview.findViewById(R.id.edt_BBB_Number__c);

                LinearLayout lay_GSA_Schedule__c = linearview.findViewById(R.id.lay_GSA_Schedule__c);
                TextView GSA_Schedule__c = linearview.findViewById(R.id.GSA_Schedule__c);
                EditText edt_GSA_Schedule__c = linearview.findViewById(R.id.edt_GSA_Schedule__c);

                LinearLayout lay_CageCode__c = linearview.findViewById(R.id.lay_CageCode__c);
                TextView CageCode__c = linearview.findViewById(R.id.CageCode__c);
                EditText edt_CageCode__c = linearview.findViewById(R.id.edt_CageCode__c);

                LinearLayout lay_distribution_Country__c = linearview.findViewById(R.id.lay_distribution_Country__c);
                TextView distribution_Country__c = linearview.findViewById(R.id.distribution_Country__c);
                EditText edt_distribution_Country__c = linearview.findViewById(R.id.edt_distribution_Country__c);

                LinearLayout lay_Manufactures_Country__c = linearview.findViewById(R.id.lay_Manufactures_Country__c);
                TextView Manufactures_Country__c = linearview.findViewById(R.id.Manufactures_Country__c);
                EditText edt_Manufactures_Country__c = linearview.findViewById(R.id.edt_Manufactures_Country__c);

                LinearLayout lay_Outside_Facilities__c = linearview.findViewById(R.id.lay_Outside_Facilities__c);
                TextView Outside_Facilities__c = linearview.findViewById(R.id.Outside_Facilities__c);
                EditText edt_Outside_Facilities__c = linearview.findViewById(R.id.edt_Outside_Facilities__c);

                LinearLayout lay_References1__c = linearview.findViewById(R.id.lay_References1__c);
                TextView References1__c = linearview.findViewById(R.id.References1__c);
                EditText edt_References1__c = linearview.findViewById(R.id.edt_References1__c);

                LinearLayout lay_References2__c = linearview.findViewById(R.id.lay_References2__c);
                TextView References2__c = linearview.findViewById(R.id.References2__c);
                EditText edt_References2__c = linearview.findViewById(R.id.edt_References2__c);

                LinearLayout lay_ScopeOfWork1__c = linearview.findViewById(R.id.lay_ScopeOfWork1__c);
                TextView ScopeOfWork1__c = linearview.findViewById(R.id.ScopeOfWork1__c);
                EditText edt_ScopeOfWork1__c = linearview.findViewById(R.id.edt_ScopeOfWork1__c);

                LinearLayout lay_ScopeOfWork2__c = linearview.findViewById(R.id.lay_ScopeOfWork2__c);
                TextView ScopeOfWork2__c = linearview.findViewById(R.id.ScopeOfWork2__c);
                EditText edt_ScopeOfWork2__c = linearview.findViewById(R.id.edt_ScopeOfWork2__c);

                LinearLayout lay_Secondary_email__c = linearview.findViewById(R.id.lay_Secondary_email__c);
                TextView Secondary_email__c = linearview.findViewById(R.id.Secondary_email__c);
                EditText edt_Secondary_email__c = linearview.findViewById(R.id.edt_Secondary_email__c);
                c = Util.db.getRegChildswithGroupName("where Item_Reg_Settings.reg_group_name__c = '" + groupname + "' AND  Item_Reg_Settings.reg_eventid = '" + checked_in_eventId + "' AND Item_Reg_Settings."+DBFeilds.REG_ITEM_ID+"='true'" + whereclause);
                if (c != null && c.getCount() > 0) {
                    for (int i = 0; i < c.getCount(); i++) {
                        c.moveToPosition(i);
                        String columnname = c.getString(c.getColumnIndex(DBFeilds.REG_COLUMN_NAME));
                        String labelname = c.getString(c.getColumnIndex(DBFeilds.REG_LABEL_NAME));
                        String feildtype = NullChecker(c.getString(c.getColumnIndex(DBFeilds.REG_FIELDTYPE)));
                        String feildname = "";
                        if (columnname.equals(DBFeilds.ATTENDEE_PRIMARY_BUSINESS_CATEGORY)) {
                            feildname = DBFeilds.ATTENDEE_PRIMARY_BUSINESS_CATEGORY;
                            columnname = DBFeilds.ATTENDEE_PRIMARY_BUSINESS_CATEGORY;
                            lay_Primary_Business_Category__c.setVisibility(View.VISIBLE);
                            Primary_Business_Category__c.setText(labelname);
                            String value = NullChecker(c.getString(c.getColumnIndex(columnname)));
                            setAdapterAndItemSelectedListener(spn_Primary_Business_Category__c, feildname, value, groupname, feildname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_SECONDARY_BUSINESS_CATEGORY)) {
                            feildname = DBFeilds.ATTENDEE_SECONDARY_BUSINESS_CATEGORY;
                            columnname = DBFeilds.ATTENDEE_SECONDARY_BUSINESS_CATEGORY;
                            lay_Secondary_Business_Category__c.setVisibility(View.VISIBLE);
                            Secondary_Business_Category__c.setText(labelname);
                            String value = NullChecker(c.getString(c.getColumnIndex(columnname)));
                            setAdapterAndItemSelectedListener(spn_Secondary_Business_Category__c, feildname, value, groupname,feildname, feildtype);
                        } else if (columnname.equals(Util.ATTENDEE_NUMBER_OF_EMPLOYEES)) {
                            feildname = Util.ATTENDEE_NUMBER_OF_EMPLOYEES;
                            columnname = DBFeilds.ATTENDEE_NUMBER_OF_EMPLOYEES;
                            lay_Number_Of_Employees.setVisibility(View.VISIBLE);
                            Number_Of_Employees.setText(labelname);
                            String value = NullChecker(c.getString(c.getColumnIndex(columnname)));
                            setAdapterAndItemSelectedListener(spn_Number_Of_Employees, feildname, value, groupname,"Number of Employees", feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_ESTABLISHED_DATE)) {
                            setDataAndEditTouchListener(lay_Established_Date__c, Established_Date__c, edt_Established_Date__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals("Business Revenue")) {
                            feildname = Util.ATTENDEE_REVENUE;
                            columnname = DBFeilds.ATTENDEE_REVENUE;
                            lay_Business_Revenue.setVisibility(View.VISIBLE);
                            Business_Revenue.setText(labelname);
                            String value = NullChecker(c.getString(c.getColumnIndex(columnname)));
                            setAdapterAndItemSelectedListener(spn_Business_Revenue, feildname, value, groupname,"Business Revenue", feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_TAX_ID)) {
                            setDataAndEditTouchListener(lay_Tax_Id__c, Tax_Id__c, edt_Tax_Id__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_WEBSITE_URL)) {
                            setDataAndEditTouchListener(lay_Website_URL__c, Website_URL__c, edt_Website_URL__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_DUNS_NUMBER)) {
                            setDataAndEditTouchListener(lay_Duns_Number__c, Duns_Number__c, edt_Duns_Number__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_BLOG_URL)) {
                            setDataAndEditTouchListener(lay_Blog_URL__c, Blog_URL__c, edt_Blog_URL__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_DESCRIPTION)) {
                            setDataAndEditTouchListener(lay_Description__c, Description__c, edt_Description__c, labelname, "company_description__c", c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_KEYWORDS)) {
                            setDataAndEditTouchListener(lay_Keywords__c, Keywords__c, edt_Keywords__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_EXCEPTIONAL_KEYWORDS)) {
                            setDataAndEditTouchListener(lay_Exceptional_Keywords__c, Exceptional_Keywords__c, edt_Exceptional_Keywords__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_DBA)) {
                            setDataAndEditTouchListener(lay_DBA__c, DBA__c, edt_DBA__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_BBB_NUMBER)) {
                            setDataAndEditTouchListener(lay_BBB_Number__c, BBB_Number__c, edt_BBB_Number__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_GSA_SCHEDULE)) {
                            setDataAndEditTouchListener(lay_GSA_Schedule__c, GSA_Schedule__c, edt_GSA_Schedule__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(Util.ATTENDEE_GEOGRAPHICAL_REGION)) {
                            feildname = Util.ATTENDEE_GEOGRAPHICAL_REGION;
                            columnname = DBFeilds.ATTENDEE_GEOGRAPHICAL_REGION;
                            lay_Geographical_Region.setVisibility(View.VISIBLE);
                            Geographical_Region.setText(labelname);
                            String value = NullChecker(c.getString(c.getColumnIndex(columnname)));
                            setAdapterAndItemSelectedListener(spn_Geographical_Region, feildname, value, groupname,feildname, feildtype);
                        } else if (columnname.equals(Util.ATTENDEE_ETHNICITY)) {
                            feildname = Util.ATTENDEE_ETHNICITY;
                            columnname = DBFeilds.ATTENDEE_ETHNICITY;
                            lay_Ethnicity.setVisibility(View.VISIBLE);
                            Ethnicity.setText(labelname);
                            String value = NullChecker(c.getString(c.getColumnIndex(columnname)));
                            setAdapterAndItemSelectedListener(spn_Ethnicity, feildname, value, groupname,feildname, feildtype);
                        } else if (columnname.equals(Util.ATTENDEE_BUSINESS_STRUCTURE)) {
                            feildname = Util.ATTENDEE_BUSINESS_STRUCTURE;
                            columnname = DBFeilds.ATTENDEE_BUSINESS_STRUCTURE;
                            lay_Business_Structure.setVisibility(View.VISIBLE);
                            Business_Structure.setText(labelname);
                            String value = NullChecker(c.getString(c.getColumnIndex(columnname)));
                            setAdapterAndItemSelectedListener(spn_Business_Structure, feildname, value, groupname,feildname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_CAGECODE)) {
                            setDataAndEditTouchListener(lay_CageCode__c, CageCode__c, edt_CageCode__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_DISTRIBUTION_COUNTRY)) {
                            setDataAndEditTouchListener(lay_distribution_Country__c, distribution_Country__c, edt_distribution_Country__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_FAXNUMBER)) {
                            setDataAndEditTouchListener(lay_FaxNumber__c, FaxNumber__c, edt_FaxNumber__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_MANUFACTURES_COUNTRY)) {
                            setDataAndEditTouchListener(lay_Manufactures_Country__c, Manufactures_Country__c, edt_Manufactures_Country__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_OUTSIDE_FACILITIES)) {
                            setDataAndEditTouchListener(lay_Outside_Facilities__c, Outside_Facilities__c, edt_Outside_Facilities__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_REFERENCES1)) {
                            setDataAndEditTouchListener(lay_References1__c, References1__c, edt_References1__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_REFERENCES2)) {
                            setDataAndEditTouchListener(lay_References2__c, References2__c, edt_References2__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_SCOPEOFWORK1)) {
                            setDataAndEditTouchListener(lay_ScopeOfWork1__c, ScopeOfWork1__c, edt_ScopeOfWork1__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_SCOPEOFWORK2)) {
                            setDataAndEditTouchListener(lay_ScopeOfWork2__c, ScopeOfWork2__c, edt_ScopeOfWork2__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_SECONDARY_EMAIL)) {
                            setDataAndEditTouchListener(lay_Secondary_email__c, Secondary_email__c, edt_Secondary_email__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_YEAR_IN_BUSINESS)) {
                            feildname = DBFeilds.ATTENDEE_YEAR_IN_BUSINESS;
                            columnname = DBFeilds.ATTENDEE_YEAR_IN_BUSINESS;
                            lay_Year_in_business__c.setVisibility(View.VISIBLE);
                            Year_in_business__c.setText(labelname);
                            String value = NullChecker(c.getString(c.getColumnIndex(columnname)));
                            setAdapterAndItemSelectedListener(spn_Year_in_business__c, feildname, value, groupname,"year_in_business__c", feildtype);
                        }

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void setPanel_naicsinfo() {
        final String groupname = Util.NAICS_INFORMATION;
        String labelgroupname = Util.db.getRegGroupLabelNamewithGroupName(" where reg_eventid='" + checked_in_eventId + "' AND " + DBFeilds.REG_GROUP_NAME + "= '" + groupname + "'", groupname);
        if (groupname.equalsIgnoreCase(Util.NAICS_INFORMATION)) {

            LinearLayout lay_NAICS_Codes = linearview.findViewById(R.id.lay_NAICS_Codes);
            TextView label_NAICS_Codes = linearview.findViewById(R.id.label_NAICS_Codes);
            final NachoTextView NAICS_Codes = linearview.findViewById(R.id.NAICS_Codes);


            c = Util.db.getRegChildswithGroupName("where Item_Reg_Settings.reg_group_name__c = '" + groupname + "' AND  Item_Reg_Settings.reg_eventid = '" + checked_in_eventId + "' AND Item_Reg_Settings."+DBFeilds.REG_ITEM_ID+"='true'" + whereclause);
            if (c != null && c.getCount() > 0) {
                for (int i = 0; i < c.getCount(); i++) {
                    c.moveToPosition(i);
                    String columnname = c.getString(c.getColumnIndex(DBFeilds.REG_COLUMN_NAME));
                    String labelname = c.getString(c.getColumnIndex(DBFeilds.REG_LABEL_NAME));
                    final String feildtype = c.getString(c.getColumnIndex(DBFeilds.REG_FIELDTYPE));
                    final String feildname =DBFeilds.ATTENDEE_NAICS;
                    if (columnname.equalsIgnoreCase(Util.NAICS_CODES)) {
                        columnname = DBFeilds.ATTENDEE_NAICS;
                        lay_NAICS_Codes.setVisibility(View.VISIBLE);
                        label_NAICS_Codes.setText(labelname);

                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(SelfCheckinAttendeeDetailActivity.this, android.R.layout.simple_list_item_1, Util.db.getFiterItems(feildname));
                        String data = c.getString(c.getColumnIndex(columnname));
                        NAICS_Codes.setAdapter(spinnerArrayAdapter);
                        final List<String> vales = new ArrayList<>();
                        if (!data.isEmpty()) {
                            String[] s = data.split("-]");
                            for (int k = 1; k < s.length; k++) {
                                vales.add(s[k]);
                            }
                        }
                        tempnaics = vales;
                        NAICS_Codes.setText(vales);
                        NAICS_Codes.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                NAICS_Codes.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {
                                        tempnaics = NAICS_Codes.getChipAndTokenValues();
                                        if (!vales.equals(tempnaics)&&tempnaics.size()>=1) {//&&!value.isEmpty()
                                            if (!Util.db.getFiterIds(feildname, tempnaics).isEmpty())
                                            {
                                                fields = new AttendeeFeildObj();
                                                fields.group_display_name = groupname;
                                                fields.field_api_name = Util.NAICS_CODES;
                                                fields.field_data = Util.db.getFiterIds(feildname, tempnaics);
                                                fields.field_type = feildtype;
                                                addobject(fields.field_api_name, fields);
                                            }
                                        }else if(!vales.equals(tempnaics)&&tempnaics.size()==0){
                                            fields = new AttendeeFeildObj();
                                            fields.group_display_name = groupname;
                                            fields.field_api_name = Util.NAICS_CODES;
                                            fields.field_data = "";
                                            fields.field_type = feildtype;
                                            addobject(fields.field_api_name, fields);
                                        }else  if (vales.equals(tempnaics)) {//&&!value.isEmpty()
                                            fields = new AttendeeFeildObj();
                                            fields.group_display_name = groupname;
                                            fields.field_api_name = Util.NAICS_CODES;
                                            fields.field_data = Util.db.getFiterIds(feildname, NAICS_Codes.getChipAndTokenValues());
                                            fields.field_type = feildtype;
                                            removeobject(fields.field_api_name, fields);
                                        }
                                    }
                                });
                                return false;
                            }
                        });
                    }
                  /*  if (c.getString(c.getColumnIndex(DBFeilds.REG_FIELDTYPE)).equals("MULTIPICKLIST")) {
                        if (columnname.equalsIgnoreCase(Util.NAICS_CODES)) {
                            feildname = DBFeilds.ATTENDEE_NAICS;
                            columnname = DBFeilds.ATTENDEE_NAICS;
                            lay_NAICS_Codes.setVisibility(View.VISIBLE);
                            label_NAICS_Codes.setText(labelname);
                            String data = c.getString(c.getColumnIndex(columnname));
                            setMultipicklistAdapterAndValues(NAICS_Codes, feildname, data, groupname);
                        }
                    }*/
                }
            }


        }
    }

    public void setPanel_commodities() {
        final String groupname = Util.COMMODITIES_INFORMATION;
        String labelgroupname = Util.db.getRegGroupLabelNamewithGroupName("where reg_eventid='" + checked_in_eventId + "' AND " + DBFeilds.REG_GROUP_NAME + "= '" + groupname + "'", groupname);
        if (groupname.equalsIgnoreCase(Util.COMMODITIES_INFORMATION)) {
            LinearLayout lay_Commodities = linearview.findViewById(R.id.lay_Commodities);
            TextView label_Commodities = linearview.findViewById(R.id.label_Commodities);
            final   NachoTextView Commodities = linearview.findViewById(R.id.Commodities);


            c = Util.db.getRegChildswithGroupName("where Item_Reg_Settings.reg_group_name__c = '" + groupname + "' AND  Item_Reg_Settings.reg_eventid = '" + checked_in_eventId + "' AND Item_Reg_Settings."+DBFeilds.REG_ITEM_ID+"='true'" + whereclause);
            if (c != null && c.getCount() > 0) {
                for (int i = 0; i < c.getCount(); i++) {
                    c.moveToPosition(i);
                    String columnname = c.getString(c.getColumnIndex(DBFeilds.REG_COLUMN_NAME));
                    String labelname = c.getString(c.getColumnIndex(DBFeilds.REG_LABEL_NAME));
                    final String feildtype = c.getString(c.getColumnIndex(DBFeilds.REG_FIELDTYPE));
                    String feildname = "";

                    if (columnname.equals(Util.ATTENDEE_COMMODITIES)) {
                        feildname = DBFeilds.ATTENDEE_COMMODITIES;
                        columnname = DBFeilds.ATTENDEE_COMMODITIES;
                        lay_Commodities.setVisibility(View.VISIBLE);
                        label_Commodities.setText(labelname);
                        String data = c.getString(c.getColumnIndex(columnname));
                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(SelfCheckinAttendeeDetailActivity.this, android.R.layout.simple_list_item_1, Util.db.getFiterItems(feildname));
                        Commodities.setAdapter(spinnerArrayAdapter);
                        final List<String> vales = new ArrayList<>();
                        if (!data.isEmpty()) {
                            String[] s = data.split("-]");
                            for (int k = 1; k < s.length; k++) {
                                vales.add(s[k]);
                            }
                        }
                        tempcommodities = vales;
                        Commodities.setText(vales);
                        Commodities.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                Commodities.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {
                                        tempcommodities = Commodities.getChipAndTokenValues();
                                        if (!vales.equals(tempcommodities)&&tempcommodities.size()>=1) {//&&!value.isEmpty()
                                            if (!Util.db.getFiterIds(Util.ATTENDEE_COMMODITIES, tempcommodities).isEmpty())
                                            {
                                                fields = new AttendeeFeildObj();
                                                fields.group_display_name = groupname;
                                                fields.field_api_name = Util.ATTENDEE_COMMODITIES;
                                                fields.field_data = Util.db.getFiterIds(Util.ATTENDEE_COMMODITIES, tempcommodities);
                                                fields.field_type = feildtype;
                                                addobject(fields.field_api_name, fields);
                                            }
                                        }else if(!vales.equals(tempcommodities)&&tempcommodities.size()==0){
                                            fields = new AttendeeFeildObj();
                                            fields.group_display_name = groupname;
                                            fields.field_api_name = Util.ATTENDEE_COMMODITIES;
                                            fields.field_data = "";
                                            fields.field_type = feildtype;
                                            addobject(fields.field_api_name, fields);
                                        }else  if (vales.equals(tempcommodities)) {//&&!value.isEmpty()
                                            fields = new AttendeeFeildObj();
                                            fields.group_display_name = groupname;
                                            fields.field_api_name = Util.ATTENDEE_COMMODITIES;
                                            fields.field_data = Util.db.getFiterIds(Util.ATTENDEE_COMMODITIES, tempcommodities);
                                            fields.field_type = feildtype;
                                            removeobject(fields.field_api_name, fields);
                                        }
                                    }
                                });
                                return false;
                            }
                        });
                    }
                }
            }
        }



    }

    public void setPanel_diversities() {
        final String groupname = Util.DIVERSITY_INFORMATION;
        String labelgroupname = Util.db.getRegGroupLabelNamewithGroupName("where reg_eventid='" + checked_in_eventId + "' AND " + DBFeilds.REG_GROUP_NAME + "= '" + groupname + "'", groupname);
        if (groupname.equalsIgnoreCase(Util.DIVERSITY_INFORMATION)) {
            try {
                LinearLayout lay_Diversity_Type = linearview.findViewById(R.id.lay_Diversity_Type);
                TextView label_Diversity_Type = linearview.findViewById(R.id.label_Diversity_Type);
                final  NachoTextView Diversity_Type = linearview.findViewById(R.id.Diversity_Type);


                c = Util.db.getRegChildswithGroupName("where Item_Reg_Settings.reg_group_name__c = '" + groupname + "' AND  Item_Reg_Settings.reg_eventid = '" + checked_in_eventId + "' AND Item_Reg_Settings."+DBFeilds.REG_ITEM_ID+"='true'" + whereclause);
                if (c != null && c.getCount() > 0) {
                    for (int i = 0; i < c.getCount(); i++) {
                        c.moveToPosition(i);
                        String columnname = c.getString(c.getColumnIndex(DBFeilds.REG_COLUMN_NAME));
                        String labelname = c.getString(c.getColumnIndex(DBFeilds.REG_LABEL_NAME));
                        final String feildtype = c.getString(c.getColumnIndex(DBFeilds.REG_FIELDTYPE));
                        String feildname = "";

                        if (columnname.equalsIgnoreCase(Util.DIVERSITY_TYPE)) {
                            feildname = DBFeilds.ATTENDEE_DIVERSITIES;
                            columnname = DBFeilds.ATTENDEE_DIVERSITIES;
                            lay_Diversity_Type.setVisibility(View.VISIBLE);
                            label_Diversity_Type.setText(labelname);
                            String data = c.getString(c.getColumnIndex(columnname));
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(SelfCheckinAttendeeDetailActivity.this, android.R.layout.simple_list_item_1, Util.db.getFiterItems(feildname));
                            Diversity_Type.setAdapter(spinnerArrayAdapter);
                            final List<String> vales = new ArrayList<>();
                            if (!data.isEmpty()) {
                                String[] s = data.split("-]");
                                for (int k = 1; k < s.length; k++) {
                                    vales.add(s[k]);
                                }
                            }
                            tempdiversities = vales;
                            Diversity_Type.setText(vales);
                            Diversity_Type.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    Diversity_Type.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                        }

                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {
                                            tempdiversities = Diversity_Type.getChipAndTokenValues();
                                            if (!vales.equals(tempdiversities)&&tempdiversities.size()>=1) {//&&!value.isEmpty()
                                                if (!Util.db.getFiterIds(Util.ATTENDEE_DIVERSITIES, tempdiversities).isEmpty())
                                                {
                                                    fields = new AttendeeFeildObj();
                                                    fields.group_display_name = groupname;
                                                    fields.field_api_name = Util.DIVERSITY_TYPE;
                                                    fields.field_data = Util.db.getFiterIds(Util.ATTENDEE_DIVERSITIES, tempdiversities);
                                                    fields.field_type = feildtype;
                                                    addobject(fields.field_api_name, fields);
                                                }
                                            }else if(!vales.equals(tempdiversities)&&tempdiversities.size()==0){
                                                fields = new AttendeeFeildObj();
                                                fields.group_display_name = groupname;
                                                fields.field_api_name = Util.DIVERSITY_TYPE;
                                                fields.field_data = "";
                                                fields.field_type = feildtype;
                                                addobject(fields.field_api_name, fields);
                                            }else  if (vales.equals(tempdiversities)) {//&&!value.isEmpty()
                                                fields = new AttendeeFeildObj();
                                                fields.group_display_name = groupname;
                                                fields.field_api_name = Util.DIVERSITY_TYPE;
                                                fields.field_data = Util.db.getFiterIds(Util.ATTENDEE_DIVERSITIES, tempdiversities);
                                                fields.field_type = feildtype;
                                                removeobject(fields.field_api_name, fields);
                                            }
                                        }
                                    });
                                    return false;
                                }
                            });
                        }


                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void setPanel_socialinfo() {
        String groupname = Util.SPEAKER_INFORMATION;
        String labelgroupname = Util.db.getRegGroupLabelNamewithGroupName("where reg_eventid='" + checked_in_eventId + "' AND " + DBFeilds.REG_GROUP_NAME + "= '" + groupname + "'", groupname);
        if (groupname.equalsIgnoreCase(Util.SPEAKER_INFORMATION)) {
            try {
                LinearLayout lay_SpeakerFaceBookId = linearview.findViewById(R.id.lay_SpeakerFaceBookId);
                TextView SpeakerFaceBookId = linearview.findViewById(R.id.SpeakerFaceBookId);
                EditText edt_SpeakerFaceBookId = linearview.findViewById(R.id.edt_SpeakerFaceBookId);

                LinearLayout lay_SpeakerLinkedInId = linearview.findViewById(R.id.lay_SpeakerLinkedInId);
                TextView SpeakerLinkedInId = linearview.findViewById(R.id.SpeakerLinkedInId);
                EditText edt_SpeakerLinkedInId = linearview.findViewById(R.id.edt_SpeakerLinkedInId);

                LinearLayout lay_SpeakerTwitterId = linearview.findViewById(R.id.lay_SpeakerTwitterId);
                TextView SpeakerTwitterId = linearview.findViewById(R.id.SpeakerTwitterId);
                EditText edt_SpeakerTwitterId = linearview.findViewById(R.id.edt_SpeakerTwitterId);

                LinearLayout lay_SpeakerBlogger = linearview.findViewById(R.id.lay_SpeakerBlogger);
                TextView SpeakerBlogger = linearview.findViewById(R.id.SpeakerBlogger);
                EditText edt_SpeakerBlogger = linearview.findViewById(R.id.edt_SpeakerBlogger);

                LinearLayout lay_WhatsApp__c = linearview.findViewById(R.id.lay_WhatsApp__c);
                TextView WhatsApp__c = linearview.findViewById(R.id.WhatsApp__c);
                EditText edt_WhatsApp__c = linearview.findViewById(R.id.edt_WhatsApp__c);

                LinearLayout lay_Wechat__c = linearview.findViewById(R.id.lay_Wechat__c);
                TextView Wechat__c = linearview.findViewById(R.id.Wechat__c);
                EditText edt_Wechat__c = linearview.findViewById(R.id.edt_Wechat__c);

                LinearLayout lay_Skype__c = linearview.findViewById(R.id.lay_Skype__c);
                TextView Skype__c = linearview.findViewById(R.id.Skype__c);
                EditText edt_Skype__c = linearview.findViewById(R.id.edt_Skype__c);

                LinearLayout lay_Snapchat__c = linearview.findViewById(R.id.lay_Snapchat__c);
                TextView Snapchat__c = linearview.findViewById(R.id.Snapchat__c);
                EditText edt_Snapchat__c = linearview.findViewById(R.id.edt_Snapchat__c);

                LinearLayout lay_Instagram__c = linearview.findViewById(R.id.lay_Instagram__c);
                TextView Instagram__c = linearview.findViewById(R.id.Instagram__c);
                EditText edt_Instagram__c = linearview.findViewById(R.id.edt_Instagram__c);

                LinearLayout lay_Biography__c = linearview.findViewById(R.id.lay_Biography__c);
                TextView Biography__c = linearview.findViewById(R.id.Biography__c);
                EditText edt_Biography__c = linearview.findViewById(R.id.edt_Biography__c);

                lay_SpeakerImage = linearview.findViewById(R.id.lay_SpeakerImage);
                SpeakerImage = linearview.findViewById(R.id.SpeakerImage);
                img_SpeakerImage = linearview.findViewById(R.id.img_SpeakerImage);

                lay_Company_Logo = linearview.findViewById(R.id.lay_Company_Logo__c);
                Company_Logo = linearview.findViewById(R.id.Company_Logo__c);
                img_Company_Logo = linearview.findViewById(R.id.img_Company_Logo__c);

                c = Util.db.getRegChildswithGroupName("where Item_Reg_Settings.reg_group_name__c = '" + groupname + "' AND  Item_Reg_Settings.reg_eventid = '" + checked_in_eventId + "' AND Item_Reg_Settings."+DBFeilds.REG_ITEM_ID+"='true'" + whereclause);
                if (c != null && c.getCount() > 0) {
                    for (int i = 0; i < c.getCount(); i++) {
                        c.moveToPosition(i);
                        String columnname = c.getString(c.getColumnIndex(DBFeilds.REG_COLUMN_NAME));
                        String labelname = c.getString(c.getColumnIndex(DBFeilds.REG_LABEL_NAME));
                        String feildtype = NullChecker(c.getString(c.getColumnIndex(DBFeilds.REG_FIELDTYPE)));
                        String feildname = "";

                        if (columnname.equals(DBFeilds.ATTENDEE_SPEAKERFACEBOOKID)) {
                            setDataAndEditTouchListener(lay_SpeakerFaceBookId, SpeakerFaceBookId, edt_SpeakerFaceBookId, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_SPEAKERLINKEDINID)) {
                            setDataAndEditTouchListener(lay_SpeakerLinkedInId, SpeakerLinkedInId, edt_SpeakerLinkedInId, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_SPEAKERTWITTERID)) {
                            setDataAndEditTouchListener(lay_SpeakerTwitterId, SpeakerTwitterId, edt_SpeakerTwitterId, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_SPEAKERBLOGGER)) {
                            setDataAndEditTouchListener(lay_SpeakerBlogger, SpeakerBlogger, edt_SpeakerBlogger, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_BIOGRAPHY)) {
                            setDataAndEditTouchListener(lay_Biography__c, Biography__c, edt_Biography__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_WHATSAPP)) {
                            setDataAndEditTouchListener(lay_WhatsApp__c, WhatsApp__c, edt_WhatsApp__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_WECHAT)) {
                            setDataAndEditTouchListener(lay_Wechat__c, Wechat__c, edt_Wechat__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_SKYPE)) {
                            setDataAndEditTouchListener(lay_Skype__c, Skype__c, edt_Skype__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_SNAPCHAT)) {
                            setDataAndEditTouchListener(lay_Snapchat__c, Snapchat__c, edt_Snapchat__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        } else if (columnname.equals(DBFeilds.ATTENDEE_INSTAGRAM)) {
                            setDataAndEditTouchListener(lay_Instagram__c, Instagram__c, edt_Instagram__c, labelname, columnname, c.getString(c.getColumnIndex(columnname)), groupname, feildtype);
                        }else if (columnname.equals("SpeakerImage")) {
                            lay_SpeakerImage.setVisibility(View.VISIBLE);
                            SpeakerImage.setText(labelname);
                            if(!NullChecker(c.getString(c.getColumnIndex(DBFeilds.ATTENDEE_IMAGE))).isEmpty()){
                                String[] fullurl=checkedin_event_record.image.split("&id=");
                                String url=fullurl[0];
                                Glide.with(SelfCheckinAttendeeDetailActivity.this).load(url+"&id="+c.getString(c.getColumnIndex(DBFeilds.ATTENDEE_IMAGE)))
                                        .dontAnimate().into(img_SpeakerImage);
                            }
                            lay_SpeakerImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View arg0) {
                                    isuserpicclicked=true;
                                    openTakeFromDialg(SelfCheckinAttendeeDetailActivity.this);
                                }
                            });
                        }else if (columnname.equals("Company_Logo__c")) {
                            lay_Company_Logo.setVisibility(View.VISIBLE);
                            Company_Logo.setText(labelname);
                            if(!NullChecker(c.getString(c.getColumnIndex(DBFeilds.ATTENDEE_COMPANY_LOGO))).isEmpty()){
                                String[] fullurl=checkedin_event_record.image.split("&id=");
                                String url=fullurl[0];
                                Glide.with(SelfCheckinAttendeeDetailActivity.this).load(url+"&id="+c.getString(c.getColumnIndex(DBFeilds.ATTENDEE_COMPANY_LOGO)))
                                        .dontAnimate().into(img_Company_Logo);
                            }
                            lay_Company_Logo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View arg0) {
                                    isuserpicclicked=false;
                                    openTakeFromDialg(SelfCheckinAttendeeDetailActivity.this);
                                }
                            });
                        }

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
}
