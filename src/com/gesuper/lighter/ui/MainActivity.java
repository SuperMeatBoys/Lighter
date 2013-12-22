package com.gesuper.lighter.ui;

import java.util.ArrayList;
import java.util.List;

import com.gesuper.lighter.R;
import com.gesuper.lighter.model.EventModel;
import com.gesuper.lighter.tools.*;
import com.gesuper.lighter.tools.theme.ThemeBase;
import com.gesuper.lighter.widget.EventItemView;
import com.gesuper.lighter.widget.MoveableListView;
import com.gesuper.lighter.widget.MoveableListView.OnCreateNewItemListener;
import com.gesuper.lighter.widget.MoveableListView.onItemClickedListener;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.graphics.Point;
import android.util.Log;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.WindowManager;

public class MainActivity extends Activity implements OnSharedPreferenceChangeListener {
	public static final String TAG = "MainActivity";
	private MoveableListView mEventList;
	private EventListAdapter mEventAdapter;
	private List<EventModel> mEventArray;
	private DbHelper dbHelper;
	private int currentItemPosition;
	
	private ThemeBase theme;
	public static int screenWidth;
	public static int screenHeight;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ActivityHelper.getInstance().setMain(this);
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
				currentItemPosition = position;
				intent.putExtra("EVENT_ID", mEventArray.get(position - 1).getId());
				overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
				MainActivity.this.startActivityForResult(intent, 1);
			}
		});
		
		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		Point p = new Point();
	    wm.getDefaultDisplay().getSize(p);
	    screenWidth = p.x;
	    screenHeight = p.y; 
	    Log.v(TAG, "width: " + screenWidth + " height: " + screenHeight);
	    
		SharedPreferences mPerferences = PreferenceManager  
		        .getDefaultSharedPreferences(this);
		int themeId = mPerferences.getInt("theme_id", 0);  
		this.theme = Utils.getThemeById(themeId);
	}

	public ViewGroup getLayoutView(){
		return this.mEventList;
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		int caseCount = data.getIntExtra("CASE_COUNT", -1);
		if(caseCount < 0) return ;
		mEventArray.get(this.currentItemPosition - 1).setCount(caseCount);
		((EventItemView) this.mEventList.getChildAt(this.currentItemPosition)).updateCount(caseCount);
		
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
		Log.v(TAG, "onPause " + index);
	}
	
	@Override
	protected void onStop(){
		super.onStop();
	}

	public int calculateColor(int o){
		int n = this.mEventAdapter.getCount();
		return theme.calculateColor(n, o);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		if(key.equals("****")){
			//do someting
		}
		
		//从应用的任意处获得Preferences
		SharedPreferences mPerferences = PreferenceManager  
		        .getDefaultSharedPreferences(this);  

		//从Preferences中获得一个值，如果不存在则值为null
		String loginName = mPerferences.getString("name", null);  

		if(loginName == null){
		    Log.v("Preferences", "User not login");
		}

		//获得Editor编辑Preferences的值
		SharedPreferences.Editor mEditor = mPerferences.edit();  
		          
		mEditor.putString("name", "admin");  
		//将更新后的值提交
		mEditor.commit();
	}

	
}
