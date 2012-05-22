package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.collect.Maps;


public class CustomListItem implements Parcelable {
	
	private String text1;
	private String text2;
	private int imageView;
	
	/**
	 * Use this class to fill in the values of your custom
	 * ListPicker. Create an ArrayList<CustomListItem> to 
	 * pass into the list picker.
	 * 
	 */
	public CustomListItem() {		
	}
	
	
	public CustomListItem(Parcel in) {
		readFromParcel(in);
	}
	
	
	
	/**
	 * Set the text of the first text view in the listpicker
	 * layout.
	 * 
	 * @param text The text to display
	 */
	public void Text(String text) {
		text1 = text;
	}
	
	/**
	 * 
	 * @return The text in the first textview
	 */
	public String Text() {
		return text1;
	}
	
	/**
	 * Set the text of the second text view in the listpicker
	 * layout.
	 * 
	 * @param text2 The text to display
	 */
	public void SecondText(String text2) {
		this.text2 = text2;
	}
	
	/**
	 * 
	 * @return The text in the second textview
	 */
	public String SecondText() {
		return text2;
	}
	
	/**
	 * Set the resource id of the drawable to use
	 * for the imageview in the listpicker layout.
	 * 
	 * @param resourceId The resource id of the drawable to use in the imageview
	 */
	public void ImageResource(int resourceId) {
		imageView = resourceId;
	}
	
	/**
	 * 
	 * @return the resource id of the drawable used in the imageview
	 */
	public int ImageResource() {
		return imageView;
	}


	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void writeToParcel(Parcel out, int flags) {
		
		out.writeString(text1);
		out.writeString(text2);
		out.writeInt(imageView);
	}
	
	private void readFromParcel(Parcel in) {
		text1 = in.readString();
		text2 = in.readString();
		imageView = in.readInt();
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public CustomListItem createFromParcel(Parcel in) {
			return new CustomListItem(in);
		}
		
		public CustomListItem[] newArray(int size) {
			return new CustomListItem[size];
		}		
	};
	
}
