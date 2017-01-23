package me.lakshay.eventsguide.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable {
	private String name;
	private String poster;
	private String date;
	private String time;
	private String location;
	private String description;

	public Event() {
	}

	public Event(String name, String poster, String date, String time, String location, String description) {
		this.name = name;
		this.poster = poster;
		this.date = date;
		this.time = time;
		this.location = location;
		this.description = description;
	}

	private Event(Parcel parcel) {
		this.name = parcel.readString();
		this.poster = parcel.readString();
		this.date = parcel.readString();
		this.time = parcel.readString();
		this.location = parcel.readString();
		this.description = parcel.readString();
	}

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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static final Parcelable.Creator<Event> CREATOR =
			new Parcelable.Creator<Event>() {
				@Override
				public Event createFromParcel(Parcel parcel) {
					return new Event(parcel);
				}

				@Override
				public Event[] newArray(int size) {
					return new Event[size];
				}
			};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(this.name);
		parcel.writeString(this.poster);
		parcel.writeString(this.date);
		parcel.writeString(this.time);
		parcel.writeString(this.location);
		parcel.writeString(this.description);
	}
}
