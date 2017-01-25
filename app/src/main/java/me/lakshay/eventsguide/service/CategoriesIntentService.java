package me.lakshay.eventsguide.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import me.lakshay.eventsguide.model.Category;

public class CategoriesIntentService extends IntentService {

	public static final String ACTION = "action";
	public static final String KEY_CATEGORIES = "categories";

	public CategoriesIntentService() {
		super("CategoriesIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		FirebaseDatabase.getInstance().getReference()
				.child("categories").addValueEventListener(
				new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						ArrayList<Category> categories =
								new ArrayList<Category>();
						for (DataSnapshot categoryDataSnapshot:
								dataSnapshot.getChildren()) {
							Category category = categoryDataSnapshot
									.getValue(Category.class);
							categories.add(category);
						}
						LocalBroadcastManager
								.getInstance(CategoriesIntentService.this)
								.sendBroadcast(new Intent(ACTION)
										.putParcelableArrayListExtra(
												KEY_CATEGORIES, categories));
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {
					}
				});
	}
}
