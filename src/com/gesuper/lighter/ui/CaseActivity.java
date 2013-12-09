package com.gesuper.lighter.ui;

import java.util.ArrayList;

import com.gesuper.lighter.R;
import com.gesuper.lighter.model.CaseModel;
import com.gesuper.lighter.model.EventModel;
import com.gesuper.lighter.tools.CaseListAdapter;
import com.gesuper.lighter.tools.DbHelper;
import com.gesuper.lighter.tools.Utils;
import com.gesuper.lighter.widget.MoveableListView;
import com.gesuper.lighter.widget.MoveableListView.OnCreateNewItemListener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

public class CaseActivity extends Activity implements OnSharedPreferenceChangeListener {
	public static String TAG = "CaseActivity";
	
	private MoveableListView mCaseList;
	private ArrayList<CaseModel> mCaseArray;
	private CaseListAdapter mCaseAdapter;
	private DbHelper dbHelper;
	private int mEventId;
	
	private double baseH = 354, baseS = 100, baseL = 46;
	private double spanH = 49, spanL = 14;
	private double stepH = 7, stepL = 2;
	private int maxColorSpan = 7;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_case);
		this.initResource();
	}

	private void initResource() {
		// TODO Auto-generated method stub
		this.mCaseList = (MoveableListView) this.findViewById(R.id.case_list);
		this.mCaseArray = new ArrayList<CaseModel>();
		this.dbHelper = DbHelper.getInstance(this);
		Intent intent = this.getIntent();
		mEventId = intent.getIntExtra("EVENT_ID", -1);
		this.mCaseAdapter = new CaseListAdapter(this, R.layout.event_item, this.mCaseArray);
		this.mCaseList.setAdapter(mCaseAdapter);
		this.mCaseList.setOnTouchListener(this.mCaseList);
		this.mCaseList.setOnCreateNewItem(new OnCreateNewItemListener(){
			@Override
			public void createNewItem(int position, String content) {
				// TODO Auto-generated method stub
				CaseModel em = new CaseModel(CaseActivity.this, content, mEventId);
				long id = dbHelper.insert(DbHelper.TABLE.CASES, em.formatContentValuesWithoutId());
				em.setId((int) id);
				Log.v(TAG, content);
				switch(position){
				case OnCreateNewItemListener.CREATE_TOP:
					mCaseArray.add(0, em);
					break;
				case OnCreateNewItemListener.CREATE_BOTTOM:
					mCaseArray.add(em);
					break;
				default:
					mCaseArray.add(position, em);
					break;
				}
				mCaseAdapter.notifyDataSetChanged();
			}
		});
	}

	private void getCasesFromDb() {
		// TODO Auto-generated method stub
		this.mCaseArray.clear();
		Cursor cursor = dbHelper.query(DbHelper.TABLE.CASES, CaseModel.mColumns, CaseModel.EVENT_ID + " = " + mEventId, null, EventModel.SEQUENCE + " asc");
		while(cursor.moveToNext()){
			this.mCaseArray.add(new CaseModel(this, cursor));
		}
		this.mCaseAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onBackPressed(){
		Intent intent = new Intent(this,  MainActivity.class);
		intent.putExtra("CASE_COUNT", this.mCaseArray.size());
	    this.setResult(1, intent);
	    this.finish();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		this.getCasesFromDb();
	}

	@Override
	protected void onPause(){
		super.onPause();
		
		int index = 0;
		for(CaseModel model : mCaseArray){
			model.setSequence(index++);
			dbHelper.update(DbHelper.TABLE.CASES, model.formatContentValuesWithoutId(), 
					EventModel.ID + " = " + model.getId(), null);
		}
	}
	

	
	public int calculateColor(int o){
		int n = this.mCaseAdapter.getCount();
		double dH = stepH, dL = stepL;
		if (n > maxColorSpan && o != 0) {
            dH = spanH / n;
            dL = spanL / n;
        }
		return Utils.HSLToRGB(baseH + o * dH, (o == 0 ? baseS - 10 : baseS)/100, (baseL + o*dL)/100);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		if(key.equals("****")){
			//do someting
		}
	}
}
