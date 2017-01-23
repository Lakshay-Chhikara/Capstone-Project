package me.lakshay.eventsguide;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import me.lakshay.eventsguide.fragments.CategoryEventsFragment;
import me.lakshay.eventsguide.model.Event;

public class WatchList extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_watch_list);

		FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
		DatabaseReference mWatchListDatabaseReference =
				mFirebaseDatabase.getReference()
						.child("users")
						.child(FirebaseAuth.getInstance()
								.getCurrentUser()
								.getUid());
		DatabaseReference mEventsDatabaseReference =
				mFirebaseDatabase.getReference().child("events");

		RecyclerView categoryPosters =
				(RecyclerView) findViewById(R.id.watch_list_events);
		categoryPosters.setHasFixedSize(true);
		categoryPosters.setLayoutManager(
				new StaggeredGridLayoutManager(
						2, StaggeredGridLayoutManager.VERTICAL)
				/*new GridLayoutManager(getContext(), 2)*/
				/*new LinearLayoutManager(getContext(),
				LinearLayoutManager.VERTICAL,
						false)*/);

		FirebaseRecyclerAdapter mCategoryPosterRecyclerAdapter =
				new FirebaseIndexRecyclerAdapter<Event, CategoryEventsFragment.CategoryPosterHolder>(
						Event.class,
						R.layout.category_poster,
						CategoryEventsFragment.CategoryPosterHolder.class,
						mWatchListDatabaseReference,
						mEventsDatabaseReference) {
			@Override
			protected void populateViewHolder(CategoryEventsFragment.CategoryPosterHolder viewHolder,
											  final Event event, int position) {
				viewHolder.setPoster(
						FirebaseStorage.getInstance()
								.getReferenceFromUrl(event.getPoster()));
				//mCategoryPosterRecyclerAdapter.notifyDataSetChanged();
				viewHolder.setOnClickListener(
						new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								Intent eventDetailsIntent =
										new Intent(WatchList.this,
												EventDetailsActivity.class);
								eventDetailsIntent.putExtra(
										EventDetailsActivity.EXTRA_EVENT,
										event);
								startActivity(eventDetailsIntent);
							}
						});
			}
		};
		categoryPosters.setAdapter(mCategoryPosterRecyclerAdapter);
	}
}
