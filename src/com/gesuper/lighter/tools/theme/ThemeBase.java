package com.gesuper.lighter.tools.theme;

public abstract class ThemeBase {
	public static final int BLUE = 0;
	public static final int GRAY = 1;
	public static final int RED = 2;
	public static final int GREEN = 3;
	
	public abstract int calculateColor(int n, int o);
}
