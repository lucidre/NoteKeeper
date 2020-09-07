package com.jwhh.notekeeper;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DataManagerTest {

	// we use {@before} or {@beforeClass must be static} to manage data state making sure the data start from the same instance . at before would be called before all other test is called
	// we use {@after} or {@afterClass must be static} to manage data state making sure the data send  from the same instance . at after would be called after all other test is called

static  DataManager sDataManager;

@BeforeClass
public static void classSetUp(){
	sDataManager = DataManager.getInstance();
}
	@Before
	public void setUp() {
	
		sDataManager.getNotes().clear();
		sDataManager.initializeExampleNotes();

	}
	@Test
	public void createNewNote() {
		final CourseInfo course = sDataManager.getCourse("android_async");
		final String noteTitle = "Test notew title";
		final String noteText = "Rhis is the body of my test";
		int index = sDataManager.createNewNote();
		NoteInfo noteInfo = sDataManager.getNotes().get(index);

		noteInfo.setCourse(course);
		noteInfo.setText(noteText);
		noteInfo.setTitle(noteTitle);

		NoteInfo noteInfo1 = sDataManager.getNotes().get(index);

		// noteinfp and noteinfo2 should contain the same data so we compare


		//	assertSame(noteInfo,noteInfo1);// order of thing to be compared and what it is being compared with , asserst same checks  if they point to the same object
		assertEquals(course, noteInfo1.getCourse()); // check if the two data are equal
		assertEquals(noteTitle, noteInfo1.getTitle()); // check if the two data are equal
		assertEquals(noteText, noteInfo1.getText()); // check if the two data are equal


	}
	@Test
	public void findSimilarNote() {
		final CourseInfo course = sDataManager.getCourse("android_async");
		final String noteTitle = "Test notew title";
		final String noteText = "Rhis is the body of my test";
		final String noteText2 = "Rhis is the body of my test 2";

		int index = sDataManager.createNewNote();
		NoteInfo noteInfo = sDataManager.getNotes().get(index);
		noteInfo.setCourse(course);
		noteInfo.setText(noteText);
		noteInfo.setTitle(noteTitle);

		int index2 = sDataManager.createNewNote();
		NoteInfo noteInfo2 = sDataManager.getNotes().get(index2);
		noteInfo2.setCourse(course);
		noteInfo2.setText(noteText2);
		noteInfo2.setTitle(noteTitle);

		int index3 = sDataManager.findNote(noteInfo);
		assertEquals(index, index3); // check if the two data are equal

		int index4 = sDataManager.findNote(noteInfo2);
		assertEquals(index2, index4); // check if the two data are equal


	}
	@Test
	public void  createNewNoteOneStepCreation(){
	final CourseInfo course  = sDataManager.getCourse("android_async");
	final String noteTitle = "Test note title";
	final String noteText = "Thisa is nodfdfdf";

	int noteIndex = sDataManager.createNewNote(course,noteTitle,noteText);

	NoteInfo compareNote = sDataManager.getNotes().get(noteIndex);
	assertEquals(course,compareNote.getCourse());
	assertEquals(noteTitle,compareNote.getTitle());
	assertEquals(noteText,compareNote.getText());

	}








}