package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBUtil {
	
	public static List<String> GetColumns(SQLiteDatabase db, String tableName) {
		List<String> ar = null;
		Cursor c = null;
		try {
			c = db.rawQuery("select * from " + tableName + " limit 1", null);
			if (c != null) {
				ar = new ArrayList<String>(Arrays.asList(c.getColumnNames()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return ar;
	}

}
