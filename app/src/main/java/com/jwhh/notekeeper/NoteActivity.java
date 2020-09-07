package com.jwhh.notekeeper;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.INotificationSideChannel;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.AlarmManagerCompat;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.jwhh.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;

import java.util.ArrayList;
import java.util.List;

import static com.jwhh.notekeeper.NoteKeeperDatabaseContract.CourseInfoEntry;
import static com.jwhh.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry.COLUMN_COURSE_ID;
import static com.jwhh.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry.COLUMN_NOTE_TEXT;
import static com.jwhh.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry.COLUMN_NOTE_TITLE;
import static com.jwhh.notekeeper.NoteKeeperProviderContract.Courses;
import static com.jwhh.notekeeper.NoteKeeperProviderContract.Notes;


// FIXME: 8/21/2020  the code for previous and next is not working well for a selected list of courses


public class NoteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	public static final String NOTE_ID = "com.jwhh.notekeeper.NOTE_POSITION";
	public static final int ID_NOT_SET = -1;
	public static final int LOADER_NOTES = 0;
	public static final int LOADER_COURSES = 1;
	//	private final DataManager dm = DataManager.getInstance();
	private NoteInfo mNote;
	private boolean mIsNewNote;
	private Spinner mSpinnerCourses;
	private EditText mTextNoteTitle;
	private EditText mTextNoteText;
	private int mNoteId;
	private boolean mIsCancelling;
	private NoteActivityViewModel mViewModel;
	private List<CourseInfo> mCourses;
	private NoteKeeperOpenHelper mDbOpenHelper;
	private Cursor mNoteCursor;
	private int mCourseIdPos;
	private int mNoteTitlePos;
	private int mNoteTextPos;
	private String mCourseId;
	private String mNoteTitle;
	private String mNoteText;
	private SimpleCursorAdapter mAdapterCourses;
	private boolean mCoursesQueryFinished;
	private boolean mNoteQueryFinished;
	private int mCursorSize;
	private ArrayList<Integer> mNotesID;
	private Uri mNoteUri;

	//work on disabling and enabling th eprevious and next button....
	//find the setup for the code so it works well
	@Override
	protected void onDestroy() {
		mDbOpenHelper.close();
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ViewModelProvider viewModelProvider = new ViewModelProvider(getViewModelStore(), ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()));
		mNotesID = new ArrayList<>();
		mViewModel = viewModelProvider.get(NoteActivityViewModel.class);


		mDbOpenHelper = new NoteKeeperOpenHelper(this);

		// use viewmodel and save instance state together
		if (!mViewModel.mIsNewlyCreated && savedInstanceState != null) {
			// makr sure to save and restore state well
			mViewModel.restoreState(savedInstanceState);
			mNoteUri = Uri.parse(mViewModel.mNoteUri);
		}
		mViewModel.mIsNewlyCreated = false;

		mSpinnerCourses = findViewById(R.id.spinner_courses);


		//previous codes
/*		mCourses = dm.getCourses();
		ArrayAdapter<CourseInfo> adapterCourses = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mCourses);
		adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinnerCourses.setAdapter(adapterCourses);*/
		mAdapterCourses = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, null,
				new String[]{CourseInfoEntry.COLUMN_COURSE_TITLE}, new int[]{android.R.id.text1}, 0);

		mAdapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinnerCourses.setAdapter(mAdapterCourses);

		//loadCourseData();
		getSupportLoaderManager().initLoader(LOADER_COURSES, null, this);

		readDisplayStateValues();
//		saveOriginalNoteValues();

		mTextNoteTitle = findViewById(R.id.text_note_title);
		mTextNoteText = findViewById(R.id.text_note_text);

		//if (!mIsNewNote) displayNote(); // it was display note before now we use a new method
		if (!mIsNewNote)/* loadNoteData();*/
			getSupportLoaderManager().initLoader(LOADER_NOTES, null, this);
	}

