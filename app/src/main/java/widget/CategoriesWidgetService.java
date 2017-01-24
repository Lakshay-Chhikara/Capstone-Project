package widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import me.lakshay.eventsguide.model.Category;

public class CategoriesWidgetService extends RemoteViewsService {
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new CategoriesRemoteViewsFactory(CategoriesWidgetService.this);
	}
}

class CategoriesRemoteViewsFactory
		implements RemoteViewsService.RemoteViewsFactory {
	private Context mContext;
	private DatabaseReference categoriesDatabaseReference;
	private List<Category> categories;

	public CategoriesRemoteViewsFactory(Context context) {
		this.mContext = context;
		categories = new ArrayList<>();
		categoriesDatabaseReference = FirebaseDatabase.getInstance()
				.getReference().child("categories");
		categoriesDatabaseReference.addValueEventListener(
				new ValueEventListener() {
						@Override
						public void onDataChange(DataSnapshot dataSnapshot) {
							for (DataSnapshot categoryDataSnapshot:
									dataSnapshot.getChildren()) {
								Category category = categoryDataSnapshot
										.getValue(Category.class);
								categories.add(category);
							}
						}

						@Override
						public void onCancelled(DatabaseError databaseError) {
						}
		});
	}

	@Override
	public void onCreate() {
	}

	@Override
	public void onDataSetChanged() {
	}

	@Override
	public void onDestroy() {
	}

	@Override
	public int getCount() {
		return categories.size();
	}

	@Override
	public RemoteViews getViewAt(int i) {
		RemoteViews remoteView = new RemoteViews(mContext.getPackageName(),
				android.R.layout.simple_list_item_1);
		remoteView.setTextViewText(android.R.id.text1,
				categories.get(i).getName());
		remoteView.setTextColor(android.R.id.text1,
				mContext.getResources()
						.getColor(android.R.color.white));

		/*Intent fillInIntent = new Intent();
		remoteView.setOnClickFillInIntent(android.R.id.text1, fillInIntent);*/

		return remoteView;
	}

	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public long getItemId(int i) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}
}
