package com.jwhh.notekeeper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NoteReminderReciever extends BroadcastReceiver {
	public static final String EXTRA_NOTE_TITLE = "com.jwhh.jim.notekeeper.extra.NOTE_TITLE";
	public static final String EXTRA_NOTE_TEXT =  "com.jwhh.jim.notekeeper.extra.NOTE_TEXT";
	public static final String EXTRA_NOTE_ID =  "com.jwhh.jim.notekeeper.extra.NOTE_ID";
	@Override
	public void onReceive(Context context, Intent intent) {
// setting an intent for a later time using broadcast
		String noteTitle = intent.getStringExtra(EXTRA_NOTE_TITLE);
		String noteText = intent.getStringExtra(EXTRA_NOTE_TEXT);
		int noteId = intent.getIntExtra(EXTRA_NOTE_ID,0);

		NoteReminderNotification.notify(context,noteTitle,noteText,noteId);
	}
}
