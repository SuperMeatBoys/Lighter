package com.gesuper.lighter.view;

import com.gesuper.lighter.R;

import android.content.Context;
import android.widget.RelativeLayout;

public class MainView extends RelativeLayout{
	
	private Context mContext;
	private EventListView mEventList;
	
	public MainView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		inflate(context, R.layout.activity_main, this);
		this.mContext = context;
		this.initResourse();
	}
	
	public void initResourse(){
		this.mEventList = (EventListView) this.findViewById(R.id.event_list);
		this.mEventList.initResource();
	}
	
}
