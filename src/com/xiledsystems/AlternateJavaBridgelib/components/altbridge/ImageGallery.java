package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.MediaUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;
import android.graphics.drawable.Drawable;


public class ImageGallery extends AndroidViewComponent implements OnItemClickListener {

	private final Gallery view;
	private final ImageAdapter adapter;
	private boolean useFullPath=false;
	
	/**
	 * Non GLE component constructor.
	 * 
	 * @param container The container to place this ImageGallery into
	 * @param picnames A string array of the names of the pictures in the gallery
	 * @param basestyle This should always be R.styleable.ImageGallery
	 */
	public ImageGallery(ComponentContainer container, String[] picnames, int[] basestyle) {
		super(container);
		view = new Gallery(container.$context());
		adapter = new ImageAdapter(container.$context(), basestyle);
		adapter.setFiles(picnames);
		view.setAdapter(adapter);
		view.setOnItemClickListener(this);
		container.$add(this);
		view.requestLayout();
	}
	
	/**
	 * GLE component constructor.
	 * 
	 * @param container The container to place this ImageGallery into
	 * @param picnames A string array of the names of the pictures in the gallery
	 * @param basestyle This should always be R.styleable.ImageGallery
	 * @param resourceId The resource id of the ImageGallery you placed in the GLE
	 */	
	public ImageGallery(ComponentContainer container, String[] picnames, int[] basestyle, int resourceId) {
		super(container, resourceId);
		view = null;
		Gallery view = (Gallery) container.$form().findViewById(resourceId);		
		adapter = new ImageAdapter(container.$context(), basestyle);
		adapter.setFiles(picnames);
		view.setAdapter(adapter);
		view.setOnItemClickListener(this);		
		view.requestLayout();
	}
	
	/**
	 * 
	 * Change the list of pics in the gallery.
	 * 
	 * @param pics A string array of the picture names
	 */
	public void setImages(String[] pics) {
		adapter.setFiles(pics);
		if (resourceId!=-1) {
			((Gallery) container.$form().findViewById(resourceId)).setAdapter(adapter);
			((Gallery) container.$form().findViewById(resourceId)).requestLayout();
		} else {
			view.setAdapter(adapter);
			view.requestLayout();
		}		
	}
	
	/**
	 * 
	 * Sets an image to the background of the image gallery
	 * 
	 * @param image The name of the image
	 */
	public void BackgroundImage(String image) {
		String file;
		if (image.contains(".")) {
			file = image.split("\\.")[0];
		} else {
			file = image;
		}
		int img = container.$context().getResources().getIdentifier(file, "drawable", container.$form().getPackageName());
		if (resourceId!=-1) {
			((Gallery) container.$form().findViewById(resourceId)).setBackgroundDrawable(container.$context().getResources().getDrawable(img));
		} else {
			view.setBackgroundDrawable(container.$context().getResources().getDrawable(img));
			view.requestLayout();
		}
	}
	
	/**
	 * Alternate method for setting the background image.
	 * This is used mostly with the SpriteSheetHelper.
	 * 
	 * @param drawable
	 */
	public void Drawable(Drawable drawable) {
		view.setBackgroundDrawable(drawable);
		view.requestLayout();
	}
	
	/**
	 * 
	 * Sets the color of the background of the gallery
	 * 
	 * @param color The color to set
	 */
	public void BackgroundColor(int color) {
		if (resourceId!=-1) {
			((Gallery) container.$form().findViewById(resourceId)).setBackgroundColor(color);
			((Gallery) container.$form().findViewById(resourceId)).invalidate();
		} else {
			view.setBackgroundColor(color);
			view.invalidate();
		}
	}
	
	/**
	 * 
	 * 
	 * @return a string array of the picture names used in the gallery
	 */
	public String[] getImageNames() {
		return adapter.getImageNames();
	}
	
	/**
	 * 
	 * Manually sets the picture size of the images displayed in the
	 * gallery.
	 * 
	 * @param width - Width of the image
	 * @param height  Height of the image
	 */
	public void setPicSize(int width, int height) {
		adapter.setWidth(width);
		adapter.setHeight(height);
		if (resourceId!=-1) {
			((Gallery) container.$form().findViewById(resourceId)).setAdapter(adapter);
			((Gallery) container.$form().findViewById(resourceId)).requestLayout();
		} else {
			view.setAdapter(adapter);
			view.requestLayout();
		}
	}
	
	@Override
	public View getView() {
		
		if (resourceId!=-1) {
			return (Gallery) container.$form().findViewById(resourceId);
		} else {
			return view;
		}
	}
	
	/**
	 * 
	 * Set this to true if you are using images NOT stored
	 * in the drawable folders.
	 * 
	 * @param bool Whether or not to force the use of full paths
	 */
	public void useFullPath(boolean bool) {
		this.useFullPath = bool;
	}
	
	private class ImageAdapter extends BaseAdapter {
		int mGalleryItemBackground;
		private Context mContext;
		
		private String[] picfiles = null;
		private int width = 150;
		private int height = 100;
		
		public ImageAdapter(Context c, int[] resId) {
			mContext = c;			
			TypedArray a = c.obtainStyledAttributes(resId);
			int secondres = container.$form().getResources().getIdentifier("ImageGallery_android_galleryItemBackground", "styleable", container.$form().getPackageName());
			mGalleryItemBackground = a.getResourceId(secondres, 0);
			a.recycle();			
			//mGalleryItemBackground = resId;			
		}
		
		public int getCount() {
			return picfiles.length;
		}
		
		public int getWidth() {
			return width;
		}
		
		public String[] getImageNames() {
			return picfiles;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}
		
		public void setFiles(String[] ids) {
			this.picfiles = ids;
		}
		
		public Object getItem(int position) {
			return position;
		}
		
		public long getItemId(int position) {
			return position;
		}
				
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(mContext);
			if (useFullPath) {
				InputStream is = null;
				try {				
				is = MediaUtil.openMedia(container.$form(), picfiles[position]);
				} catch (FileNotFoundException e) {
					Toast.makeText(mContext, "File Not Found!", Toast.LENGTH_LONG).show();
					return null;
				} catch (IOException e) {
					return null;
					}
			
				//i.setImageBitmap(new BitmapDrawable(is).getBitmap());
				i.setImageDrawable(new BitmapDrawable(is));
			} else {
				int tmp = container.$context().getResources().getIdentifier(picfiles[position], "drawable", container.$context().getPackageName());
				i.setImageResource(tmp);
			}			
			i.setLayoutParams(new Gallery.LayoutParams(width, height));
			i.setScaleType(ImageView.ScaleType.FIT_XY);
			i.setBackgroundResource(mGalleryItemBackground);			
			return i;
		}
		
	}
	
	public static class LayoutParams extends Gallery.LayoutParams {
		public LayoutParams(int width, int height) {
			super(width, height);
		}
	}

	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
		EventDispatcher.dispatchEvent(this, "Click", position);
		
	}
	
	@Override
	public void postAnimEvent() {
		EventDispatcher.dispatchEvent(this, "AnimationMiddle");
		
	}


}
