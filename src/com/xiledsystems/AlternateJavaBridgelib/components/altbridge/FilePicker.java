package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.BaseAdapter;

public class FilePicker extends Picker implements ActivityResultListener, Deleteable {
	
	public static final String FILE_PICKER_ACTIVITY_CLASS = FilePickerActivity.class.getName();
	public static final String LAYOUT_ID = FILE_PICKER_ACTIVITY_CLASS + ".layoutid";
	public static final String ROW_LAYOUT_ID = FILE_PICKER_ACTIVITY_CLASS + ".rowlayoutid";
	public static final String SHOW_HIDDEN = FILE_PICKER_ACTIVITY_CLASS + ".showhidden";
	public static final String FILE_PREFS = FILE_PICKER_ACTIVITY_CLASS + ".prefs";
	public static final String FILE_ADAPTER = FILE_PICKER_ACTIVITY_CLASS + ".adapter";
	public static final String FILE_RESULT = FILE_PICKER_ACTIVITY_CLASS + ".result"; 
	
	
	private int rowLayoutId;
	private int layoutId;
	private String filePath;
	private boolean showHidden;
	
	/**
	 * Constructor for FilePicker.
	 * 
	 * @param container
	 */
	public FilePicker(ComponentContainer container) {
		super(container);
						
	}
	
	/**
	 * Constructor for FilePicker when placed in the GLE
	 * 
	 * @param container
	 * @param resourceId the resource id of the component placed in the GLE
	 */
	public FilePicker(ComponentContainer container, int resourceId) {
		super(container, resourceId);
		
	}
		
	/**
	 * 
	 * @return The absolute file path of the selected file.
	 */
	public String SelectedFilePath() {
		return filePath;
	}
	
	/**
	 * Use this if you wish you specify your own layout for
	 * the picker's activity. You must have a listview with the
	 * resource id of android.R.id.list. There are a few hooks
	 * for other things as well. A "top" button which returns
	 * to the top level directory available (this is determined
	 * by Environment.getExternalStorageDirectory(). Note that this
	 * does NOT necessarily mean external storage. This is the top
	 * level directory the normal user has write access to if the
	 * phone is not rooted). The top button's id should be "topBtn". 
	 * There's also the back button, which should have the id "backBtn".
	 * There is also a hook for a label to output the current directory.
	 * That id should be "directoryLabel".
	 * 
	 * Custom listviews will be coming at a later date.
	 * 
	 * @param layoutId The resource id of the custom layout to use
	 */
	public void Layout(int layoutId) {
		this.layoutId = layoutId;
	}
	
	public void rowLayout(int rowLayoutId) {
		this.rowLayoutId = rowLayoutId;
	}
	
	public void ShowHiddenFilesAndFolders(boolean showHidden) {
		this.showHidden = showHidden;
	}

	@Override
	public void resultReturned(int requestCode, int resultCode, Intent data) {
		if (requestCode == this.requestCode && resultCode == Activity.RESULT_OK) {
			if (data.hasExtra(FILE_RESULT)) {
				filePath = data.getStringExtra(FILE_RESULT);
			} else {
				filePath = "";
			}
			AfterPicking();
		}
	}
	
	
	@Override
	protected Intent getIntent() {
		Intent intent = new Intent();
		intent.setClassName(container.$context(), FILE_PICKER_ACTIVITY_CLASS);
		if (layoutId > 0) {
			intent.putExtra(LAYOUT_ID, layoutId);			
		}
		if (rowLayoutId > 0) {
			intent.putExtra(ROW_LAYOUT_ID, rowLayoutId);
		}
		if (showHidden) {
			intent.putExtra(SHOW_HIDDEN, showHidden);
		}
				
		return intent;	
	}

	@Override
	public void onDelete() {
		container.$form().unregisterForActivityResult(this);
	}


}
