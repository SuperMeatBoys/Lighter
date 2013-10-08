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

	public EventModel(Context context) {
		// TODO Auto-generated constructor stub
	}

	public EventModel(Context context, Cursor cursor) {
		// TODO Auto-generated constructor stub
	}

}
