package me.lakshay.eventsguide.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import me.lakshay.eventsguide.EventDetailsActivity;
import me.lakshay.eventsguide.R;
import me.lakshay.eventsguide.model.Category;
import me.lakshay.eventsguide.model.Event;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnItemSelectedListener} interface
 * to handle interaction events.
 */
public class CategoryEventsFragment extends Fragment {

	private static final String ARG_CATEGORY = "category";

	private Category category;

	private OnItemSelectedListener mListener;

	private FirebaseDatabase mFirebaseDatabase;
	private DatabaseReference mCategoryEventsDatabaseReference;
	private DatabaseReference mEventsDatabaseReference;
	private ChildEventListener mChildEventListener;

	//private List<Category> categories;
	private FirebaseRecyclerAdapter mCategoryPosterRecyclerAdapter;

	public CategoryEventsFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param category Category.
	 * @return A new instance of fragment CategoryEventsFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static CategoryEventsFragment newInstance(Category category) {
		CategoryEventsFragment fragment = new CategoryEventsFragment();
		Bundle args = new Bundle();
		args.putParcelable(ARG_CATEGORY, category);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			category = getArguments().getParcelable(ARG_CATEGORY);
		}
		mFirebaseDatabase = FirebaseDatabase.getInstance();
		mCategoryEventsDatabaseReference = mFirebaseDatabase.getReference()
				.child("categories").child(category.getName()).child("events");
		mEventsDatabaseReference = mFirebaseDatabase.getReference()
				.child("events");
		/*categories = new ArrayList<>();*/
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_category_events, container, false);

		RecyclerView categoryPosters =
				(RecyclerView) rootView.findViewById(R.id.category_events);
		//categoryPosters.setHasFixedSize(true);
		categoryPosters.setLayoutManager(
				new StaggeredGridLayoutManager(
						2, StaggeredGridLayoutManager.VERTICAL)
				/*new GridLayoutManager(getContext(), 2)*/
				/*new LinearLayoutManager(getContext(),
				LinearLayoutManager.VERTICAL,
						false)*/);

		mCategoryPosterRecyclerAdapter = new FirebaseIndexRecyclerAdapter<Event, CategoryPosterHolder>(
				Event.class,
				R.layout.category_poster,
				CategoryPosterHolder.class,
				mCategoryEventsDatabaseReference,
				mEventsDatabaseReference) {
			@Override
			protected void populateViewHolder(CategoryPosterHolder viewHolder,
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
											new Intent(getContext(),
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

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		/*attachDatabaseReadListener();*/
	}

	@Override
	public void onPause() {
		super.onPause();
		/*detachDatabaseReadListener();
		categories.clear();*/
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mCategoryPosterRecyclerAdapter.cleanup();
	}

	/*private void attachDatabaseReadListener() {
		if (mChildEventListener == null) {
			mChildEventListener = new ChildEventListener() {
				@Override
				public void onChildAdded(DataSnapshot dataSnapshot, String s) {
					categories.add(dataSnapshot.getValue(Category.class));
				}

				@Override
				public void onChildChanged(DataSnapshot dataSnapshot, String s) {
				}

				@Override
				public void onChildRemoved(DataSnapshot dataSnapshot) {
				}

				@Override
				public void onChildMoved(DataSnapshot dataSnapshot, String s) {
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
				}
			};
			mCategoryEventsDatabaseReference.addChildEventListener(mChildEventListener);
		}
	}

	private void detachDatabaseReadListener() {
		if (mChildEventListener != null) {
			mCategoryEventsDatabaseReference.removeEventListener(mChildEventListener);
			mChildEventListener = null;
		}
	}*/

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnItemSelectedListener) {
			mListener = (OnItemSelectedListener) context;
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement OnItemSelectedListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnItemSelectedListener {
		void onCategorySelected(Category category);
		void onEventSelected(Event event);
	}

	public static class CategoryPosterHolder extends RecyclerView.ViewHolder {
		private final ImageView poster;

		public CategoryPosterHolder(View categoryPosterView) {
			super(categoryPosterView);
			this.poster =
					(ImageView) categoryPosterView.findViewById(R.id.poster);
		}

		public void setPoster(StorageReference storageReference) {
			Glide.with(poster.getContext())
					.using(new FirebaseImageLoader())
					.load(storageReference)
					.into(poster);
		}

		public void setOnClickListener(View.OnClickListener onClickListener) {
			poster.setOnClickListener(onClickListener);
		}
	}
}
