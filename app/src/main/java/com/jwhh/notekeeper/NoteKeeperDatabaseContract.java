package com.jwhh.notekeeper;

import android.provider.BaseColumns;

public final class NoteKeeperDatabaseContract {
	// starting SQL here

	private NoteKeeperDatabaseContract() {
	}

	//  A COLUMN CAN HAVE A NAME , A STORAGE CLASS AND A CONSTRAINT
	//STORAGE CLASS IS OPTIONAL IF HELPS TO GUIDE THE PREFERRED FORM WHEN STORING DATA IN THAT AFFININITY
	/*
	BY DEFALT IT IS BLOB IT ARRAGES IT AS IT COMES .. NO CHANGES ,, INT AS INT STRING AS STRING
	IF TYPE IS TEXT  it would prefare a text type so all string , int doulble etc are stored as text
	type INTEGER .. .stores all as an integer  ... therefore "123" == 123  and double to int
	type REAL all is real number "123"==123.0 , 123==123.0
	type NUMERIC  it is just like integer but it can keep real numbers like 123.1
	do note this storage option does not affect the data entered as "abc" is still abc

	for constraint to prevent the data we can use many but some is NOTNULL and UNIQUE

	PRIMARY KEY CONSTRAINT ... it is used to idetify the table data easily
	 */

	public static final class CourseInfoEntry implements BaseColumns {
		public static final String TABLE_NAME = "course_info";
		public static final String COLUMN_COURSE_ID = "course_id";
		public static final String COLUMN_COURSE_TITLE = "course_title";

		// CREATE UNIQUE INDEX ... TO ENFORE UNIQUENESS
		// CREATE INDEX nameofindex
		// CREATE INDEX course_info_index1 ON course_info (course_title,othercolumn1,othercolumn2)
		// CREATE INDEX course_info_index1 ON course_info (course_title)
		public static final String INDEX1 = TABLE_NAME+"_index1";
		public static  final String SQL_CREATE_INDEX1= "CREATE INDEX "+INDEX1+" ON "+TABLE_NAME+"("+COLUMN_COURSE_TITLE+")";


		//sql command to create table   \
		// sql command    table name      list of columns
		//CREATE TABLE course-info (course-id,course-title)
		// it is not a must the sql key be in capital but we use it as that
		public static final String SQL_CREATE_TABLE = "CREATE TABLE "
				+ TABLE_NAME + " ("
				+ _ID + " INTEGER PRIMARY KEY, "
				+ COLUMN_COURSE_ID + " TEXT UNIQUE NOT NULL, "
				+ COLUMN_COURSE_TITLE + " TEXT NOT NULL)";

		public static final String getQName(String columnName) {
			return TABLE_NAME + "." + columnName;
		}

	}

	public static final class NoteInfoEntry implements BaseColumns {
		public static final String TABLE_NAME = "note_info";
		public static final String COLUMN_COURSE_ID = "course_id";
		public static final String COLUMN_NOTE_TITLE = "note_title";
		public static final String COLUMN_NOTE_TEXT = "note_text";


		public static final String INDEX1 = TABLE_NAME+"_index1";
		public static  final String SQL_CREATE_INDEX1= "CREATE INDEX "+INDEX1+" ON "+TABLE_NAME+"("+COLUMN_NOTE_TITLE+")";


		//sql command to create table   \
		// sql command    table name      list of columns
		//CREATE TABLE course-info (course-id,course-title)
		// it is not a must the sql key be in capital but we use it as that
		public static final String SQL_CREATE_TABLE = "CREATE TABLE " +
				TABLE_NAME + " (" +
				_ID + " INTEGER PRIMARY KEY, " +
				COLUMN_NOTE_TITLE + " TEXT NOT NULL, " +
				COLUMN_NOTE_TEXT + " TEXT, " +
				COLUMN_COURSE_ID + " TEXT NOT NULL)";
		public static final String getQName(String columnName) {
			return TABLE_NAME + "." + columnName;
		}

	}

}
