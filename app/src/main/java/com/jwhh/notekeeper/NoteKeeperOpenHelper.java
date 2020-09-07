package com.jwhh.notekeeper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import static com.jwhh.notekeeper.NoteKeeperDatabaseContract.CourseInfoEntry;
import static com.jwhh.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;

public class NoteKeeperOpenHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "NoteKeeper.db";
	//since we are upgrading the version we need to change the constant
//	public static final int DATABASE_VERSION = 1;
	public static final int DATABASE_VERSION = 2;

	public NoteKeeperOpenHelper(@Nullable Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CourseInfoEntry.SQL_CREATE_TABLE);
		db.execSQL(NoteInfoEntry.SQL_CREATE_TABLE);

		db.execSQL(CourseInfoEntry.SQL_CREATE_INDEX1);
		db.execSQL(NoteInfoEntry.SQL_CREATE_INDEX1);

		DatabaseDataWorker databaseDataWorker = new DatabaseDataWorker(db);
		databaseDataWorker.insertCourses();
		databaseDataWorker.insertSampleNotes();
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		super.onDowngrade(db, oldVersion, newVersion);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < 2){// adding 2 litrally so we can handle each upgrade differently
			db.execSQL(CourseInfoEntry.SQL_CREATE_INDEX1);
			db.execSQL(NoteInfoEntry.SQL_CREATE_INDEX1);
		}

	}
}
