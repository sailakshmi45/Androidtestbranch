package com.globalnest.scanattendee;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.cursoradapter.widget.CursorAdapter;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.globalnest.database.DBFeilds;
import com.globalnest.database.DataBase;
import com.globalnest.mvc.OrderDetailsHandler;
import com.globalnest.network.HttpPostData;
import com.globalnest.network.WebServiceUrls;
import com.globalnest.objects.AttendeeFeildObj;
import com.globalnest.objects.RegistrationSettingsController;
import com.globalnest.utils.Util;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

public class AttendeePagerSampleActivity extends BaseActivity {
    private ViewPager view_pages;
    private PagerSlidingTabStrip tabs;
    private MyPagerAdapter adapter;
    private ArrayList<String> TITLES = new ArrayList<String>();
    private ArrayList<RegistrationSettingsController> tablabels= new ArrayList<>();
    private ArrayList<Integer> payment_option_layouts = new ArrayList<Integer>();
    ListView list_data;
    Button btn_savegroupdata;
    private AttendeeDataAdapter detailAdapter;
    private Cursor att_cursor;
    String attendee_id = "", event_id = "", order_id = "",whereClause="";
    public AttendeeFeildObj fields=new AttendeeFeildObj();
    public ArrayList<AttendeeFeildObj> attendeefieldsarray;
    public HashMap<String,AttendeeFeildObj> parentarray=new HashMap<>();
    ArrayAdapter<String> adaptercountry;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent attendee_intent = getIntent();
        attendee_id = attendee_intent.getStringExtra("ATTENDEE_ID");
        event_id = attendee_intent.getStringExtra("EVENT_ID");
        order_id = attendee_intent.getStringExtra("ORDER_ID");
        // att = Util.db.getBadgeableTicketOrderDetails(order_id);
        //badge_id= attendee_intent.getStringExtra("BADGE_ID");
        whereClause = " AND " + DBFeilds.TABLE_ATTENDEE_DETAILS+"."+DBFeilds.ATTENDEE_EVENT_ID + " = '"
                + checked_in_eventId + "'" + " AND " +  DBFeilds.TABLE_ATTENDEE_DETAILS+"."+DBFeilds.ATTENDEE_ID
                + " = " + "'" + attendee_id + "'" + " AND "
                + DBFeilds.TABLE_ATTENDEE_DETAILS+"."+ DBFeilds.ATTENDEE_ORDER_ID + " = " + "'" + order_id + "'";
        setCustomContentView(R.layout.attendeedetail_regsettings);
        tablabels= Util.db.getRegGroupandLabelNameList("where "+ DBFeilds.REG_EVENT_ID+"='"+checked_in_eventId+"'");

        /*TITLES= Util.db.getRegGroupNameList("where "+ DBFeilds.REG_EVENT_ID+"='"+checked_in_eventId+"'");
        for (int i=0;i<TITLES.size();i++) {
            payment_option_layouts.add(R.layout.attendeedata_itemview);
        }*/
        for(int i=0;i<tablabels.size();i++){
            payment_option_layouts.add(R.layout.attendeedata_itemview);
        }
        //Util.db.createtemptable();
        adapter = new MyPagerAdapter();
        view_pages.setAdapter(adapter);
        tabs.setViewPager(view_pages);
        txtprint_selfcheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(parentarray!=null&&parentarray.values().size()>0) {
                            // Toast.makeText(AttendeePagerSampleActivity.this, "Saving " + Util.db.getRegGroupLabelNamewithGroupName("where " + DBFeilds.REG_EVENT_ID + "='" + checked_in_eventId + "' AND " + DBFeilds.REG_GROUP_NAME + "= '" + tablabels.get(position).Group_Name__c + "'", tablabels.get(position).Group_Name__c), Toast.LENGTH_LONG).show();
                            doRequest();
                        }else{
                            //Toast.makeText(AttendeePagerSampleActivity.this, "No data changed in " + Util.db.getRegGroupLabelNamewithGroupName("where " + DBFeilds.REG_EVENT_ID + "='" + checked_in_eventId + "' AND " + DBFeilds.REG_GROUP_NAME + "= '" + tablabels.get(position).Group_Name__c + "'", tablabels.get(position).Group_Name__c), Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }
        });
/*
        view_pages.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int o=position;
                //view_pages.setCurrentItem(position);
                //AttendeeView(tablabels.get(position).Group_Name__c);
            }

            @Override
            public void onPageSelected(int position) {
                view_pages.setCurrentItem(position);

                try {
                    */
/*for (int i=0;i<=tablabels.size();i++) {
                        if (Util.db.getRegGroupLabelNamewithGroupName("where " + DBFeilds.REG_EVENT_ID + "='" + checked_in_eventId + "' AND " + DBFeilds.REG_GROUP_NAME + "= '" + tablabels.get(position).Group_Name__c + "'", tablabels.get(i).Group_Name__c).equalsIgnoreCase("Basic Information")) {
                   *//*
         //AttendeeView(tablabels.get(position).Group_Name__c);
                    tabs.notifyDataSetChanged();
                    // }
                    //}
                   */
