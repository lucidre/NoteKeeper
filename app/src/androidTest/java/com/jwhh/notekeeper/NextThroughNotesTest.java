package com.jwhh.notekeeper;

import static org.junit.Assert.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.Espresso.onView;
import static org.hamcrest.Matchers.*;
import java.util.List;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

public class NextThroughNotesTest {

	@Rule
	public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

	@Test
	public  void NextThroughNotes(){
		onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
		onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_notes));

		onView(withId(R.id.list_items)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));

		List<NoteInfo> notes = DataManager.getInstance().getNotes();

		for (int index = 0; index<notes.size();index++) {
			NoteInfo noteInfo = notes.get(index);

			onView(withId(R.id.spinner_courses)).check(matches(withSpinnerText(noteInfo.getCourse().getTitle())));
			onView(withId(R.id.text_note_title)).check(matches(withText(noteInfo.getTitle())));
			onView(withId(R.id.text_note_text)).check(matches(withText(noteInfo.getText())));

		if (index<notes.size()-1)
			onView(allOf(withId(R.id.action_next), withContentDescription("Next"), isDisplayed())).perform(click());
		}
		onView(allOf(withId(R.id.action_next), withContentDescription("Next"))).check(matches(not(isEnabled())));
		pressBack();

	}

}