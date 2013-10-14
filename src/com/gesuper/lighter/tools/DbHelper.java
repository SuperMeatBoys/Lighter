package com.gesuper.lighter.tools;

import com.gesuper.lighter.model.CaseModel;
import com.gesuper.lighter.model.EventModel;
import com.gesuper.lighter.model.ItemModelBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper{

	public static final String TAG = "DbHelperModel";
	public static final String DB_NAME = "alert.db";
	
	private static final int DB_VERSION = 4;
	
	public interface TABLE {
		public static final String EVENTS = "events";
		public static final String CASES = "cases";
	}
	
	private static final String CREATE_EVENT_TABLE_SQL = 
			"CREATE TABLE " + TABLE.EVENTS + " (" +
					ItemModelBase.ID + " INTEGER PRIMARY KEY," +
					ItemModelBase.CREATE_DATE + " INTEGER NOT NULL DEFAULT 0," +
					ItemModelBase.MODIFY_DATE + " INTEGER NOT NULL DEFAULT 0," +
					ItemModelBase.CONTENT + " TEXT��NOT NULL DEFAULT ''," +
					ItemModelBase.SEQUENCE + " INTEGER NOT NULL DEFAULT 0," +
					ItemModelBase.STATUS + " INTEGER NOT NULL DEFAULT 0," +
					EventModel.COUNT + " INTEGER NOT NULL DEFAULT 0" +
			")";
	
	private static final String CREATE_CASE_TABLE_SQL = 
			"CREATE TABLE " + TABLE.CASES + " (" +
					ItemModelBase.ID + " INTEGER PRIMARY KEY," +
					ItemModelBase.CREATE_DATE + " INTEGER NOT NULL DEFAULT 0," +
					ItemModelBase.MODIFY_DATE + " INTEGER NOT NULL DEFAULT 0," +
					ItemModelBase.CONTENT + " TEXT��NOT NULL DEFAULT ''," +
					ItemModelBase.SEQUENCE + " INTEGER NOT NULL DEFAULT 0," +
					ItemModelBase.STATUS + " INTEGER NOT NULL DEFAULT 0," +
					CaseModel.EVENT_ID + " INTEGER NOT NULL DEFAULT 0" +
			")";
	private SQLiteDatabase db;
	
	public static DbHelper instance;
	
	public DbHelper(Context context){
		super(context, DB_NAME, null, DB_VERSION);
		this.db = this.getWritableDatabase();
	}

	public static DbHelper getInstance(Context context){
		if(instance == null){
			instance = new DbHelper(context);
		}
		return  instance;
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

	private void createTableAlerts(SQLiteDatabase db) {
		db.execSQL(CREATE_EVENT_TABLE_SQL);
		db.execSQL(CREATE_CASE_TABLE_SQL);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		createTableAlerts(db);
	}
	
	public void runSql(String sql){
		db = getWritableDatabase();
		db.execSQL(sql);
	}
	
	public long insert(String table, ContentValues values){
		if(this.db.isOpen() && this.db.isReadOnly()){
			db.close();
			db = this.getWritableDatabase();
		}
		long id = db.insert(table, null, values);
		return id;
	}
	
	public Cursor query(String table, String[] columns, String selection,
			String[] selectionArgs, String orderBy){
		if(this.db.isOpen() && !this.db.isReadOnly()){
			db.close();
			db = this.getReadableDatabase();
		}
		Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, orderBy);
		return cursor;
	}
	
	public boolean update(String table, ContentValues values, String whereClause, String[] whereArgs){
		if(this.db.isOpen() && this.db.isReadOnly())
			db = this.getWritableDatabase();
		int rows = db.update(table, values, whereClause, whereArgs);
		if(rows < 0)
			return false;
		return true;
	}
	
	public boolean delete(String table, String whereClause, String[] whereArgs){
		if(this.db.isOpen() && this.db.isReadOnly())
			db = this.getWritableDatabase();
		int rows = db.delete(table, whereClause, whereArgs);
		if(rows<1)
			return false;
		return true;
	}
	
	public Cursor getAll(){
		return null;
	}
	
	public void close(){
		this.db.close();
	}
}

