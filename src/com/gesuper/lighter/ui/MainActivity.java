package com.gesuper.lighter.ui;

import com.gesuper.lighter.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {
	
	private EventListView mEventList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.initResource();
	}

	private void initResource() {
		// TODO Auto-generated method stub
		this.mEventList = (EventListView) this.findViewById(R.id.event_list);
		this.mEventList.initResource();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
