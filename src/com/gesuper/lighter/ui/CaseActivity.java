package com.gesuper.lighter.ui;

import java.util.ArrayList;

import com.gesuper.lighter.R;
import com.gesuper.lighter.model.CaseModel;
import com.gesuper.lighter.model.EventModel;
import com.gesuper.lighter.tools.CaseListAdapter;
import com.gesuper.lighter.tools.DbHelper;
import com.gesuper.lighter.widget.MoveableListView;
import com.gesuper.lighter.widget.MoveableListView.OnCreateNewItemListener;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;

public class CaseActivity extends Activity{
	
	private MoveableListView mCaseList;
	private ArrayList<CaseModel> mCaseArray;
	private CaseListAdapter mCaseAdapter;
	private DbHelper dbHelper;

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
		
		String[] exampls = this.getResources().getStringArray(R.array.example_adapter_value);
		for(String s : exampls){
			CaseModel em = new CaseModel(this, s, -1);
			this.mCaseArray.add(em);
		}
		this.mCaseAdapter = new CaseListAdapter(this, R.layout.event_item, this.mCaseArray);
		this.mCaseList.setAdapter(mCaseAdapter);
		this.mCaseList.setOnTouchListener(this.mCaseList);
		this.mCaseList.setOnCreateNewItem(new OnCreateNewItemListener(){
			@Override
			public void createNewItem(int position, String content) {
				// TODO Auto-generated method stub
				CaseModel em = new CaseModel(CaseActivity.this, content, -1);
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
		Cursor cursor = dbHelper.query(DbHelper.TABLE.CASES, CaseModel.mColumns, null, null, EventModel.SEQUENCE + " asc");
		cursor.moveToFirst();
		do{
			this.mCaseArray.add(new CaseModel(this, cursor));
			cursor.moveToNext();
		}while(cursor.moveToNext());
		this.mCaseAdapter.notifyDataSetChanged();
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
}
