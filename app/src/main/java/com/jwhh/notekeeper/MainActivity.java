package com.jwhh.notekeeper;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import static com.jwhh.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;
import static com.jwhh.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry.COLUMN_NOTE_TITLE;
import static com.jwhh.notekeeper.NoteKeeperProviderContract.Courses;
import static com.jwhh.notekeeper.NoteKeeperProviderContract.Notes;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {
	public static final int NOTE_UPLOADER_JOB_ID = 1;
	private NoteRecyclerAdapter mNoteRecyclerAdapter;
	private RecyclerView mRecyclerItems;
	private LinearLayoutManager mNoteLayoutManager;
	private NavigationView mNavigationView;
	private CourseRecyclerAdapter mCourseRecyclerAdapter;
	private List<CourseInfo> mCourses;
	private StaggeredGridLayoutManager mCourseLayoutManager;
	private NoteKeeperOpenHelper mDbOpenHelper;
	private int LOADER_NOTES = 3;
	private int LOADER_COURSE = 67;
	private boolean mIsSeen = false;
	public static int NOTIFICATION_NUMBER = 0;
	/**
	 * fixme link http://bit.ly/buildandroidlib  for making your content provider a jar file
	 * // FIXME: 8/25/2020 working with the otherapp and the coursersevent
	 *
	 * @param savedInstanceState
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// must be called initially before any notification is called to register the notification
		String name = getString(R.string.channel_name);
		String desc = getString(R.string.channel_desc);
		NoteReminderNotification.createNotificationChannel(this, name, desc);

		enableStrictMode();
		mDbOpenHelper = new NoteKeeperOpenHelper(this);

		FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(MainActivity.this, NoteActivity.class));
			}
		});

		// FIXME: 8/21/2020 why is the read again false
		PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);


		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.setDrawerListener(toggle);
		toggle.syncState();

		mNavigationView = findViewById(R.id.nav_view);
		mNavigationView.setNavigationItemSelectedListener(this);

		initializeDisplayContent();
	}

	private void enableStrictMode() {
		//strictmode is only in debug and testing .. so remove it in the main application
		if (BuildConfig.DEBUG) {// only true during debug mode
			//StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork(); // if you want to detect just some
			//we can have multiple penalty
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build();// penaltylog would print error to logcat
			StrictMode.setThreadPolicy(policy);

		}
	}

	@Override
	protected void onDestroy() {
		mDbOpenHelper.close();
		super.onDestroy();
	}

	@Override
	protected void onStop() {

		super.onStop();
	}

	private void initializeDisplayContent() {
//		DataManager.LoadFromDatabase(mDbOpenHelper);//connecting to the database
		mRecyclerItems = findViewById(R.id.list_items);
		mNoteLayoutManager = new LinearLayoutManager(this);
		mCourseLayoutManager = new StaggeredGridLayoutManager(getResources().getInteger(R.integer.colom_count), LinearLayoutManager.VERTICAL);

//		List<NoteInfo> notes = DataManager.getInstance().getNotes();
//		mNoteRecyclerAdapter = new NoteRecyclerAdapter(this, notes)

		mNoteRecyclerAdapter = new NoteRecyclerAdapter(this, null);

		//mCourses = DataManager.getInstance().getCourses();
		mCourseRecyclerAdapter = new CourseRecyclerAdapter(this, null);

		displayNotes();


	}

	private void displayNotes() {
		mRecyclerItems.setLayoutManager(mNoteLayoutManager);
		mRecyclerItems.setAdapter(mNoteRecyclerAdapter);
		selectNavigationMenuItem(R.id.nav_notes);

	}

	private void displayCourses() {
		mRecyclerItems.setLayoutManager(mCourseLayoutManager);
		mRecyclerItems.setAdapter(mCourseRecyclerAdapter);

		selectNavigationMenuItem(R.id.nav_courses);

	}

	@Override
	protected void onResume() {
		//called before our activity moves to foreground
		super.onResume();
//		mNoteRecyclerAdapter.notifyDataSetChanged();
//		loadNotes();
		//	getSupportLoaderManager().initLoader(LOADER_NOTES,null,this); //repeated call wont repeat the query
		getSupportLoaderManager().restartLoader(LOADER_NOTES, null, this);
		getSupportLoaderManager().restartLoader(LOADER_COURSE, null, this);
		updateNavHeader();

		openDrawer();
	}

	private void openDrawer() {
		// opening the app on initial loading
		if (!mIsSeen) {
			final DrawerLayout drawer = findViewById(R.id.drawer_layout);
			Handler handler = new Handler(Looper.getMainLooper());
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					drawer.openDrawer(GravityCompat.START);
				}
			}, 1000);
			mIsSeen = !mIsSeen;
		}

	}

	private void loadNotes() {
		SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
		String[] noteColumn = {COLUMN_NOTE_TITLE, NoteInfoEntry.COLUMN_COURSE_ID, NoteInfoEntry._ID};
		String noteOrderBy = NoteInfoEntry.COLUMN_COURSE_ID + "," + COLUMN_NOTE_TITLE + " DESC";
		Cursor noteCursor = db.query(NoteInfoEntry.TABLE_NAME, noteColumn, null, null, null, null, noteOrderBy);
		mNoteRecyclerAdapter.changeCursor(noteCursor);

	}

	private void updateNavHeader() {
		NavigationView navigationView = findViewById(R.id.nav_view);
		View headerVie = navigationView.getHeaderView(0);

		TextView textUserName = headerVie.findViewById(R.id.text_user_name);
		TextView textUserEmail = headerVie.findViewById(R.id.text_email_address);

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		String userName = pref.getString("user_display_name", "");
		String email = pref.getString("user_email_address", "");

		textUserName.setText(userName);
		textUserEmail.setText(email);

	}

	private void selectNavigationMenuItem(int id) {
		NavigationView navigationView = findViewById(R.id.nav_view);
		Menu menu = navigationView.getMenu();
		menu.findItem(id).setChecked(true);
	}


	@Override
	public void onBackPressed() {
		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		} else if (id == R.id.action_backup_notes) {
			backupNotes();
		} else if (id == R.id.action_upload_notes) {
			scheduleNoteUpload();
		}

		return super.onOptionsItemSelected(item);
	}

	//works on job scheduler
	private void scheduleNoteUpload() {
		PersistableBundle extra = new PersistableBundle();
		extra.putString(NoteUploaderJobService.EXTRA_DATA_URI, Notes.CONTENT_URI.toString());
		ComponentName componentName = new ComponentName(this, NoteUploaderJobService.class);
		JobInfo jobInfo = new JobInfo.Builder(NOTE_UPLOADER_JOB_ID, componentName)
				.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
				.setExtras(extra)
				.build();

		JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
		jobScheduler.schedule(jobInfo);
	}


	//works on services
	private void backupNotes() {
		Intent intent = new Intent(this, NoteBackUpService.class);
		intent.putExtra(NoteBackUpService.EXTRA_COURSE_ID, NoteBackup.ALL_COURSES);
		startService(intent);
//previously
		//	NoteBackup.doBackup(this, NoteBackup.ALL_COURSES);
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();
		if (id == R.id.nav_notes) {
			displayNotes();
		} else if (id == R.id.nav_courses) {
			displayCourses();
		} else if (id == R.id.nav_share) {
			handleShare();
		} else if (id == R.id.nav_send) {
			handleSelection("Send");
		}

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	private void handleShare() {
		View view = findViewById(R.id.list_items);
		Snackbar.make(view, "Share to -" + PreferenceManager.getDefaultSharedPreferences(this).getString("user_favourite_social", ""), Snackbar.LENGTH_LONG).show();
	}


	private void handleSelection(String message) {
		View view = findViewById(R.id.list_items);
		Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
	}

	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
		CursorLoader loader = null;

		if (id == LOADER_NOTES) {
		/*	loader = new CursorLoader(this) {
				@Override
				public Cursor loadInBackground() {
					SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
//					String[] noteColumn = {COLUMN_NOTE_TITLE, NoteInfoEntry.COLUMN_COURSE_ID, NoteInfoEntry._ID};
					// we qualify column course id as it appears in both side
					String[] noteColumn = {COLUMN_NOTE_TITLE,getQName(NoteInfoEntry._ID),CourseInfoEntry.COLUMN_COURSE_TITLE};
//					String noteOrderBy = NoteInfoEntry.COLUMN_COURSE_ID + "," + COLUMN_NOTE_TITLE + " DESC";
					String noteOrderBy = CourseInfoEntry.COLUMN_COURSE_TITLE + "," + COLUMN_NOTE_TITLE + " DESC";

					//table qualified column names
					//note_info JOIN course_info ON  note_info.course_id = course_info.course_id
					String tablesWithJoin = NoteInfoEntry.TABLE_NAME+" JOIN "+ CourseInfoEntry.TABLE_NAME+" ON "+ NoteInfoEntry.getQName(NoteInfoEntry.COLUMN_COURSE_ID )+" = "+ CourseInfoEntry.getQName(CourseInfoEntry.COLUMN_COURSE_ID);
//					return db.query(NoteInfoEntry.TABLE_NAME, noteColumn, null, null, null, null, noteOrderBy);
					return db.query(tablesWithJoin, noteColumn, null, null, null, null, noteOrderBy);

				}


			};*/
//					String[] noteColumn = {COLUMN_NOTE_TITLE, NoteInfoEntry.COLUMN_COURSE_ID, NoteInfoEntry._ID};
			// we qualify column course id as it appears in both side

			String[] noteColumn = {Notes.COLUMN_NOTE_TITLE, Notes._ID, Notes.COLUMN_COURSE_TITLE};
			String noteOrderBy = Notes.COLUMN_COURSE_TITLE + "," + Notes.COLUMN_NOTE_TITLE + " DESC";
			loader = new CursorLoader(this, Notes.CONTENT_EXPANDED_URI, noteColumn, null, null, noteOrderBy);
		} else if (id == LOADER_COURSE) {
			/*loader = new CursorLoader(this) {
				@Override
				public Cursor loadInBackground() {
					SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
//					String[] noteColumn = {COLUMN_NOTE_TITLE, NoteInfoEntry.COLUMN_COURSE_ID, NoteInfoEntry._ID};
					// we qualify column course id as it appears in both side
					String[] noteColumn = {CourseInfoEntry.COLUMN_COURSE_ID,CourseInfoEntry.COLUMN_COURSE_TITLE};
//					String noteOrderBy = NoteInfoEntry.COLUMN_COURSE_ID + "," + COLUMN_NOTE_TITLE + " DESC";
					String noteOrderBy = CourseInfoEntry.COLUMN_COURSE_TITLE ;


//					return db.query(NoteInfoEntry.TABLE_NAME, noteColumn, null, null, null, null, noteOrderBy);
					return db.query(CourseInfoEntry.TABLE_NAME, noteColumn, null, null, null, null, noteOrderBy);

				}


			};*/
			String[] noteColumn = {Courses.COLUMN_COURSE_ID, Courses.COLUMN_COURSE_TITLE};
			String noteOrderBy = Courses.COLUMN_COURSE_TITLE;
			loader = new CursorLoader(this, Courses.CONTENT_URI, noteColumn, null, null, noteOrderBy);
		}
		return loader;
	}

	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
		if (loader.getId() == LOADER_NOTES) {
			mNoteRecyclerAdapter.changeCursor(data);
		} else if (loader.getId() == LOADER_COURSE) {
			mCourseRecyclerAdapter.changeCursor(data);
		}
	}

	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> loader) {
		if (loader.getId() == LOADER_NOTES) {
			mNoteRecyclerAdapter.changeCursor(null);
		} else if (loader.getId() == LOADER_COURSE) {
			mCourseRecyclerAdapter.changeCursor(null);
		}
	}


}