/* if (TITLES.get(position).equalsIgnoreCase("Basic Information")) {
                        AttendeeView("Basic Information");
                    }else if (TITLES.get(position).equalsIgnoreCase("Work Information")) {
                        AttendeeView("Work Information");
                    }else if (TITLES.get(position).equalsIgnoreCase("Address Information")) {
                        AttendeeView("Address Information");
                    }else if (TITLES.get(position).equalsIgnoreCase("Speaker Information")) {
                        AttendeeView("Speaker Information");
                    }
                    else {

                    }*//*

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
*/

    }
    /*private Observable<ArrayList<String>> setChildData(){

    }*/
    @Override
    public void setCustomContentView(int layout) {
        v = inflater.inflate(layout, null);
        linearview.addView(v);
        txt_title.setText("Attendee Detail");
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        img_socket_scanner.setVisibility(View.GONE);
        img_scanner_base.setVisibility(View.GONE);
        img_setting.setVisibility(View.GONE);
        txtprint_selfcheckin.setVisibility(View.VISIBLE);
        txtprint_selfcheckin.setText("SAVE");
        img_menu.setImageResource(R.drawable.back_button);
        event_layout.setVisibility(View.GONE);
        button_layout.setVisibility(View.GONE);
        event_layout.setVisibility(View.VISIBLE);

        view_pages = (ViewPager)linearview.findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip)linearview.findViewById(R.id.tabs);
        back_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void doRequest() {
        String access_token = sfdcddetails.token_type + " " + sfdcddetails.access_token;
        String url = sfdcddetails.instance_url+WebServiceUrls.SA_BLN_MM_SAVETKTPROFILEDATA+"&eventid="+checkedin_event_record.Events.Id+"&tktprofileid="+attendee_id;
        postMethod = new HttpPostData("Saving Attendee Info", url,setjsonbody().toString(), access_token, AttendeePagerSampleActivity.this);
        postMethod.execute();
        //setjsonbody();
    }
    public void addcobject(String key,AttendeeFeildObj ee){
        parentarray.put(key,ee);
    }
    private JSONObject setjsonbody() {
        JSONObject parent = new JSONObject();
        JSONArray fields = new JSONArray();
        JSONObject fieldobj = new JSONObject();
        try {
            Set<String> setOfKeySet = parentarray.keySet();
            for(String key : setOfKeySet) {
                fieldobj=new JSONObject();
                fieldobj.put("field_api_name",key);
                fieldobj.put("group_display_name",parentarray.get(key).group_display_name);
                fieldobj.put("field_data",parentarray.get(key).field_data);
                // fieldobj.put("field_type",parentarray.get(key).field_type);
                fields.put(fieldobj);
            }
            parent.put("fields",fields);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return parent;
    }
    //  Example body mandatory fields
    /*{
        "fields": [
        {
            "group_display_name": "Basic Information",
                "field_type": "STRING",
                "field_api_name": "TKT_Company__c",
                "field_data": "Microsoft"
        },
        {
            "group_display_name": "Basic Information",
                "field_type": "STRING",
                "field_api_name": "tkt_job_title__c",
                "field_data": "Sr Developer"
        }
  ]
    } */

    @Override
    public void parseJsonResponse(String response) {
        try {
            if(!isValidResponse(response)){
                openSessionExpireAlert(errorMessage(response));
            }else {
                gson = new Gson();
                OrderDetailsHandler orderDetailsHandler = gson.fromJson(response, OrderDetailsHandler.class);
                if(orderDetailsHandler.ticketsInn.size()>0)
                    Util.db.InsertAndUpdateAttendee(orderDetailsHandler.ticketsInn,"");
                if(orderDetailsHandler.listValues.size()>0)
                    Util.db.InsertAndUpdateAttendeeListValues(orderDetailsHandler.listValues);

            }

        }catch (Exception e){
        }
    }

    @Override
    public void insertDB() {

    }
    public class MyPagerAdapter extends PagerAdapter {


        //private final String[] TITLES = { "Profile", "Change Password" };



        @Override
        public CharSequence getPageTitle(int position) {

            Locale l = Locale.getDefault();
            Drawable drawableIcon = null;
            return Util.db.getRegGroupLabelNamewithGroupName("where "+ DBFeilds.REG_EVENT_ID+"='"+checked_in_eventId+"' AND " + DBFeilds.REG_GROUP_NAME+"= '"+tablabels.get(position).Group_Name__c+"'",tablabels.get(position).Group_Name__c);

            //return TITLES.get(position);
        }

        @Override
        public int getCount() {
            return tablabels.size();
        }

        @Override
        public Object instantiateItem(View collection, final int position) {

            //View view = inflater.inflate(position, null);
            View view = inflater.inflate(payment_option_layouts.get(position), null);
            list_data = (ListView)view.findViewById(R.id.list_data);
            //btn_savegroupdata = (Button) view.findViewById(R.id.btn_savegroupdata);
            /// View view = inflater.inflate(TITLES.get(position), null);
            // btn_savegroupdata.setText("Save "+Util.db.getRegGroupLabelNamewithGroupName("where "+ DBFeilds.REG_EVENT_ID+"='"+checked_in_eventId+"' AND " + DBFeilds.REG_GROUP_NAME+"= '"+tablabels.get(position).Group_Name__c+"'",tablabels.get(position).Group_Name__c));
            // btn_savegroupdata.setVisibility(View.GONE);
            AttendeeView(tablabels.get(position).Group_Name__c);
           /* btn_savegroupdata.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(parentarray!=null&&parentarray.values().size()>0) {
                                // Toast.makeText(AttendeePagerSampleActivity.this, "Saving " + Util.db.getRegGroupLabelNamewithGroupName("where " + DBFeilds.REG_EVENT_ID + "='" + checked_in_eventId + "' AND " + DBFeilds.REG_GROUP_NAME + "= '" + tablabels.get(position).Group_Name__c + "'", tablabels.get(position).Group_Name__c), Toast.LENGTH_LONG).show();
                                doRequest();
                            }else{
                                //Toast.makeText(AttendeePagerSampleActivity.this, "No data changed in " + Util.db.getRegGroupLabelNamewithGroupName("where " + DBFeilds.REG_EVENT_ID + "='" + checked_in_eventId + "' AND " + DBFeilds.REG_GROUP_NAME + "= '" + tablabels.get(position).Group_Name__c + "'", tablabels.get(position).Group_Name__c), Toast.LENGTH_LONG).show();

                            }
                        }
                    });}
            });*/
            // if (TITLES.get(position).equalsIgnoreCase("WORK Information")) {
           /* if (tablabels.get(position).Group_Name__c.equalsIgnoreCase("Basic Information")) {
                AttendeeView("Basic Information");
            }*/
            /*if (TITLES.get(position).equalsIgnoreCase("Basic Information")) {
                AttendeeView("Basic Information");
            }else if (TITLES.get(position).equalsIgnoreCase("Work Information")) {
                AttendeeView("Work Information");
            }else if (TITLES.get(position).equalsIgnoreCase("Address Information")) {
                AttendeeView("Address Information");
            } else if (TITLES.get(position).equalsIgnoreCase("Speaker Information")) {
                AttendeeView("Speaker Information");
            }*//*else if (TITLES.get(position).equalsIgnoreCase("Naics Code Information")) {
                AttendeeView("Naics Code Information");
            }*//*else {

            }*/
            /*}else{
              //  CreditCardView(view);

            }*/

            ((ViewPager) collection).addView(view, 0);
            return view;
        }


        @Override
        public void destroyItem(View collection, int position, Object view) {
            ((ViewPager) collection).removeView((View) view);

        }

        @Override
        public boolean isViewFromObject(View view, Object object) {

            return view == ((View) object);
        }
    }
    private class AttendeeDataAdapter extends CursorAdapter {

        Cursor c_new;
        String groupname;

        public AttendeeDataAdapter(Context context, Cursor c,String groupname) {
            super(context, c);
            this.c_new = c;
            this.groupname=groupname;

        }

        public void changeCursor(Cursor cursor) {
            super.changeCursor(cursor);
            this.c_new = cursor;
        }

        @Override
        public int getViewTypeCount() {
            // menu type count
            return 1;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            try {
                View v = inflater.inflate(R.layout.attendeedetail_childitem, null, false);
                ViewHolder holder = new ViewHolder();
                holder.homelayout = (LinearLayout) v.findViewById(R.id.home_layout);
                holder.txt_label = (TextView) v.findViewById(R.id.txt_label);
                holder.edt_name = (EditText) v.findViewById(R.id.edt_name);
                holder.spinner = (Spinner) v.findViewById(R.id.spn_name);
                holder.edt_longtext = (EditText) v.findViewById(R.id.edt_name);
                holder.edt_phone = (EditText) v.findViewById(R.id.edt_phone);
                holder.edt_email = (EditText) v.findViewById(R.id.edt_email);
                holder.imageView = (ImageView) v.findViewById(R.id.imageView);
                v.setTag(holder);
                return v;
            }catch (Exception e){
                e.printStackTrace();
                return v;
            }
        }

        @Override
        public void bindView(final View v, Context context, final Cursor c) {
            try {
                // holder.ThreeDimension.setVisibility(object.getVisibility());
                final ViewHolder holder = (ViewHolder) v.getTag();
                //c.moveToNext();
                holder.txt_label.setText(c.getString(c.getColumnIndex(DBFeilds.REG_LABEL_NAME)));
                String columnname = c.getString(c.getColumnIndex(DBFeilds.REG_COLUMN_NAME));
                //String feildtype = NullChecker(c.getString(c.getColumnIndex(DBFeilds.REG_FIELDTYPE)));

                if (groupname.equalsIgnoreCase(Util.SPEAKER_INFORMATION)) {
                    holder.spinner.setVisibility(View.GONE);
                    holder.edt_email.setVisibility(View.GONE);
                    holder.edt_phone.setVisibility(View.GONE);
                    if (c.getString(c.getColumnIndex(DBFeilds.REG_INCLUDE)).equals("true")) {
                        if (c.getString(c.getColumnIndex(DBFeilds.REG_FIELDTYPE)).equalsIgnoreCase("String") &&
                                (columnname.equalsIgnoreCase(DBFeilds.ATTENDEE_COMPANY_LOGO) || columnname.equalsIgnoreCase(Util.ATTENDEE_IMAGE))) {
                            holder.edt_longtext.setVisibility(View.GONE);
                            holder.edt_name.setVisibility(View.GONE);
                            holder.imageView.setVisibility(View.VISIBLE);
                            if(columnname.equalsIgnoreCase(Util.ATTENDEE_IMAGE)){
                                columnname=DBFeilds.ATTENDEE_IMAGE;
                            }
                            holder.imageView.setTag(columnname);
                            String[] fullurl = checkedin_event_record.image.split("&id=");
                            String url = fullurl[0];
                            Picasso.with(AttendeePagerSampleActivity.this).load(url + "&id=" + c.getString(c.getColumnIndex(columnname)))
                                    .placeholder(R.drawable.default_image)
                                    .error(R.drawable.default_image).into(holder.imageView);
                            if (c.getString(c.getColumnIndex(DBFeilds.REG_ITEM_ID)).equals("true")) {
                                holder.imageView.setEnabled(true);
                                holder.imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        openTakeFromDialg(AttendeePagerSampleActivity.this);
                                    }
                                });
                            }
                            else {
                                holder.imageView.setEnabled(false);
                            }
                        } else if (c.getString(c.getColumnIndex(DBFeilds.REG_FIELDTYPE)).equalsIgnoreCase("String")) {
                            holder.imageView.setVisibility(View.GONE);
                            holder.edt_longtext.setVisibility(View.GONE);
                            holder.edt_name.setVisibility(View.VISIBLE);
                            holder.edt_name.setId(c.getPosition());
                            if(columnname.equalsIgnoreCase(Util.ATTENDEE_SPEAKERBLOGGER)){
                                columnname=DBFeilds.ATTENDEE_SPEAKERBLOGGER;
                            }else if(columnname.equalsIgnoreCase(Util.ATTENDEE_SPEAKERFACEBOOKID)){
                                columnname=DBFeilds.ATTENDEE_SPEAKERFACEBOOKID;
                            }else if(columnname.equalsIgnoreCase(Util.ATTENDEE_SPEAKERLINKEDINID)){
                                columnname=DBFeilds.ATTENDEE_SPEAKERLINKEDINID;
                            }else if(columnname.equalsIgnoreCase(Util.ATTENDEE_SPEAKERTWITTERID)){
                                columnname=DBFeilds.ATTENDEE_SPEAKERTWITTERID;
                            }else if(columnname.equalsIgnoreCase(Util.ATTENDEE_SPEAKERVIDEO)){
                                columnname=DBFeilds.ATTENDEE_SPEAKERVIDEO;
                            }

                            holder.edt_name.setText(NullChecker(c.getString(c.getColumnIndex(columnname))));
                            holder.edt_name.setTag(columnname);
                            if (c.getString(c.getColumnIndex(DBFeilds.REG_ITEM_ID)).equals("true")) {
                                holder.edt_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.edit_pencil, 0);
                                holder.edt_name.setEnabled(true);
                                holder.edt_name.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                            final String beforetext = holder.edt_name.getText().toString();
                                            attendeefieldsarray = new ArrayList<>();
                                            holder.edt_name.addTextChangedListener(new TextWatcher() {
                                                @Override
                                                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                                                @Override
                                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                    if (!beforetext.equalsIgnoreCase(holder.edt_name.getText().toString())) {
                                                        fields = new AttendeeFeildObj();
                                                        fields.group_display_name = groupname;
                                                        fields.field_api_name = holder.edt_name.getTag().toString();
                                                        fields.field_data = holder.edt_name.getText().toString();
                                                        //fields.field_type = "String";
                                                        addcobject(fields.field_api_name, fields);
                                                    }
                                                }
                                                @Override
                                                public void afterTextChanged(Editable s) { }
                                            });
                                        }
                                        return false;
                                    }
                                });
                            } else {
                                holder.edt_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                holder.edt_name.setEnabled(false);
                            }
                        } else if (c.getString(c.getColumnIndex(DBFeilds.REG_FIELDTYPE)).equalsIgnoreCase("TEXTAREA")) {
                            holder.imageView.setVisibility(View.GONE);
                            holder.edt_name.setVisibility(View.GONE);
                            holder.edt_longtext.setVisibility(View.VISIBLE);
                            holder.edt_longtext.setText(Html.fromHtml(NullChecker(c.getString(c.getColumnIndex(columnname)))));
                            holder.edt_longtext.setId(c.getPosition());
                            holder.edt_longtext.setTag(columnname);
                            if (c.getString(c.getColumnIndex(DBFeilds.REG_ITEM_ID)).equals("true")) {
                                holder.edt_longtext.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.edit_pencil, 0);
                                holder.edt_longtext.setEnabled(true);
                                holder.edt_longtext.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                            final String beforetext = holder.edt_longtext.getText().toString();
                                            attendeefieldsarray = new ArrayList<>();
                                            holder.edt_longtext.addTextChangedListener(new TextWatcher() {
                                                @Override
                                                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                                                @Override
                                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                    if (!beforetext.equalsIgnoreCase(holder.edt_longtext.getText().toString())) {
                                                        fields = new AttendeeFeildObj();
                                                        fields.group_display_name = groupname;
                                                        fields.field_api_name = holder.edt_longtext.getTag().toString();
                                                        fields.field_data = holder.edt_longtext.getText().toString();
                                                        //fields.field_type = "String";
                                                        addcobject(fields.field_api_name, fields);
                                                    }
                                                }
                                                @Override
                                                public void afterTextChanged(Editable s) { }
                                            });
                                        }
                                        return false;
                                    }
                                });
                            } else {
                                holder.edt_longtext.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                holder.edt_longtext.setEnabled(false);
                            }
                        }
                    }
                }

                else if (c.getString(c.getColumnIndex(DBFeilds.REG_INCLUDE)).equals("true")) {
                    if (c.getString(c.getColumnIndex(DBFeilds.REG_FIELDTYPE)).equalsIgnoreCase("String")
                            || c.getString(c.getColumnIndex(DBFeilds.REG_FIELDTYPE)).equalsIgnoreCase("URL")) {
                        holder.spinner.setVisibility(View.GONE);
                        holder.edt_email.setVisibility(View.GONE);
                        holder.edt_phone.setVisibility(View.GONE);
                        holder.edt_longtext.setVisibility(View.GONE);
                        holder.edt_name.setVisibility(View.VISIBLE);

                        if (groupname.equalsIgnoreCase(Util.WORK_ADDRESS) || groupname.equalsIgnoreCase(Util.HOME_ADDRESS) ||
                                groupname.equalsIgnoreCase(Util.BILLING_ADDRESS)) {
                            if (columnname.equalsIgnoreCase(Util.ATTENDEE_WORK_COUNTRY)||
                                    columnname.equalsIgnoreCase(Util.ATTENDEE_HOME_COUNTRY)||
                                    columnname.equalsIgnoreCase(Util.ATTENDEE_BILLING_COUNTRY)||
                                    columnname.equalsIgnoreCase(Util.ATTENDEE_WORK_STATE)||
                                    columnname.equalsIgnoreCase(Util.ATTENDEE_HOME_STATE)||
                                    columnname.equalsIgnoreCase(Util.ATTENDEE_BILLING_STATE)){
                                holder.edt_name.setVisibility(View.GONE);
                                holder.spinner.setVisibility(View.VISIBLE);
                                holder.spinner.setId(c.getPosition());
                                if (columnname.equalsIgnoreCase(Util.ATTENDEE_WORK_COUNTRY) ||
                                        columnname.equalsIgnoreCase(Util.ATTENDEE_HOME_COUNTRY) ||
                                        columnname.equalsIgnoreCase(Util.ATTENDEE_BILLING_COUNTRY)){
                                    holder.spinner.setTag("Country__c");

                                    if (columnname.equalsIgnoreCase(Util.ATTENDEE_HOME_COUNTRY)) {
                                        columnname = DBFeilds.ATTENDEE_HOME_COUNTRY;
                                    }else if (columnname.equalsIgnoreCase(Util.ATTENDEE_WORK_COUNTRY)) {
                                        columnname = DBFeilds.ATTENDEE_WORK_COUNTRY;
                                    }else if (columnname.equalsIgnoreCase(Util.ATTENDEE_BILLING_COUNTRY)) {
                                        columnname = DBFeilds.ATTENDEE_BILLING_COUNTRY;
                                    }
                                    country_list = Util.db.getCountryList("");
                                    adaptercountry = new ArrayAdapter<String>(AttendeePagerSampleActivity.this, android.R.layout.simple_spinner_item, country_list);
                                    holder.spinner.setAdapter(adaptercountry);
                                    for(int i=0; i<country_list.size(); i++){
                                        if(country_list.get(i).trim().equalsIgnoreCase(NullChecker(c.getString(c.getColumnIndex(columnname))))){
                                            holder.spinner.setSelection(i);
                                            String cc=country_list.get(i);
                                            state_list = Util.db.getStateList(Util.db.getCountryId(country_list.get(i)));
                                        }
                                    }

                                }
                                else if (columnname.equalsIgnoreCase(Util.ATTENDEE_WORK_STATE) ||
                                        columnname.equalsIgnoreCase(Util.ATTENDEE_HOME_STATE) ||
                                        columnname.equalsIgnoreCase(Util.ATTENDEE_BILLING_STATE)) {
                                    holder.spinner.setTag("State__c");
                                    if (columnname.equalsIgnoreCase(Util.ATTENDEE_WORK_STATE)) {
                                        columnname = DBFeilds.ATTENDEE_WORK_STATE;
                                    }else if (columnname.equalsIgnoreCase(Util.ATTENDEE_HOME_STATE)) {
                                        columnname = DBFeilds.ATTENDEE_HOME_STATE;
                                    }else if (columnname.equalsIgnoreCase(Util.ATTENDEE_BILLING_STATE)) {
                                        columnname = DBFeilds.ATTENDEE_BILLING_STATE;
                                    }
                                    // state_list = Util.db.getStateList(Util.db.getCountryId(country));
                                    ArrayAdapter<String>   adapterstate = new ArrayAdapter<String>(AttendeePagerSampleActivity.this, android.R.layout.simple_spinner_item, state_list);
                                    holder.spinner.setAdapter(adapterstate);
                                    for(int i=0; i<state_list.size(); i++){
                                        if(state_list.get(i).trim().equalsIgnoreCase(NullChecker(c.getString(c.getColumnIndex(columnname))))){
                                            holder.spinner.setSelection(i);

                                        }
                                    }
                                }


                            }
                            else {
                                holder.spinner.setVisibility(View.GONE);
                                holder.edt_name.setVisibility(View.VISIBLE);
                                if (columnname.equalsIgnoreCase(Util.ATTENDEE_WORK_ADDRESS_1) ||
                                        columnname.equalsIgnoreCase(Util.ATTENDEE_HOME_ADDRESS_1) ||
                                        columnname.equalsIgnoreCase(Util.ATTENDEE_BILLING_ADDRESS_1))
                                    holder.edt_name.setTag(DBFeilds.ATTENDEE_WORK_ADDRESS_1);
                                else if (columnname.equalsIgnoreCase(Util.ATTENDEE_WORK_ADDRESS_2) ||
                                        columnname.equalsIgnoreCase(Util.ATTENDEE_HOME_ADDRESS_2) ||
                                        columnname.equalsIgnoreCase(Util.ATTENDEE_BILLING_ADDRESS_2))
                                    holder.edt_name.setTag(DBFeilds.ATTENDEE_WORK_ADDRESS_2);
                                else if (columnname.equalsIgnoreCase(Util.ATTENDEE_WORK_CITY) ||
                                        columnname.equalsIgnoreCase(Util.ATTENDEE_HOME_CITY) ||
                                        columnname.equalsIgnoreCase(Util.ATTENDEE_BILLING_CITY))
                                    holder.edt_name.setTag("City__c");
                                else if (columnname.equalsIgnoreCase(Util.ATTENDEE_WORK_COUNTRY) ||
                                        columnname.equalsIgnoreCase(Util.ATTENDEE_HOME_COUNTRY) ||
                                        columnname.equalsIgnoreCase(Util.ATTENDEE_BILLING_COUNTRY))
                                    holder.edt_name.setTag("Country__c");
                                else if (columnname.equalsIgnoreCase(Util.ATTENDEE_WORK_STATE) ||
                                        columnname.equalsIgnoreCase(Util.ATTENDEE_HOME_STATE) ||
                                        columnname.equalsIgnoreCase(Util.ATTENDEE_BILLING_STATE))
                                    holder.edt_name.setTag("State__c");
                                else if (columnname.equalsIgnoreCase(Util.ATTENDEE_WORK_ZIPCODE) ||
                                        columnname.equalsIgnoreCase(Util.ATTENDEE_HOME_ZIPCODE) ||
                                        columnname.equalsIgnoreCase(Util.ATTENDEE_BILLING_ZIPCODE))
                                    holder.edt_name.setTag("ZipCode__c");

                                if (columnname.equalsIgnoreCase(Util.ATTENDEE_WORK_ADDRESS_1)) {
                                    columnname = DBFeilds.ATTENDEE_WORK_ADDRESS_1;
                                } else if (columnname.equalsIgnoreCase(Util.ATTENDEE_WORK_ADDRESS_2)) {
                                    columnname = DBFeilds.ATTENDEE_WORK_ADDRESS_2;
                                } else if (columnname.equalsIgnoreCase(Util.ATTENDEE_WORK_CITY)) {
                                    columnname = DBFeilds.ATTENDEE_WORK_CITY;
                                } else if (columnname.equalsIgnoreCase(Util.ATTENDEE_WORK_COUNTRY)) {
                                    columnname = DBFeilds.ATTENDEE_WORK_COUNTRY;
                                } else if (columnname.equalsIgnoreCase(Util.ATTENDEE_WORK_STATE)) {
                                    columnname = DBFeilds.ATTENDEE_WORK_STATE;
                                } else if (columnname.equalsIgnoreCase(Util.ATTENDEE_WORK_ZIPCODE)) {
                                    columnname = DBFeilds.ATTENDEE_WORK_ZIPCODE;
                                } else if (columnname.equalsIgnoreCase(Util.ATTENDEE_HOME_ADDRESS_1)) {
                                    columnname = DBFeilds.ATTENDEE_HOME_ADDRESS_1;
                                } else if (columnname.equalsIgnoreCase(Util.ATTENDEE_HOME_ADDRESS_2)) {
                                    columnname = DBFeilds.ATTENDEE_HOME_ADDRESS_2;
                                } else if (columnname.equalsIgnoreCase(Util.ATTENDEE_HOME_CITY)) {
                                    columnname = DBFeilds.ATTENDEE_HOME_CITY;
                                } else if (columnname.equalsIgnoreCase(Util.ATTENDEE_HOME_COUNTRY)) {
                                    columnname = DBFeilds.ATTENDEE_HOME_COUNTRY;
                                } else if (columnname.equalsIgnoreCase(Util.ATTENDEE_HOME_STATE)) {
                                    columnname = DBFeilds.ATTENDEE_HOME_STATE;
                                } else if (columnname.equalsIgnoreCase(Util.ATTENDEE_HOME_ZIPCODE)) {
                                    columnname = DBFeilds.ATTENDEE_HOME_ZIPCODE;
                                } else if (columnname.equalsIgnoreCase(Util.ATTENDEE_BILLING_ADDRESS_1)) {
                                    columnname = DBFeilds.ATTENDEE_BILLING_ADDRESS_1;
                                } else if (columnname.equalsIgnoreCase(Util.ATTENDEE_BILLING_ADDRESS_2)) {
                                    columnname = DBFeilds.ATTENDEE_BILLING_ADDRESS_2;
                                } else if (columnname.equalsIgnoreCase(Util.ATTENDEE_BILLING_CITY)) {
                                    columnname = DBFeilds.ATTENDEE_BILLING_CITY;
                                } else if (columnname.equalsIgnoreCase(Util.ATTENDEE_BILLING_COUNTRY)) {
                                    columnname = DBFeilds.ATTENDEE_BILLING_COUNTRY;
                                } else if (columnname.equalsIgnoreCase(Util.ATTENDEE_BILLING_STATE)) {
                                    columnname = DBFeilds.ATTENDEE_BILLING_STATE;
                                } else if (columnname.equalsIgnoreCase(Util.ATTENDEE_BILLING_ZIPCODE)) {
                                    columnname = DBFeilds.ATTENDEE_BILLING_ZIPCODE;
                                }
                                holder.edt_name.setText(NullChecker(c.getString(c.getColumnIndex(columnname))));
                                holder.edt_name.setId(c.getPosition());
                                if (c.getString(c.getColumnIndex(DBFeilds.REG_ITEM_ID)).equals("true")) {
                                    holder.edt_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.edit_pencil, 0);
                                    holder.edt_name.setEnabled(true);
                                    holder.edt_name.setOnTouchListener(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View v, MotionEvent event) {
                                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                                //Toast.makeText(AttendeePagerSampleActivity.this, "Touched "+holder.edt_name.getId()+holder.edt_name.getTag(), Toast.LENGTH_SHORT).show();
                                                final String beforetext = holder.edt_name.getText().toString();
                                                attendeefieldsarray = new ArrayList<>();
                                                holder.edt_name.addTextChangedListener(new TextWatcher() {
                                                    @Override
                                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                                    }

                                                    @Override
                                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                        if (!beforetext.equalsIgnoreCase(holder.edt_name.getText().toString())) {
                                                            String tagname = holder.edt_name.getTag().toString();
                                                            String data = "{\"attributes\":{\"type\":\"BLN_Address__c\"},\"" + tagname + "\":\"" + holder.edt_name.getText().toString() + "\"}";
                                                            fields = new AttendeeFeildObj();
                                                            //fields.group_display_name = groupname;
                                                            if (groupname.equalsIgnoreCase(Util.WORK_ADDRESS))
                                                                fields.field_api_name = "work_address__c";
                                                            else if (groupname.equalsIgnoreCase(Util.BILLING_ADDRESS))
                                                                fields.field_api_name = "billing_address__c";
                                                            else if (groupname.equalsIgnoreCase(Util.HOME_ADDRESS))
                                                                fields.field_api_name = "home_address__c";
                                                            fields.field_data = data;
                                                            //"{\"attributes\":{\"type\":\"BLN_Address__c\"},\"Address1__c\":\"Patterson Village Court\"}"
                                                            addcobject(fields.field_api_name, fields);
                                                        }
                                                    }

                                                    @Override
                                                    public void afterTextChanged(Editable s) {
                                                        String sdf = c.getString(c.getColumnIndex(DBFeilds.REG_COLUMN_NAME));
                                                        //Toast.makeText(AttendeePagerSampleActivity.this, s.toString()+holder.edt_name.getId(), Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            }
                                            return false;
                                        }
                                    });
                                } else {
                                    holder.edt_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                    holder.edt_name.setEnabled(false);
                                }
                            }} else {
                            if (groupname.equalsIgnoreCase(Util.BASIC_INFORMATION)) {
                                if (columnname.equalsIgnoreCase(Util.ATTENDEECOMPANY)) {
                                    columnname = DBFeilds.ATTENDEE_COMPANY;
                                }
                                holder.edt_name.setTag(columnname);
                                holder.edt_name.setText(NullChecker(c.getString(c.getColumnIndex(columnname))));
                            } else {
                                holder.edt_name.setTag(columnname);
                                holder.edt_name.setText(NullChecker(c.getString(c.getColumnIndex(columnname))));
                            }


                            //holder.edt_name.setText(NullChecker(c.getString(c.getColumnIndex(columnname))));
                            holder.edt_name.setId(c.getPosition());
                            if (c.getString(c.getColumnIndex(DBFeilds.REG_ITEM_ID)).equals("true")) {
                                holder.edt_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.edit_pencil, 0);
                                holder.edt_name.setEnabled(true);
                                holder.edt_name.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                            //Toast.makeText(AttendeePagerSampleActivity.this, "Touched "+holder.edt_name.getId()+holder.edt_name.getTag(), Toast.LENGTH_SHORT).show();
                                            final String beforetext = holder.edt_name.getText().toString();
                                            attendeefieldsarray = new ArrayList<>();
                                            holder.edt_name.addTextChangedListener(new TextWatcher() {
                                                @Override
                                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                                }

                                                @Override
                                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                    if (!beforetext.equalsIgnoreCase(holder.edt_name.getText().toString())) {
                                                        fields = new AttendeeFeildObj();
                                                        fields.group_display_name = groupname;
                                                        fields.field_api_name = holder.edt_name.getTag().toString();
                                                        fields.field_data = holder.edt_name.getText().toString();
                                                        //fields.field_type = "String";
                                                        addcobject(fields.field_api_name, fields);
                                                    }
                                                }

                                                @Override
                                                public void afterTextChanged(Editable s) {
                                                    String sdf = c.getString(c.getColumnIndex(DBFeilds.REG_COLUMN_NAME));
                                                    //Toast.makeText(AttendeePagerSampleActivity.this, s.toString()+holder.edt_name.getId(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        return false;
                                    }
                                });
                            } else {
                                holder.edt_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                holder.edt_name.setEnabled(false);
                            }

                        }
                    }
                    else if (c.getString(c.getColumnIndex(DBFeilds.REG_FIELDTYPE)).equalsIgnoreCase("PICKLIST")) {
                        holder.spinner.setVisibility(View.VISIBLE);
                        holder.edt_name.setVisibility(View.GONE);
                        holder.edt_email.setVisibility(View.GONE);
                        holder.edt_phone.setVisibility(View.GONE);
                        holder.edt_longtext.setVisibility(View.GONE);
                        holder.spinner.setTag(columnname);
                        holder.spinner.setId(c.getPosition());
                        String feildname = columnname;
                        final String value = c.getString(c.getColumnIndex(columnname));
                        if (columnname.equalsIgnoreCase(DBFeilds.ATTENDEE_ETHNICITY)) {
                            feildname = Util.ATTENDEE_ETHNICITY;
                        } else if (columnname.equalsIgnoreCase(DBFeilds.ATTENDEE_GENDER)) {
                            feildname = Util.ATTENDEE_GENDER;
                        } else if (columnname.equalsIgnoreCase(DBFeilds.ATTENDEE_BUSINESS_STRUCTURE)) {
                            feildname = Util.ATTENDEE_BUSINESS_STRUCTURE;
                        } else if (columnname.equalsIgnoreCase(DBFeilds.ATTENDEE_GEOGRAPHICAL_REGION)) {
                            feildname = Util.ATTENDEE_GEOGRAPHICAL_REGION;
                        } else if (columnname.equalsIgnoreCase(DBFeilds.ATTENDEE_NUMBER_OF_EMPLOYEES)) {
                            feildname = Util.ATTENDEE_NUMBER_OF_EMPLOYEES;
                        } else if (columnname.equalsIgnoreCase(DBFeilds.ATTENDEE_REVENUE)) {
                            feildname = Util.ATTENDEE_REVENUE;
                        }else{
                            feildname = columnname;
                        }
                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(AttendeePagerSampleActivity.this, android.R.layout.simple_spinner_item, Util.db.getFiterItems(feildname));
                        holder.spinner.setAdapter(spinnerArrayAdapter);
                        int k = 0;
                        for (String key : Util.db.getFiterItems(feildname)) {
                            if (value.equalsIgnoreCase(key)) {
                                holder.spinner.setSelection(k, false);
                                break;
                            }
                            k++;
                        }
                        if (c.getString(c.getColumnIndex(DBFeilds.REG_ITEM_ID)).equals("true")) {
                            holder.spinner.setEnabled(true);
                            final String finalFeildname = feildname;
                            holder.spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                           int arg2, long arg3) {
                                    if(!holder.spinner.getSelectedItem().toString().equalsIgnoreCase(value)) {
                                        fields = new AttendeeFeildObj();
                                        fields.group_display_name = groupname;
                                        fields.field_api_name = finalFeildname;
                                        fields.field_data = Util.db.getPicklistItemId(holder.spinner.getTag().toString(), holder.spinner.getSelectedItem().toString());
                                        //fields.field_type = "String";
                                        addcobject(fields.field_api_name, fields);
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> arg0) {
                                }
                            });
                        } else {
                            holder.spinner.setEnabled(false);
                            //showCustomToast(AttendeePagerSampleActivity.this,"No Update Access",R.drawable.img_like, R.drawable.toast_redrounded, false);
                        }

                    }
                    else if (c.getString(c.getColumnIndex(DBFeilds.REG_FIELDTYPE)).equalsIgnoreCase("PHONE")
                            || c.getString(c.getColumnIndex(DBFeilds.REG_FIELDTYPE)).equalsIgnoreCase("DOUBLE")) {
                        holder.edt_name.setVisibility(View.GONE);
                        holder.edt_email.setVisibility(View.GONE);
                        holder.edt_longtext.setVisibility(View.GONE);
                        holder.spinner.setVisibility(View.GONE);
                        holder.edt_phone.setVisibility(View.VISIBLE);
                        holder.edt_phone.setId(c.getPosition());
                        holder.edt_phone.setTag(columnname);
                        holder.edt_phone.setText(NullChecker(c.getString(c.getColumnIndex(columnname))));
                        if (c.getString(c.getColumnIndex(DBFeilds.REG_ITEM_ID)).equals("true")) {
                            holder.edt_phone.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.edit_pencil, 0);
                            holder.edt_phone.setEnabled(true);
                        } else {
                            holder.edt_phone.setEnabled(false);
                        }

                    }
                    else if (c.getString(c.getColumnIndex(DBFeilds.REG_FIELDTYPE)).equalsIgnoreCase("TEXTAREA")) {
                        holder.edt_name.setVisibility(View.GONE);
                        holder.edt_phone.setVisibility(View.GONE);
                        holder.edt_email.setVisibility(View.GONE);
                        holder.spinner.setVisibility(View.GONE);
                        holder.edt_longtext.setVisibility(View.VISIBLE);
                        //holder.edt_longtext.setText(NullChecker(c.getString(c.getColumnIndex(columnname))));
                        holder.edt_longtext.setText(Html.fromHtml(NullChecker(c.getString(c.getColumnIndex(columnname)))));
                        holder.edt_longtext.setId(c.getPosition());
                        holder.edt_longtext.setTag(columnname);
                        if (c.getString(c.getColumnIndex(DBFeilds.REG_ITEM_ID)).equals("true")) {
                            holder.edt_longtext.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.edit_pencil, 0);
                            holder.edt_longtext.setEnabled(true);
                            holder.edt_longtext.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                        //Toast.makeText(AttendeePagerSampleActivity.this, "Touched "+holder.edt_longtext.getId()+holder.edt_longtext.getTag(), Toast.LENGTH_SHORT).show();
                                        final String beforetext = holder.edt_longtext.getText().toString();
                                        attendeefieldsarray = new ArrayList<>();
                                        holder.edt_longtext.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                                            @Override
                                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                if (!beforetext.equalsIgnoreCase(holder.edt_longtext.getText().toString())) {
                                                    fields = new AttendeeFeildObj();
                                                    fields.group_display_name = groupname;
                                                    fields.field_api_name = holder.edt_longtext.getTag().toString();
                                                    fields.field_data = holder.edt_longtext.getText().toString();
                                                    //fields.field_type = "String";
                                                    addcobject(fields.field_api_name, fields);
                                                }
                                            }
                                            @Override
                                            public void afterTextChanged(Editable s) { }
                                        });
                                    }
                                    return false;
                                }
                            });
                        } else {
                            holder.edt_longtext.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            holder.edt_longtext.setEnabled(false);
                        }



                    }
                    else if (c.getString(c.getColumnIndex(DBFeilds.REG_FIELDTYPE)).equalsIgnoreCase("EMAIL")) {
                        holder.edt_longtext.setVisibility(View.GONE);
                        holder.edt_name.setVisibility(View.GONE);
                        holder.edt_phone.setVisibility(View.GONE);
                        holder.spinner.setVisibility(View.GONE);
                        holder.edt_email.setVisibility(View.VISIBLE);
                        holder.edt_email.setId(c.getPosition());
                        holder.edt_email.setTag(columnname);
                        holder.edt_email.setText(NullChecker(c.getString(c.getColumnIndex(columnname))));
                        if (c.getString(c.getColumnIndex(DBFeilds.REG_ITEM_ID)).equals("true")) {
                            holder.edt_email.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.edit_pencil, 0);
                            holder.edt_email.setEnabled(true);
                        } else {
                            holder.edt_email.setEnabled(false);
                        }

                    }
                    else{
                        holder.edt_longtext.setVisibility(View.GONE);
                        holder.edt_phone.setVisibility(View.GONE);
                        holder.spinner.setVisibility(View.GONE);
                        holder.edt_email.setVisibility(View.GONE);
                        holder.edt_name.setVisibility(View.VISIBLE);
                        holder.edt_name.setId(c.getPosition());
                        holder.edt_name.setTag(columnname);
                        holder.edt_name.setText(NullChecker(c.getString(c.getColumnIndex(columnname))));
                    }

                }
            }


            catch (Exception e){
                e.printStackTrace();
            }
        }


    }


