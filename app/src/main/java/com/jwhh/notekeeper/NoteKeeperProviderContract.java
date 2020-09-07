package com.jwhh.notekeeper;

import android.net.Uri;
import android.provider.BaseColumns;

public class NoteKeeperProviderContract {
	//we adda general authority to know the provider .. package name and .provider
	public static final String AUTHORITY = "com.jwhh.notekeeper.provider";
	public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

	private NoteKeeperProviderContract() {
	}

	protected interface CoursesColumn {
		// general to both types
		String COLUMN_COURSE_TITLE = "course_title";
	}
	protected interface NotesColumn {
		String COLUMN_NOTE_TITLE = "note_title";
		String COLUMN_NOTE_TEXT = "note_text";

	}
	protected interface CoursesIdColumns {
		String COLUMN_COURSE_ID = "course_id";
	}

	public static final class Courses implements CoursesColumn, BaseColumns, CoursesIdColumns {
		public static final String PATH = "courses";
		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);// the uri for the courses
	}

	public static final class Notes implements NotesColumn, BaseColumns, CoursesIdColumns ,CoursesColumn{
		public static final String PATH = "notes";
		public static final String PATH_EXPANDED = "notes_expanded";
		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
		public static final Uri CONTENT_EXPANDED_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH_EXPANDED);
	}


}
