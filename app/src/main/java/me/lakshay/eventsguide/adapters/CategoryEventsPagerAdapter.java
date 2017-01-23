package me.lakshay.eventsguide.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import me.lakshay.eventsguide.fragments.CategoryEventsFragment;
import me.lakshay.eventsguide.model.Category;

public class CategoryEventsPagerAdapter extends FragmentPagerAdapter {

	private List<Category> categories;

	public CategoryEventsPagerAdapter(FragmentManager fm,
			List<Category> categories) {
		super(fm);
		this.categories = categories;
	}

	@Override
	public Fragment getItem(int position) {
		return CategoryEventsFragment.newInstance(categories.get(position));
	}

	@Override
	public int getCount() {
		return categories.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return categories.get(position).getName();
	}
}
