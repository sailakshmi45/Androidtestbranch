//  ScanAttendee Android
//  Created by Ajay on Dec 24, 2015
//  This class is used to get the all the orders and tickets information from backend.
//  Copyright (c) 2014 Globalnest. All rights reserved

/**
 *
 */
package com.globalnest.scanattendee;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.drawerlayout.widget.DrawerLayout;

import com.brother.ptouch.sdk.NetPrinter;
import com.brother.ptouch.sdk.Printer;
import com.brother.ptouch.sdk.PrinterInfo;
import com.globalnest.BackgroundReciver.DownloadResultReceiver;
import com.globalnest.BackgroundReciver.DownloadService;
import com.globalnest.brother.ptouch.sdk.printdemo.common.Common;
import com.globalnest.classes.RateTextCircularProgressBar;
import com.globalnest.database.DBFeilds;
import com.globalnest.mvc.BadgeDataNew;
import com.globalnest.mvc.BadgeResponseNew;
import com.globalnest.mvc.CSVDataLoaderTask;
import com.globalnest.mvc.CSVFile;
import com.globalnest.mvc.ExternalSettings;
import com.globalnest.mvc.RefreshResponse;
import com.globalnest.network.HttpGetMethod;
import com.globalnest.network.HttpPostData;
import com.globalnest.network.WebServiceUrls;
import com.globalnest.objects.WebsocketResponseObj;
import com.globalnest.printer.BrotherPrinter;
import com.globalnest.printer.GetZebraPrinterConfigTask;
import com.globalnest.printer.PrinterDetails;
import com.globalnest.printer.ZebraPrinter;
import com.globalnest.scanattendee.ExpandablePanel.OnExpandListener;
import com.globalnest.scanattendee.socketmobile.ScannerSettingsApplication;
import com.globalnest.utils.AlertDialogCustom;
import com.globalnest.utils.AppUtils;
import com.globalnest.utils.ITransaction;
import com.globalnest.utils.Util;
import com.google.gson.Gson;
import com.zebra.sdk.comm.ConnectionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ExternalSettingsActivity extends BaseActivity implements DownloadResultReceiver.Receiver {

	/*
	 * (non-Javadoc)
	 *
	 * @see com.globalnest.network.IPostResponse#doRequest()
	 */
	private static final int REQUEST_ENABLE_BT = 1;
	LinearLayout layout_printer, layout_bluetooth,info_zebra;
	FrameLayout layout_scanner, layout_export, layout_download, ticketdefaultsetting, frame_checkin_out, layout_import,frame_zebrasettings;
	ToggleButton toggle_print, toggle_checkin, toggle_barcode,toggle_doubleSideBadge,toggle_validate_badge,
			toggle_promocode,toggle_apppermission,toggle_online_mode,toggle_zebra,toggle_livedata,toggle_groupcheckin,toggle_idonthaveemail;
	TextView txt_on_off_printer, txt_on_off_print, txt_on_off_checkin, txt_on_off_barcode,txt_on_off_validate_badge,
			txt_moreSetting, txt_checkout_settings, txt_export, txt_printer_name,txt_on_off_doubleSideBadge,txt_offline_print_cout,
			txt_on_off_promocode,txt_on_off_permission,txt_on_off_offlinemode,txt_on_off_zebra,txt_reset_settings,txt_pixelvalue;
	RadioGroup radio_doubleside;
	TextView right_pixelvalue;
	RadioButton radio_identical,radio_mirror,radio_identical_two_in_one,radio_mirror_two_in_one;
	ImageView img_printer, img_export;
	LinearLayout layout_reset_settings,layout_refreshcheckins;

	private ProgressBar progress_loader;
	public static Button btn_scanner_setting;
	public static ImageView img_scanner;
	public static TextView txt_on_off_scanner, txt_scanner_name, txt_battery_level;
	// public static TextView txt_scanner_name;
	ExternalSettings ext_settings;
	SearchThread search_thread;
	BluetoothAdapter _bluetoothAdapter;
	private ExpandablePanel panel, checkin_out_panel,reset_panel;
	private File fileToExport;
	boolean _bluetoothIsOn = false;
	private String request_type = ITransaction.EMPTY_STRING;
	private ArrayList<BadgeResponseNew> badge_res = new ArrayList<BadgeResponseNew>();
	private RadioButton radio_checkin_btn, radio_checkin_out_btn;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCustomContentView(R.layout.external_settings_layout);

		_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (_bluetoothAdapter != null){
			_bluetoothIsOn = _bluetoothAdapter.isEnabled();
		}

		Util.external_setting_pref = getSharedPreferences(Util.EXTERNAL_PREF, MODE_PRIVATE);
		ext_settings = new ExternalSettings();
		if (!Util.external_setting_pref.getString(sfdcddetails.user_id + checked_in_eventId, "").isEmpty()){
			ext_settings = new Gson().fromJson(
					Util.external_setting_pref.getString(sfdcddetails.user_id + checked_in_eventId, ""),
					ExternalSettings.class);
		}
		final String where_att = " Where " + DBFeilds.BADGE_NEW_EVENT_ID + " = '" + checked_in_eventId + "' AND "
				+ DBFeilds.BADGE_NEW_ID + " = '" + checkedin_event_record.Events.Mobile_Default_Badge__c + "'";
		badge_res = Util.db.getAllBadges(where_att);

		// setUI();
		back_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ext_settings.custom_barcode = toggle_barcode.isChecked();
				ext_settings.allow_promocode = toggle_promocode.isChecked();
				ext_settings.quick_checkin = toggle_checkin.isChecked();
				ext_settings.quick_print = toggle_print.isChecked();
				ext_settings.doubleSide_badge = toggle_doubleSideBadge.isChecked();
				ext_settings.online_mode = toggle_online_mode.isChecked();
				ext_settings.zebra_settings = toggle_zebra.isChecked();
				ext_settings.checkin_checkout = radio_checkin_out_btn.isChecked();
				ext_settings.allow_livedata = toggle_livedata.isChecked();
				ext_settings.group_checkin = toggle_groupcheckin.isChecked();
				ext_settings.donthaveemail = toggle_idonthaveemail.isChecked();
				ext_settings.Android_Badge_Pixel = right_pixelvalue.getText().toString();
				if(toggle_doubleSideBadge.isChecked()){
					ext_settings.identical_doubleSide_badge=radio_identical.isChecked();
					ext_settings.mirror_doubleSide_badge=radio_mirror.isChecked();
					ext_settings.mirror_doubleSide_badge_two_in_one=radio_mirror_two_in_one.isChecked();
					ext_settings.identical_doubleSide_badge_two_in_one=radio_identical_two_in_one.isChecked();
				}
				else {
					ext_settings.identical_doubleSide_badge=false;
					ext_settings.mirror_doubleSide_badge=false;
					ext_settings.mirror_doubleSide_badge_two_in_one=false;
					ext_settings.identical_doubleSide_badge_two_in_one=false;
				}
				ext_settings.isValidateBadge=toggle_validate_badge.isChecked();
				isValidate_badge_reg_settings=toggle_validate_badge.isChecked();
				//Util.dashboardHandler.isLoadBadgeId=toggle_validate_badge.isChecked();
				//Util.dashboard_data_pref.edit().putString(sfdcddetails.user_id + checked_in_eventId, new Gson().toJson(Util.dashboardHandler)).commit();
				// Util.external_setting_pref.edit().putString(Util.EXTERNAL_STRING,
				// new Gson().toJson(ext_settings).toString()).commit();
				Util.checkin_only_pref.edit().putBoolean(sfdcddetails.user_id+checked_in_eventId, !ext_settings.checkin_checkout).commit();
				Util.external_setting_pref.edit().putString(sfdcddetails.user_id + checked_in_eventId,
						new Gson().toJson(ext_settings).toString()).commit();
				/*if(!PrinterDetails.selectedPrinterPrefrences.getString(ZebraPrinter.SELECTED_PRINTER,"").equals("Zebra")) {
					new GetZebraPrinterConfigTask(ExternalSettingsActivity.this).execute();

				}*/
				finish();
			}
		});
		right_pixelvalue.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				openPixelDialog(ExternalSettingsActivity.this);
			}
		});

		toggle_online_mode.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					toggle_validate_badge.setChecked(false);
					txt_appofflinemode.setVisibility(View.GONE);
					//showCustomToast(ExternalSettingsActivity.this,"Sorry! In Offline Mode Validation is OFF", R.drawable.img_like, R.drawable.toast_redrounded, false);
				}else
					txt_appofflinemode.setVisibility(View.VISIBLE);

			}
		});
		toggle_zebra.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(PrinterDetails.selectedPrinterPrefrences.getString(ZebraPrinter.SELECTED_PRINTER,"").equals("Zebra")) {
					ext_settings.zebra_settings=!ext_settings.zebra_settings;
					if(ext_settings.zebra_settings) {
						Util.setCustomAlertDialog(ExternalSettingsActivity.this);
						Util.txt_dismiss.setVisibility(View.VISIBLE);
						Util.setCustomDialogImage(R.drawable.alert);
						Util.txt_okey.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								// ticket_dialog.dismiss();
								Util.alert_dialog.dismiss();
								txt_on_off_zebra.setText("Turned ON");
								txt_on_off_zebra.setTextColor(getResources().getColor(R.color.fb_color));
								ext_settings.zebra_settings = true;
								Util.external_setting_pref.edit().putString(sfdcddetails.user_id + checked_in_eventId,
										new Gson().toJson(ext_settings).toString()).commit();
								new GetZebraPrinterConfigTask(ExternalSettingsActivity.this).execute();


							}
						});
						Util.txt_dismiss.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								// ticket_dialog.dismiss();
								toggle_zebra.setChecked(false);
								ext_settings.zebra_settings = false;
								Util.alert_dialog.dismiss();
								txt_on_off_zebra.setText("Turned OFF");
								txt_on_off_zebra.setTextColor(getResources().getColor(R.color.gray_color));

							}
						});
						double length = 0, width = 0;
						if (badge_res.size() > 0) {
							BadgeDataNew badge_data = new Gson().fromJson(badge_res.get(0).badge.Data__c, BadgeDataNew.class);
							width = (double) badge_data.canvasWidth;
							length = (double) badge_data.canvasHeight;
							if(ext_settings.mirror_doubleSide_badge_two_in_one || ext_settings.identical_doubleSide_badge_two_in_one&&ext_settings!=null && ext_settings.zebra_settings){
								length =(double)length*2;
							}
						}
						final int dpi=Integer.valueOf(PrinterDetails.selectedPrinterPrefrences.getString(ZebraPrinter.ZEBRA_RESOLUTION, "").replace("?",""));

						Util.openCustomDialog("Caution : Advanced Setting", "This setting will disable the printer width and length and will be overwritten by selected badge size [ Length:" + String.format("%.2f",length) +"''"+" ("+(int)(length*dpi)+") ,"+" Width:" + String.format("%.2f",width) +"''"+" ("+(int)(width*dpi)+")"+ "]");
						// Util.openCustomDialog("Alert", "You are overridding the Zebra printer Label Length and Width values, Please make sure you are doing this as advised by Admin");
					}else {

						Util.setCustomAlertDialog(ExternalSettingsActivity.this);
						Util.txt_dismiss.setVisibility(View.GONE);
						Util.setCustomDialogImage(R.drawable.alert);
						Util.txt_okey.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								Util.alert_dialog.dismiss();
								toggle_zebra.setChecked(false);
								ext_settings.zebra_settings=false;
								txt_on_off_zebra.setText("Turned OFF");
								txt_on_off_zebra.setTextColor(getResources().getColor(R.color.gray_color));
								Util.external_setting_pref.edit().putString(sfdcddetails.user_id + checked_in_eventId,
										new Gson().toJson(ext_settings).toString()).commit();

							}
						});
						Util.openCustomDialog("Alert", "To reset and use printer default settings, recalibrate the printer and select the printer again in the app.");
					}
				}else{
					toggle_zebra.setChecked(false);
					Toast.makeText(ExternalSettingsActivity.this, "Please connect with Zebra printer for this settings!.", Toast.LENGTH_SHORT)
							.show();}


			}});
		/*toggle_zebra.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				ext_settings.zebra_settings=isChecked;
				Util.external_setting_pref.edit().putString(sfdcddetails.user_id + checked_in_eventId,
						new Gson().toJson(ext_settings).toString()).commit();
				if(isChecked){
					txt_on_off_zebra.setText("Turned ON");
					txt_on_off_zebra.setTextColor(getResources().getColor(R.color.fb_color));
				}else {
					txt_on_off_zebra.setText("Turned OFF");
					txt_on_off_zebra.setTextColor(getResources().getColor(R.color.gray_color));
				}if(isChecked&&PrinterDetails.selectedPrinterPrefrences.getString(ZebraPrinter.SELECTED_PRINTER,"").equals("Zebra")) {
					Util.setCustomAlertDialog(ExternalSettingsActivity.this);
					Util.txt_dismiss.setVisibility(View.VISIBLE);
					Util.setCustomDialogImage(R.drawable.alert);
					Util.txt_okey.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// ticket_dialog.dismiss();
							Util.alert_dialog.dismiss();
							new GetZebraPrinterConfigTask(ExternalSettingsActivity.this).execute();

						}
					});
					Util.txt_dismiss.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// ticket_dialog.dismiss();
							Util.alert_dialog.dismiss();

						}
					});
					Util.openCustomDialog("Alert", "You are overridding the Zebra printer Label Length and Width values, Please make sure you are doing this as advised by Admin");
				}else
					new GetZebraPrinterConfigTask(ExternalSettingsActivity.this).execute();
				}

		});*/
		toggle_validate_badge.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					txt_on_off_validate_badge.setText("Turned ON");
					txt_on_off_validate_badge.setTextColor(getResources().getColor(R.color.fb_color));
				}else {
					txt_on_off_validate_badge.setText("Turned OFF");
					txt_on_off_validate_badge.setTextColor(getResources().getColor(R.color.gray_color));
				}
			}
		});
		toggle_print.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					if (Util.db.getBadgeSelectedElseifDefaultbadge().size()==0) {
						toggle_print.setChecked(false);
						/*AlertDialogCustom custom = new AlertDialogCustom(ExternalSettingsActivity.this);
						custom.setParamenters("Alert", "No Badges Found, Do you want to select a Badge.",
								new Intent(ExternalSettingsActivity.this, BadgeTemplateNewActivity.class), null, 2,
								false);
						custom.show();*/
					} else {
						toggle_print.setChecked(true);
						txt_on_off_print.setText("Turned ON");
						txt_on_off_print.setTextColor(getResources().getColor(R.color.fb_color));
					}

				} else {
					txt_on_off_print.setText("Turned OFF");
					txt_on_off_print.setTextColor(getResources().getColor(R.color.gray_color));
				}

			}
		});

		/*toggle_doubleSideBadge.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					txt_on_off_doubleSideBadge.setText("Turned ON");
					txt_on_off_doubleSideBadge.setTextColor(getResources().getColor(R.color.fb_color));
				} else {
					txt_on_off_doubleSideBadge.setText("Turned OFF");
					txt_on_off_doubleSideBadge.setTextColor(getResources().getColor(R.color.gray_color));
				}
			}
		});*/

		toggle_doubleSideBadge.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					radio_doubleside.setVisibility(View.VISIBLE);
					txt_on_off_doubleSideBadge.setText("Turned ON");
					txt_on_off_doubleSideBadge.setTextColor(getResources().getColor(R.color.fb_color));
					if(!ext_settings.doubleSide_badge) {
						ext_settings.doubleSide_badge = true;
						ext_settings.mirror_doubleSide_badge_two_in_one=true;
						radio_mirror_two_in_one.setChecked(ext_settings.mirror_doubleSide_badge_two_in_one);
						Util.external_setting_pref.edit().putString(sfdcddetails.user_id + checked_in_eventId,
								new Gson().toJson(ext_settings).toString()).commit();
						if (PrinterDetails.selectedPrinterPrefrences.getString(ZebraPrinter.SELECTED_PRINTER, "").equals("Zebra")) {
							//new GetZebraPrinterConfigTask(ExternalSettingsActivity.this).execute();
						}
					}
				} else {
					radio_doubleside.setVisibility(View.GONE);
					txt_on_off_doubleSideBadge.setText("Turned OFF");
					txt_on_off_doubleSideBadge.setTextColor(getResources().getColor(R.color.gray_color));
					if(ext_settings.doubleSide_badge) {
						ext_settings.doubleSide_badge = false;
						ext_settings.identical_doubleSide_badge=false;
						ext_settings.mirror_doubleSide_badge=false;
						ext_settings.mirror_doubleSide_badge_two_in_one=false;
						ext_settings.identical_doubleSide_badge_two_in_one=false;
						Util.external_setting_pref.edit().putString(sfdcddetails.user_id + checked_in_eventId,
								new Gson().toJson(ext_settings).toString()).commit();
						if (PrinterDetails.selectedPrinterPrefrences.getString(ZebraPrinter.SELECTED_PRINTER, "").equals("Zebra")) {
							//new GetZebraPrinterConfigTask(ExternalSettingsActivity.this).execute();
						}
					}
				}
			}
		});
		radio_mirror.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(!ext_settings.mirror_doubleSide_badge&&isChecked){
					setdoublesided(false,true,false,false);
				}
			}
		});


		radio_identical.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(!ext_settings.identical_doubleSide_badge&&isChecked){
					setdoublesided(true,false,false,false);
				}
			}
		});
		radio_identical_two_in_one.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(!ext_settings.identical_doubleSide_badge_two_in_one&&isChecked){
					setdoublesided(false,false,false,true);
				}
			}
		});
		radio_mirror_two_in_one.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(!ext_settings.mirror_doubleSide_badge_two_in_one&&isChecked){
					setdoublesided(false,false,true,false);
				}
			}
		});
		/*if(mSocket!=null){
			toggle_livedata.setChecked(true);
		}else*/
		//ext_settings.allow_livedata=Boolean.valueOf(BaseActivity.checkedin_event_record.Events.Real_Time_Checkin__c);

		if(Boolean.valueOf(BaseActivity.checkedin_event_record.Events.Real_Time_Checkin__c)&&ext_settings.allow_livedata) {
			ext_settings.allow_livedata = true;
			Util.external_setting_pref.edit().putString(sfdcddetails.user_id + checked_in_eventId,
					new Gson().toJson(ext_settings).toString()).commit();
			try {
				mSocket = IO.socket("https://pchat.eventdex.com");
				toggle_livedata.setChecked(true);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}else {
			ext_settings.allow_livedata = false;
			toggle_livedata.setChecked(false);
			Util.external_setting_pref.edit().putString(sfdcddetails.user_id + checked_in_eventId,
					new Gson().toJson(ext_settings).toString()).commit();
		}
		toggle_livedata.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
				if(!Boolean.valueOf(BaseActivity.checkedin_event_record.Events.Real_Time_Checkin__c)&&isChecked) {
					Util.setCustomAlertDialog(ExternalSettingsActivity.this);
					Util.txt_dismiss.setVisibility(View.GONE);
					Util.setCustomDialogImage(R.drawable.alert);
					Util.txt_okey.setText("OK");
					Util.txt_okey.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// ticket_dialog.dismiss();
							Util.alert_dialog.dismiss();
						}
					});

					Util.openCustomDialog("Alert", "Live Updates are off at Event level. Please contact Event Organizer!");
					toggle_livedata.setChecked(false);
				}else {
					dissconnectIOSocket();
				}
				//Util.db.insertandupdaterealtime(String.valueOf(isChecked),BaseActivity.checkedin_event_record.Events.Id);
			}
		});

		toggle_apppermission.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					if(AppUtils.isAllPermissionGranted(ExternalSettingsActivity.this)){
						/*Toast.makeText(ExternalSettingsActivity.this, "All Permissions are Allowed!.", Toast.LENGTH_SHORT)
								.show();*/
					}else {
						//txt_on_off_permission.setTextColor(getResources().getColor(R.color.orange_bg));
						//AppUtils.givepermission(ExternalSettingsActivity.this);
						Uri packageURI = Uri.parse("package:" + getPackageName());
						Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", packageURI);
						startActivity(intent);

					}
				}else if(!isChecked&&AppUtils.isAllPermissionGranted(ExternalSettingsActivity.this)) {
					toggle_apppermission.setChecked(true);
				}else {
					toggle_apppermission.setChecked(false);
				}
			}
		});
		toggle_promocode.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					txt_on_off_promocode.setText("Turned ON");
					txt_on_off_promocode.setTextColor(getResources().getColor(R.color.fb_color));
				} else {
					txt_on_off_promocode.setText("Turned OFF");
					txt_on_off_promocode.setTextColor(getResources().getColor(R.color.gray_color));
				}
			}
		});
		toggle_barcode.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					txt_on_off_barcode.setText("Turned ON");
					txt_on_off_barcode.setTextColor(getResources().getColor(R.color.fb_color));
				} else {
					txt_on_off_barcode.setText("Turned OFF");
					txt_on_off_barcode.setTextColor(getResources().getColor(R.color.gray_color));
				}
			}
		});

		toggle_checkin.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					txt_on_off_checkin.setText("Turned ON");
					txt_on_off_checkin.setTextColor(getResources().getColor(R.color.fb_color));
				} else {
					txt_on_off_checkin.setText("Turned OFF");
					txt_on_off_checkin.setTextColor(getResources().getColor(R.color.gray_color));
				}
			}
		});

		if(ext_settings.checkin_checkout) {
			Util.checkin_only_pref.edit().putBoolean(sfdcddetails.user_id+checked_in_eventId, false).commit();
			radio_checkin_out_btn.setChecked(true);
		}
		else {
			Util.checkin_only_pref.edit().putBoolean(sfdcddetails.user_id+checked_in_eventId, true).commit();
			radio_checkin_btn.setChecked(true);
		}
		/*if(Util.checkin_only_pref.getBoolean(sfdcddetails.user_id+checked_in_eventId, false)) {
			radio_checkin_btn.setChecked(Util.checkin_only_pref.getBoolean(sfdcddetails.user_id + checked_in_eventId, false));
		}else {
			radio_checkin_out_btn.setChecked(!Util.checkin_only_pref.getBoolean(sfdcddetails.user_id + checked_in_eventId, false));
		}*/
		radio_checkin_btn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				Util.checkin_only_pref.edit().putBoolean(sfdcddetails.user_id+checked_in_eventId, isChecked).commit();
			}
		});
		radio_checkin_out_btn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				Util.checkin_only_pref.edit().putBoolean(sfdcddetails.user_id+checked_in_eventId, !isChecked).commit();
			}
		});
		radio_checkin_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				/*ext_settings.checkin_checkout=false;
				Util.external_setting_pref.edit().putString(sfdcddetails.user_id + checked_in_eventId,
						new Gson().toJson(ext_settings).toString()).commit();
				Util.checkin_only_pref.edit().putBoolean(sfdcddetails.user_id+checked_in_eventId, true).commit();*/
				Util.scanmode_checkin_pref.edit().putString(sfdcddetails.user_id+checked_in_eventId, "Checkin").commit();
				AppUtils.displayLog("----------checkin button click---------",Util.scanmode_checkin_pref.getString(sfdcddetails.user_id+checked_in_eventId,"")+" and "+String.valueOf(Util.checkin_only_pref.getBoolean(sfdcddetails.user_id+checked_in_eventId, false)));

			}
		});
		radio_checkin_out_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				/*ext_settings.checkin_checkout=true;
				Util.external_setting_pref.edit().putString(sfdcddetails.user_id + checked_in_eventId,
						new Gson().toJson(ext_settings).toString()).commit();
				Util.checkin_only_pref.edit().putBoolean(sfdcddetails.user_id+checked_in_eventId, false).commit();*/
				Util.scanmode_checkin_pref.edit().putString(sfdcddetails.user_id+checked_in_eventId, "Checkinout").commit();
				AppUtils.displayLog("----------checkout button click---------",Util.scanmode_checkin_pref.getString(sfdcddetails.user_id+checked_in_eventId,"")+" and "+String.valueOf(Util.checkin_only_pref.getBoolean(sfdcddetails.user_id+checked_in_eventId, false)));

			}
		});
		layout_printer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*if (Util.selectedPrinterPref.getString("printer", "").isEmpty()) {
					setDialog();
					search_thread = new SearchThread();
					search_thread.start();
				} else {
					PrinterDisconnectAlert();
				}*/
				startActivity(new Intent(ExternalSettingsActivity.this,PrintersListActivity.class));
			}
		});

		layout_scanner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setSoftScannerAPI();
				openSoftScannerDialog(ExternalSettingsActivity.this, _bluetoothAdapter);
			}
		});
		// ScannerSettingsApplication.getInstance().increaseViewCount();
		btn_scanner_setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setSoftScannerAPI();
				openScannerSettingsClearAlert();
			}
		});
		panel.setOnExpandListener(new ExpandablePanel.OnExpandListener() {

			@Override
			public void onExpand(View handle, View content) {
				// TODO Auto-generated method stub
				txt_moreSetting.setCompoundDrawablesWithIntrinsicBounds(0, // left
						0, // top
						R.drawable.minus_green, // right
						0);

			}

			@Override
			public void onCollapse(View handle, View content) {
				// TODO Auto-generated method stub
				txt_moreSetting.setCompoundDrawablesWithIntrinsicBounds(0, // left
						0, // top
						R.drawable.plus_green, // right
						0);

			}
		});
		reset_panel.isEnabled();
		reset_panel.setOnExpandListener(new OnExpandListener() {
			@Override
			public void onExpand(View handle, View content) {
				// TODO Auto-generated method stub
				txt_reset_settings.setCompoundDrawablesWithIntrinsicBounds(0, // left
						0, // top
						R.drawable.minus_green, // right
						0);
			}

			@Override
			public void onCollapse(View handle, View content) {
				// TODO Auto-generated method stub
				txt_reset_settings.setCompoundDrawablesWithIntrinsicBounds(0, // left
						0, // top
						R.drawable.plus_green, // right
						0);
			}
		});
		checkin_out_panel.setOnExpandListener(new OnExpandListener() {

			@Override
			public void onExpand(View handle, View content) {
				// TODO Auto-generated method stub
				txt_checkout_settings.setCompoundDrawablesWithIntrinsicBounds(0, // left
						0, // top
						R.drawable.minus_green, // right
						0);
			}

			@Override
			public void onCollapse(View handle, View content) {
				// TODO Auto-generated method stub
				txt_checkout_settings.setCompoundDrawablesWithIntrinsicBounds(0, // left
						0, // top
						R.drawable.plus_green, // right
						0);
			}
		});

		img_export.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				exportCSV(fileToExport);
			}
		});
		info_zebra.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Util.setCustomAlertDialog(ExternalSettingsActivity.this);
				Util.openCustomDialog("Note: Zebra printer setup:", "1. Calibrate the printer with the new badge paper.     \n" +
						"2. Select the printer in the ScanAttendee app to read the new badge values from the connected printer.\n" +
						"3. Please repeat above two steps, when ever badge paper is changed.");
				Util.txt_okey.setText("OK");
				Util.txt_dismiss.setVisibility(View.GONE);
				Util.txt_okey.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Util.alert_dialog.dismiss();
					}
				});
			}
		});
		layout_reset_settings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!isOnline()) {
					startErrorAnimation(getResources().getString(R.string.network_error), txt_error_msg);
				} else {
					showNoAttendeesAlert();
				}

			}
		});

		layout_refreshcheckins.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				request_type = Util.CHEKINS_REFRESH;
				doRequest();
			}
		});
		layout_export.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				exportCSV(fileToExport);
			}
		});
		layout_download.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!isOnline()) {
					startErrorAnimation(getResources().getString(R.string.network_error), txt_error_msg);
				}  else if (Util.db.totalOrderCountwithoutCancelled(checked_in_eventId) < Util.dashboardHandler.totalOrders) {
					progress_loader.setVisibility(View.VISIBLE);
					layout_download.setEnabled(false);
					img_download.setVisibility(View.GONE);
					progress_download_data.setVisibility(View.VISIBLE);
					startService("");
				} else if (Util.db.totalOrderCountwithoutCancelled(checked_in_eventId) > Util.dashboardHandler.totalOrders) {

				}else {
					/*AlertDialogCustom dialog = new AlertDialogCustom(ExternalSettingsActivity.this);
					dialog.setParamenters("Alert", "All attendees are downloaded.", null, null, 1, false);
					dialog.show();*/

					Util.setCustomAlertDialog(ExternalSettingsActivity.this);
					Util.txt_dismiss.setVisibility(View.VISIBLE);
					Util.setCustomDialogImage(R.drawable.alert);
					Util.txt_okey.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// ticket_dialog.dismiss();
							Util.alert_dialog.dismiss();
							progress_loader.setVisibility(View.VISIBLE);
							layout_download.setEnabled(false);
							img_download.setVisibility(View.GONE);
							progress_download_data.setVisibility(View.VISIBLE);
							startService(DownloadService.reload);

						}
					});
					Util.txt_dismiss.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// ticket_dialog.dismiss();
							Util.alert_dialog.dismiss();

						}
					});
					Util.openCustomDialog("Alert", "All attendees are downloaded. Do you want to refresh all attendees?");
				}

			}
		});



		layout_import.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(Util.db.getGroupCount(checked_in_eventId) == 0){
					showScannedTicketsAlert("Please Buy at least one scanattendee ticket to scan session.",false);
				}else if(Util.db.getSwitchedOnScanItem(checked_in_eventId).isEmpty()){
					showScannedTicketsAlert("Please TurnON at least one session for scanning.",true);
				}else if(Util.isMyServiceRunning(DownloadService.class, ExternalSettingsActivity.this)){
					showServiceRunningAlert(checkedin_event_record.Events.Name);
				}else if(AppUtils.isStoragePermissionGranted(ExternalSettingsActivity.this)){
					Intent intent = new Intent(ExternalSettingsActivity.this, FilePicker.class);
					intent.putExtra(FilePicker.EXTRA_SHOW_HIDDEN_FILES, true);
					ArrayList<String> file_formats = new ArrayList<>();
					file_formats.add(".csv");
					intent.putStringArrayListExtra(FilePicker.EXTRA_ACCEPTED_FILE_EXTENSIONS, file_formats);
					startActivityForResult(intent, 2017);
				}else{
					AppUtils.giveStoragermission(ExternalSettingsActivity.this);
				}
			}
		});
		frame_checkin_out.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				frame_checkin_out.performClick();
			}
		}, 500);

		ticketdefaultsetting.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				ticketdefaultsetting.performClick();
			}
		}, 500);

	}
	public void setdoublesided(Boolean identical_doubleSide_badge,Boolean mirror_doubleSide_badge,Boolean mirror_doubleSide_badge_two_in_one,Boolean identical_doubleSide_badge_two_in_one) {
		try {
			ext_settings.identical_doubleSide_badge = identical_doubleSide_badge;
			ext_settings.mirror_doubleSide_badge = mirror_doubleSide_badge;
			ext_settings.mirror_doubleSide_badge_two_in_one = mirror_doubleSide_badge_two_in_one;
			ext_settings.identical_doubleSide_badge_two_in_one = identical_doubleSide_badge_two_in_one;
			Util.external_setting_pref.edit().putString(sfdcddetails.user_id + checked_in_eventId,
					new Gson().toJson(ext_settings).toString()).commit();
			if (PrinterDetails.selectedPrinterPrefrences.getString(ZebraPrinter.SELECTED_PRINTER, "").equals("Zebra")) {
				//new GetZebraPrinterConfigTask(ExternalSettingsActivity.this).execute();
			}
		}catch (Exception e){
			e.printStackTrace();
		}



	}
	private void openScannerSettingsClearAlert() {

		Util.setCustomAlertDialog(ExternalSettingsActivity.this);
		Util.setCustomDialogImage(R.drawable.alert);
		Util.txt_okey.setText("CLEAR");
		Util.txt_dismiss.setVisibility(View.VISIBLE);
		Util.txt_dismiss.setText("CANCEL");
		// Util.alert_dialog.setCancelable(false);
		String msg = "Do you want to clear all socket scanners bluetooth settings?";

		Util.txt_okey.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Util.alert_dialog.dismiss();
				unPairSocketDevices();
			}
		});
		Util.txt_dismiss.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Util.alert_dialog.dismiss();

			}
		});
		Util.openCustomDialog("Alert", msg);
	}

	private void PrinterDisconnectAlert() {
		Util.setCustomAlertDialog(ExternalSettingsActivity.this);
		Util.openCustomDialog("Alert", "Do you want to disconnect the printer?");
		Util.txt_okey.setText("DISCONNECT");
		Util.txt_dismiss.setVisibility(View.VISIBLE);
		Util.txt_dismiss.setText("CANCEL");
		Util.txt_okey.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				PrinterDetails.selectedPrinterPrefrences.edit().clear().commit();
				sharedPreferences.edit().clear().commit();
				Util.alert_dialog.dismiss();
				setPrinterStatus();
			}
		});
		Util.txt_dismiss.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Util.alert_dialog.dismiss();
			}
		});

	}

	private void setUI() {

		toggle_checkin.setChecked(ext_settings.quick_checkin);
		toggle_barcode.setChecked(ext_settings.custom_barcode);
		toggle_promocode.setChecked(ext_settings.allow_promocode);

		if(AppUtils.isAllPermissionGranted(ExternalSettingsActivity.this)){
			toggle_apppermission.setChecked(true);
		}else {
			txt_on_off_permission.setTextColor(getResources().getColor(R.color.orange_bg));
			toggle_apppermission.setChecked(false);
		}
		toggle_print.setChecked(ext_settings.quick_print);
		toggle_doubleSideBadge.setChecked(ext_settings.doubleSide_badge);
		toggle_online_mode.setChecked(ext_settings.online_mode);
		toggle_zebra.setChecked(ext_settings.zebra_settings);
		toggle_validate_badge.setChecked(ext_settings.isValidateBadge);
		toggle_groupcheckin.setChecked(ext_settings.group_checkin);
		right_pixelvalue.setText(ext_settings.Android_Badge_Pixel);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(ExternalSettingsActivity.this, R.layout.lighttext_dropdown,
				getResources().getStringArray(R.array.pixels_array));

		/*right_pixelvalue.setAdapter(adapter);
		int i =0;
		for(String key : getResources().getStringArray(R.array.pixels_array)){
			if(NullChecker(ext_settings.Android_Badge_Pixel).equalsIgnoreCase(key)){
				right_pixelvalue.setSelection(i,false);
				break;
			}
			i++;
		}*/
		//txt_pixelvalue.setText("Default Pixel value is 96");
		if (toggle_checkin.isChecked()) {
			txt_on_off_checkin.setText("Turned ON");
			txt_on_off_checkin.setTextColor(getResources().getColor(R.color.fb_color));
		}
		if (toggle_barcode.isChecked()) {
			txt_on_off_barcode.setText("Turned ON");
			txt_on_off_barcode.setTextColor(getResources().getColor(R.color.fb_color));
		}
		if (toggle_promocode.isChecked()) {
			txt_on_off_promocode.setText("Turned ON");
			txt_on_off_promocode.setTextColor(getResources().getColor(R.color.fb_color));
		}

		if (toggle_print.isChecked()) {
			txt_on_off_print.setText("Turned ON");
			txt_on_off_print.setTextColor(getResources().getColor(R.color.fb_color));
		}
		if (toggle_doubleSideBadge.isChecked()) {
			txt_on_off_doubleSideBadge.setText("Turned ON");
			txt_on_off_doubleSideBadge.setTextColor(getResources().getColor(R.color.fb_color));
			if(ext_settings.mirror_doubleSide_badge){
				radio_mirror.setChecked(ext_settings.mirror_doubleSide_badge);
			}else if(ext_settings.mirror_doubleSide_badge_two_in_one){
				radio_mirror_two_in_one.setChecked(ext_settings.mirror_doubleSide_badge_two_in_one);
			}else if(ext_settings.identical_doubleSide_badge_two_in_one){
				radio_identical_two_in_one.setChecked(ext_settings.identical_doubleSide_badge_two_in_one);
			}else {
				radio_identical.setChecked(ext_settings.identical_doubleSide_badge);
			}
		}

		if (toggle_validate_badge.isChecked()) {
			txt_on_off_validate_badge.setText("Turned ON");
			txt_on_off_validate_badge.setTextColor(getResources().getColor(R.color.fb_color));
		}

		if (_bluetoothIsOn) {
			btn_scanner_setting.setVisibility(View.VISIBLE);
		}
		if((PrinterDetails.selectedPrinterPrefrences.getString(ZebraPrinter.SELECTED_PRINTER, "").equalsIgnoreCase("Zebra"))){
			info_zebra.setVisibility(View.VISIBLE);
			frame_zebrasettings.setVisibility(View.VISIBLE);
		}else {
			info_zebra.setVisibility(View.GONE);
			frame_zebrasettings.setVisibility(View.GONE);
		}
		if(!(PrinterDetails.selectedPrinterPrefrences.getString(ZebraPrinter.SELECTED_PRINTER, "").equalsIgnoreCase("Brother"))&&
				!(PrinterDetails.selectedPrinterPrefrences.getString(ZebraPrinter.SELECTED_PRINTER, "").equalsIgnoreCase("Zebra")))
		{
			//if (PrinterDetails.selectedPrinterPrefrences.getString("printer", "").isEmpty()) {
			img_printer.setImageResource(R.drawable.red_circle_1);
			txt_on_off_printer.setText("Not Connected");
			txt_on_off_printer.setTextColor(getResources().getColor(R.color.gray_color));
			txt_printer_name.setVisibility(View.GONE);
		}
		//else if(!PrinterDetails.selectedPrinterPrefrences.getString("printer", "").isEmpty()&&"( "+PrinterDetails.selectedPrinterPrefrences.getString("address", "")+" )".equalsIgnoreCase(Util.getWifiIpAdress(ExternalSettingsActivity.this))){
		//else if(!PrinterDetails.selectedPrinterPrefrences.getString("printer", "").isEmpty()) {
		else if (PrinterDetails.selectedPrinterPrefrences.getString(ZebraPrinter.SELECTED_PRINTER, "").equalsIgnoreCase("Brother")) {
			connectSavedPrinter();
			//new SearchPrinterStatusThread().run();
			/*if(!(PrinterDetails.selectedPrinterPrefrences.getBoolean("isConnected",false))) {
				txt_on_off_printer.setText("Waiting for ");
				txt_on_off_printer.setTextColor(getResources().getColor(R.color.fb_color));
				img_printer.setImageResource(R.drawable.orange_button_bg);
			}else if((PrinterDetails.selectedPrinterPrefrences.getBoolean("isConnected",true))){
				txt_on_off_printer.setText("Connected");
				img_printer.setImageResource(R.drawable.green_circle_1);
				txt_on_off_printer.setTextColor(getResources().getColor(R.color.green_connected));
			}
			txt_printer_name.setVisibility(View.VISIBLE);
			txt_printer_name.setTextColor(getResources().getColor(R.color.fb_color));
			txt_printer_name.setText(PrinterDetails.selectedPrinterPrefrences.getString("printer", ""));*/
		} else if(PrinterDetails.selectedPrinterPrefrences.getString(ZebraPrinter.SELECTED_PRINTER, "").equalsIgnoreCase("Zebra")) {
			if((PrinterDetails.selectedPrinterPrefrences.getBoolean("isConnected",true))){
				txt_on_off_printer.setText("Connected");
				img_printer.setImageResource(R.drawable.green_circle_1);
				txt_on_off_printer.setTextColor(getResources().getColor(R.color.green_connected));
			}
			txt_printer_name.setVisibility(View.VISIBLE);
			txt_printer_name.setTextColor(getResources().getColor(R.color.fb_color));
			if(!PrinterDetails.selectedPrinterPrefrences.getString(ZebraPrinter.ZEBRA_WIFI_IP, "").isEmpty())
				txt_printer_name.setText("Zebra Printer ("+PrinterDetails.selectedPrinterPrefrences.getString(ZebraPrinter.ZEBRA_WIFI_IP, "")+")");
			else{txt_printer_name.setText(PrinterDetails.selectedPrinterPrefrences.getString("printer", "")
					+"( "+PrinterDetails.selectedPrinterPrefrences.getString("macAddress", "")+" )");}
			connectSavedPrinter();
		}

		/*if (PrinterDetails.selectedPrinterPrefrences.getString("printer", "").isEmpty()) {
			img_printer.setImageResource(R.drawable.red_circle_1);
			txt_on_off_printer.setText("Not Connected");
			txt_on_off_printer.setTextColor(getResources().getColor(R.color.gray_color));
			txt_printer_name.setVisibility(View.GONE);
		} else {
			img_printer.setImageResource(R.drawable.orange_button_bg);
			if (!PrinterDetails.selectedPrinterPrefrences.getString("printer", "").isEmpty()) {
				txt_on_off_printer.setText("Waiting for...");
				txt_on_off_printer.setTextColor(getResources().getColor(R.color.fb_color));
			}
			if (!(PrinterDetails.selectedPrinterPrefrences.getString("printer", "").isEmpty())&&(PrinterDetails.selectedPrinterPrefrences.getBoolean("isConnected",true))){
				txt_on_off_printer.setText("Connected");
				img_printer.setImageResource(R.drawable.green_circle_1);
				txt_on_off_printer.setTextColor(getResources().getColor(R.color.green_connected));
			}1
			txt_printer_name.setVisibility(View.VISIBLE);
			txt_printer_name.setTextColor(getResources().getColor(R.color.fb_color));
			txt_printer_name.setText(PrinterDetails.selectedPrinterPrefrences.getString("printer", ""));
		}*/



		/*
		 * if(ScannerSettingsApplication.getInstance().getCurrentDevice() !=
		 * null){ Log.i("-----------------Device Connected------------"
		 * ,":"+ScannerSettingsApplication.getInstance().getCurrentDevice().
		 * getBTAddress()+ScannerSettingsApplication.getInstance().
		 * getCurrentDevice().getName()); }
		 */
		/*
		 * if (Util.socket_device_pref.getBoolean(Util.SOCKET_DEVICE_CONNECTED,
		 * false) && _bluetoothIsOn) {
		 * img_scanner.setImageResource(R.drawable.green_circle_1);
		 * txt_on_off_scanner.setText("Connected");
		 * txt_on_off_scanner.setTextColor(getResources().getColor(R.color.
		 * fb_color)); txt_battery_level.setVisibility(View.VISIBLE); }
		 * else { img_scanner.setImageResource(R.drawable.red_circle_1);
		 * txt_on_off_scanner.setText("Not Connected");
		 * txt_on_off_scanner.setTextColor(getResources().getColor(R.color.
		 * gray_color)); txt_battery_level.setVisibility(View.GONE); }
		 */

		if (!Util.socket_device_pref.getString(Util.SOCKET_DEVICE_NAME, "").trim().isEmpty()) {
			txt_scanner_name.setText("Connected to " + Util.socket_device_pref.getString(Util.SOCKET_DEVICE_NAME, ""));
		}
		String text = "<font color=#8D8D8D>Battery Level:</font> <font color=#3B5998>"
				+ Util.socket_device_pref.getString(Util.SOCKET_DEVICE_BATTERY_LEVEL, "") + "</font>";
		txt_battery_level.setText(Html.fromHtml(text));
		if (ScannerSettingsApplication.getInstance().getCurrentDevice() != null && _bluetoothIsOn) {
			img_scanner.setImageResource(R.drawable.green_circle_1);
			txt_on_off_scanner.setText("Connected");
			txt_on_off_scanner.setTextColor(getResources().getColor(R.color.fb_color));
			txt_battery_level.setVisibility(View.VISIBLE);
			// BaseActivity.img_scanner_base.setImageResource(R.drawable.green_circle_1);
		} else {
			img_scanner.setImageResource(R.drawable.red_circle_1);
			txt_on_off_scanner.setText("Not Connected");
			txt_on_off_scanner.setTextColor(getResources().getColor(R.color.gray_color));
			txt_battery_level.setVisibility(View.GONE);
			// BaseActivity.img_scanner_base.setImageResource(R.drawable.red_circle_1);
		}

		if (Util.isMyServiceRunning(DownloadService.class, ExternalSettingsActivity.this)) {
			img_download.setVisibility(View.GONE);
			progress_download_data.setVisibility(View.VISIBLE);
			if (progress_download_data != null) {
				if (Util.db.totalOrderCountwithoutCancelled(checked_in_eventId) == Util.dashboardHandler.totalOrders) {
					progress_download_data.setProgress(0);
				} else {
					progress_download_data.setProgress(Util.db.totalOrderCountwithoutCancelled(checked_in_eventId));
				}
			} else {
				img_download.setVisibility(View.VISIBLE);
				progress_download_data.setVisibility(View.GONE);
			}
			// int total_attendees = Util.dashboardHandler.totalOrders -
			if (Util.db.totalOrderCountwithoutCancelled(checked_in_eventId) == 0) {
				txt_download_attendees_count.setText("Total Orders :" + Util.dashboardHandler.totalOrders);
			} else {
				txt_download_attendees_count.setText("You have downloaded " + Util.db.totalOrderCountwithoutCancelled(checked_in_eventId)
						+ " Orders out of " + Util.dashboardHandler.totalOrders);
			}
		}else txt_download_attendees_count.setText("You have downloaded " + Util.db.totalOrderCountwithoutCancelled(checked_in_eventId)
				+ " Orders out of " + Util.dashboardHandler.totalOrders);
		/*
		 * runOnUiThread(new Runnable() {
		 *
		 * @Override public void run() { // TODO Auto-generated method stub
		 * ticketdefaultsetting.performClick(); } });
		 */

	}

	@Override
	public void setCustomContentView(int layout) {
		// TODO Auto-generated method stub
		activity = this;
		View v = inflater.inflate(layout, null);
		linearview.addView(v);
		txt_title.setText("External Settings");
		img_menu.setImageResource(R.drawable.back_button);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		img_socket_scanner.setEnabled(false);
		layout_printer = (LinearLayout) linearview.findViewById(R.id.layout_printer);
		layout_scanner = (FrameLayout) linearview.findViewById(R.id.layout_scanners);
		layout_bluetooth = (LinearLayout) linearview.findViewById(R.id.layout_bluetooth);
		layout_reset_settings = (LinearLayout) linearview.findViewById(R.id.layout_reset_settings);
		layout_refreshcheckins  = (LinearLayout) linearview.findViewById(R.id.layout_refreshcheckins);
		layout_download = (FrameLayout) linearview.findViewById(R.id.layout_download);
		layout_export = (FrameLayout) linearview.findViewById(R.id.layout_export);
		layout_import = (FrameLayout) linearview.findViewById(R.id.layout_import);
		frame_zebrasettings = (FrameLayout) linearview.findViewById(R.id.frame_zebrasettings);
		toggle_print = (ToggleButton) linearview.findViewById(R.id.toggle_print);
		toggle_checkin = (ToggleButton) linearview.findViewById(R.id.toggle_checkin);
		toggle_barcode = (ToggleButton) linearview.findViewById(R.id.toggle_custmbarcode);
		toggle_promocode = (ToggleButton) linearview.findViewById(R.id.toggle_promocode);
		toggle_apppermission = (ToggleButton) linearview.findViewById(R.id.toggle_apppermission);
		toggle_livedata = (ToggleButton) linearview.findViewById(R.id.toggle_livedata);
		toggle_groupcheckin = (ToggleButton) linearview.findViewById(R.id.toggle_groupcheckin);
		toggle_idonthaveemail = (ToggleButton) linearview.findViewById(R.id.toggle_idonthaveemail);
		toggle_doubleSideBadge = (ToggleButton) linearview.findViewById(R.id.toggle_doubleSideBadge);
		radio_doubleside =(RadioGroup) linearview.findViewById(R.id.radio_group);
		right_pixelvalue =(TextView) linearview.findViewById(R.id.right_pixelvalue);
		radio_identical =(RadioButton) linearview.findViewById(R.id.radio_identical);
		radio_mirror =(RadioButton) linearview.findViewById(R.id.radio_mirror);
		toggle_validate_badge = (ToggleButton) linearview.findViewById(R.id.toggle_validate_badge);
		toggle_online_mode = (ToggleButton) linearview.findViewById(R.id.toggle_onlinemode);
		toggle_zebra = (ToggleButton) linearview.findViewById(R.id.toggle_zebra);
		radio_identical_two_in_one =(RadioButton) linearview.findViewById(R.id.radio_identical_two_in_one);
		radio_mirror_two_in_one =(RadioButton) linearview.findViewById(R.id.radio_mirror_two_in_one);

		/*toggle_validate_badge.setEnabled(false);
		toggle_validate_badge.setClickable(false);
		toggle_validate_badge.setFocusable(false);*/
		img_printer = (ImageView) linearview.findViewById(R.id.img_printer);
		img_scanner = (ImageView) linearview.findViewById(R.id.img_scanner);
		txt_on_off_printer = (TextView) linearview.findViewById(R.id.txt_on_off_printer);
		txt_on_off_scanner = (TextView) linearview.findViewById(R.id.txt_on_off_scanner);
		txt_on_off_barcode = (TextView) linearview.findViewById(R.id.txt_on_off_barcode);
		txt_on_off_checkin = (TextView) linearview.findViewById(R.id.txt_on_off_checkin);
		txt_on_off_print = (TextView) linearview.findViewById(R.id.txt_on_off_print);
		txt_on_off_offlinemode  =(TextView) linearview.findViewById(R.id.txt_offlinemode);
		txt_on_off_zebra  =(TextView) linearview.findViewById(R.id.txt_on_off_zebra);
		txt_on_off_promocode = (TextView) linearview.findViewById(R.id.txt_on_off_promocode);
		txt_on_off_permission  = (TextView) linearview.findViewById(R.id.txt_on_off_permission);
		txt_on_off_doubleSideBadge = (TextView) linearview.findViewById(R.id.txt_on_off_doubleSideBadge);
		txt_on_off_validate_badge = (TextView) linearview.findViewById(R.id.txt_validate_badge);
		txt_moreSetting = (TextView) linearview.findViewById(R.id.txt_moreSetting);
		txt_checkout_settings = (TextView) linearview.findViewById(R.id.txt_checkout_settings);
		txt_reset_settings = (TextView) linearview.findViewById(R.id.txt_reset_settings);
		txt_pixelvalue = (TextView) linearview.findViewById(R.id.txt_pixelvalue);
		txt_download_attendees_count = (TextView) linearview.findViewById(R.id.txt_download_attendees_count);
		txt_export = (TextView) linearview.findViewById(R.id.txt_export);
		txt_printer_name = (TextView) linearview.findViewById(R.id.txt_printer_name);
		//txt_offline_print_cout=(TextView) linearview.findViewById(R.id.txt_offline_print_cout);
		img_export = (ImageView) linearview.findViewById(R.id.img_export);
		info_zebra = (LinearLayout) linearview.findViewById(R.id.info_zebra);
		panel = (ExpandablePanel) linearview.findViewById(R.id.ticketsettingpanel);
		checkin_out_panel = (ExpandablePanel) linearview.findViewById(R.id.checkin_out_panel);
		reset_panel = (ExpandablePanel) linearview.findViewById(R.id.reset_panel);
		ticketdefaultsetting = (FrameLayout) linearview.findViewById(R.id.ticketdefaultsetting);
		frame_checkin_out = (FrameLayout) linearview.findViewById(R.id.frame_checkin_out);
		txt_scanner_name = (TextView) linearview.findViewById(R.id.txt_scanner_name);
		radio_checkin_btn = (RadioButton) linearview.findViewById(R.id.radio_checkin_only);
		radio_checkin_out_btn = (RadioButton) linearview.findViewById(R.id.radio_checkin_out);


		btn_scanner_setting = (Button) linearview.findViewById(R.id.btn_scanner_settings);
		btn_scanner_setting.setVisibility(View.GONE);
		txt_battery_level = (TextView) linearview.findViewById(R.id.txt_battery_level);
		txt_battery_level.setVisibility(View.GONE);
		progress_download_data = (RateTextCircularProgressBar) linearview.findViewById(R.id.progress_download_data);
		progress_download_data.setVisibility(View.GONE);
		progress_download_data.setMax(Util.dashboardHandler.totalOrders);
		progress_download_data.clearAnimation();
		progress_download_data.getCircularProgressBar().setCircleWidth(10);
		img_download = (ImageView) linearview.findViewById(R.id.img_download);
		progress_loader = (ProgressBar) linearview.findViewById(R.id.progress_loader);
		progress_loader.setVisibility(View.GONE);
		fileToExport = getCSVTOExport();
		txt_export.setText("You have " + getCSVCount(fileToExport) + " attendees to export");
		//txt_offline_print_cout.setText(String.valueOf(Util.db.getAttendeeForOfflinePrint().size()));
	}

	protected void onResume() {
		super.onResume();
		setUI();

		if (txt_export != null) {
			fileToExport = getCSVTOExport();
			txt_export.setText("You have " + getCSVCount(fileToExport) + " attendees to export");
		}


		// registerSocketScannerBroadcast();
	}

	public void connectBrotherPrinter() {
		if(!PrinterDetails.selectedPrinterPrefrences.getString(ZebraPrinter.ZEBRA_WIFI_IP, "").isEmpty())
			new BrotherPrinter().getPrinter(PrinterDetails.selectedPrinterPrefrences.getString(ZebraPrinter.PRINTERMODEL, ""),this);
		else
			setPrinterStatus();
	}

	public void connectZebraPrinter() {

		if(sharedPreferences!=null){
			if(sharedPreferences.getString(ZebraPrinter.SELECTED_PRINTER,"").equals("Zebra")){
				if(zebraPrinter.getTCPConnection()==null || zebraPrinter==null){
					try {
						zebraPrinter=new ZebraPrinter();
						zebraPrinter.createTCPConnection();
					} catch (ConnectionException e) {
						//AppUtils.showError(this,ZebraPrinter.getErrorFromException(e));
					}
				}
			}
		}
	}

	public void connectSavedPrinter() {
		if (PrinterDetails.selectedPrinterPrefrences!= null) {
			if (PrinterDetails.selectedPrinterPrefrences.getString(ZebraPrinter.SELECTED_PRINTER, "").equalsIgnoreCase("Zebra")) {
				connectZebraPrinter();
			} else if (PrinterDetails.selectedPrinterPrefrences.getString(ZebraPrinter.SELECTED_PRINTER, "").equalsIgnoreCase("Brother")) {
				setPrinterStatus();
			}
		}
	}

	@Override
	public void doRequest() {
		String access_token = sfdcddetails.token_type + " " + sfdcddetails.access_token;
		if (!isOnline()) {
			startErrorAnimation(getResources().getString(R.string.network_error), txt_error_msg);
		} else if (request_type.equals(Util.EVENT_REFRESH)) {
			String url = sfdcddetails.instance_url + WebServiceUrls.SA_REFRESH_URL + getValues(request_type);
			postMethod = new HttpPostData("Refreshing Event...", url, null, access_token, ExternalSettingsActivity.this);
			postMethod.execute();
		}else if(request_type.equals(Util.CHEKINS_REFRESH)){
			String url = sfdcddetails.instance_url + WebServiceUrls.SA_BLN_ASC_CHECKINREFRESH +  "Event_id=" +checked_in_eventId+"&User_id=" + sfdcddetails.user_id+"&LastModifiedDate="+Util.NullChecker(checkedin_event_record.lastRefreshDate).replace(" ", "%20");
			/*postMethod = new HttpPostData("Refreshing Checkins...", url, null, access_token, ExternalSettingsActivity.this);
			postMethod.execute();*/
			getMehod = new HttpGetMethod(url, sfdcddetails.token_type, sfdcddetails.access_token, ExternalSettingsActivity.this);
			getMehod.execute();
		}
	}

	private String getValues(String request_type) {
		List<NameValuePair> values = new ArrayList<NameValuePair>();
		values.add(new BasicNameValuePair("Event_id", checked_in_eventId));
		values.add(new BasicNameValuePair("User_id", sfdcddetails.user_id));
		values.add(new BasicNameValuePair("appname", ""));
		values.add(new BasicNameValuePair("ResetSett", "true"));
		if (request_type.equalsIgnoreCase(Util.ITEMS_ORDERS_REFRESH)) {
			// values.add(new BasicNameValuePair("LastModifiedDate",
			// checkedin_event_record.lastRefreshDate));
			values.add(new BasicNameValuePair("LastModifiedDate", Util.lastModifideDate.getString(Util.ITEMSANDATTENDEESLASTMODITIFEDATE,"")));
			values.add(new BasicNameValuePair("Request_Flag", "Itemandattendees"));
		} else {
			values.add(new BasicNameValuePair("LastModifiedDate", checkedin_event_record.lastRefreshDate));
			values.add(new BasicNameValuePair("Request_Flag", "Event"));
		}

		return AppUtils.getQuery(values);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.globalnest.network.IPostResponse#parseJsonResponse(java.lang.String)
	 */
	@Override
	public void parseJsonResponse(String response) {
		// TODO Auto-generated method stub
		try {
			if (!isValidResponse(response)) {
				openSessionExpireAlert(errorMessage(response));
			} else {
				gson = new Gson();
				if (request_type.equals(Util.EVENT_REFRESH)) {
					RefreshResponse refresh = gson.fromJson(response, RefreshResponse.class);
					Util.db.InsertAndUpdateRefresh(refresh, checked_in_eventId, true);
					hideAttendeeTab();
					Util.external_setting_pref.edit().putString(sfdcddetails.user_id + checked_in_eventId, "").commit();
					fillExternalSettings(true);
					Util.external_setting_pref = getSharedPreferences(Util.EXTERNAL_PREF, MODE_PRIVATE);
					ext_settings = new ExternalSettings();
					if (!Util.external_setting_pref.getString(sfdcddetails.user_id + checked_in_eventId, "")
							.isEmpty()) {
						ext_settings = new Gson().fromJson(
								Util.external_setting_pref.getString(sfdcddetails.user_id + checked_in_eventId, ""),
								ExternalSettings.class);
					}
					isselfcheckinpopupopen=false;
					setUI();
				}
			}

		} catch (Exception e){
			e.printStackTrace();
		}
	}
	public void openPixelDialog(Context ctx) {
		try {
			String[] array = {"90","91","92","93","94","95","96","97","98","99","100","101","102","103","104","105","106","107","108","109","110"};

			final Dialog sort_dialog = new Dialog(ctx);
			sort_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			sort_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
			sort_dialog.setContentView(R.layout.filter_itempool_dialog_layout);

			// Grab the window of the dialog, and change the width
			WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
			Window window = sort_dialog.getWindow();
			lp.copyFrom(window.getAttributes());
			// This makes the dialog take up the full width
			lp.width = WindowManager.LayoutParams.MATCH_PARENT;
			lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
			sort_dialog.getWindow().setAttributes(lp);
			TextView txt_filetr_by = (TextView) sort_dialog.findViewById(R.id.txtsortby);
			ListView list_view = (ListView) sort_dialog.findViewById(R.id.list_view_items);
			Button btn_done = (Button) sort_dialog.findViewById(R.id.btn_done);
			ImageView img_close = (ImageView) sort_dialog.findViewById(R.id.img_close);
			img_close.setVisibility(View.VISIBLE);
			txt_filetr_by.setText("Badge Pixel Settings");
			txt_filetr_by.setTypeface(Util.roboto_regular);
			sort_dialog.show();
			ArrayAdapter<String> adapter =new ArrayAdapter(ExternalSettingsActivity.this,android.R.layout.simple_list_item_single_choice,getResources().getStringArray(R.array.pixels_array));

			list_view.setAdapter(adapter);

			list_view.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			for (int i = 0;i<array.length;i++) {
				if (ext_settings.Android_Badge_Pixel.equals(array[i])) {
					list_view.setItemChecked(i,true);
					break;
				}
			}

			list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					ext_settings.Android_Badge_Pixel=array[position];

				}
			});
			img_close.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					sort_dialog.dismiss();
				}
			});
			btn_done.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					right_pixelvalue.setText(ext_settings.Android_Badge_Pixel);
					sort_dialog.dismiss();
					Util.external_setting_pref.edit()
							.putString(sfdcddetails.user_id + checked_in_eventId, new Gson().toJson(ext_settings).toString())
							.commit();

				}
			});

		} catch (Exception e) {
			e.printStackTrace();		}
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see com.globalnest.network.IPostResponse#insertDB()
	 */
	@Override
	public void insertDB() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.globalnest.scanattendee.BaseActivity#setCustomContentView(int)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ext_settings.custom_barcode = toggle_barcode.isChecked();
			ext_settings.allow_promocode = toggle_promocode.isChecked();
			ext_settings.quick_checkin = toggle_checkin.isChecked();
			ext_settings.quick_print = toggle_print.isChecked();
			ext_settings.doubleSide_badge=toggle_doubleSideBadge.isChecked();
			ext_settings.online_mode=toggle_online_mode.isChecked();
			ext_settings.zebra_settings=toggle_zebra.isChecked();
			ext_settings.checkin_checkout = radio_checkin_out_btn.isChecked();
			ext_settings.allow_livedata = toggle_livedata.isChecked();
			ext_settings.group_checkin = toggle_groupcheckin.isChecked();
			ext_settings.donthaveemail = toggle_idonthaveemail.isChecked();
			ext_settings.Android_Badge_Pixel = right_pixelvalue.getText().toString();
			if(toggle_doubleSideBadge.isChecked()){
				ext_settings.identical_doubleSide_badge=radio_identical.isChecked();
				ext_settings.mirror_doubleSide_badge=radio_mirror.isChecked();
				ext_settings.mirror_doubleSide_badge_two_in_one=radio_mirror_two_in_one.isChecked();
				ext_settings.identical_doubleSide_badge_two_in_one=radio_identical_two_in_one.isChecked();

			}else {
				ext_settings.identical_doubleSide_badge=false;
				ext_settings.mirror_doubleSide_badge=false;
				ext_settings.mirror_doubleSide_badge_two_in_one=false;
				ext_settings.identical_doubleSide_badge_two_in_one=false;
			}
			ext_settings.isValidateBadge=toggle_validate_badge.isChecked();
			isValidate_badge_reg_settings=toggle_validate_badge.isChecked();
			//Util.dashboard_data_pref.edit().putString(sfdcddetails.user_id + checked_in_eventId, new Gson().toJson(Util.dashboardHandler)).commit();
			// Util.external_setting_pref.edit().putString(Util.EXTERNAL_STRING,
			// new Gson().toJson(ext_settings).toString()).commit();
			Util.checkin_only_pref.edit().putBoolean(sfdcddetails.user_id+checked_in_eventId, !ext_settings.checkin_checkout).commit();
			Util.external_setting_pref.edit()
					.putString(sfdcddetails.user_id + checked_in_eventId, new Gson().toJson(ext_settings).toString())
					.commit();
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	public class SearchThread extends Thread {
		/* search for the printer for 10 times until printer has been found. */
		@Override
		public void run() {
			try {
				// search for net printer.
				if (netPrinterList(5)) {
					isPrinter = true;
					msgDialog.close();
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							/*img_printer.setImageResource(R.drawable.green_circle_1);
							txt_on_off_printer.setText("Connected");
							txt_on_off_printer.setTextColor(getResources().getColor(R.color.fb_color));
							txt_printer_name.setText(sharedPreferences.getString("printer", "QL_720NW"));*/
							setPrinterStatus();
						}
					});

				} else {
					msgDialog.close();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Util.setCustomAlertDialog(ExternalSettingsActivity.this);
							Util.openCustomDialog("Alert", "No printer found. Do you want to search?");
							Util.txt_okey.setText("SEARCH");
							Util.txt_dismiss.setVisibility(View.VISIBLE);
							Util.txt_dismiss.setText("CANCEL");
							Util.txt_okey.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View arg0) {

									Util.alert_dialog.dismiss();
									setDialog();
									search_thread = new SearchThread();
									search_thread.start();

								}
							});
							Util.txt_dismiss.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									Util.alert_dialog.dismiss();
								}
							});
						}
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private boolean netPrinterList(int times) {
		boolean searchEnd = false;
		try {
			// clear the item list
			if (mItems != null) {
				mItems.clear();
			}
			// get net printers of the particular model.
			mItems = new ArrayList<String>();
			Printer myPrinter = new Printer();
			mNetPrinter = myPrinter.getNetPrinters(PrinterDetails.selectedPrinterPrefrences.getString(ZebraPrinter.PRINTERMODEL,"QL_720NW").replace("_","-"));
			final int netPrinterCount = mNetPrinter.length;
			// when find printers,set the printers' information to the list.
			if (netPrinterCount > 0) {
				searchEnd = true;
			} else if (netPrinterCount == 0 && times == (Common.SEARCH_TIMES - 1)){ // when no printer is found
				String dispBuff[] = new String[1];
				dispBuff[0] = getString(R.string.noNetDevice);
				mItems.add(dispBuff[0]);
				searchEnd = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return searchEnd;
	}

	public void setDialog() {
		try {
			msgDialog.showMsgNoButton(getString(R.string.netPrinterListTitle_label),
					getString(R.string.search_printer));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setPrefereces(NetPrinter mNetPrinter) {
		try {
			// initialization for print
			PrinterInfo printerInfo = new PrinterInfo();
			Printer printer = new Printer();
			printerInfo = printer.getPrinterInfo();
			PrinterDetails.selectedPrinterPrefrences.edit().putString("printer", "QL_720NW").commit();
			if (sharedPreferences.getString("printerModel", "").equals("")) {
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putString("printerModel", "QL_720NW");
				editor.putString("port", "NET");
				editor.putString("address", printerInfo.ipAddress.toString());
				editor.putString("macAddress", printerInfo.macAddress.toString());
				editor.putString("address", mNetPrinter.ipAddress);
				editor.putString("macAddress", mNetPrinter.macAddress);
				editor.putString("printer", mNetPrinter.modelName);
				editor.putString("paperSize", "W62H100");
				if (badge_res.size() > 0) {
					BadgeDataNew badge_data = new Gson().fromJson(badge_res.get(0).badge.Data__c, BadgeDataNew.class);
					if (badge_data.paperSize.contains(Util.BROTHER_DK_1202)) {
						// editor.putString("paperSize", "W62H100");
						editor.putString("paperSize", "W62H100");
					} else if (badge_data.paperSize.contains(Util.BROTHER_DK_12345)) {
						editor.putString("paperSize", "W60H86");
					} else if (badge_data.paperSize.contains(Util.BROTHER_DKN_5224)) {
						editor.putString("paperSize", "W54");
					} else {
						editor.putString("paperSize", "W62");
					}
				}
				editor.putString("orientation", "LANDSCAPE");
				editor.putString("numberOfCopies", "1");
				editor.putString("halftone", "PATTERNDITHER");
				editor.putString("printMode", "FIT_TO_PAGE");
				editor.putString("pjCarbon", "false");
				editor.putString("pjDensity", "5");
				editor.putString("pjFeedMode", "PJ_FEED_MODE_FIXEDPAGE");
				editor.putString("align", "CENTER");
				editor.putString("leftMargin", "0");
				editor.putString("valign", "TOP");
				editor.putString("topMargin", "0");
				editor.putString("customPaperWidth", "0");
				editor.putString("customPaperLength", "0");
				editor.putString("customFeed", "0");
				editor.putString("customSetting", sharedPreferences.getString("customSetting", ""));
				editor.putString("rjDensity", "0");
				editor.putString("rotate180", "false");
				editor.putString("peelMode", "false");
				editor.putString("autoCut", "true");
				editor.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setSoftScannerAPI() {
		try {

			// If the adapter is null, then Bluetooth is not supported
			if (_bluetoothAdapter == null) {
				AlertDialogCustom dialog = new AlertDialogCustom(ExternalSettingsActivity.this);
				dialog.setParamenters("Error", "Bluetooth is not available", null, null, 1, false);
				dialog.setAlertImage(R.drawable.alert_error, "error");
				dialog.show();
				return;
			}

			_bluetoothIsOn = _bluetoothAdapter.isEnabled();
			if (!_bluetoothIsOn) {
				Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			switch (requestCode) {
				case REQUEST_ENABLE_BT:
					_bluetoothIsOn = _bluetoothAdapter.isEnabled();

					break;
				case 2017:
					if (data.hasExtra(FilePicker.EXTRA_FILE_PATH)) {
						File selectedFile = new File(data.getStringExtra(FilePicker.EXTRA_FILE_PATH));
						FileInputStream fileInputStream = new FileInputStream(selectedFile);
						CSVFile csv_reader = new CSVFile(fileInputStream);
						List<String[]> csv_data = csv_reader.read();
						if(csv_data.size() == 0){
							showMessageAlert("No coulmns in this sheet, Please check your sheet and try again!", false);
						}else if(csv_data.get(0).length == 0){
							showMessageAlert("No coulmns in this sheet, Please check your sheet and try again!", false);
						}else if(!csv_data.get(0)[0].contains("Barcode")){
							showMessageAlert("Barcode coulmns are missing in this sheet, Please check your sheet and try again!", false);
						}else if(csv_data.get(0).length < 3){
							showMessageAlert("Coulmns is missing in this sheet, Please check your sheet and try again!", false);
						}else{
							new CSVDataLoaderTask(ExternalSettingsActivity.this, csv_data).execute();
						}
					}
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void unPairSocketDevices() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		// Log.i("------------Scanner List--------",":"+pairedDevices.size());
		for (BluetoothDevice bt : pairedDevices) {
			// Log.i("------------Scanner Names--------",":"+bt.getName());
			if (bt.getName().contains("Socket")) {
				unpairDevice(bt);
			}
		}
		Toast.makeText(ExternalSettingsActivity.this, "All socket devices settings are cleared.", Toast.LENGTH_LONG)
				.show();
	}

	private void unpairDevice(BluetoothDevice device) {
		try {
			Method m = device.getClass().getMethod("removeBond", (Class[]) null);
			m.invoke(device, (Object[]) null);
		} catch (Exception e) {
			// Log.i(SocketBroadCastReciever.class.getName(), e.getMessage());
			e.printStackTrace();
		}
	}

	private int getCSVCount(File fileToExport) {
		int count = -1;

		if (fileToExport.exists()) {
			BufferedReader br;
			try {
				br = new BufferedReader(new FileReader(fileToExport));
				while (br.readLine() != null) {
					count++;
				}
				br.close();
				return count;
			} catch (FileNotFoundException e) {

				e.printStackTrace();
				return 0;
			} catch (IOException e) {

				e.printStackTrace();
				return 0;
			}

		}
		return 0;
	}

	private void exportCSV(File fileToExport) {
		if (fileToExport.exists()) {
			Uri u1 = Uri.fromFile(fileToExport);
			Intent sendIntent = new Intent(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_SUBJECT,
					"Offline Attendees For The Event : " + checkedin_event_record.Events.Name);
			sendIntent.putExtra(Intent.EXTRA_TEXT,
					"Hello \n\n Please find attached offline scans of " + checkedin_event_record.Events.Name);
			sendIntent.putExtra(Intent.EXTRA_STREAM, u1);
			sendIntent.setType("text/richtext");
			startActivity(sendIntent);
		} else {
			AlertDialogCustom dialog = new AlertDialogCustom(ExternalSettingsActivity.this);
			dialog.setParamenters("Alert !", "Sorry ! no offline attendee to export.", null, null, 1, false);
			dialog.show();
		}
	}

	private File getCSVTOExport() {
		String name = checkedin_event_record.Events.Name.replaceAll("/","") + "_" + checkedin_event_record.Events.Id + "_"
				+ sfdcddetails.user_id+"_"+user_profile.Profile.Email__c;
		File file;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "ScanAttendee Barcodes");
		}else{
			file= new File(Environment.getExternalStorageDirectory(), "ScanAttendee Barcodes");
		}

		File mfileToExport = new File(file.getPath() + File.separator + name + "_barcodes.csv");
		return mfileToExport;
	}

	private void showNoAttendeesAlert() {
		Util.setCustomAlertDialog(ExternalSettingsActivity.this);
		Util.txt_dismiss.setVisibility(View.VISIBLE);
		Util.setCustomDialogImage(R.drawable.alert);
		Util.txt_okey.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// ticket_dialog.dismiss();
				Util.alert_dialog.dismiss();
				request_type = Util.EVENT_REFRESH;
				doRequest();
			}
		});
		Util.txt_dismiss.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// ticket_dialog.dismiss();
				Util.alert_dialog.dismiss();

			}
		});
		//		Util.openCustomDialog("Alert", "Do you want to reset all your event settings?");
		Util.openCustomDialog("Alert", "Do you want to reset all below event settings?\n" +
				" 1) Selfcheckin settings       \n" +
				"2) Badge settings              \n" +
				"      3) Ticket Hide/Show settings\n" + "4) External Settings          \n" +
				"  a)Quick Checkin  \n" +
				"    b)Validate Badge  \n" +
				"   c)Print on Scan    \n"+
				"      d)Custom Barcode \n"+
				"     e)Group Check-ins \n" +
				"                   f)Allow Live Data Updates\n"+
				"            g)Badge Pixel Settings \n");
	}

	public void startService(String reload) {
		DownloadResultReceiver mReceiver = new DownloadResultReceiver(new Handler());
		mReceiver.setReceiver(ExternalSettingsActivity.this);
		Intent intent = new Intent(Intent.ACTION_SYNC, null, ExternalSettingsActivity.this, DownloadService.class);
		String ticURL = sfdcddetails.instance_url + WebServiceUrls.SA_GET_TICKET_LIST + "Event_id="
				+ checked_in_eventId;
		String offset =  "";

		if(!reload.equals(DownloadService.reload)){
			offset = Util.offset_pref.getString(checked_in_eventId, "");
		}
		/*if(Util.db.totalOrderCountwithoutCancelled(checked_in_eventId)==0){
			attURL = attURL+"&"+"offset=";
		}else {
			attURL = attURL + "&" + "offset=" + Util.offset_pref.getString(eventId, "");
		}*/
		String attURL = sfdcddetails.instance_url + WebServiceUrls.SA_ATTENDEE_LOAD_MORE + "Event_id="
				+ checked_in_eventId + "&User_id=" + sfdcddetails.user_id + "&offset=" + offset + "&limit="
				+ checkedin_event_record.Events.scan_attendee_limit__c;
		if(Util.db.totalOrderCountwithoutCancelled(checked_in_eventId)==0){
			attURL = sfdcddetails.instance_url + WebServiceUrls.SA_ATTENDEE_LOAD_MORE + "Event_id="
					+ checked_in_eventId + "&User_id=" + sfdcddetails.user_id + "&offset=" + "&limit="
					+ checkedin_event_record.Events.scan_attendee_limit__c;
		}else {
			attURL = sfdcddetails.instance_url + WebServiceUrls.SA_ATTENDEE_LOAD_MORE + "Event_id="
					+ checked_in_eventId + "&User_id=" + sfdcddetails.user_id + "&offset=" + offset + "&limit="
					+ checkedin_event_record.Events.scan_attendee_limit__c;
		}
		intent.putExtra(DownloadService.ATT_URL, attURL);
		intent.putExtra(DownloadService.TIC_URL, ticURL);
		intent.putExtra(DownloadService.BADGE_URL, sfdcddetails.user_id);
		intent.putExtra(DownloadService.ACCESSTOKEN, sfdcddetails.token_type + " " + sfdcddetails.access_token);
		intent.putExtra(DownloadService.EVENTID, checked_in_eventId);
		intent.putExtra(DownloadService.REQUESTTYPE, "orders");
		intent.putExtra(DownloadService.RECEIVER, mReceiver);
		intent.putExtra(DownloadService.ACTIVITY_NAME, ExternalSettingsActivity.class.getName());
		intent.putExtra("requestId", 101);
		startService(intent);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.globalnest.BackgroundReciver.DownloadResultReceiver.Receiver#
	 * onReceiveResult(int, android.os.Bundle)
	 */
	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		// TODO Auto-generated method stub
		if (progress_download_data != null) {
			progress_download_data.setMax(Util.dashboardHandler.totalOrders);
			progress_download_data.clearAnimation();
			progress_download_data.getCircularProgressBar().setCircleWidth(10);
		}
		// Log.i("--------------Result Code In
		// External-----------",":"+resultCode+":"+txt_download_attendees_count);
		if (resultCode == DownloadService.STATUS_FINISHED || resultCode == DownloadService.STATUS_ERROR) {
			progress_loader.setVisibility(View.GONE);
			layout_download.setEnabled(true);
			if (progress_download_data != null) {
				progress_download_data.setVisibility(View.GONE);
			}
			if (img_download != null) {
				img_download.setVisibility(View.VISIBLE);
			}
			if (resultCode == DownloadService.STATUS_ERROR) {
				if (txt_download_attendees_count != null) {
					new CountDownTimer(3000, 1000) {

						@Override
						public void onTick(long millisUntilFinished) {
							// TODO Auto-generated method stub
							txt_download_attendees_count
									.setText("Error in downloading data, Please check internet connection.");
							txt_download_attendees_count.setTextColor(getResources().getColor(R.color.red));
						}

						@Override
						public void onFinish() {
							// TODO Auto-generated method stub
							txt_download_attendees_count
									.setText("You have downloaded " + Util.db.totalOrderCountwithoutCancelled(checked_in_eventId)
											+ " Orders out of " + Util.dashboardHandler.totalOrders);
							txt_download_attendees_count.setTextColor(getResources().getColor(R.color.gray_color));
						}
					}.start();
				}
			} else {
				if (txt_download_attendees_count != null) {
					txt_download_attendees_count
							.setText("You have downloaded " + Util.db.totalOrderCountwithoutCancelled(checked_in_eventId)
									+ " Orders out of " + Util.dashboardHandler.totalOrders);
					txt_download_attendees_count.setTextColor(getResources().getColor(R.color.gray_color));
					Util.setCustomAlertDialog(ExternalSettingsActivity.this);
					Util.txt_dismiss.setVisibility(View.GONE);
					Util.setCustomDialogImage(R.drawable.alert);
					Util.txt_okey.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// ticket_dialog.dismiss();
							Util.alert_dialog.dismiss();
							progress_loader.setVisibility(View.GONE);
							// layout_download.setEnabled(false);
							//img_download.setVisibility(View.GONE);
							progress_download_data.setVisibility(View.GONE);

						}
					});
					Util.openCustomDialog("Alert", "All attendees are downloaded Successfully!");
					showCustomToast(this, "All attendees are downloaded Successfully!",
							R.drawable.img_like, R.drawable.toast_greenroundededge, true);
				}
			}

		} else {
			String reload = "";
			int Count=0;
			if(resultData !=null){
				reload = resultData.getString(DownloadService.reload);
				Count = resultData.getInt("Count");
			}
			if(reload != null && reload.equals(DownloadService.reload)){
				progress_loader.setVisibility(View.VISIBLE);
				if (txt_download_attendees_count != null) {
					txt_download_attendees_count
							.setText("You have downloaded " + + Util.db.totalOrderCountwithoutCancelled(checked_in_eventId)
									+ " Orders out of " + Util.dashboardHandler.totalOrders);
				}

				if (progress_download_data != null) {
					progress_download_data.setProgress(Count);
				}
			}else{
				progress_loader.setVisibility(View.VISIBLE);
				if (txt_download_attendees_count != null) {
					txt_download_attendees_count
							.setText("You have downloaded " + Util.db.totalOrderCountwithoutCancelled(checked_in_eventId)
									+ " Orders out of " + Util.dashboardHandler.totalOrders);
				}

				if (progress_download_data != null) {
					progress_download_data.setProgress(Util.db.totalOrderCountwithoutCancelled(checked_in_eventId));
				}
			}

		}
	}
	public void setPrinterStatus(){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				PrinterDetails printerDetails=new PrinterDetails();
				/*if(!(PrinterDetails.selectedPrinterPrefrences.getString(ZebraPrinter.SELECTED_PRINTER, "").equalsIgnoreCase("Brother"))|| !(PrinterDetails.selectedPrinterPrefrences.getString(ZebraPrinter.SELECTED_PRINTER, "").equalsIgnoreCase("Zebra")))
				{
					//if (PrinterDetails.selectedPrinterPrefrences.getString("printer", "").isEmpty()) {
					img_printer.setImageResource(R.drawable.red_circle_1);
					txt_on_off_printer.setText("Not Connected");
					txt_on_off_printer.setTextColor(getResources().getColor(R.color.gray_color));
					txt_printer_name.setVisibility(View.GONE);
				}
				else*/ if(PrinterDetails.selectedPrinterPrefrences.getString(ZebraPrinter.SELECTED_PRINTER, "").equalsIgnoreCase("Brother")){
					if(!PrinterDetails.selectedPrinterPrefrences.getString(ZebraPrinter.ZEBRA_WIFI_IP, "").isEmpty()){
						runOnUiThread(new Runnable() {
							@Override public void run() { // TODO Auto-generated method stub
								new SearchPrinterStatusThread().run();
							} });
					}else{
						txt_on_off_printer.setText("Connected");
						img_printer.setImageResource(R.drawable.green_circle_1);
						txt_on_off_printer.setTextColor(getResources().getColor(R.color.green_connected));
						txt_printer_name.setVisibility(View.VISIBLE);
						txt_printer_name.setTextColor(getResources().getColor(R.color.fb_color));
						txt_printer_name.setText(PrinterDetails.selectedPrinterPrefrences.getString("printer", "")
								+"( "+PrinterDetails.selectedPrinterPrefrences.getString("macAddress", "")+" )");

					}
				}else if(PrinterDetails.selectedPrinterPrefrences.getString(ZebraPrinter.SELECTED_PRINTER, "").equalsIgnoreCase("Zebra")){
					if((PrinterDetails.selectedPrinterPrefrences.getBoolean("isConnected",true))){
						txt_on_off_printer.setText("Connected");
						img_printer.setImageResource(R.drawable.green_circle_1);
						txt_on_off_printer.setTextColor(getResources().getColor(R.color.green_connected));
					}
					txt_printer_name.setVisibility(View.VISIBLE);
					txt_printer_name.setTextColor(getResources().getColor(R.color.fb_color));
					if(!PrinterDetails.selectedPrinterPrefrences.getString(ZebraPrinter.ZEBRA_WIFI_IP, "").isEmpty())
						txt_printer_name.setText("Zebra Printer ("+PrinterDetails.selectedPrinterPrefrences.getString(ZebraPrinter.ZEBRA_WIFI_IP, "")+")");
					else{txt_printer_name.setText(PrinterDetails.selectedPrinterPrefrences.getString("printer", "")
							+"( "+PrinterDetails.selectedPrinterPrefrences.getString("macAddress", "")+" )");}
				}else{
					img_printer.setImageResource(R.drawable.red_circle_1);
					txt_on_off_printer.setText("Not Connected");
					txt_on_off_printer.setTextColor(getResources().getColor(R.color.gray_color));
					txt_printer_name.setVisibility(View.GONE);
				}
			}
		});

	}
	public class SearchPrinterStatusThread extends Thread {
		/* search for the printer for 10 times until printer has been found. */
		@Override
		public void run() {
			try {
				// search for net printer.
				if (netPrinterList(1)) {
					if(!(PrinterDetails.selectedPrinterPrefrences.getBoolean("isConnected",false))) {
						txt_on_off_printer.setText("Waiting for ");
						txt_on_off_printer.setTextColor(getResources().getColor(R.color.fb_color));
						img_printer.setImageResource(R.drawable.orange_button_bg);
					}else if((PrinterDetails.selectedPrinterPrefrences.getBoolean("isConnected",true))){
						txt_on_off_printer.setText("Connected");
						img_printer.setImageResource(R.drawable.green_circle_1);
						txt_on_off_printer.setTextColor(getResources().getColor(R.color.green_connected));
					}
					txt_printer_name.setVisibility(View.VISIBLE);
					txt_printer_name.setTextColor(getResources().getColor(R.color.fb_color));
					txt_printer_name.setText(PrinterDetails.selectedPrinterPrefrences.getString("printer", "")
							+"( "+PrinterDetails.selectedPrinterPrefrences.getString("address", "")+" )");
					//init();
				} else {
					img_printer.setImageResource(R.drawable.red_circle_1);
					txt_on_off_printer.setTextColor(getResources().getColor(R.color.orange_bg));
					txt_on_off_printer.setText("Disconnected");
					txt_printer_name.setVisibility(View.VISIBLE);
					txt_printer_name.setTextColor(getResources().getColor(R.color.fb_color));
					txt_printer_name.setText(PrinterDetails.selectedPrinterPrefrences.getString("printer", ""));
					//openPrinterNotConnectedDialog();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	private void connectIOSocket(){
		try {
			mSocket = IO.socket("https://pchat.eventdex.com");
			//mSocket = IO.socket("https://chat.eventdex.com");
			mSocket.connect();
			mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
				@Override
				public void call(Object... args) {

					System.out.println(mSocket.connected()+"sai"); // true
				}
			});
			mSocket.on("/event/PL_BLN_Tstatus__e",onNewCheckins);
			mSocket.on("/event/PL_PrintStatus__e",onPrintStatus);
			//mSocket.isActive();
		} catch (URISyntaxException e) {
			e.getMessage();
		}
	}
	private void dissconnectIOSocket(){
		try {
			mSocket = IO.socket("https://pchat.eventdex.com");
			//mSocket = IO.socket("https://chat.eventdex.com");
			mSocket.disconnect();
			mSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
				@Override
				public void call(Object... args) {

					System.out.println(mSocket.isActive()+"sai"); // true
				}
			});

			//mSocket.isActive();
		} catch (URISyntaxException e) {
			e.getMessage();
		}
	}
	public Emitter.Listener onNewCheckins = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					JSONObject data = (JSONObject) args[0];

					try {
						WebsocketResponseObj websocketResponseObj = gson.fromJson(String.valueOf(data), WebsocketResponseObj.class);
						if(NullChecker(websocketResponseObj.payload.Event_Id__c).equalsIgnoreCase(checked_in_eventId)) {
							//if(websocketResponseObj.channel.equalsIgnoreCase("/event/PL_BLN_Tstatus__e")) {
							boolean isFreeSession = false;
							isFreeSession = Util.db.isItemPoolFreeSession(websocketResponseObj.payload.BLN_Item_Pool_Id__c, checked_in_eventId);

							boolean status = Boolean.valueOf(websocketResponseObj.payload.Tstatus_Name__c);// success.optJSONObject(0).optBoolean("Status");
							//String time = websocketResponseObj.data.payload.scan_time__c;// success.optJSONObject(0).optString("TimeStamp");
							if (isFreeSession) {
								Util.db.InsertAndUpdateWebsocketSessionAttendees(websocketResponseObj, checked_in_eventId);
							} else {

								Util.db.insertandupdatecheckincounts(websocketResponseObj.payload.BLN_Group__c,checked_in_eventId,websocketResponseObj.payload.totalItemschkQty__c);
								Util.db.InsertAndUpdateTSTATUSbyWebsocket(websocketResponseObj.payload,
										checked_in_eventId);
							}

						}
						//}
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
				}
			});
		}
	};
	private Emitter.Listener onPrintStatus = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					JSONObject data = (JSONObject) args[0];

					//showCustomToast(AttendeeListActivity.this, "Received data from IO", R.drawable.img_like,R.drawable.toast_greenroundededge,false);

					try {

						WebsocketResponseObj websocketResponseObj = gson.fromJson(String.valueOf(data), WebsocketResponseObj.class);
						if(websocketResponseObj.payload.Event_Id__c.equalsIgnoreCase(checked_in_eventId)) {
							String printstatus = websocketResponseObj.payload.Status__c;
							String TicketId = websocketResponseObj.payload.Ticket_Id__c;
							String lastmodifieddate = websocketResponseObj.payload.CreatedDate;
							Util.db.insertandupdateAttendeeBadgeIdandPrintstatus(TicketId, printstatus, lastmodifieddate);
						}
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
				}
			});
		}
	};

}
