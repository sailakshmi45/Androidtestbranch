package com.globalnest.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.globalnest.network.WebServiceUrls;
import com.globalnest.scanattendee.BaseActivity;
import com.globalnest.scanattendee.R;

import org.apache.http.NameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.BLUETOOTH;
import static android.Manifest.permission.BLUETOOTH_ADMIN;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.CHANGE_NETWORK_STATE;
import static android.Manifest.permission.CHANGE_WIFI_STATE;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.MANAGE_DOCUMENTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class AppUtils {
	public static final String URL = "URL";
	public static final String VALUES = "VALUES";
	public static final String IMAGE = "IMAGE";
	public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	public static SharedPreferences eventPrefer, logn_prefer;
	public final static String APP_ACCESS_TOKEN_TYPE = "TOKEN_TYPE";
	public final static String APP_ACCESS_TOKEN = "OAUTH TOKEN";
	// till herei
	public static final String INTENT_KEY = "intent_key";
	public static final String REVOKE_KEY="revoke_key";
	public static final String IS_DEV_PREF="is_dev_pref";
	public static final String IS_QA_PREF = "is_qa_pref";
	public static final String USER_CRED_PRF = "user_cred_pref";
	public static final String USER_EMAIL = "user_email";
	public static SharedPreferences isDevPref=null;
	public static SharedPreferences user_credentials = null;
	public static final boolean isLogEnabled = true;
	public static boolean isCrashReportEnable=true;//ON when moving to store
	public static final String QA_USER_EMAIL_ID = "qa_useAccept_Terms_Conditions__cr_email_id";
	public static final String PRO_USER_EMAIL_ID = "pro_user_email_id";
	public static final String Mirror_Separate_Image ="Mirror Separate Image";
	public static final String Identical_Single_Image="Identical Single Image";
	public static final String Mirror_Single_Image="Mirror Single Image";
	public static final String Identical_Separate_Image ="Identical Separate Image";


	public static void showError(Context ctxt, String message) {
		if (ctxt != null) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					ctxt);
			alertDialogBuilder.setTitle("Error");
			alertDialogBuilder
					.setMessage(message)
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
													int id) {
									dialog.cancel();
								}
							});
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}
	}
	public static void showError(Context ctxt, String message,Exception e) {
		String errorMessage="";
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		errorMessage=sw.toString();
		if(errorMessage.trim().isEmpty()){
			errorMessage=getErrorFromException(e);
		}
		message=message+" "+errorMessage;
		if (ctxt != null) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					ctxt);
			alertDialogBuilder.setTitle("Error");
			alertDialogBuilder
					.setMessage(message)
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
													int id) {
									dialog.cancel();
								}
							});
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}
	}
	public static String getErrorFromException(Exception e){
		return e.getMessage()+String.valueOf(e.getStackTrace()[0].getLineNumber());
	}
	public static Boolean isOnline(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null && ni.isConnected())
			return true;
		return false;
	}
	/*public static void givePhoneStatepermission(Context context){
		ActivityCompat.requestPermissions((Activity) context, new String[]{READ_PHONE_STATE}, 1);
		ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
				String.valueOf(new String[]{READ_PHONE_STATE}));
	}*/
	/*public static boolean isPhoneStatePermissionGranted(Context context) {
		int microphone = ContextCompat.checkSelfPermission(context.getApplicationContext(), READ_PHONE_STATE);
		return microphone ==PackageManager.PERMISSION_GRANTED;
	}*/
	public static void  givePhoneStateandStoragepermission(Context context){
		ActivityCompat.requestPermissions((Activity) context, new String[]{INTERNET,ACCESS_NETWORK_STATE
				,CHANGE_NETWORK_STATE,ACCESS_WIFI_STATE,CHANGE_WIFI_STATE,BLUETOOTH_ADMIN,
				BLUETOOTH,WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE,MANAGE_DOCUMENTS//MANAGE_EXTERNAL_STORAGE, ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION,ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
				}, 1);//READ_PHONE_STATE,//Added separatly ,RECORD_AUDIO
		ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, String.valueOf(new String[]{
				INTERNET,
				ACCESS_NETWORK_STATE,
				CAMERA,
				CHANGE_NETWORK_STATE,
				ACCESS_WIFI_STATE,CHANGE_WIFI_STATE,
				BLUETOOTH_ADMIN,
				BLUETOOTH,
				WRITE_EXTERNAL_STORAGE,
				READ_EXTERNAL_STORAGE//,MANAGE_EXTERNAL_STORAGE,ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION,ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
				}));//READ_PHONE_STATE//Added separatly ,RECORD_AUDIO
		//String.valueOf(UriPermission.PARCELABLE_WRITE_RETURN_VALUE)
	}

	/*
        public static void giveStoragepermission(Context context) {
            try{

                if(!Environment.isExternalStorageManager()) {
                    // You can use the API that requires the permission.
                    Uri packageURI = Uri.parse("package:"+ BuildConfig.APPLICATION_ID);
                    context.startActivity(
                            new Intent(
                                    Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                                    packageURI
                            )
                    );
                    ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, String.valueOf(new String[]{WRITE_EXTERNAL_STORAGE,
                            READ_EXTERNAL_STORAGE,MANAGE_EXTERNAL_STORAGE}));
                }
                */
	/*if(!Environment.isExternalStorageManager()){
	 *//*
	 */
