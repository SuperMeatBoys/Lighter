package com.gesuper.lighter.ui;

import com.gesuper.lighter.R;
import com.gesuper.lighter.widget.MoveableListView;

import android.content.Context;
import android.util.AttributeSet;

public class EventListView extends MoveableListView {
	protected MainActivity context;
	public EventListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public EventListView(Context context, AttributeSet attrs){
		super(context, attrs, R.layout.event_head, R.layout.event_foot, true);
		// TODO Auto-generated constructor stub
	}
}
