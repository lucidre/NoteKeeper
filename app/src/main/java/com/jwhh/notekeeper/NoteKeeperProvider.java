package com.jwhh.notekeeper;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.jwhh.notekeeper.NoteKeeperDatabaseContract.CourseInfoEntry;
import static com.jwhh.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;
import static com.jwhh.notekeeper.NoteKeeperProviderContract.AUTHORITY;
import static com.jwhh.notekeeper.NoteKeeperProviderContract.Courses;
import static com.jwhh.notekeeper.NoteKeeperProviderContract.CoursesIdColumns;
import static com.jwhh.notekeeper.NoteKeeperProviderContract.Notes;

public class NoteKeeperProvider extends ContentProvider {


	public static final int COURSES = 0;
	public static final int NOTES = 1;
	public static final int NOTES_EXPANDED = 2;
	public static final int NOTE_ROW = 3;
	public static final String MIME_VENDOR_TYPE = "vnd" + AUTHORITY + ".";  //we are using the vnd in front of the authority to show that it is an application mime type .....
	private static final int COURSES_ROW = 4;
	private static final int NOTES_EXPANDED_ROW = 5;
	private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	static {

		sUriMatcher.addURI(AUTHORITY, Courses.PATH, COURSES); // it would check if it has both
		sUriMatcher.addURI(AUTHORITY, Notes.PATH, NOTES);
		sUriMatcher.addURI(AUTHORITY, Notes.PATH_EXPANDED, NOTES_EXPANDED);
		sUriMatcher.addURI(AUTHORITY, Notes.PATH + "/#", NOTE_ROW); //# is integer value
		sUriMatcher.addURI(AUTHORITY, Courses.PATH + "/#", COURSES_ROW);
		sUriMatcher.addURI(AUTHORITY, Notes.PATH_EXPANDED + "/#", NOTES_EXPANDED_ROW);
	}

	private NoteKeeperOpenHelper mDbOpenHelper;

	public NoteKeeperProvider() {
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		long rowId = -1;
		String rowSelection = null;
		String[] rowSelectionArgs = null;
		int nRows = -1;
		SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();

		int uriMatch = sUriMatcher.match(uri);
		switch (uriMatch) {
			case COURSES:
				nRows = db.delete(CourseInfoEntry.TABLE_NAME, selection, selectionArgs);
				break;
			case NOTES:
				nRows = db.delete(NoteInfoEntry.TABLE_NAME, selection, selectionArgs);
				break;
			case NOTES_EXPANDED:
				// throw exception saying that this is a read-only table
			case COURSES_ROW:
				rowId = ContentUris.parseId(uri); // if this is it it would have an appended uri to the end
				rowSelection = CourseInfoEntry._ID + " = ?";
				rowSelectionArgs = new String[]{Long.toString(rowId)};
				nRows = db.delete(CourseInfoEntry.TABLE_NAME, rowSelection, rowSelectionArgs);
				break;
			case NOTE_ROW:
				rowId = ContentUris.parseId(uri);//
				rowSelection = NoteInfoEntry._ID + " = ?";
				rowSelectionArgs = new String[]{Long.toString(rowId)};
				nRows = db.delete(NoteInfoEntry.TABLE_NAME, rowSelection, rowSelectionArgs);
				break;
			case NOTES_EXPANDED_ROW:
				// throw exception saying that this is a read-only table
				break;
		}

		return nRows;

	}

	@Override
	public String getType(Uri uri) {
		String mimeType = null;
		int uriMatch = sUriMatcher.match(uri);
		switch (uriMatch) {
			case COURSES:
				//vnd.android.cursor.dir/vnd.com.jwhh.notekeeper.provider.courses
				mimeType = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + MIME_VENDOR_TYPE + Courses.PATH; // becauuse there are many files in it
				break;
			case NOTES:
				//vnd.android.cursor.dir/vnd.com.jwhh.notekeeper.provider.courses
				mimeType = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + MIME_VENDOR_TYPE + Notes.PATH;
				break;
			case NOTES_EXPANDED:
				//vnd.android.cursor.dir/vnd.com.jwhh.notekeeper.provider.courses
				mimeType = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + MIME_VENDOR_TYPE + Notes.PATH_EXPANDED;
				break;
			case NOTE_ROW:
				mimeType = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + MIME_VENDOR_TYPE + Notes.PATH;// becauser we are getting just one row it is singular
				break;
			case NOTES_EXPANDED_ROW:
				mimeType = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + MIME_VENDOR_TYPE + Notes.PATH_EXPANDED;
				break;

		}


		return mimeType;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
		long rowId;
		Uri rowUri = null;
		int UriMatch = sUriMatcher.match(uri);
		switch (UriMatch) {
			case NOTES:
				rowId = db.insert(NoteInfoEntry.TABLE_NAME, null, values);
				rowUri = ContentUris.withAppendedId(Notes.CONTENT_URI, rowId);
				break;
			case COURSES:
				rowId = db.insert(CourseInfoEntry.TABLE_NAME, null, values);
				rowUri = ContentUris.withAppendedId(Courses.CONTENT_URI, rowId);
				break;
			case NOTES_EXPANDED:
				throw new UnsupportedOperationException("You cant get that data :)");

		}

		return rowUri;
	}

