package com.gesuper.lighter.view;

import java.util.ArrayList;

import com.gesuper.lighter.R;
import com.gesuper.lighter.model.EventModel;
import com.gesuper.lighter.tools.DbHelper;
import com.gesuper.lighter.tools.EventListAdapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.ListView;

public class EventListView extends ListView {
	public static final String TAG = "EventListView";
	public static int PULL_TO_CREATE = 0;
	public static int RELEASE_TO_CREATE = 1;
	public static int CREATE_REFRESH_DONE = 0;
	private Context mContext;
	private ArrayList<EventModel> mEventListArray;
	private EventListAdapter mAdapter;

	private int status;
	public EventListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}
	
	public void initListView(){
		//hide scroll bar
		this.setVerticalScrollBarEnabled(true);
//		this.mGesture = new MyGesture(this.getContext(), this, null);
//		this.mGesture.setIsLongpressEnabled(true);
//		this.mTouchSlop = ViewConfiguration.get(this.getContext()).getScaledTouchSlop();
//		this.mHeight = this.getHeight();
		this.status = CREATE_REFRESH_DONE;
//		this.mDragItemView = null;
//		this.mLongPress = false;
//		this.mScroll = 0;
//		this.initAdapter();
//		this.setOnTouchListener(this);
//		this.setSmoothScrollbarEnabled(true);
//		this.mHeadViewHandler.sendEmptyMessageDelayed(0, 100);
	}

	public void initResource() {
		// TODO Auto-generated method stub
		mEventListArray  = new ArrayList<EventModel>();
		EventModel mAlertItem;
		DbHelper dbHelper = DbHelper.getInstance(this.getContext());
		Cursor cursor = dbHelper.query(DbHelper.TABLE.EVENTS, EventModel.mColumns, null, null, EventModel.SEQUENCE + " asc");
		while(cursor.moveToNext()){
			mAlertItem = new EventModel(this.getContext(), cursor);
			mEventListArray.add(mAlertItem);
		}
		this.mEventListArray.add(0, new EventModel(this.getContext()));
		this.mAdapter = new EventListAdapter(this.getContext(), R.layout.event_item,
				mEventListArray);
		Log.d(TAG, "set adapter");
		this.setAdapter(this.mAdapter);
	}
}
