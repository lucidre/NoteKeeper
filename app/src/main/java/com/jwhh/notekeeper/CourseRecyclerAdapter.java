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

public class CourseRecyclerAdapter extends RecyclerView.Adapter<CourseRecyclerAdapter.ViewHolder> {
	private final Context mContext;
	private final LayoutInflater mLayoutInflater;
//	private final List<CourseInfo> mCourses;
	private Cursor mCursor;
	private int mCourseTitlePos;
	private int mCourseIdPos;

/*	public CourseRecyclerAdapter(Context context, List<CourseInfo> courses) {
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
		mCourses = courses;
	}*/

	public CourseRecyclerAdapter(Context context,  Cursor cursor) {
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
		mCursor = cursor;

		populateColumnPosition();
	}

	private void populateColumnPosition() {
		if (mCursor==null) return;
		mCourseTitlePos = mCursor.getColumnIndex(NoteKeeperDatabaseContract.CourseInfoEntry.COLUMN_COURSE_TITLE);
		mCourseIdPos = mCursor.getColumnIndex(NoteKeeperDatabaseContract.CourseInfoEntry.COLUMN_COURSE_ID);

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
		View itemView = mLayoutInflater.inflate(R.layout.item_course_list, parent, false);
		return new ViewHolder(itemView);
	}

/*	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		CourseInfo course = mCourses.get(position);
		holder.mTextCourse.setText(course.getTitle());
	}*/
	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		if (!mCursor.isClosed()) {
			mCursor.moveToPosition(position);
			String course = mCursor.getString(mCourseTitlePos);
			String  id = mCursor.getString(mCourseIdPos);

			holder.mTextCourse.setText(course);
			holder.course = id;
		}
	}

/*	@Override
	public int getItemCount() {
		return mCourses.size();
	}*/
	@Override
	public int getItemCount() {
		return mCursor==null?0:mCursor.getCount();
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		public final TextView mTextCourse;
//		public int mCurrentPosition;
		public String course;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			mTextCourse = itemView.findViewById(R.id.text_course);

			itemView.setOnClickListener(this);
		}


		@Override
		public void onClick(View view) {
			Intent intent = new Intent(mContext, NoteListActivity.class);
			/*mCurrentPosition = getAdapterPosition();*/
			intent.putExtra(NoteListActivity.COURSE, course);
			intent.putExtra(NoteListActivity.IS_COURSE, true);

			Pair pairs = new Pair<View, String>(mTextCourse, "course list");
			AppCompatActivity appCompatActivity = (AppCompatActivity) mContext;
			ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(appCompatActivity, pairs);
			mContext.startActivity(intent, activityOptions.toBundle());
		}
	}
}