/*	private void loadCourseData() {
		//laoding data from database
		SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
		String[] courseColumn = {CourseInfoEntry.COLUMN_COURSE_TITLE, CourseInfoEntry.COLUMN_COURSE_ID, CourseInfoEntry._ID};
		Cursor cursor = db.query(CourseInfoEntry.TABLE_NAME, courseColumn, null, null, null, null, CourseInfoEntry.COLUMN_COURSE_TITLE);
		mAdapterCourses.changeCursor(cursor);
	}

	private void loadNoteData() {
		SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

		String selection = NoteInfoEntry._ID + " = ? ";
		String selectionArgs[] = {String.valueOf(mNoteId)};// % means all the rest
		String[] noteColumns = {COLUMN_COURSE_ID, COLUMN_NOTE_TITLE, COLUMN_NOTE_TEXT};
		mNoteCursor = db.query(TABLE_NAME, noteColumns, selection, selectionArgs, null, null, null);

		mCourseIdPos = mNoteCursor.getColumnIndex(COLUMN_COURSE_ID);
		mNoteTitlePos = mNoteCursor.getColumnIndex(COLUMN_NOTE_TITLE);
		mNoteTextPos = mNoteCursor.getColumnIndex(COLUMN_NOTE_TEXT);

		mNoteCursor.moveToNext();
		displayNote();


	}*/
//previous one
/*	private void saveOriginalNoteValues() {
		if (mIsNewNote) return;
		mViewModel.mOriginalNoteCouseId = mNote.getCourse().getCourseId();
		mViewModel.mNoteTitle = mNote.getTitle();
		mViewModel.mNoteText = mNote.getText();
	}*/

	private void saveOriginalNoteValues() {
		if (mIsNewNote) return;
		mViewModel.mOriginalNoteCouseId = mCourseId;
		mViewModel.mNoteTitle = mNoteTitle;
		mViewModel.mNoteText = mNoteText;
		if (mNoteUri!=null)mViewModel.mNoteUri = mNoteUri.toString();
	}

	// the previous display note
