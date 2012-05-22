package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.collect;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.CustomListItem;

public class PickerList extends ArrayList<CustomListItem> implements Parcelable {

	private static final long serialVersionUID = 8275983245324876L;
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		 
    public PickerList createFromParcel(Parcel in) {
          return new PickerList(in);
    }

    public Object[] newArray(int arg0) {
            return null;
        }
	};
		
	public PickerList() {		
	}
	
	public PickerList(ArrayList<CustomListItem> items) {
		this.clear();
		this.addAll(items);
	}
	
	public PickerList(Parcel in) {
		readFromParcel(in);
	}
	
	private void readFromParcel(Parcel in) {
		this.clear();
		
		// Get the list size
		int size = in.readInt();
		
		for (int i = 0; i < size; i++) {
			CustomListItem c = new CustomListItem();
			c.Text(in.readString());
			c.SecondText(in.readString());
			c.ImageResource(in.readInt());
			this.add(c);
		}
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		int size = this.size();
		// write the size of the list
		dest.writeInt(size);
		
		for (int i = 0; i < size; i++) {
			CustomListItem c = this.get(i);
			dest.writeString(c.Text());
			dest.writeString(c.SecondText());
			dest.writeInt(c.ImageResource());
		}
	}
	
	

}
