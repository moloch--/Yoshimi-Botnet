package com.redactedlabs.yoshimi;

import java.util.UUID;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLite extends SQLiteOpenHelper {

	private static final String TAG = "SQLite";
	private static final String DATABASE_NAME = "yohshimi.db";
	private static final int DATABASE_VERSION = 2;
	private static final String CREATE_STORAGE = "CREATE TABLE storage(id INTEGER PRIMARY KEY, name TEXT, value TEXT)";
	private static final String CREATE_VERSION = "CREATE TABLE version(id INTEGER PRIMARY KEY, name TEXT, value TEXT)";
	private String uuid;

	public SQLite(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		Log.d(TAG, "Initializing database ...");
		database.execSQL(CREATE_STORAGE);
		database.execSQL(CREATE_VERSION);
		uuid = UUID.randomUUID().toString();
		Log.d(TAG, "Generated uuid: " + uuid);
		database.execSQL("INSERT INTO storage VALUES (NULL, ?, ?)", new String[] { "uuid", uuid });	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/* Do nothing */
	}

} 