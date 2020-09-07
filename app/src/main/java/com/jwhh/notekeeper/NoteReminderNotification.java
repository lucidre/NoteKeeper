package com.jwhh.notekeeper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * Helper class for showing and canceling note reminder
 * notifications.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class NoteReminderNotification {
	public static final String CHANNEL_ID = "note_reminder_channel_id";
	public static final String REVIEW_NOTE = "Review note";
	/**
	 * The unique identifier for this type of notification.
	 */
	private static final String NOTIFICATION_TAG = "NoteReminder";

	//must be called everytime before a  notification is added .. so i used the main activty
	public static void createNotificationChannel(Context context, String channelName, String channelDescription) {
		// Create the NotificationChannel, but only on API 26+ because
		// the NotificationChannel class is new and not in the support library
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			int importance = NotificationManager.IMPORTANCE_DEFAULT;
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
			channel.setDescription(channelDescription);
			// Register the channel with the system; you can't change the importance
			// or other notification behaviors after this
			NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
			assert notificationManager != null;
			notificationManager.createNotificationChannel(channel);
		}
	}

	/**
	 * Shows the notification, or updates a previously shown notification of
	 * this type, with the given parameters.
	 * <p>
	 * TODO: Customize this method's arguments to present relevant content in
	 * the notification.
	 * <p>
	 * TODO: Customize the contents of this method to tweak the behavior and
	 * presentation of note reminder notifications. Make
	 * sure to follow the
	 * <a href="https://developer.android.com/design/patterns/notifications.html">
	 * Notification design guidelines</a> when doing so.
	 *
	 * @see #cancel(Context)
	 */
	public static void notify(final Context context, final String noteTitle, final String noteText, int noteId) {
		final Resources res = context.getResources();

		// This image is used as the notification's large icon (thumbnail).
		// TODO: Remove this if your notification has no relevant thumbnail.
		final Bitmap picture = BitmapFactory.decodeResource(res, R.drawable.ic_menu_camera);

		Intent noteActivityIntent = new Intent(context, NoteActivity.class);
		noteActivityIntent.putExtra(NoteActivity.NOTE_ID, noteId);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, noteActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);


		Intent backupIntent = new Intent(context, NoteBackUpService.class);
		backupIntent.putExtra(NoteBackUpService.EXTRA_COURSE_ID, NoteBackup.ALL_COURSES);

		PendingIntent pendingIntent2 = PendingIntent.getService(context, 0, backupIntent, PendingIntent.FLAG_UPDATE_CURRENT);


		final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
				// Set appropriate defaults for the notification light, sound,
				// and vibration.
				.setDefaults(Notification.DEFAULT_ALL)  // let this behaviours be default
				// Set required fields, including the small icon, the
				// notification title, and text.
				.setSmallIcon(R.drawable.ic_assignment_black_24dp)
				.setContentTitle(REVIEW_NOTE) // the purpose of the notification
				.setContentText(noteText) // the body

				// All fields below this line are optional.

				// Use a default priority (recognized on devices running Android
				// 4.1 or later)
				.setPriority(NotificationCompat.PRIORITY_DEFAULT)

				// Provide a large icon, shown with the notification in the
				// notification drawer on devices running Android 3.0 or later.
				.setLargeIcon(picture)

				// Set ticker text (preview) information for this notification.
				.setTicker("Review note")// for screen readers it is old
			//	.setNumber(MainActivity.NOTIFICATION_NUMBER) //.... this is for the number of notification present but we have only one

				/* ntification style...
				 * bigtextstyle, bigpicturestyle,inboxstyle
				 * all can text and different title for expanded and standard state
				 * bigpicture--- for large bitmap
				 * indoxstyle ------ a list of up to 5 items
				 */
				.setStyle(new NotificationCompat.BigTextStyle().bigText(noteText).setBigContentTitle(noteTitle).setSummaryText(REVIEW_NOTE))
				// If this notification relates to a past or upcoming event, you
				// should set the relevant time information using the setWhen
				// method below. If this call is omitted, the notification's
				// timestamp will by set to the time at which it was shown.
				// TODO: Call setWhen if this notification relates to a past or
				// upcoming event. The sole argument to this method should be
				// the notification timestamp in milliseconds.
				//.setWhen(...)

				// Set the pending intent to be initiated when the user touches
				// the notification.
				.setContentIntent(pendingIntent)
				.addAction(0, "View all notes", PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT))
				.addAction(0, "Backup Notes", pendingIntent2)
				// Automatically dismiss the notification when it is touched.
				.setAutoCancel(true);


// notificationId is a unique int for each notification that you must define
		notify(context, builder.build());
	}

	@TargetApi(Build.VERSION_CODES.ECLAIR)
	private static void notify(final Context context, final Notification notification) {
		final NotificationManagerCompat nm = NotificationManagerCompat.from(context);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
			nm.notify(NOTIFICATION_TAG, 0, notification);
		} else {
			nm.notify(NOTIFICATION_TAG.hashCode(), notification);
		}
	}

	/**
	 * Cancels any notifications of this type previously shown using
	 * .
	 */
	@TargetApi(Build.VERSION_CODES.ECLAIR)
	public static void cancel(final Context context) {
		final NotificationManagerCompat nm = NotificationManagerCompat.from(context);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
			nm.cancel(NOTIFICATION_TAG, 0);
		} else {
			nm.cancel(NOTIFICATION_TAG.hashCode());
		}
	}
}
