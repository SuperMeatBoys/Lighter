package com.gesuper.lighter.tools.theme;

import com.gesuper.lighter.tools.Utils;

public class BlueTheme extends ThemeBase{
	private double baseH = 212, baseS = 93, baseL = 53;
	private double spanH = -12.5, spanS = 5, spanL = 12.5;
	private double stepH = -2.5, stepS = 1, stepL = 2.5;
	private int maxColorSpan = 6;
	
	public int calculateColor(int n, int o){
		double dH = stepH, dS = stepS, dL = stepL;
		if(n > this.maxColorSpan){
			dH = spanH / n;
			dS = spanS / n;
			dL = spanL / n;
		}
		return Utils.HSLToRGB(baseH + o * dH, Math.min(100, baseS + o * dS)/100, Math.min(100, baseL + o * dL)/100);
	}
}
