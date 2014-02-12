package com.gesuper.lighter.tools;

import com.gesuper.lighter.model.CaseModel;
import com.gesuper.lighter.model.EventModel;
import com.gesuper.lighter.model.ItemModelBase;
import com.gesuper.lighter.tools.DbHelper.TABLE;
import com.gesuper.lighter.tools.theme.*;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class Utils {
	public static String TAG = "Utils";
	//change color from hsl to rgb
	public static int HSLToRGB(double h, double s, double l){
		double R,G,B;
	    double var_1, var_2;
	    if (s == 0)                       //HSL values = 0 ÷ 1
	    {
	        R = l * 255.0;                   //RGB results = 0 ÷ 255
	        G = l * 255.0;
	        B = l * 255.0;
	    }
	    else
	    {
	        if (l < 0.5) var_2 = l * (1 + s);
	        else         var_2 = (l + s) - (s * l);

	        var_1 = 2.0 * l - var_2;

	        R = 255.0 * Hue2RGB(var_1, var_2, h/360 + (1.0 / 3.0));
	        G = 255.0 * Hue2RGB(var_1, var_2, h/360);
	        B = 255.0 * Hue2RGB(var_1, var_2, h/360 - (1.0 / 3.0));
	    }
	    R = (int)((R- (int)R)>0.5?R+1:R);
	    G = (int)((G- (int)G)>0.5?G+1:G);
	    B = (int)((B- (int)B)>0.5?B+1:B);
		return (int) (0xFF000000 + (((int)R)<<16) + (((int)G)<<8) + B);
	}
	
	private static double Hue2RGB(double v1, double v2, double vH)
	{
	    if (vH < 0) vH += 1;
	    if (vH > 1) vH -= 1;
	    if (6.0 * vH < 1) return v1 + (v2 - v1) * 6.0 * vH;
	    if (2.0 * vH < 1) return v2;
	    if (3.0 * vH < 2) return v1 + (v2 - v1) * ((2.0 / 3.0) - vH) * 6.0;
	    return (v1);
	}

	public static int getEventCount(Context context) {
		// TODO Auto-generated method stub
		DbHelper dbHelper = DbHelper.getInstance(context);
		Cursor cursor = dbHelper.query(TABLE.EVENTS, null, null, null, null);
		return cursor.getCount();
	}

	public static ThemeBase getThemeById(int themeId) {
		// TODO Auto-generated method stub
		switch(themeId){
		case ThemeBase.GRAY:
			return new GreyTheme();
		case ThemeBase.RED:
			return new RedTheme();
		case ThemeBase.GREEN:
			return new GreenTheme();
		case ThemeBase.BLUE:
		default:
			return new BlueTheme();
		}
	}
	

    
    public static Bitmap getBitmapofView(ViewGroup view) {
		// TODO Auto-generated method stub
    	//EventItemView mHeadView = new EventItemView(this.context);
    	//this.measureView(mHeadView);
    	view.measure(
				MeasureSpec.makeMeasureSpec(view.getMeasuredWidth(), MeasureSpec.EXACTLY),
		        MeasureSpec.makeMeasureSpec(view.getMeasuredHeight(), MeasureSpec.EXACTLY));
    	view.layout(0, 0, view.getMeasuredWidth(),view.getMeasuredHeight());
		Bitmap b = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.RGB_565);
		Canvas c = new Canvas(b);
		view.draw(c);
		return b;
	}

	public static Bitmap getBitmapofView(ViewGroup view, int screenWidth) {
		// TODO Auto-generated method stub
		view.measure(
				MeasureSpec.makeMeasureSpec(screenWidth, MeasureSpec.EXACTLY),
		        MeasureSpec.makeMeasureSpec(view.getMeasuredHeight(), MeasureSpec.EXACTLY));
    	view.layout(0, 0, view.getMeasuredWidth(),view.getMeasuredHeight());
		Bitmap b = Bitmap.createBitmap(screenWidth, view.getMeasuredHeight(), Bitmap.Config.RGB_565);
		Canvas c = new Canvas(b);
		view.draw(c);
		return b;
	}
    
    public static Animation createFadeOutAnimation(AnimationListener listener){
    	AlphaAnimation animation = new AlphaAnimation(1.0F, 0.0F);
    	animation.setDuration(100);
    	animation.setAnimationListener(listener);
    	return animation;
    }
    
    public static Animation createFadeInAnimation(AnimationListener listener){
    	AlphaAnimation animation = new AlphaAnimation(0.0F, 1.0F);
    	animation.setDuration(100);
    	animation.setAnimationListener(listener);
    	return animation;
    }
    
    public static String getDeviceInfo(Context context) {
        try{
          org.json.JSONObject json = new org.json.JSONObject();
          android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
              .getSystemService(Context.TELEPHONY_SERVICE);
      
          String device_id = tm.getDeviceId();
          
          android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);
              
          String mac = wifi.getConnectionInfo().getMacAddress();
          json.put("mac", mac);
          
          if( TextUtils.isEmpty(device_id) ){
            device_id = mac;
          }
          
          if( TextUtils.isEmpty(device_id) ){
            device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
          }
          
          json.put("device_id", device_id);
          
          return json.toString();
        }catch(Exception e){
          e.printStackTrace();
        }
      return null;
    }
    
    public static boolean deleteEventByEventId(Context context, int eventId){
    	DbHelper dbHelper = DbHelper.getInstance(context);

		dbHelper.delete(DbHelper.TABLE.CASES, CaseModel.EVENT_ID + " = " + eventId, null);
		return dbHelper.delete(DbHelper.TABLE.EVENTS, EventModel.ID + " = " + eventId, null);
    }
    
	public static boolean deleteCasesByEventId(Context context, int itemId) {
		// TODO Auto-generated method stub
		DbHelper dbHelper = DbHelper.getInstance(context);
		return dbHelper.delete(DbHelper.TABLE.CASES, CaseModel.EVENT_ID + " = " + itemId, null);
	}

	public static boolean deleteCaseByCaseId(Context context, int itemId) {
		// TODO Auto-generated method stub
		DbHelper dbHelper = DbHelper.getInstance(context);
		return dbHelper.delete(DbHelper.TABLE.CASES, ItemModelBase.ID + " = " + itemId, null);
	}
	
	public static int getThemeColor(Context context, boolean isEvent, int n, int o){
		SharedPreferences mPerferences = PreferenceManager  
		        .getDefaultSharedPreferences(context);
		int themeId = mPerferences.getInt(isEvent?"event_theme_id":"case_theme_id", isEvent?0:2);  
		ThemeBase theme = Utils.getThemeById(themeId);
		Log.v(TAG, ""+n+" " + o + " "+ theme.calculateColor(n, o));
		return theme.calculateColor(n, o);
	}

	public static void saveItemContent(Context context, boolean isEvent, int itemId, String content) {
		// TODO Auto-generated method stub
		DbHelper dbHelper = DbHelper.getInstance(context);
		ContentValues cv = new ContentValues();
		cv.put(ItemModelBase.CONTENT, content);
		dbHelper.update(isEvent?DbHelper.TABLE.EVENTS:DbHelper.TABLE.CASES, cv, ItemModelBase.ID + " = " + itemId, null);
	}
}
