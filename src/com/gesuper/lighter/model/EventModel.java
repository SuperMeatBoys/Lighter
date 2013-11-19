package com.gesuper.lighter.model;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class EventModel extends ItemModelBase{

	public static final String COUNT = "count";
	
	public static String[] mColumns = new String[]{
		ItemModelBase.ID, ItemModelBase.CREATE_DATE,
		ItemModelBase.MODIFY_DATE, ItemModelBase.CONTENT,
		ItemModelBase.SEQUENCE,ItemModelBase.STATUS,
		EventModel.COUNT
	};

	private static final int COUNT_COLUMN = 6;
	
	private int count;
	private List<CaseModel> cases;
	
	public EventModel(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.count  = 0;
		this.cases = new ArrayList<CaseModel>();
	}

	public EventModel(Context context, Cursor cursor) {
		super(context, cursor);
		// TODO Auto-generated constructor stub
		this.count = cursor.getInt(COUNT_COLUMN);
		this.cases = new ArrayList<CaseModel>(this.count);
	}
	
	public EventModel(Context context, String content){
		super(context, content);
		// TODO Auto-generated constructor stub
		this.count = 0;
		this.cases = new ArrayList<CaseModel>();
	}
	
	public List<CaseModel> getCases(){
		return cases;
	}
	
	public void addCase(CaseModel c){
		this.cases.add(c);
		this.count +=1;
	}
	
	public void removeCase(int i){
		this.cases.remove(i);
		this.count -= 1;
	}
	
	public ContentValues formatContentValues(){
		ContentValues cv = this.formatContentValuesWithoutId();
		cv.put(ID, this.id);
		return cv;
	}
	
	public ContentValues formatContentValuesWithoutId(){
		ContentValues cv = super.formatContentValuesWithoutId();
		cv.put(COUNT, this.count);
		return cv;
	}
}
