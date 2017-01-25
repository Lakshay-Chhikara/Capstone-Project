package me.lakshay.eventsguide;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import me.lakshay.eventsguide.adapters.CategoryEventsPagerAdapter;
import me.lakshay.eventsguide.fragments.CategoryEventsFragment;
import me.lakshay.eventsguide.model.Category;
import me.lakshay.eventsguide.model.Event;
import me.lakshay.eventsguide.service.CategoriesIntentService;

public class NavigationActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener,
		CategoryEventsFragment.OnItemSelectedListener,
		LoaderManager.LoaderCallbacks<Bitmap> {

	private FirebaseAuth mFirebaseAuth;
	private FirebaseAuth.AuthStateListener mAuthStateListener;

	private FirebaseDatabase mFirebaseDatabase;
	private DatabaseReference mCategoriesDatabaseReference;

	private NavigationView navigationView;
	private View headerView;
	private ImageView profilePic;
	private TextView name, email;

	private FirebaseRecyclerAdapter mCategoryPosterRecyclerAdapter;

	private List<Category> categories;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navigation);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle =
				new ActionBarDrawerToggle(
						this,
						drawer,
						toolbar,
						R.string.navigation_drawer_open,
						R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

		headerView = navigationView.getHeaderView(0);
		profilePic = (ImageView) headerView.findViewById(R.id.profile_pic);
		name = (TextView) headerView.findViewById(R.id.name);
		email = (TextView) headerView.findViewById(R.id.email);

		headerView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(
						AuthUI.getInstance()
								.createSignInIntentBuilder()
								.setIsSmartLockEnabled(false)
								.setProviders(AuthUI.EMAIL_PROVIDER,
										AuthUI.GOOGLE_PROVIDER)
								.build());
			}
		});

		categories = new ArrayList<>();

		final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
		viewPager.setAdapter(
				new CategoryEventsPagerAdapter(
						getSupportFragmentManager(), categories));

		TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
		tabLayout.setupWithViewPager(viewPager);

		mFirebaseAuth = FirebaseAuth.getInstance();
		mAuthStateListener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
				FirebaseUser user = firebaseAuth.getCurrentUser();
				if (user != null) {
					if (user.isAnonymous()) {
						cleanupHeaderViewAndSignoutOption();
					} else {
						onSignedInInitialize(user);
					}
				} else {
					onSignedOutCleanup();
					mFirebaseAuth.signInAnonymously();
				}
			}
		};

		mFirebaseDatabase = FirebaseDatabase.getInstance();
		mCategoriesDatabaseReference = mFirebaseDatabase
				.getReference().child("categories");

		RecyclerView categoryPosters =
				(RecyclerView) findViewById(R.id.category_posters);
		//categoryPosters.setHasFixedSize(true);
		categoryPosters.setLayoutManager(
				new LinearLayoutManager(NavigationActivity.this,
						LinearLayoutManager.HORIZONTAL,
						false));

		mCategoryPosterRecyclerAdapter =
				new FirebaseRecyclerAdapter<Category, CategoryPosterHolder>(
						Category.class,
						R.layout.category_poster,
						CategoryPosterHolder.class,
						mCategoriesDatabaseReference) {
			@Override
			protected void populateViewHolder(CategoryPosterHolder viewHolder,
					Category category, int position) {
				/*categories.add(category);
				//mCategoryPosterRecyclerAdapter.notifyDataSetChanged();
				viewPager.getAdapter().notifyDataSetChanged();*/
				viewHolder.setPoster(
						FirebaseStorage.getInstance()
						.getReferenceFromUrl(category.getPoster()));
			}
		};
		categoryPosters.setAdapter(mCategoryPosterRecyclerAdapter);

		LocalBroadcastManager.getInstance(NavigationActivity.this)
				.registerReceiver(mBroadcastReceiver,
						new IntentFilter(CategoriesIntentService.ACTION));

		startService(
				new Intent(NavigationActivity.this,
						CategoriesIntentService.class));
	}

	private BroadcastReceiver mBroadcastReceiver =
			new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					categories.clear();
					List<Category> categories1 = intent.getParcelableArrayListExtra(
							CategoriesIntentService.KEY_CATEGORIES);
					categories.addAll(categories1);
					((ViewPager) findViewById(R.id.view_pager))
							.getAdapter()
							.notifyDataSetChanged();
				}
			};

	private void onSignedInInitialize(FirebaseUser user) {
		Glide.with(NavigationActivity.this)
				.load(user.getPhotoUrl())
				.into(profilePic);
		name.setText(user.getDisplayName());
		email.setText(user.getEmail());
		navigationView.getMenu()
				.findItem(R.id.nav_signout)
				.setVisible(true);
	}

	private void onSignedOutCleanup() {
		cleanupHeaderViewAndSignoutOption();
	}

	private void cleanupHeaderViewAndSignoutOption() {
		Glide.with(NavigationActivity.this)
				.load(R.mipmap.ic_launcher)
				.into(profilePic);
		name.setText(getString(R.string.name));
		email.setText(getString(R.string.email));
		navigationView.getMenu()
				.findItem(R.id.nav_signout)
				.setVisible(false);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mAuthStateListener != null) {
			mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mFirebaseAuth.addAuthStateListener(mAuthStateListener);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mCategoryPosterRecyclerAdapter.cleanup();

		if (mBroadcastReceiver != null) {
			LocalBroadcastManager.getInstance(NavigationActivity.this)
					.unregisterReceiver(mBroadcastReceiver);
		}
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();
		if (id == R.id.nav_signout) {
			mFirebaseAuth.signOut();
			mFirebaseAuth.signInAnonymously();
		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	@Override
	public Loader<Bitmap> onCreateLoader(int id, Bundle args) {
		return new AsyncTaskLoader<Bitmap>(NavigationActivity.this) {
			@Override
			public Bitmap loadInBackground() {
				Bitmap bitmap = null;
				FirebaseUser user = mFirebaseAuth.getCurrentUser();
				if (user != null && !user.isAnonymous()) {
					String url = user.getPhotoUrl().toString();
					try {
						InputStream is = (InputStream) new URL(url).openConnection().getInputStream();
						bitmap = BitmapFactory.decodeStream(is);
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return bitmap;
			}
		};
	}

	@Override
	public void onCategorySelected(Category category) {
	}

	@Override
	public void onEventSelected(Event event) {
	}

	@Override
	public void onLoadFinished(Loader<Bitmap> loader, Bitmap data) {
		if (data != null) {
			profilePic.setImageBitmap(data);
		}
	}

	@Override
	public void onLoaderReset(Loader loader) {
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
	}
}