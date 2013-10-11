package com.gesuper.lighter.ui;

import java.util.ArrayList;
import java.util.List;

import com.gesuper.lighter.R;
import com.gesuper.lighter.model.EventModel;
import com.gesuper.lighter.tools.EventListAdapter;
import com.gesuper.lighter.widget.MoveableListView;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;

public class MainActivity extends Activity {
	public static final String TAG = "MainActivity";
	private MoveableListView mEventList;
	private EventListAdapter mEventAdapter;
	private List<EventModel> mEventArray;
	
	public int screenWidth;
	public int screenHeight;
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
		
		String[] exampls = this.getResources().getStringArray(R.array.example_adapter_value);
		for(String s : exampls){
			EventModel em = new EventModel(this, s);
			this.mEventArray.add(em);
		}
		this.mEventAdapter = new EventListAdapter(this, R.layout.event_item, this.mEventArray);
		this.mEventList.setAdapter(mEventAdapter);
		this.mEventList.setOnTouchListener(this.mEventList);
		
		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
	    screenWidth = wm.getDefaultDisplay().getWidth();
	    screenHeight = wm.getDefaultDisplay().getHeight();
	    Log.v(TAG, "width: " + screenWidth + " height: " + screenHeight);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