/*	private void displayNote() {
		List<CourseInfo> courses = dm.getCourses();
		int courseIndex = courses.indexOf(mNote.getCourse());
		mSpinnerCourses.setSelection(courseIndex);
		mTextNoteTitle.setText(mNote.getTitle());
		mTextNoteText.setText(mNote.getText());
		invalidateOptionsMenu();
	}*/
	private void displayNote() {
		mCourseId = mNoteCursor.getString(mCourseIdPos);
		mNoteTitle = mNoteCursor.getString(mNoteTitlePos);
		mNoteText = mNoteCursor.getString(mNoteTextPos);

		saveOriginalNoteValues();//because of changes

		int courseIndex = getIndexOfCourseId(mCourseId);
		mSpinnerCourses.setSelection(courseIndex);
		mTextNoteTitle.setText(mNoteTitle);
		mTextNoteText.setText(mNoteText);
		invalidateOptionsMenu();

		//sending a broadcast // other app recieves it
		CourseEventBroadcastHelper.sendEventBroadcast(this,mCourseId,"Editing Note");
	}

	private int getIndexOfCourseId(String courseId) {
		Cursor cursor = mAdapterCourses.getCursor();
		int courseIdPos = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID);
		int courseRowIndex = 0;
		boolean more = cursor.moveToFirst();
		while (more) {
			String cursorCourseId = cursor.getString(courseIdPos);
			if (courseId.equalsIgnoreCase(cursorCourseId)) break;
			courseRowIndex++;
			more = cursor.moveToNext();
		}
		return courseRowIndex;
	}

	private void readDisplayStateValues() {
		Intent intent = getIntent();
		mNoteId = intent.getIntExtra(NOTE_ID, ID_NOT_SET);

		mIsNewNote = mNoteId == ID_NOT_SET;

		if (mIsNewNote) {
			createNewNote();
		} else {
			//		mNote = dm.getNotes().get(mNoteId);

		}
	}

	/*	private void createNewNote() {
			DataManager dm = this.dm;
			mNoteId = dm.createNewNote();
	//		mNote = dm.getNotes().get(mNoteId);
		}*/

	private void createNewNote() {
		final ContentValues values = new ContentValues();
		values.put(Notes.COLUMN_COURSE_ID, "");
		values.put(Notes.COLUMN_NOTE_TITLE, "");
		values.put(Notes.COLUMN_NOTE_TEXT, "");

		//before
/*		values.put(COLUMN_COURSE_ID, "");
		values.put(COLUMN_NOTE_TITLE, "");
		values.put(COLUMN_NOTE_TEXT, "");*/
		mNotesID.clear();
		//previous on only sql
		/*new AsyncTask() {
			@Override
			protected Object doInBackground(Object[] objects) {
				SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
				mNoteId = (int) db.insert(NoteInfoEntry.TABLE_NAME, null, values);
				return null;
			}
		}.execute();*/
		AsyncTask<ContentValues, Integer, Uri> task = new AsyncTask<ContentValues, Integer, Uri>() {
			private ProgressBar mProgressBar;

			@Override
			protected void onPreExecute() {
				mProgressBar = findViewById(R.id.progress_bar);
				mProgressBar.setVisibility(View.VISIBLE);
				mProgressBar.setProgress(1);

			}

			@Override
			protected void onProgressUpdate(Integer... values) {
				mProgressBar.setProgress(values[0]);
			}

			@Override
			protected Uri doInBackground(ContentValues... contentValues) {
				ContentValues insertValues = contentValues[0];
				Uri uri = getContentResolver().insert(Notes.CONTENT_URI, insertValues);
				simulateLongRunnigWork();
				publishProgress(2);
				simulateLongRunnigWork();
				publishProgress(3);
				return uri;
			}

			@Override
			protected void onPostExecute(Uri uri) {
				mProgressBar.setVisibility(View.GONE);
				mNoteUri = uri;
				mNoteId = (int) ContentUris.parseId(mNoteUri);

			}
		};


		task.execute(values);
	}

	private void simulateLongRunnigWork() {
		try {
			Thread.sleep(2000);
		} catch(Exception ex) {}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_note, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		switch (id) {
			case R.id.action_send_mail:
				sendEmail();
				return true;
			case R.id.action_next:
				move(true);
				return true;
			case R.id.action_previous:
				move(false);
				return true;
			case R.id.action_cancel:
				mIsCancelling = true;
				finish();
			case R.id.item_reminder:
				showReminderNotification();
				break;
		}


		return super.onOptionsItemSelected(item);
	}


	private void showReminderNotification() {
		String noteText = mTextNoteText.getText().toString();
		String noteTitle = mTextNoteTitle.getText().toString();

		Intent intent = new Intent(this,NoteReminderReciever.class);
		intent.putExtra(NoteReminderReciever.EXTRA_NOTE_TITLE,noteTitle);
		intent.putExtra(NoteReminderReciever.EXTRA_NOTE_TEXT,noteText);
		intent.putExtra(NoteReminderReciever.EXTRA_NOTE_ID,mNoteId);

		//this flag tells android to replace any previous pending intent received with this current ones
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		//set method just does it once
		//set repeating use it as a repeating alarm and would contine until cancel is called
		/*
		we use elapsed real time to set a relative time like an hour from now
		commonly used in conjuction with SystemClock.elapsedRealTime  method+ value

		we also have real time clock for absolute time
		commonly used in conjuction to the calender class

		when setting an alarm we have to discuss if the device is asleep would it wait or cancel
		but the alarm manager cant wake up the device
		Elapsed real time constants
	ELAPSED_REALTIME  it would work work elapsed time and would wait for the device to be on before it goes of
	ELAPSED_REALTIME_WAKEUP  it would work work elapsed time and would not wait for the device to be on before it goes of.. it would wake it up
	Real time clock constants constants
	RTC  it would work work real time and would wait for the device to be on before it goes of
	RTC_WAKEUP  it would work work real time and would not wait for the device to be on before it goes of.. it would wake it up

		 */
		long currentTimeInMiliSeconds = SystemClock.elapsedRealtime();
		long ONE_HOUR = 60/*min*/*60/*sec*/*1000/*miliseconds*/;
		long TEN_SECONDS = 1000;
//		long alarmTime = currentTimeInMiliSeconds +ONE_HOUR;
		long alarmTime = currentTimeInMiliSeconds +TEN_SECONDS;
		alarmManager.set(AlarmManager.ELAPSED_REALTIME,alarmTime,pendingIntent);


		//previous code
		//NoteReminderNotification.notify(this, noteTitle, noteText, mNoteId);
	}

	/*	private void move(boolean f_b) {
			saveNote();

			if (f_b) mNoteId++;
			else mNoteId--;

			mNote = dm.getNotes().get(mNoteId);
			saveOriginalNoteValues();
			displayNote();

		}*/
	private void move(boolean f_b) {
		saveNote();
		int pos = mNotesID.indexOf(mNoteId);
		Log.e("pos", "move:pos1= " + pos);
		Log.e("pos", "move:id1 =" + mNoteId);

		if (f_b && pos != mNotesID.size() - 1) {
			mNoteId = mNotesID.get(pos + 1);
			Log.e("pos", "move:pos2 = " + (pos + 1));
			Log.e("pos", "move:id2 =" + mNoteId);
			getSupportLoaderManager().restartLoader(LOADER_NOTES, null, this);
		} else if (!f_b && pos != 0) {
			mNoteId = mNotesID.get(pos - 1);
			Log.e("pos", "move:pos3 = " + (pos - 1));
			Log.e("pos", "move:id3 = " + mNoteId);
			getSupportLoaderManager().restartLoader(LOADER_NOTES, null, this);
		}

//		mNote = dm.getNotes().get(mNoteId);
		//saveOriginalNoteValues();

//		displayNote();

	}


	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		MenuItem menuNext = menu.findItem(R.id.action_next);
		MenuItem menuPrevious = menu.findItem(R.id.action_previous);

		int lastNoteIndex = mNotesID.size() - 1;
		int firstNoteIndex = 0;

		int pos = mNotesID.indexOf(mNoteId);
		menuNext.setEnabled(pos < lastNoteIndex);
		menuNext.setIcon(pos < lastNoteIndex ? R.drawable.ic_next_clear : R.drawable.ic_next_unclear);

	/*	item2.setEnabled(mNoteId > firstNoteIndex);
		item2.setVisible(mNoteId > firstNoteIndex);*/
		menuPrevious.setEnabled(pos > firstNoteIndex);
		menuPrevious.setIcon(pos > firstNoteIndex ? R.drawable.ic_previous_clear : R.drawable.ic_previous_unclear);

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void invalidateOptionsMenu() {

		super.invalidateOptionsMenu();
	}

	@Override
	protected void onPause() {
		super.onPause();
	/*	if (mIsCancelling) {
			if (mIsNewNote) dm.removeNote(mNoteId);
			else {
				storePreviousNoteValues();
			}
		} else {
			saveNote();
		}*/
		if (mIsCancelling) {
			if (mIsNewNote) deleteNoteFromDatabase();

			else {
				storePreviousNoteValues();
			}
		} else {
			saveNote();
		}
	}

	private void deleteNoteFromDatabase() {
		final String selection = NoteInfoEntry._ID + " = ?";
		final String[] selectionsArgs = {Integer.toString(mNoteId)};

		AsyncTask task = new AsyncTask() {
			@Override
			protected Object doInBackground(Object[] objects) {
				//	SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
				getContentResolver().delete(Notes.CONTENT_URI, selection, selectionsArgs);
				//db.delete(TABLE_NAME, selection, selectionsArgs);
				return null;
			}

		};
		task.execute();

	}

	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		if (outState != null) {
			mViewModel.saveState(outState);
		}
	}

	private void storePreviousNoteValues() {
		/*CourseInfo course = dm.getCourse(mViewModel.mOriginalNoteCouseId);
		mNote.setCourse(course);
		mNote.setTitle(mViewModel.mNoteTitle);
		mNote.setText(mViewModel.mNoteText);*/

		String noteTitle = mViewModel.mNoteTitle;
		String noteText = mViewModel.mNoteText;
		String courseId = mViewModel.mOriginalNoteCouseId;

		saveNoteToDataBase(courseId, noteTitle, noteText);

	}

	/*	private void saveNote() {
			mNote.setCourse((CourseInfo) mSpinnerCourses.getSelectedItem());
			mNote.setTitle(mTextNoteTitle.getText().toString());
			mNote.setText(mTextNoteText.getText().toString());
		}*/
	private void saveNote() {
		String noteTitle = mTextNoteTitle.getText().toString();
		String noteText = mTextNoteText.getText().toString();
		String courseId = selectedCourseId();

		saveNoteToDataBase(courseId, noteTitle, noteText);
	}

	private String selectedCourseId() {
		int selectedPosition = mSpinnerCourses.getSelectedItemPosition();
		Cursor cursor = mAdapterCourses.getCursor();
		cursor.moveToPosition(selectedPosition);
		int courseIdPos = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID);
		return cursor.getString(courseIdPos);
	}

	private void saveNoteToDataBase(String courseId, String noteTitle, String noteText) {
		final String selection = NoteInfoEntry._ID + " =?";
		final String[] selectionArgs = {Integer.toString(mNoteId)};

		final ContentValues values = new ContentValues();
		values.put(COLUMN_COURSE_ID, courseId);
		values.put(COLUMN_NOTE_TITLE, noteTitle);
		values.put(COLUMN_NOTE_TEXT, noteText);

		new AsyncTask() {
			@Override
			protected Object doInBackground(Object[] objects) {
				getContentResolver().update(mNoteUri, values, selection, selectionArgs);
				//previous code
/*				SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
				db.update(TABLE_NAME, values, selection, selectionArgs);*/
				return null;
			}
		}.execute();

	}

	private void sendEmail() {
		CourseInfo course = (CourseInfo) mSpinnerCourses.getSelectedItem();
		String subject = mTextNoteTitle.getText().toString();
		String text = "Checkout what i learnt in the plurasight course \"" + course.getTitle() + "\"\n" + mTextNoteText.getText();
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc2822");
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, text);
		startActivity(intent);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		CursorLoader loader = null;
		if (i == LOADER_NOTES) loader = createLoaderNotes();
		else if (i == LOADER_COURSES) loader = createLoaderCourses();
		return loader;
	}


	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor o) {
		switch (loader.getId()) {
			case LOADER_NOTES:
				loadFinishedNotes(o);
				break;
			case LOADER_COURSES:
				mCoursesQueryFinished = true;
				mAdapterCourses.changeCursor(o);
				displayNoteWhenQueriesFinished();//so it works both sides..
				break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (loader.getId() == LOADER_NOTES) {
			if (mNoteCursor != null) mNoteCursor.close();
		} else if (loader.getId() == LOADER_COURSES) {
			mAdapterCourses.changeCursor(null);
		}
	}

	private void loadFinishedNotes(Cursor o) {

		mNoteCursor = o;
		mNotesID.clear();
		while (mNoteCursor.moveToNext()) {
			mNotesID.add(mNoteCursor.getInt(mNoteCursor.getColumnIndex(NoteInfoEntry._ID)));
		}

		mCourseIdPos = mNoteCursor.getColumnIndex(COLUMN_COURSE_ID);
		mNoteTitlePos = mNoteCursor.getColumnIndex(COLUMN_NOTE_TITLE);
		mNoteTextPos = mNoteCursor.getColumnIndex(COLUMN_NOTE_TEXT);

		mNoteCursor.moveToFirst();
		if (mNoteCursor.getInt(mNoteCursor.getColumnIndex(NoteInfoEntry._ID)) != mNoteId) {
			while (mNoteCursor.moveToNext()) {
				if (mNoteCursor.getInt(mNoteCursor.getColumnIndex(NoteInfoEntry._ID)) == mNoteId)
					break;
			}
		}
		mCursorSize = mNoteCursor.getColumnCount();
		mNoteQueryFinished = true;
		displayNoteWhenQueriesFinished();
	}

	private void displayNoteWhenQueriesFinished() {
		if (mNoteQueryFinished && mCoursesQueryFinished) displayNote();
	}

	//before
	/*private CursorLoader createLoaderCourses() {
		mCoursesQueryFinished = false;
		return new CursorLoader(this) {
			@Override
			public Cursor loadInBackground() {

				SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
				String[] courseColumn = {CourseInfoEntry.COLUMN_COURSE_TITLE, CourseInfoEntry.COLUMN_COURSE_ID, CourseInfoEntry._ID};
				return db.query(CourseInfoEntry.TABLE_NAME, courseColumn, null, null, null, null, CourseInfoEntry.COLUMN_COURSE_TITLE);

			}
		};

	}*/

	private CursorLoader createLoaderCourses() {
		mCoursesQueryFinished = false;

//		Uri uri = Uri.parse("content://com.jwhh.notekeeper.provider");
		Uri uri = Courses.CONTENT_URI;

//		String[] courseColumn = {CourseInfoEntry.COLUMN_COURSE_TITLE, CourseInfoEntry.COLUMN_COURSE_ID, CourseInfoEntry._ID};
		String[] courseColumn = {Courses.COLUMN_COURSE_TITLE, Courses.COLUMN_COURSE_ID, Courses._ID};
//		return new CursorLoader(this, uri, courseColumn, null, null, CourseInfoEntry.COLUMN_COURSE_TITLE);
		return new CursorLoader(this, uri, courseColumn, null, null, Courses.COLUMN_COURSE_TITLE);
		//no need for the previous do in background as it would handle it implicitly
	}

	private CursorLoader createLoaderNotes() {
		mNoteQueryFinished = false;
	/*	return new CursorLoader(NoteActivity.this) {
			@Override
			public Cursor loadInBackground() {
				SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

				*//*String selection = NoteInfoEntry._ID + " = ? ";
				String selectionArgs[] = {String.valueOf(mNoteId)}; *//*

				// % means all the rest this is because we want all the notes in this category
				String[] noteColumns = {NoteInfoEntry.getQName(COLUMN_COURSE_ID), COLUMN_NOTE_TITLE, COLUMN_NOTE_TEXT, NoteInfoEntry.getQName(NoteInfoEntry._ID), CourseInfoEntry.COLUMN_COURSE_TITLE};
//				return db.query(TABLE_NAME, noteColumns, selection, selectionArgs, null, null, null);
				String tablesWithJoin = NoteInfoEntry.TABLE_NAME + " JOIN " + CourseInfoEntry.TABLE_NAME + " ON " + NoteInfoEntry.getQName(NoteInfoEntry.COLUMN_COURSE_ID) + " = " + CourseInfoEntry.getQName(CourseInfoEntry.COLUMN_COURSE_ID);

				String noteOrderBy = CourseInfoEntry.COLUMN_COURSE_TITLE + "," + COLUMN_NOTE_TITLE + " DESC";
				return db.query(tablesWithJoin, noteColumns, null, null, null, null, noteOrderBy);
			}
		};*/

		mNoteUri = ContentUris.withAppendedId(Notes.CONTENT_URI, mNoteId);

		//moving to the content resolver
		String[] noteColumns = {Notes.COLUMN_COURSE_ID, Notes.COLUMN_NOTE_TITLE, Notes.COLUMN_NOTE_TEXT, Notes._ID, Courses.COLUMN_COURSE_TITLE};
		String noteOrderBy = CourseInfoEntry.COLUMN_COURSE_TITLE + "," + COLUMN_NOTE_TITLE + " DESC";
		return new CursorLoader(this, Notes.CONTENT_EXPANDED_URI, noteColumns, null, null, noteOrderBy);
//		return new CursorLoader(this, mNoteUri, noteColumns, null, null, noteOrderBy);

	}

}
