package com.gesuper.lighter.ui;

import java.util.ArrayList;

import com.gesuper.lighter.R;
import com.gesuper.lighter.model.EventModel;
import com.gesuper.lighter.tools.DbHelper;
import com.gesuper.lighter.tools.EventListAdapter;
import com.gesuper.lighter.widget.MoveableListView;

import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ListView;

public class EventListView extends MoveableListView {
	public static final String TAG = "EventListView";
	public static int PULL_TO_CREATE = 0;
	public static int RELEASE_TO_CREATE = 1;
	public static int CREATE_REFRESH_DONE = 0;
	private Context mContext;
	private ArrayList<EventModel> mEventListArray;
	private EventListAdapter mAdapter;

	private int status;
	protected MainActivity context;
	public EventListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}
	
	public EventListView(Context context, AttributeSet attrs){
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = (MainActivity) context;
		this.initResource();
	}
}