/*
    private class AttendeeListData extends CursorAdapter {

        Cursor c_new;

        public AttendeeListData(Context context, Cursor c) {
            super(context, c);
            this.c_new = c;

        }

        public void changeCursor(Cursor cursor) {
            super.changeCursor(cursor);
            this.c_new = cursor;
        }

        @Override
        public int getViewTypeCount() {
            // menu type count
            return 1;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            try {
                View v = inflater.inflate(R.layout.attendeedetail_childitem, null, false);
                ViewHolder holder = new ViewHolder();
                holder.homelayout = (LinearLayout) v.findViewById(R.id.home_layout);
                holder.txt_label = (TextView) v.findViewById(R.id.txt_label);
                holder.edt_name = (EditText) v.findViewById(R.id.edt_name);
                holder.edt_longtext = (EditText) v.findViewById(R.id.edt_name);
                holder.edt_phone = (EditText) v.findViewById(R.id.edt_phone);
                v.setTag(holder);
                return v;
            }catch (Exception e){
                e.printStackTrace();
                return v;
            }
        }

        @Override
        public void bindView(View v, Context context, final Cursor c) {
            try {
                // holder.ThreeDimension.setVisibility(object.getVisibility());
                final ViewHolder holder = (ViewHolder) v.getTag();
                //c.moveToNext();
                holder.txt_label.setText(c.getString(c.getColumnIndex(DBFeilds.REG_LABEL_NAME)));
                String s = c.getString(c.getColumnIndex(DBFeilds.REG_COLUMN_NAME));
                if(!s.equalsIgnoreCase("Billing Address")&&!s.equalsIgnoreCase("Work Address")&&
                        !s.equalsIgnoreCase("Home Address")&&c.getColumnIndex(s)!=-1)
                {
                    holder.edt_name.setText(NullChecker(c.getString(c.getColumnIndex(s))));
                }else{
                    Toast.makeText(AttendeePagerSampleActivity.this, s, Toast.LENGTH_LONG).show();

                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
*/


    private class ViewHolder {
        TextView txt_label;
        EditText edt_name,edt_phone,edt_longtext,edt_email;
        Spinner spinner;
        ImageView imageView;
        LinearLayout homelayout;


    }

    private void AttendeeView(String groupname) {
        try {

            att_cursor = Util.db.getRegChildswithGroupName("where Item_Reg_Settings." + DBFeilds.REG_GROUP_NAME + " = '" + groupname + "'"+whereClause);
            if (att_cursor != null && att_cursor.getCount() > 0) {
                if (detailAdapter != null) {
                    detailAdapter = new AttendeeDataAdapter(AttendeePagerSampleActivity.this, att_cursor, groupname);
                    list_data.setAdapter(detailAdapter);
                }else {
                    detailAdapter = new AttendeeDataAdapter(AttendeePagerSampleActivity.this, att_cursor, groupname);
                    list_data.setAdapter(detailAdapter);
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//		Log.i("---------------onActivity Result------------", ":" + requestCode + " : " + data.getStringExtra(Util.INTENT_KEY_1));
        try {
            if((requestCode == REQUEST_CODE_CROP_IMAGE)&& (data!=null)){

                String path = data.getStringExtra(CropImage.IMAGE_PATH);

                if ((path == null)||(TextUtils.isEmpty(path))) {
                    return;
                }
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                holder.setImageBitmap(bitmap);
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
                            startCropImage(AttendeeDetailActivity.this);

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
                    startCropImage(AttendeeDetailActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == CROP_FROM_CAMERA) {

                Bundle extras = data.getExtras();

                if (extras != null) {
                    attendee_photo = extras.getParcelable("data");
                    img_attendee.setImageBitmap(attendee_photo);

                }
            }else if (requestCode == FINISH_RESULT) {
                startActivity(new Intent(AttendeeDetailActivity.this, SplashActivity.class));
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public class MyAdapter extends BaseExpandableListAdapter {
        public MyAdapter() {
        }

        public int getGroupCount() {
            return AttendeePagerSampleActivity.this.tablabels.size();
        }

        public int getChildrenCount(int groupPosition) {
            return 0;
        }

        public Object getGroup(int position) {
            Locale locale = Locale.getDefault();
            DataBase dataBase = Util.db;
            return dataBase.getRegGroupLabelNamewithGroupName("where reg_eventid='" + AttendeePagerSampleActivity.this.checked_in_eventId + "' AND " + DBFeilds.REG_GROUP_NAME + "= '" + ((RegistrationSettingsController) AttendeePagerSampleActivity.this.tablabels.get(position)).Group_Name__c + "'", ((RegistrationSettingsController) AttendeePagerSampleActivity.this.tablabels.get(position)).Group_Name__c);
        }

        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        public long getGroupId(int groupPosition) {
            return 0;
        }

        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        public boolean hasStableIds() {
            return false;
        }

        public View getGroupView(int position, boolean isExpanded, View convertView, ViewGroup parent) {
            View view = AttendeePagerSampleActivity.this.inflater.inflate(((Integer) AttendeePagerSampleActivity.this.payment_option_layouts.get(position)).intValue(), (ViewGroup) null);
            AttendeePagerSampleActivity.this.list_data = (ListView) view.findViewById(R.id.list_data);
            AttendeePagerSampleActivity AttendeePagerSampleActivity = AttendeePagerSampleActivity.this;
            AttendeePagerSampleActivity.AttendeeView(((RegistrationSettingsController) AttendeePagerSampleActivity.tablabels.get(position)).Group_Name__c);
            return view;
        }

        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            return null;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }

}
