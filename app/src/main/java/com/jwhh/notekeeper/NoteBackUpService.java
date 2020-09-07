package com.jwhh.notekeeper;

import android.app.IntentService;
import android.content.Intent;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class NoteBackUpService extends IntentService {


	public static final String EXTRA_COURSE_ID = "com.jwhh.notekeeper.extra.COURSE_ID";

	public NoteBackUpService() {
		super("NoteBackUpService");
	}
//service lifecylce may not be destroyed if app is destroyed
	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent != null) {
			String backupCourseId = intent.getStringExtra(EXTRA_COURSE_ID);
			//this is referencing a service.. we can also use service for context
			NoteBackup.doBackup(this,backupCourseId);

		}

	}


}
