package com.gesuper.lighter.ui;

import java.util.ArrayList;
import java.util.List;

import com.gesuper.lighter.R;
import com.gesuper.lighter.model.EventModel;
import com.gesuper.lighter.tools.DbHelper;
import com.gesuper.lighter.tools.EventListAdapter;
import com.gesuper.lighter.widget.MoveableListView;
import com.gesuper.lighter.widget.MoveableListView.OnCreateNewItemListener;
import com.gesuper.lighter.widget.MoveableListView.onItemClickedListener;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;

public class MainActivity extends Activity {
	public static final String TAG = "MainActivity";
	private MoveableListView mEventList;
	private EventListAdapter mEventAdapter;
	private List<EventModel> mEventArray;
	private DbHelper dbHelper;
	
	public static int screenWidth;
	public static int screenHeight;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.initResource();
	}

	private void initResource() {
		// TODO Auto-generated method stub
		this.mEventList = (MoveableListView) this.findViewById(R.id.event_list);
		this.mEventArray = new ArrayList<EventModel>();
		
		this.dbHelper =  DbHelper.getInstance(this);
		this.mEventAdapter = new EventListAdapter(this, R.layout.event_item, this.mEventArray);
		this.mEventList.setAdapter(mEventAdapter);
		this.getEventFromDb();
		this.mEventList.setOnTouchListener(this.mEventList);
		this.mEventList.setOnCreateNewItem(new OnCreateNewItemListener(){
			@Override
			public void createNewItem(int position, String content) {
				// TODO Auto-generated method stub
				EventModel em = new EventModel(MainActivity.this, content);
				long id = dbHelper.insert(DbHelper.TABLE.EVENTS, em.formatContentValuesWithoutId());
				em.setId((int) id);
				Log.v(TAG, content);
				switch(position){
				case OnCreateNewItemListener.CREATE_TOP:
					mEventArray.add(0, em);
					break;
				case OnCreateNewItemListener.CREATE_BOTTOM:
					mEventArray.add(em);
					break;
				default:
					mEventArray.add(position, em);
					break;
				}
				mEventAdapter.notifyDataSetChanged();
			}
		});
		this.mEventList.setOnItemClickedListener(new onItemClickedListener(){
			@Override
			public void onItemClicked(int position) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this, CaseActivity.class);
				MainActivity.this.startActivity(intent);
			}
		});
		
		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		Point p = new Point();
	    wm.getDefaultDisplay().getSize(p);
	    screenWidth = p.x;
	    screenHeight = p.y; 
	    Log.v(TAG, "width: " + screenWidth + " height: " + screenHeight);
	}

	private void getEventFromDb(){
		this.mEventArray.clear();
		Cursor cursor = dbHelper.query(DbHelper.TABLE.EVENTS, EventModel.mColumns, null, null, EventModel.SEQUENCE + " asc");
		while(cursor.moveToNext()){
			this.mEventArray.add(new EventModel(this, cursor));
		}
		this.mEventAdapter.notifyDataSetChanged();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		this.getEventFromDb();
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		
		int index = 0;
		for(EventModel model : mEventArray){
			model.setSequence(index++);
			dbHelper.update(DbHelper.TABLE.EVENTS, model.formatContentValuesWithoutId(), 
					EventModel.ID + " = " + model.getId(), null);
		}
		Log.v(TAG, "onPause" + index);
	}
	
	@Override
	protected void onStop(){
		super.onStop();
	}

}