/*Uri packageURI = Uri.parse("package:"+ BuildConfig.APPLICATION_ID);
				startActivity(
      Intent(
        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
        uri
      )
    )Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, packageURI);
				context.startActivity(intent);*//*
	 */
/*
				ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, String.valueOf(new String[]{Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION}));
			} *//*

		} catch (Exception ex){
			ex.printStackTrace();
			Intent intent = new Intent();
			intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
			context.startActivity(intent);
		}}
*/
	public static void  givepermission(Context context){
		ActivityCompat.requestPermissions((Activity) context, new String[]{INTERNET, READ_EXTERNAL_STORAGE,ACCESS_NETWORK_STATE
				,CAMERA,WRITE_EXTERNAL_STORAGE,CHANGE_NETWORK_STATE,ACCESS_WIFI_STATE,CHANGE_WIFI_STATE,BLUETOOTH_ADMIN,
				BLUETOOTH}, 1);//READ_PHONE_STATE//Added separatly ,RECORD_AUDIO
		ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, String.valueOf(new String[]{
				INTERNET,
				ACCESS_NETWORK_STATE,
				CAMERA,
				CHANGE_NETWORK_STATE,
				ACCESS_WIFI_STATE,CHANGE_WIFI_STATE,
				BLUETOOTH_ADMIN,
				BLUETOOTH,
				READ_EXTERNAL_STORAGE,
				WRITE_EXTERNAL_STORAGE}));//READ_PHONE_STATE//Added separatly ,RECORD_AUDIO
		//String.valueOf(UriPermission.PARCELABLE_WRITE_RETURN_VALUE)
	}
	public static void giveMicrophonepermission(Context context){
		ActivityCompat.requestPermissions((Activity) context, new String[]{RECORD_AUDIO}, 1);
		ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
				String.valueOf(new String[]{RECORD_AUDIO}));
	}
	public static boolean isMicrophonePermissionGranted(Context context) {
		int microphone = ContextCompat.checkSelfPermission(context.getApplicationContext(), RECORD_AUDIO);
		return microphone ==PackageManager.PERMISSION_GRANTED;
	}
	public static void giveStoragermission(Context context){
		try {
			//if (PermissionUtils.neverAskAgainSelected((Activity) context, String.valueOf(new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}))) {

				Util.setCustomAlertDialog(context);
				Util.txt_dismiss.setVisibility(View.VISIBLE);
				Util.setCustomDialogImage(R.drawable.alert);
				if(isStoragePermissionDenied(context)){
					Util.txt_okey.setText("Settings");
					Util.txt_okey.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// ticket_dialog.dismiss();
							Util.alert_dialog.dismiss();
							BaseActivity.isstoragepermissionrequested=true;
							if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
									String.valueOf(new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}))) {
								Uri packageURI = Uri.parse("package:" + "com.globalnest.scanattendee");
								context.startActivity(new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", packageURI));
							}
						}

					});
					Util.txt_dismiss.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// ticket_dialog.dismiss();
							Util.alert_dialog.dismiss();
							BaseActivity.isstoragepermissionrequested=false;
						}
					});
					if (android.os.Build.VERSION.SDK_INT > 29)
						Util.openCustomDialog("Alert!", "Please allow the files and media permissions for the app to function and print badges properly! \nTap Settings-> Permissions, & allow Files and Media.");
					else
						Util.openCustomDialog("Alert!", "Please allow the storage permissions for the app to function and print badges properly! \n Tap Settings-> Permissions, and turn Storage on.");

				}else {
					if (android.os.Build.VERSION.SDK_INT > 29)
						Util.openCustomDialog("Alert!", "Please allow the files and media permissions for the app to function and print badges properly!");
					else
						Util.openCustomDialog("Alert!", "Please allow the storage permissions for the app to function and print badges properly!");

					Util.txt_okey.setText("Continue");
					Util.txt_okey.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// ticket_dialog.dismiss();
							Util.alert_dialog.dismiss();
							requeststorage(context);
							/*if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
									String.valueOf(new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}))) {
								Uri packageURI = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
								context.startActivity(new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", packageURI));
							}*/
						}

					});
					Util.txt_dismiss.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// ticket_dialog.dismiss();
							Util.alert_dialog.dismiss();

						}
					});

				}






		}


				/*if (android.os.Build.VERSION.SDK_INT >29 )
					Util.openCustomDialog("Alert!","For preview & badge printing, please allow the following! \n Permissions--> Files and Media --> Allow");
				else
					Util.openCustomDialog("Alert!","For preview & badge printing, please allow the following! \n Permissions--> Storage --> Allow");
*/

		catch (Exception e){
			e.printStackTrace();
			/*Uri packageURI = Uri.parse("package:"+ BuildConfig.APPLICATION_ID);
			context.startActivity(new Intent("android.settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION", packageURI));*/
		}

	}
	public static void requeststorage(Context context) {
		BaseActivity.isstoragepermissionrequested=true;

		ActivityCompat.requestPermissions((Activity) context, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE,}, 1);

	}
	public static boolean isStoragePermissionDenied(Context context) {
		int writestorage = ContextCompat.checkSelfPermission(context.getApplicationContext(), WRITE_EXTERNAL_STORAGE);
		int readstorage = ContextCompat.checkSelfPermission(context.getApplicationContext(), READ_EXTERNAL_STORAGE);
		return writestorage == PackageManager.PERMISSION_DENIED &&
				readstorage == PackageManager.PERMISSION_DENIED ;
	}
	public static boolean isStoragePermissionGranted(Context context) {
		int writestorage = ContextCompat.checkSelfPermission(context.getApplicationContext(), WRITE_EXTERNAL_STORAGE);
		int readstorage = ContextCompat.checkSelfPermission(context.getApplicationContext(), READ_EXTERNAL_STORAGE);
		return writestorage == PackageManager.PERMISSION_GRANTED &&
				readstorage == PackageManager.PERMISSION_GRANTED ;
	}
	public static void giveCampermission(Context context){
		if(!isCamPermissionGranted(context)){
			Util.setCustomAlertDialog(context);
			Util.txt_dismiss.setVisibility(View.VISIBLE);
			Util.setCustomDialogImage(R.drawable.alert);
				Util.txt_okey.setText("Settings");
				Util.txt_okey.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// ticket_dialog.dismiss();
						Util.alert_dialog.dismiss();
						if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
								String.valueOf(new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}))) {
							Uri packageURI = Uri.parse("package:" + "com.globalnest.scanattendee");
							context.startActivity(new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", packageURI));
						}
					}

				});
				Util.txt_dismiss.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// ticket_dialog.dismiss();
						Util.alert_dialog.dismiss();

					}
				});
				if (android.os.Build.VERSION.SDK_INT > 29)
					Util.openCustomDialog("Alert!", "Please allow the Camera permissions for scanning! \n Tap Settings-> Permissions, and allow Camera.");
				else
					Util.openCustomDialog("Alert!", "Please allow the Camera permissions for scanning! \n Tap Settings-> Permissions, and turn Camera on.");

			}
		/*ActivityCompat.requestPermissions((Activity) context, new String[]{CAMERA}, 1);
		ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
				String.valueOf(new String[]{CAMERA}));*/
	}

	public static boolean isCamPermissionGranted(Context context) {
		int microphone = ContextCompat.checkSelfPermission(context.getApplicationContext(), CAMERA);
		return microphone ==PackageManager.PERMISSION_GRANTED;
	}
	public static boolean isLocationPermissionGranted(Context context) {
		int location = ContextCompat.checkSelfPermission(context.getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION);
		return location ==PackageManager.PERMISSION_GRANTED;
	}
	public static void giveLocationpermission(Context context){
		ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
		ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
				String.valueOf(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}));
	}
	public static boolean isPermissionGranted(Context context) {
		int writestorage = ContextCompat.checkSelfPermission(context.getApplicationContext(), WRITE_EXTERNAL_STORAGE);
		int readstorage = ContextCompat.checkSelfPermission(context.getApplicationContext(), READ_EXTERNAL_STORAGE);
		int camera = ContextCompat.checkSelfPermission(context.getApplicationContext(), CAMERA);
		return writestorage == PackageManager.PERMISSION_GRANTED &&
				readstorage == PackageManager.PERMISSION_GRANTED &&
				camera ==PackageManager.PERMISSION_GRANTED;
	}
	public static boolean isAllPermissionGranted(Context context) {
		return isStoragePermissionGranted(context) &&
				isCamPermissionGranted(context) &&
				isMicrophonePermissionGranted(context);//isPhoneStatePermissionGranted(context)&&
	}
	public static String getQuery(List<NameValuePair> params) {
		StringBuilder result = new StringBuilder();
		try {
			boolean first = true;
			for (NameValuePair pair : params) {
				if (first)
					first = false;
				else
					result.append("&");

				result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
				result.append("=");
				result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result.toString();
	}

	public static String _getPreference(SharedPreferences perf, String key) {

		String value = perf.getString(key, "");

		return value;

	}

	public static HashMap<String, String> splitQuery(URL url) {
		HashMap<String, String> query_pairs = new LinkedHashMap<String, String>();

		try {
			String query = url.getQuery();
			String[] pairs = query.split("&");
			for (String pair : pairs) {
				int idx = pair.indexOf("=");
				query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),	URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return query_pairs;
	}

	public static String NullChecker(String var) {

		if (var == null || var.equals("null")) {
			return "";
		} else {
			return var;
		}

	}

	public static String prepareRequest(HashMap<String, String> params){
		String request = "";
		try {
			for (String key : params.keySet()) {
				if(!key.equalsIgnoreCase(WebServiceUrls.WEBSERVICE_CODE)){
					////Log.i("----------------Key And Values----------",":"+key+" : "+params.get(key));
					request = request+key+"="+(URLEncoder.encode(params.get(key),"UTF-8"))+"&";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return request;
	}

	public static void _saveOrgPreferences(SharedPreferences pref,String key,String value){
		pref.edit().putString(key, value).commit();
	}

	public static String _getOrgPreferences(SharedPreferences pref,String key){
		try {
			String value = pref.getString(key, "DEV");

			return value;
		}catch (Exception e){
			return "";
		}
	}
	
	/*public static String prepareRequest(HashMap<String, String> params){
		String request = "";
		try {
			for (String key : params.keySet()) {
				if(!key.equalsIgnoreCase(WebServiceUrls.WEBSERVICE_CODE)){
					//Log.i("----------------Key And Values----------",":"+key+" : "+params.get(key));
				   request = request+key+"="+(URLEncoder.encode(params.get(key),"UTF-8"))+"&";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return request;
	}*/

	public static void displayLog(String Tag,String message){
		try {
			if(isLogEnabled){
				Log.i(Tag,message);
			}
		}catch (Exception e){

		}
	}
	public static String resizeAndCompressImageBeforeSend(Context context,String filePath,String fileName){
		final int MAX_IMAGE_SIZE = 700 * 1024; // max final file size in kilobytes

		// First decode with inJustDecodeBounds=true to check dimensions of image
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath,options);

		// Calculate inSampleSize(First we are going to resize the image to 800x800 image, in order to not have a big but very low quality image.
		//resizing the image will already reduce the file size, but after resizing we will check the file size and start to compress image
		options.inSampleSize = calculateInSampleSize(options, 800, 800);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		options.inPreferredConfig= Bitmap.Config.ARGB_8888;

		Bitmap bmpPic = BitmapFactory.decodeFile(filePath,options);


		int compressQuality = 100; // quality decreasing by 5 every loop.
		int streamLength;
		do{
			ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
			Log.d("compressBitmap", "Quality: " + compressQuality);
			bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream);
			byte[] bmpPicByteArray = bmpStream.toByteArray();
			streamLength = bmpPicByteArray.length;
			compressQuality -= 5;
			Log.d("compressBitmap", "Size: " + streamLength/1024+" kb");
		}while (streamLength >= MAX_IMAGE_SIZE);

		try {
			//save the resized and compressed file to disk cache
			Log.d("compressBitmap","cacheDir: "+context.getCacheDir());
			FileOutputStream bmpFile = new FileOutputStream(context.getCacheDir()+fileName);
			bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpFile);
			bmpFile.flush();
			bmpFile.close();
		} catch (Exception e) {
			Log.e("compressBitmap", "Error on saving file");
		}
		//return the path of resized and compressed file
		return  context.getCacheDir()+fileName;
	}



	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		String debugTag = "MemoryInformation";
		// Image nin islenmeden onceki genislik ve yuksekligi
		final int height = options.outHeight;
		final int width = options.outWidth;
		Log.d(debugTag,"image height: "+height+ "---image width: "+ width);
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		Log.d(debugTag,"inSampleSize: "+inSampleSize);
		return inSampleSize;
	}
	public static void CopyStream(InputStream is, OutputStream os)
	{
		final int buffer_size=1024;
		try
		{
			byte[] bytes=new byte[buffer_size];
			for(;;)
			{
				int count=is.read(bytes, 0, buffer_size);
				if(count==-1)
					break;
				os.write(bytes, 0, count);
			}
		}
		catch(Exception ex){}
	}
}
