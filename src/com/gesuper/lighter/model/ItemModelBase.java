package com.gesuper.lighter.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

public class ItemModelBase {

	public static final String ID = "id";
	public static final String CREATE_DATE = "create_date";
	public static final String MODIFY_DATE = "modify_date";
	public static final String CONTENT = "content";
	public static final String SEQUENCE = "sequence";
	public static final String STATUS = "finished";
	
	public static String[] mColumns = new String[]{
		ItemModelBase.ID, ItemModelBase.CREATE_DATE,
		ItemModelBase.MODIFY_DATE, ItemModelBase.CONTENT,
		ItemModelBase.SEQUENCE,ItemModelBase.STATUS
	};
	
	private static final int ID_COLUMN = 0;
	private static final int CREATE_DATE_COLUMN = 1;
	private static final int MODIFY_DATE_COLUMN = 2;
	private static final int CONTENT_COLUMN = 3;
	private static final int SEQUENCE_COLUMN = 4;
	private static final int STATUS_COLUMN = 5;
	
	private int id;
	private long createDate;
	private long modifyDate;
	private String content;
	private int sequence;
	private short finished;
	
	public ItemModelBase(Context context) {
		// TODO Auto-generated constructor stub
	}

	public ItemModelBase(Context context, Cursor cursor) {
		// TODO Auto-generated constructor stub
		this.id = cursor.getInt(ID_COLUMN);
		this.createDate = cursor.getLong(CREATE_DATE_COLUMN);
		this.modifyDate = cursor.getLong(MODIFY_DATE_COLUMN);
		this.content = cursor.getString(CONTENT_COLUMN);
		this.sequence = cursor.getInt(SEQUENCE_COLUMN);
		this.finished = cursor.getShort(STATUS_COLUMN);
	}
	
	public ItemModelBase(Context context, String content){
		this.id = -1;
		this.createDate = System.currentTimeMillis();
		this.modifyDate = System.currentTimeMillis();
		this.content = content;
		this.sequence = 0;
		this.finished = 0;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public void setContent(String c){
		this.content = c;
		this.modifyDate = System.currentTimeMillis();
	}

	public String getContent(){
		return this.content;
	}
	
	public void setSequence(int s){
		this.sequence = s;
	}
	
	public int getSequence(){
		return this.sequence;
	}
	
	public boolean isFinished(){
		return this.finished == 1 ? true : false;
	}
}
