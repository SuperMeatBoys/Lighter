package com.gesuper.lighter.ui;

import com.gesuper.lighter.R;
import com.gesuper.lighter.tools.Utils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SetActivity extends Activity{

	private LinearLayout mEventLinear;
	private TextView mEventCount;
	private TextView mThemes;
	private TextView mAuthor;
	private TextView mAbouts;
	private TextView mDonate;
	
	protected void onCreate(Bundle savedInstanceState){
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.activity_set);
		this.initResource();
	}

	private void initResource() {
		// TODO Auto-generated method stub
		this.mEventLinear = (LinearLayout)this.findViewById(R.id.set_evnet_linear);
		this.mEventCount = (TextView)this.findViewById(R.id.set_event_count);
		this.mThemes = (TextView)this.findViewById(R.id.set_themes);
		this.mAuthor = (TextView)this.findViewById(R.id.set_abouts);
		this.mDonate = (TextView)this.findViewById(R.id.set_donate);
		this.mEventCount.setText(String.valueOf(Utils.getEventCount(this)));
		
		this.mEventLinear.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SetActivity.this.showEvents();
			}
		});
		
		this.mThemes.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SetActivity.this.showThemes();
			}
		});
		
		this.mAuthor.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SetActivity.this.showAuthor();
			}
		});
		
		this.mAbouts.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SetActivity.this.showAbouts();
			}
		});
		
		this.mDonate.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SetActivity.this.showDonate();
			}
		});
		
		
	}

	protected void showEvents() {
		// TODO Auto-generated method stub
		this.finish();
	}

	protected void showThemes() {
		// TODO Auto-generated method stub
		
	}

	protected void showAuthor() {
		// TODO Auto-generated method stub
		
	}
	protected void showAbouts() {
		// TODO Auto-generated method stub
		
	}

	protected void showDonate() {
		// TODO Auto-generated method stub
		
	}
}
