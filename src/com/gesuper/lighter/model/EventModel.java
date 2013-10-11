package com.gesuper.lighter.model;

import android.content.Context;
import android.database.Cursor;

public class EventModel {

	public static final String ID = "id";
	public static final String CREATE_DATE = "create_date";
	public static final String MODIFY_DATE = "modify_date";
	public static final String CONTENT = "content";
	public static final String SEQUENCE = "sequence";
	
	public static String[] mColumns = new String[]{
		EventModel.ID, EventModel.CREATE_DATE,
		EventModel.MODIFY_DATE, EventModel.CONTENT,
		EventModel.SEQUENCE
	};
	private int id;
	private long createDate;
	private long modifyDate;
	private String content;
	private int sequence;
	public EventModel(Context context) {
		// TODO Auto-generated constructor stub
	}

	public EventModel(Context context, Cursor cursor) {
		// TODO Auto-generated constructor stub
	}
	
	public EventModel(Context context, String content){
		this.id = -1;
		this.createDate = System.currentTimeMillis();
		this.modifyDate = System.currentTimeMillis();
		this.content = content;
		this.sequence = 0;
	}
	
	public String getContent(){
		return this.content;
	}
}
