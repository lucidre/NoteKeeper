package com.jwhh.notekeeper;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static com.jwhh.notekeeper.NoteKeeperDatabaseContract.*;
import static com.jwhh.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry.COLUMN_NOTE_TITLE;
import static com.jwhh.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry.getQName;
import static com.jwhh.notekeeper.NoteKeeperProviderContract.*;

public class NoteListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	public static final String IS_COURSE = "com.jwhh.notekeeper.NOTE_IS_COURSE";
	public static final String COURSE = "com.jwhh.notekeeper.NOTE_IS_COURSE_ID";
	public static  final  int LOADER_NOTE = 45;
	private NoteRecyclerAdapter mNoteRecyclerAdapter;
	private CourseInfo mCourses;
	private List<NoteInfo> mNotes;
	private NoteKeeperOpenHelper mDbOpenHelper;
	private String mCourseId;
	private boolean mBooleanIsCourse;

	@Override
	protected void onStop() {
		mDbOpenHelper.close();
		super.onStop();
	}

//    private ArrayAdapter<NoteInfo> mAdapterNotes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note_list);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		mDbOpenHelper = new NoteKeeperOpenHelper(this);
		FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(NoteListActivity.this, NoteActivity.class));
			}
		});

		//CourseInfo course = getIntent().getParcelableExtra(COURSE);

		mCourseId = getIntent().getStringExtra(COURSE); // the course id


		initializeDisplayContent();
	}


/*	private void initializeDisplayContent() {
    /*
        final ListView listNotes = findViewById(R.id.list_notes);

        List<NoteInfo> notes = DataManager.getInstance().getNotes();
        mAdapterNotes = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notes);

        listNotes.setAdapter(mAdapterNotes);

        listNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(NoteListActivity.this, NoteActivity.class);
//                NoteInfo note = (NoteInfo) listNotes.getItemAtPosition(position);
//                intent.putExtra(NoteActivity.NOTE_POSITION, note);
                intent.putExtra(NoteActivity.NOTE_POSITION,position);
                startActivity(intent);
            }
        });
*//*
		final RecyclerView recyclerNotes = findViewById(R.id.list_notes);
		final LinearLayoutManager noteLinearLayoutManager = new LinearLayoutManager(this);
		recyclerNotes.setLayoutManager(noteLinearLayoutManager);

		if (mCourses != null && getIntent().getBooleanExtra(IS_COURSE, false)) {
			mNotes = DataManager.getInstance().getNotes(mCourses);

		} else {
			mNotes = DataManager.getInstance().getNotes();

		}

			mNoteRecyclerAdapter = new NoteRecyclerAdapter(this, mNotes);
			recyclerNotes.setAdapter(mNoteRecyclerAdapter);


		}*/

		private void initializeDisplayContent() {

		final RecyclerView recyclerNotes = findViewById(R.id.list_notes);
		final LinearLayoutManager noteLinearLayoutManager = new LinearLayoutManager(this);
		recyclerNotes.setLayoutManager(noteLinearLayoutManager);

			mBooleanIsCourse = getIntent().getBooleanExtra(IS_COURSE, false);
	/*		if (mCourses != null && mBooleanIsCourse) {
//			mNotes = DataManager.getInstance().getNotes(mCourses);

		}*/

			mNoteRecyclerAdapter = new NoteRecyclerAdapter(this,null);
			recyclerNotes.setAdapter(mNoteRecyclerAdapter);

		}


		@Override
		protected void onResume () {
			super.onResume();
//        mAdapterNotes.notifyDataSetChanged();
		if (mBooleanIsCourse) {
			getSupportLoaderManager().restartLoader(LOADER_NOTE,null,this);
		}

			mNoteRecyclerAdapter.notifyDataSetChanged();


		}


	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
		CursorLoader loader = null;

		if (id == LOADER_NOTE) {
		/*	loader = new CursorLoader(this) {
				@Override
				public Cursor loadInBackground() {
					SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
//					String[] noteColumn = {COLUMN_NOTE_TITLE, NoteInfoEntry.COLUMN_COURSE_ID, NoteInfoEntry._ID};
					// we qualify column course id as it appears in both side
					String[] noteColumn = {COLUMN_NOTE_TITLE,getQName(NoteInfoEntry._ID), CourseInfoEntry.COLUMN_COURSE_TITLE};
//					String noteOrderBy = NoteInfoEntry.COLUMN_COURSE_ID + "," + COLUMN_NOTE_TITLE + " DESC";
					String noteOrderBy = CourseInfoEntry.COLUMN_COURSE_TITLE + "," + COLUMN_NOTE_TITLE + " DESC";

					//table qualified column names
					//note_info JOIN course_info ON  note_info.course_id = course_info.course_id
					String tablesWithJoin = NoteInfoEntry.TABLE_NAME+" JOIN "+ CourseInfoEntry.TABLE_NAME+" ON "+ NoteInfoEntry.getQName(NoteInfoEntry.COLUMN_COURSE_ID )+" = "+ CourseInfoEntry.getQName(CourseInfoEntry.COLUMN_COURSE_ID);

					String selection = CourseInfoEntry.getQName(CourseInfoEntry.COLUMN_COURSE_ID)+" = ?";
					String [] selectionArgs = {mCourseId};

					return db.query(tablesWithJoin, noteColumn, selection, selectionArgs, null, null, noteOrderBy);

				}


			};*/
			String[] noteColumn = {Notes.COLUMN_NOTE_TITLE,Notes._ID, Courses.COLUMN_COURSE_TITLE};
			String noteOrderBy = Notes.COLUMN_COURSE_TITLE + "," + Notes.COLUMN_NOTE_TITLE + " DESC";
			String selection = CourseInfoEntry.getQName(CourseInfoEntry.COLUMN_COURSE_ID)+" = ?";
			String [] selectionArgs = {mCourseId};
			loader =new CursorLoader(this, Notes.CONTENT_EXPANDED_URI,noteColumn,selection,selectionArgs,noteOrderBy);

		}
		return loader;
	}

	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
		if (loader.getId() == LOADER_NOTE) {
			mNoteRecyclerAdapter.changeCursor(data);
		}
	}

	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> loader) {
		if (loader.getId() == LOADER_NOTE) {
			mNoteRecyclerAdapter.changeCursor(null);
		}
	}
}



