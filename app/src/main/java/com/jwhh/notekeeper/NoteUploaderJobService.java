package com.jwhh.notekeeper;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.net.Uri;
import android.os.AsyncTask;

//all services must be added to the manifest file for it to work
// this is a must put        android:permission="android.permission.BIND_JOB_SERVICE"

public class NoteUploaderJobService extends JobService {
	// to add checks on the work
	//we must add a permission to it in the manifest file
	//fixme criteria..
	public static final String EXTRA_DATA_URI = "com.jwhh.notekeeper.DATA_URI";
	private NoteUploader mNoteUploader;

	private NoteUploaderJobService() {
	}

	@Override
	public boolean onStartJob(final JobParameters jobParameters) {
		//it works on the main activity thread so limit operations here

		// fixme initial is what it needs , second is what progress update needs , 3 is  what do in back returns and what post execute collects
		//AsyncTask<Params, Progress, Result>
		  AsyncTask<JobParameters, Void, Void> task = new AsyncTask<JobParameters, Void, Void>() {
			@Override
			protected Void doInBackground(JobParameters... backgroundParameters) {
				JobParameters parameters = backgroundParameters[0];
				String stringDataUrl = parameters.getExtras().getString(EXTRA_DATA_URI);
				Uri dataUri = Uri.parse(stringDataUrl);
				mNoteUploader.doUpload(dataUri);
				if (!mNoteUploader.isCanceled()) jobFinished(parameters, false); //the second is for rescheduling// call this method so it knows the job has ended
				return null;
			}
		};
		mNoteUploader = new NoteUploader(this);
		task.execute(jobParameters);

		return true; // so it knows we did some work
	}

	@Override
	public boolean onStopJob(JobParameters jobParameters) {
		mNoteUploader.cancel(); // if the job is cancelled ie it looses the required stuff

		return true; // so the job is done again
	}

}
