package com.gesuper.lighter.tools;

import com.gesuper.lighter.ui.MainActivity;
import com.gesuper.lighter.ui.SetActivity;

public class ActivityHelper {
	private MainActivity mMain;
	private SetActivity mSet;
	
	private static ActivityHelper instance;
	
	private ActivityHelper(){
		this.mMain = null;
		this.mSet = null;
	}
	
	public static ActivityHelper getInstance(){
		if(instance == null){
			instance = new ActivityHelper();
		}
		return instance;
	}
	
	public void  setMain(MainActivity main){
		this.mMain = main;
	}
	
	public MainActivity getMain(){
		return this.mMain;
	}
	
	public void  setSetting(SetActivity set){
		this.mSet = set;
	}
	
	public SetActivity getSetting(){
		return this.mSet;
	}
}
