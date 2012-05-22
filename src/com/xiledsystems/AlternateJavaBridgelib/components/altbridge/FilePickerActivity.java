package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.io.File;
import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class FilePickerActivity extends ListActivity {
	
	private static final String TOP_LEVEL = Environment.getExternalStorageDirectory().getPath();
	private static final String FILE = "File";
	private static final String DIR = "Directory";
	private static final String TAG = "FilePicker";
	
	private Button top;
	private Button back;
	
	private TextView directoryLabel;
	
	private String currentDir = "";
	private String[] fileNames;
	private String[] fileLocations;
	private String[] fileOrDir;
	private int rowlayoutid;
	private FileAdapter mAdapter;
	private ArrayList<FileInfo> files;
	private boolean showHidden;
			
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		
		if (intent.hasExtra(FilePicker.LAYOUT_ID)) {
			setContentView(intent.getIntExtra(FilePicker.LAYOUT_ID, android.R.layout.simple_list_item_2));
			top = (Button) findViewById(getResources().getIdentifier("topBtn", "id", getPackageName()));
			// Check to make sure it exists, if not, throw a warning to the log			
			if (top != null) {
				top.setOnClickListener(new ClickListener());
			}
			back = (Button) findViewById(getResources().getIdentifier("backBtn", "id", getPackageName()));
			if (back != null) {
				back.setOnClickListener(new ClickListener());
			}
			directoryLabel = (TextView) findViewById(getResources().getIdentifier("directoryLabel", "id", getPackageName()));				
		}
		if (intent.hasExtra(FilePicker.ROW_LAYOUT_ID)) {
			rowlayoutid = intent.getIntExtra(FilePicker.ROW_LAYOUT_ID, 0);
		}
		if (intent.hasExtra(FilePicker.SHOW_HIDDEN)) {
			showHidden = true;
		}
		
		fillList();
				
	}
	
	private void fillList() {
		
		File file = new File(Environment.getExternalStorageDirectory(), currentDir);
		File[] filelist = file.listFiles();
		
		if (rowlayoutid > 0) {
			
			files = new ArrayList<FilePickerActivity.FileInfo>();
			for (int i = 0; i < filelist.length; i++) {
				FileInfo f = new FileInfo();
				String n = filelist[i].getName();
				if (!showHidden && n.startsWith(".")) {
					// Don't add it to the list if showhidden isn't true
					// otherwise, add the file to the list
				} else {
					f.setName(n);
					f.setPath(filelist[i].getAbsolutePath());
					if (filelist[i].isDirectory()) {					
						f.setFileorDir(DIR);
					} else {					
						f.setFileorDir(FILE);
					}
					files.add(f);
				}
				
			}
			
			mAdapter = new FileAdapter(this, rowlayoutid, files);
			setListAdapter(mAdapter);
			
		} else {
			
			fileNames = new String[filelist.length];
			fileLocations = new String[filelist.length];
			fileOrDir = new String[filelist.length];
			
			for (int i = 0; i < filelist.length; i++) {		
				String n = filelist[i].getName();
				if (!showHidden && n.startsWith(".")) {
					// Don't add it to the list if it's hidden, and showhidden isnt true
				} else {
					fileNames[i] = filelist[i].getName();			
					fileLocations[i] = filelist[i].getAbsolutePath();				
					if (filelist[i].isDirectory()) {
						fileOrDir[i] = DIR;					
					} else {
						fileOrDir[i] = FILE;					
					}
				}								
			}
			MatrixCursor c = new MatrixCursor(new String[] { "_id", "Header", "Content" });
			int size = filelist.length;
			for (int i = 0; i < size; i++) {
				if (fileNames[i] != null || !fileNames[i].equals("")) {
					c.addRow(new Object[] { i, fileNames[i], fileOrDir[i] });
				}
			}
			setListAdapter(new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, c, 
					new String[] { "Header", "Content" }, new int[] { android.R.id.text1, android.R.id.text2 }));
			getListView().setTextFilterEnabled(true);
		}		
		
		if (directoryLabel != null) {			
			directoryLabel.setText(currentDir + "/");
		}		
		
	}	
	
	@Override
	public void onBackPressed() {
		if (currentDir.equals("")) {
			super.onBackPressed();
		} else {
			int lastIndex = currentDir.lastIndexOf("/");
			if (lastIndex == 0) {
				currentDir = "";
			} else {
				currentDir = currentDir.substring(0, lastIndex );
			}
			fillList();
		}
	}
	
	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		if (rowlayoutid > 0) {
			FileInfo f = files.get(position);
			if (f.getFileorDir().equals(FILE)) {
				Intent resultIntent = new Intent();
				resultIntent.putExtra(FilePicker.FILE_RESULT, f.getPath());
				setResult(RESULT_OK, resultIntent);
				finish();
			} else {
				currentDir = currentDir + "/" + f.getName();
				fillList();
			}
		} else {
			if (fileOrDir[position].equals(FILE)) {
				Intent resultIntent = new Intent();
				resultIntent.putExtra(FilePicker.FILE_RESULT, fileLocations[position]);
				setResult(RESULT_OK, resultIntent);
				finish();
			} else {			
				currentDir = currentDir + "/" + fileNames[position];
				fillList();
			}
		}
		
	}	
	
	private class FileInfo {
		
		private String name;
		private String path;
		private String fileorDir;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getPath() {
			return path;
		}
		public void setPath(String path) {
			this.path = path;
		}
		public String getFileorDir() {
			return fileorDir;
		}
		public void setFileorDir(String fileorDir) {
			this.fileorDir = fileorDir;
		}		
	}
	
	private class FileAdapter extends ArrayAdapter<FileInfo> {
		
		private ArrayList<FileInfo> files;
		private static final String FILE = "File";
		private static final String DIR = "Directory";
		
		public FileAdapter(Context context, int textviewResourceId, ArrayList<FileInfo> files) {
			super(context, textviewResourceId, files);
			this.files = files;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);				
				v = vi.inflate(rowlayoutid, null);
			}
			FileInfo f = files.get(position);
			if (f != null) {
				int textview = getResources().getIdentifier("text1", "id", getPackageName());		
				int imgviewId = getResources().getIdentifier("imageView1", "id", getPackageName());
				int imgId;
				String fileordir = f.getFileorDir();
				if (fileordir.equals(FILE)) {
					imgId = getResources().getIdentifier("file_icon", "drawable", getPackageName());
				} else {
					imgId = getResources().getIdentifier("folder_icon", "drawable", getPackageName());
				}
				TextView tv = (TextView) v.findViewById(textview);	
				ImageView imgview = (ImageView) v.findViewById(imgviewId);
				if (tv != null) {
					tv.setText(f.getName());
				}
				if (imgview != null) {
					imgview.setImageDrawable(getResources().getDrawable(imgId));
				}				
			}
			return v;
		}
		
	}
	
	private class ClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (v.equals(top)) {
				currentDir = "";
				fillList();
				
			} else if (v.equals(back)) {
				if (currentDir.equals("")) {
					finish();
				} else {
					int lastIndex = currentDir.lastIndexOf("/");
					if (lastIndex == 0) {
						currentDir = "";
					} else {
						currentDir = currentDir.substring(0, lastIndex );
					}
					fillList();
				}
			}
		}
		
	}

}
