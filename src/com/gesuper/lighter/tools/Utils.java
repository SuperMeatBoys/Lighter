package com.gesuper.lighter.tools;

import com.gesuper.lighter.tools.DbHelper.TABLE;
import com.gesuper.lighter.tools.theme.*;

import android.content.Context;
import android.database.Cursor;

public class Utils {
	
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

	        R = 255.0 * Hue2RGB(var_1, var_2, h + (1.0 / 3.0));
	        G = 255.0 * Hue2RGB(var_1, var_2, h);
	        B = 255.0 * Hue2RGB(var_1, var_2, h - (1.0 / 3.0));
	    }
		return (int) ((((int)R)<<16) + (((int)G)<<8) + B);
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
}
