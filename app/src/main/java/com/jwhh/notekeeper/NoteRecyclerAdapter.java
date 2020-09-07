package com.jwhh.notekeeper;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.jwhh.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;

import java.util.List;

import static com.jwhh.notekeeper.NoteKeeperDatabaseContract.*;

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.ViewHolder> {
	private final Context mContext;
	private final LayoutInflater mLayoutInflater;
	private  List<NoteInfo> mNotes;
	private Cursor mCursor;
	private int mCoursePos;
	private int mNoteTitlePos;
	private int mIdPos;

/*
	public NoteRecyclerAdapter(Context context, List<NoteInfo> notes) {
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
		mNotes = notes;
	}
*/

	public NoteRecyclerAdapter(Context context, Cursor cursor) {
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
		mCursor = cursor;
		populateColumnPosition();
	}

	private void populateColumnPosition() {
		if (mCursor==null) return;
		//get column index from mCursor
//		mCoursePos = mCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
		mCoursePos = mCursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_TITLE);
		mNoteTitlePos = mCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
		mIdPos = mCursor.getColumnIndex(NoteInfoEntry._ID);

	}
public void  changeCursor(Cursor  cursor){
		if (mCursor!= null) mCursor.close();
		mCursor = cursor;
		populateColumnPosition();
		notifyDataSetChanged();
}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View itemView = mLayoutInflater.inflate(R.layout.item_note_list, parent, false);
		return new ViewHolder(itemView);
	}
//before
/*	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

		NoteInfo note = mNotes.get(position);
		holder.mTextCourse.setText(note.getCourse().getTitle());
		holder.mTextTitle.setText(note.getTitle());
		holder.mId =note.getId();
	}*/
	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		if (!mCursor.isClosed()) {
			mCursor.moveToPosition(position);
			String course = mCursor.getString(mCoursePos);
			String noteTitle = mCursor.getString(mNoteTitlePos);
			int id = mCursor.getInt(mIdPos);
			holder.mTextCourse.setText(course);
			holder.mTextTitle.setText(noteTitle);
			holder.mId = id;
		}
	}

/*	@Override
	public int getItemCount() {
		return mNotes.size();
	}*/
	@Override
	public int getItemCount() {
		return mCursor==null?0:mCursor.getCount();
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		public final TextView mTextCourse;
		public final TextView mTextTitle;
		public int mId;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			mTextCourse = itemView.findViewById(R.id.text_course);
			mTextTitle = itemView.findViewById(R.id.text_title);

			itemView.setOnClickListener(this);
		}


		@Override
		public void onClick(View view) {
			Intent intent = new Intent(mContext, NoteActivity.class);
			intent.putExtra(NoteActivity.NOTE_ID, mId);
			Pair[] pairs = new Pair[2];
			pairs[0] = new Pair<View, String>(mTextCourse, "course");
			pairs[1] = new Pair<View, String>(mTextTitle, "title");
			AppCompatActivity appCompatActivity = (AppCompatActivity) mContext;
			ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(appCompatActivity, pairs);
			mContext.startActivity(intent,activityOptions.toBundle());
		}
	}
}
