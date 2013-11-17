package com.gesuper.lighter.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class CaseModel extends ItemModelBase{
	public static final String EVENT_ID = "event_id";
	
	public static String[] mColumns = new String[]{
		ItemModelBase.ID, ItemModelBase.CREATE_DATE,
		ItemModelBase.MODIFY_DATE, ItemModelBase.CONTENT,
		ItemModelBase.SEQUENCE, ItemModelBase.STATUS,
		CaseModel.EVENT_ID
	};
	
	private static final int EVENT_ID_COLUMN = 6;
	
	private int eventId;
	public CaseModel(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.eventId = -1;
	}

	public CaseModel(Context context, Cursor cursor) {
		super(context, cursor);
		// TODO Auto-generated constructor stub
		this.eventId = cursor.getInt(CaseModel.EVENT_ID_COLUMN);
	}
	
	public CaseModel(Context context, String content, int event){
		super(context, content);
		// TODO Auto-generated constructor stub
		this.eventId = event;
	}
	
	public int getEventId(){
		return this.eventId;
	}
	
	public ContentValues formatContentValues(){
		ContentValues cv = this.formatContentValuesWithoutId();
		cv.put(ID, this.id);
		return cv;
	}
	
	public ContentValues formatContentValuesWithoutId(){
		ContentValues cv = this.formatContentValuesWithoutId();
		cv.put(EVENT_ID, this.eventId);
		return cv;
	}
}
