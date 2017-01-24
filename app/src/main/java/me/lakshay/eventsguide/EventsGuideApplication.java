package me.lakshay.eventsguide;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class EventsGuideApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();

		FirebaseDatabase.getInstance().setPersistenceEnabled(true);
	}
}