	@Override
	public boolean onCreate() {

		mDbOpenHelper = new NoteKeeperOpenHelper(getContext()); // dont use this for content use getcontent


		return true;// so it can know it has been sucessfully created
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Cursor cursor = null;
		SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

		int match = sUriMatcher.match(uri);
		switch (match) {
			case COURSES:
				cursor = db.query(CourseInfoEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
				break;
			case NOTES:
				cursor = db.query(NoteInfoEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
				break;
			case NOTES_EXPANDED:
				cursor = NoteExpandedQuery(projection, selection, selectionArgs, sortOrder, db);
				break;

			case NOTE_ROW:
				long rowId = ContentUris.parseId(uri);
				String rowSelection = NoteInfoEntry._ID + " = ?";
				String[] rowSelectionArgs = new String[]{Long.toString(rowId)};
				cursor = db.query(NoteInfoEntry.TABLE_NAME, projection, rowSelection, rowSelectionArgs, null, null, null);

				break;
			case NOTES_EXPANDED_ROW:
				rowId = ContentUris.parseId(uri);
				rowSelection = NoteInfoEntry.getQName(NoteInfoEntry._ID) + " = ?";
				rowSelectionArgs = new String[]{Long.toString(rowId)};
				cursor = NoteExpandedQuery(projection, rowSelection, rowSelectionArgs, null, db);
				break;
		}


		return cursor;
	}

	private Cursor NoteExpandedQuery(String[] projection, String selection, String[] selectionArgs, String sortOrder, SQLiteDatabase db) {
		String[] columns = new String[projection.length];
		for (int i = 0; i < projection.length; i++) {
			columns[i] = projection[i].equals(BaseColumns._ID) || projection[i].equalsIgnoreCase(CoursesIdColumns.COLUMN_COURSE_ID) ? // so teh ones that are common would be taken care of
					NoteInfoEntry.getQName(projection[i]) : projection[i];
		}
		String tablesWithJoin = NoteInfoEntry.TABLE_NAME + " JOIN " + CourseInfoEntry.TABLE_NAME + " ON " +
				NoteInfoEntry.getQName(NoteInfoEntry.COLUMN_COURSE_ID) + " = " +
				CourseInfoEntry.getQName(CourseInfoEntry.COLUMN_COURSE_ID);

		return db.query(tablesWithJoin, columns, selection, selectionArgs, null, null, sortOrder);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		long rowId = -1;
		String rowSelection = null;
		String[] rowSelectionArgs = null;
		int nRows = -1;
		SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();

		int uriMatch = sUriMatcher.match(uri);
		switch (uriMatch) {
			case COURSES:
				nRows = db.update(CourseInfoEntry.TABLE_NAME, values, selection, selectionArgs);
				break;
			case NOTES:
				nRows = db.update(NoteInfoEntry.TABLE_NAME, values, selection, selectionArgs);
				break;
			case NOTES_EXPANDED:
				// throw exception saying that this is a read-only table
			case COURSES_ROW:
				rowId = ContentUris.parseId(uri);
				rowSelection = CourseInfoEntry._ID + " = ?";
				rowSelectionArgs = new String[]{Long.toString(rowId)};
				nRows = db.update(CourseInfoEntry.TABLE_NAME, values, rowSelection, rowSelectionArgs);
				break;
			case NOTE_ROW:
				rowId = ContentUris.parseId(uri);
				rowSelection = NoteInfoEntry._ID + " = ?";
				rowSelectionArgs = new String[]{Long.toString(rowId)};
				nRows = db.update(NoteInfoEntry.TABLE_NAME, values, rowSelection, rowSelectionArgs);
				break;
			case NOTES_EXPANDED_ROW:
				// throw exception saying that this is a read-only table
				break;
		}

		return nRows;
	}
}
