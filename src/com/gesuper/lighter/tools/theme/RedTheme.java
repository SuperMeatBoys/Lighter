package com.gesuper.lighter.tools.theme;

import com.gesuper.lighter.tools.Utils;

public class RedTheme extends ThemeBase{

	private double baseH = 354, baseS = 100, baseL = 46;
	private double spanH = 49, spanL = 14;
	private double stepH = 7, stepL = 2;
	private int maxColorSpan = 7;
	
	@Override
	public int calculateColor(int n, int o) {
		// TODO Auto-generated method stub
		double dH = stepH, dL = stepL;
		if (n > maxColorSpan && o != 0) {
            dH = spanH / n;
            dL = spanL / n;
        }
		return Utils.HSLToRGB(baseH + o * dH, (o == 0 ? baseS - 10 : baseS)/100, (baseL + o*dL)/100);
	}

}
