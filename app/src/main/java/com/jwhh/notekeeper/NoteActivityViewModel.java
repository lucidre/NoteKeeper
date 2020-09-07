package com.jwhh.notekeeper;

import android.os.Bundle;

import androidx.lifecycle.ViewModel;


public class NoteActivityViewModel extends ViewModel {
	public static final String NORIGINAL_NOTE_COURSE_ID = "com.jwhh.notekeeper.ORIGINAL_NOTE_COURSE_ID";
	public static final String NORIGINAL_NOTE_COURSE_TITLE = "com.jwhh.notekeeper.ORIGINAL_NOTE_COURSE_TITLE";
	public static final String NORIGINAL_NOTE_COURSE_TEXT = "com.jwhh.notekeeper.ORIGINAL_NOTE_COURSE_TEXT";
	private static final String NNOTE_URI =  "com.jwhh.notekeeper.ORIGINAL_NOTE_URI";
	public String mOriginalNoteCouseId;
	public String mNoteTitle;
	public String mNoteText;
	public boolean mIsNewlyCreated = true;
	public String mNoteUri ;


	public void saveState(Bundle outState) {
		outState.putString(NORIGINAL_NOTE_COURSE_ID, mOriginalNoteCouseId);
		outState.putString(NORIGINAL_NOTE_COURSE_TITLE,mNoteTitle );
		outState.putString(NORIGINAL_NOTE_COURSE_TEXT,mNoteText);
		outState.putString(NNOTE_URI,mNoteUri);
	}



	public void restoreState(Bundle inState){
		mOriginalNoteCouseId = inState.getString(NORIGINAL_NOTE_COURSE_ID, mOriginalNoteCouseId);
		mNoteText = inState.getString(NORIGINAL_NOTE_COURSE_TEXT,mNoteText);
		mNoteTitle = inState.getString(NORIGINAL_NOTE_COURSE_TITLE,mNoteTitle );
		mNoteUri = inState.getString(NNOTE_URI,mNoteUri );
	}






}
