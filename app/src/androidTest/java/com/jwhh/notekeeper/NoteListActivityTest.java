package com.jwhh.notekeeper;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NoteListActivityTest {

	@Rule
	public ActivityTestRule<NoteListActivity> mActivityTestRule = new ActivityTestRule<>(NoteListActivity.class);

	@Test
	public void noteListActivityTest() {
		DataInteraction appCompatTextView = onData(anything())
				.inAdapterView(allOf(withId(R.id.list_notes),
						childAtPosition(
								withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
								0)))
				.atPosition(3);
		appCompatTextView.perform(click());

		ViewInteraction appCompatSpinner = onView(
				allOf(withId(R.id.spinner_courses),
						childAtPosition(
								childAtPosition(
										withClassName(is("androidx.coordinatorlayout.widget.CoordinatorLayout")),
										1),
								0),
						isDisplayed()));
		appCompatSpinner.perform(click());

		DataInteraction appCompatCheckedTextView = onData(anything())
				.inAdapterView(childAtPosition(
						withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
						0))
				.atPosition(3);
		appCompatCheckedTextView.perform(click());

		ViewInteraction appCompatEditText = onView(
				allOf(withId(R.id.text_note_title), withText("Long running operations"),
						childAtPosition(
								childAtPosition(
										withClassName(is("androidx.coordinatorlayout.widget.CoordinatorLayout")),
										1),
								1),
						isDisplayed()));
		appCompatEditText.perform(replaceText("Long running operations in check"));

		ViewInteraction appCompatEditText2 = onView(
				allOf(withId(R.id.text_note_title), withText("Long running operations in check"),
						childAtPosition(
								childAtPosition(
										withClassName(is("androidx.coordinatorlayout.widget.CoordinatorLayout")),
										1),
								1),
						isDisplayed()));
		appCompatEditText2.perform(closeSoftKeyboard());

		ViewInteraction appCompatEditText3 = onView(
				allOf(withId(R.id.text_note_text), withText("Foreground Services can be tied to a notification icon"),
						childAtPosition(
								childAtPosition(
										withClassName(is("androidx.coordinatorlayout.widget.CoordinatorLayout")),
										1),
								2),
						isDisplayed()));
		appCompatEditText3.perform(replaceText("Foreground Services can be tied to a notification icon.."));

		ViewInteraction appCompatEditText4 = onView(
				allOf(withId(R.id.text_note_text), withText("Foreground Services can be tied to a notification icon.."),
						childAtPosition(
								childAtPosition(
										withClassName(is("androidx.coordinatorlayout.widget.CoordinatorLayout")),
										1),
								2),
						isDisplayed()));
		appCompatEditText4.perform(closeSoftKeyboard());



		ViewInteraction appCompatEditText5 = onView(
				allOf(withId(R.id.text_note_text), withText("Foreground Services can be tied to a notification icon.."),
						childAtPosition(
								childAtPosition(
										withClassName(is("androidx.coordinatorlayout.widget.CoordinatorLayout")),
										1),
								2),
						isDisplayed()));
		appCompatEditText5.perform(replaceText("Foreground Services can be tied to a notification icon......."));

		ViewInteraction appCompatEditText6 = onView(
				allOf(withId(R.id.text_note_text), withText("Foreground Services can be tied to a notification icon......."),
						childAtPosition(
								childAtPosition(
										withClassName(is("androidx.coordinatorlayout.widget.CoordinatorLayout")),
										1),
								2),
						isDisplayed()));
		appCompatEditText6.perform(closeSoftKeyboard());

		pressBack();

	}

	private static Matcher<View> childAtPosition(
			final Matcher<View> parentMatcher, final int position) {

		return new TypeSafeMatcher<View>() {
			@Override
			public void describeTo(Description description) {
				description.appendText("Child at position " + position + " in parent ");
				parentMatcher.describeTo(description);
			}

			@Override
			public boolean matchesSafely(View view) {
				ViewParent parent = view.getParent();
				return parent instanceof ViewGroup && parentMatcher.matches(parent)
						&& view.equals(((ViewGroup) parent).getChildAt(position));
			}
		};
	}
}
