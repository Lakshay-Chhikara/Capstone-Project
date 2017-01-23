package me.lakshay.eventsguide;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;

import me.lakshay.eventsguide.model.Event;

public class EventDetailsActivity extends AppCompatActivity {

	public static final String EXTRA_EVENT = "event";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_details);

		AppBarLayout appBar = (AppBarLayout) findViewById(R.id.app_bar);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		ImageView poster = (ImageView) findViewById(R.id.poster);
		final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

		TextView date = (TextView) findViewById(R.id.date);
		TextView time = (TextView) findViewById(R.id.time);
		TextView location = (TextView) findViewById(R.id.location);
		TextView description = (TextView) findViewById(R.id.description);

		Intent intent = getIntent();
		if (intent != null) {
			final Event event = intent.getParcelableExtra(EXTRA_EVENT);

			Glide.with(EventDetailsActivity.this)
					.using(new FirebaseImageLoader())
					.load(FirebaseStorage.getInstance()
							.getReferenceFromUrl(event.getPoster()))
					.into(poster);
			toolbar.setTitle(event.getName());
			date.setText(event.getDate());
			time.setText(event.getTime());
			location.setText(event.getLocation());
			description.setText(event.getDescription());

			fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					FirebaseDatabase.getInstance()
							.getReference()
							.child("users")
							.child(FirebaseAuth.getInstance()
									.getCurrentUser().getUid())
							.setValue(new HashMap<String, Boolean>()
									.put(event.getName(), true))
							.addOnSuccessListener(new OnSuccessListener<Void>() {
								@Override
								public void onSuccess(Void aVoid) {
									fab.setBackgroundResource(android.R.drawable.star_big_on);
								}
							});
				}
			});
		}

		setSupportActionBar(toolbar);
	}
}
