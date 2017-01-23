package me.lakshay.eventsguide.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class Category implements Parcelable {
	//private HashMap<String, Boolean> events;
	private String name;
	private String poster;

	public Category() {
	}

	public Category(HashMap<String, Boolean> events, String name, String poster) {
		//this.events = events;
		this.name = name;
		this.poster = poster;
	}

	private Category(Parcel parcel) {
		//parcel.readTypedList(this.events, Event.CREATOR);
		this.name = parcel.readString();
		this.poster = parcel.readString();
	}

	/*public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}*/

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public static final Parcelable.Creator<Category> CREATOR =
			new Parcelable.Creator<Category>() {
				@Override
				public Category createFromParcel(Parcel parcel) {
					return new Category(parcel);
				}

				@Override
				public Category[] newArray(int size) {
					return new Category[size];
				}
			};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		//parcel.writeTypedList(this.events);
		parcel.writeString(this.name);
		parcel.writeString(this.poster);
	}
}
