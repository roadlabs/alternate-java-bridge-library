package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.DBUtil;


public class SimpleSQL extends AndroidNonvisibleComponent implements OnDestroySvcListener, OnResumeListener, 
				OnStopListener {

	private String DATABASE_NAME;
	private int DATABASE_VERSION = 1;
	private static String TABLE_NAME = "table1";
	private String COLUMN_ID = "_id";
	@SuppressWarnings("unused")
	private String COLUMN_NAME = "column1";
	private String DATABASE_CREATE; 
	private SQLiteDatabase db;
	private BigDBSqlOpenHelper dbHelper;
	private DBBuilder builder;
	private String[] whereArgs;
	private int batchCount;
	private int maxBatchCount = 2000;
	
	private boolean batch;
			
	@Override
	public void onStop() {
		if (batch) {
			db.setTransactionSuccessful();
			db.endTransaction();
		}
		if (db != null) {
			db.close();
			db.releaseMemory();
			db = null;
		}
	}

	@Override
	public void onResume() {
		dbCheck();	
	}
	
	/**
	 * Checks to see if the db is null, if it is, it reopens it
	 * Mostly used internally, but while restoring/backing up error
	 * handling, it can be useful
	 * 
	 */
	public void dbCheck() {
		if (db == null) {
			db = dbHelper.getWritableDatabase();
		}
	}
	
	public synchronized void closeDB() {
		if (db != null) {
			db.close();
			db.releaseMemory();
			db = null;			
		}
	}
	
	/**
	 * Use this to open a seperate db file (Perfect for backup and restoring
	 * your db)
	 * 
	 * @param path The absolute path to the db file
	 * @throws SQLiteDatabaseCorruptException - This is thrown if the db is corrupt, or not a Sqlite db file. It may also be
	 * thrown when the builder doesn't match the table/columns in the specified db file
	 */
	public synchronized void openDB(String path) throws SQLiteException {
		if (path != null) {
			if (db != null && db.isOpen()) {
				db.close();
			}			
			db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
		}
	}
	
	/**
	 * 
	 * Constructor for SimpleSQL component. This requires you to define
	 * the table structure using DBBuilder before creating the
	 * database.
	 * 
	 * @param form Always use this
	 * @param builder The table/column configuration helper. If builder is null, this will cause a RuntimeException.
	 */
	public SimpleSQL(Form form, DBBuilder builder) {
		super(form);
		DATABASE_NAME = form.getApplication().getPackageName() + "-SQL.db";	
		if (builder == null) {
			throw new RuntimeException("SimpleSQL: Builder passed to DB is null!");
		}
		this.builder = builder;
		if (builder.Version() != 1) {
			DATABASE_VERSION = builder.Version();
		}
		if (builder.DBName() != null) {
			DATABASE_NAME = builder.DBName();
		}
		form.registerForOnResume(this);
		form.registerForOnStop(this);
		dbHelper = new BigDBSqlOpenHelper(form.$context());			
		db = dbHelper.getWritableDatabase();		
	}
	
	/**
	 * 
	 * Constructor for SimpleSQL component. This requires you to define
	 * the table structure using DBBuilder before creating the
	 * database.
	 * 
	 * @param formservice Always use this
	 * @param builder The table/column configuration helper. If builder is null, this will cause a RuntimeException.
	 */
	public SimpleSQL(FormService formservice, DBBuilder builder) {
		super(formservice);
		DATABASE_NAME = formservice.getApplicationContext().getPackageName() + "-SQL.db";
		if (builder == null) {
			throw new RuntimeException("SimpleSQL: Builder passed to DB is null!");
		}
		this.builder = builder;
		if (builder.Version() != 1) {
			DATABASE_VERSION = builder.Version();
		}
		if (builder.DBName() != null) {
			DATABASE_NAME = builder.DBName();
		}
		formservice.registerForOnDestroy(this);
		dbHelper = new BigDBSqlOpenHelper(formservice.$context());
		db = dbHelper.getWritableDatabase();		
	}
	
	/**
	 * Returns the path of the database file.	
	 * @return
	 */
	public String getDBPath() {
		dbCheck();
		return db.getPath();
	}
	
	/**
	 * Use this when inserting, or updating a lot of entries. You MUST
	 * remember to run FinishBatch() after you are done
	 * with your inserts/updates. This will not work for reading
	 * the database.
	 * 
	 */
	public synchronized void BatchWrite() {
		dbCheck();
		batch = true;
		db.beginTransaction();				
	}
	
	/**
	 * Use this after performing a batch of inserts/updates, 
	 * (if you have run BatchWrite() prior).
	 */
	public synchronized void FinishBatch() {
		dbCheck();
		batch = false;
		db.setTransactionSuccessful();
		db.endTransaction();		
		batchCount = 0;
	}
	
	/**
	 * Only needed in rare situations. This just sets the boolean which
	 * controls the isInBatchMode method. Only use this if you start
	 * batch mode, then need to stop batch mode, but haven't actually
	 * written anything to the db. If you call FinishBatch() without
	 * having written anything to the db, it will cause an 
	 * IllegalStateException to be thrown.
	 */
	public synchronized void unBatch() {
		batch = false;
		batchCount = 0;
	}
	
	/**
	 * Use this method carefully. The default is 2000 transactions per batch.
	 * You can use this method to override the default to another number. As you
	 * increase the number, you are increasing the amount of RAM the db takes up, 
	 * and could cause out of memory crashes if it's set too large.
	 * 
	 * @param count the maximum amount of transactions allowed in a BatchWrite
	 */
	public void MaxBatchCount(int count) {
		maxBatchCount = count;
	}
	
	/**
	 * 
	 * @return The maximum transactions in a batch. The default is 2000. The higher you
	 * go, the more memory it will take up, possibly leading to Out of Memory
	 * crashes. 
	 */
	public int getMaxBatchCount() {
		return maxBatchCount;
	}
				
	/**
	 * 
	 * This is for advanced users! This method returns a Cursor
	 * object, which you'll have to deal with. Make sure it is
	 * closed when you are done, or you'll run into memory leaks.
	 * 
	 * @param queryString The query string to pass to the sqlite method db.rawQuery()
	 * @param selectionArgs any values used in the where statement
	 * @return the cursor object with the data returned. Make sure to close it when done!
	 */
	public synchronized Cursor AdvancedQuery(String queryString, String[] selectionArgs) {
		dbCheck();
		if (!batch) {
			return db.rawQuery(queryString, selectionArgs);
		}
		return null;
	}
	
	/**
	 * 
	 * Insert data into the database in the specified table
	 * and column name. The method returns the rowId resulting
	 * from the add to the database.
	 * 
	 * @param table - The table to add data to
	 * @param columnName - The column to add data into
	 * @param data - The data to put into the database
	 * @return returns a long of the rowId. -1 will be returned if the insert failed.
	 */	
	public synchronized long Insert(String table, String... items) {
		dbCheck();
		if (batch) {
			if (batchCount >= maxBatchCount) {
				db.setTransactionSuccessful();
				db.endTransaction();
				batchCount = 0;
				db.beginTransaction();
			}
			batchCount++;
		}
		if (items != null) {
			int amt = items.length;
			int	tableid = builder.TableId(table);
			ContentValues values = new ContentValues();
			for (int i = 0; i < amt; i++) {
				values.put(builder.Column(tableid)[i], items[i]);
			}		
			try {
				long rowId = db.insert(table, null, values);
				return 	rowId;
			} catch (SQLException e) {
				Log.e("SimpleSQL", "Unable to insert data. Either the table doesn't exist, or incorrect amount of data items." + table);
				e.printStackTrace();
				return -1;
			}
		} else {			
			try {
				long rowid = db.insert(table, null, null);
				return rowid;
			} catch (SQLException e) {
				Log.e("SimpleSQL", "Unable to insert data. Either the table doesn't exist, or incorrect amount of data items." + table);
				e.printStackTrace();
				return -1;
			}
		}
	}
	
	/**
	 * 
	 * Use this method to insert data into one specific column in a table.
	 * 
	 * @param table The table to insert to
	 * @param columntoaddto The column you want to add data into
	 * @param data The data to store
	 * @return The rowid of the newly inserted data
	 */
	public synchronized long Insert(String table, String columntoaddto, String data) {	
		dbCheck();
		if (batch) {
			if (batchCount >= maxBatchCount) {
				db.setTransactionSuccessful();
				db.endTransaction();
				batchCount = 0;
				db.beginTransaction();
			}
			batchCount++;
		}
		ContentValues values = new ContentValues();
		values.put(columntoaddto, data);
		try {
			long rowid = db.insert(table, null, values);
			return rowid;
		} catch (SQLException e) {
			Log.e("SimpleSQL", "Unable to insert data. Either the table doesn't exist, or incorrect amount of data items." + table);
			e.printStackTrace();
			return -1;
		}		
	}
	
	/**
	 * Clear all records (rows) in the specified table.
	 * This is destructive, once run, that table's data will
	 * be gone forever.
	 * 
	 * @param table The table to clear
	 * 
	 * @return The number of rows deleted from the table. If the table doesn't exist,
	 *  -1 will be thrown.
	 */
	public synchronized int ClearTable(String table) {	
		dbCheck();
		if (builder.containsTable(table)) {
			int rmvd = db.delete(table, "1", null);
			db.execSQL("VACUUM");
			db.close();			
			db = null;
			db = dbHelper.getWritableDatabase();
			return rmvd;
		}
		return -1;
	}
	
	/**
	 * 
	 *  Deletes the table from the database. This will delete all
	 *  data in the table as well.
	 *  
	 * @param table The table to delete
	 * @return False if the table wasnt found.
	 */
	public synchronized boolean DeleteTable(String table) {
		dbCheck();
		if (builder.removeTable(table)) {
			db.execSQL("DROP TABLE IF EXISTS "+table);
			db.close();
			db = null;
			db = dbHelper.getWritableDatabase();
			return true;
		} else {
			return false;
		}
	}
			
	/**
	 * 
	 * Update the data in an existing row.
	 * 
	 * @param table The table the row exists in
	 * @param column The column of the data you'd like to change
	 * @param rowid The rowid where the data resides
	 * @param data The new data to store in this location
	 */
	public synchronized void Update(String table, String column, long rowid, String data) {
		dbCheck();
		if (batch) {
			if (batchCount >= maxBatchCount) {
				db.setTransactionSuccessful();
				db.endTransaction();
				batchCount = 0;
				db.beginTransaction();
			}
			batchCount++;
		}
		ContentValues values = new ContentValues();
		values.put(column, data);
		String where = "_id=?";
		String[] whereArgs = { String.valueOf(rowid) } ;
		db.update(table, values, where, whereArgs);		
	}
	
	/**
	 * "Simplified" query method for returning data from the database. This can
	 * support two where statements combined by either AND, or OR. If you need
	 * to run a more complicated query, use Query3.
	 * 
	 * @param table The table to search
	 * @param whereStatement Your where statement. Do NOT include the word where ex: "name=John AND purchases>25"
	 * @param ColumnsToReturn The columns of data you want returned.
	 * @return An ArrayList<ArrayList<String>> of all the data returned from the database.
	 */
	public synchronized ArrayList<ArrayList<String>> Query(String table, String whereStatement, String... ColumnsToReturn) {		
		dbCheck();
		if (!batch) {
			ArrayList<ArrayList<String>> biglist = new ArrayList<ArrayList<String>>();
			ArrayList<String> smallist = new ArrayList<String>();
			if (ColumnsToReturn == null || ColumnsToReturn.length < 1) {
				int tble = builder.TableId(table);
				ColumnsToReturn = builder.Column(tble).clone();
			} 
			whereStatement = parseWhereStmt(whereStatement);		
			synchronized (db) {
				Cursor cursor = db.query(table, ColumnsToReturn, whereStatement, whereArgs, null, null, null);
				if (cursor.moveToFirst()) {
					int length = cursor.getColumnCount();
					do {
						smallist.clear();
						for (int i = 0; i < length; i++) {
							smallist.add(cursor.getString(i));
						}
						biglist.add(new ArrayList<String>(smallist));
					
					} while (cursor.moveToNext());
				}
			}				
			return new ArrayList<ArrayList<String>>(biglist);
		}
		throw new RuntimeException("SimpleSQL received a query call while in BatchWrite mode!");
	}
	
	/**
	 * "Simplified" query method for returning data from the database. This can
	 * support two where statements combined by either AND, or OR. If you need
	 * to run a more complicated query, use Query3. This Query is only different
	 * from Query in that you can set a limit of the amount of rows returned.
	 * 
	 * @param table The table to search
	 * @param whereStatement Your where statement. Do NOT include the word where ex: "name=John AND purchases>25"
	 * @param limit The maximum amount of rows you want to pull from the database
	 * @param ColumnsToReturn The columns of data you want returned.
	 * @return An ArrayList<ArrayList<String>> of all the data returned from the database.
	 */
	public synchronized ArrayList<ArrayList<String>> Query(String table, String whereStatement, int limit, String... ColumnsToReturn) {		
		dbCheck();
		if (!batch) {
			ArrayList<ArrayList<String>> biglist = new ArrayList<ArrayList<String>>();
			ArrayList<String> smallist = new ArrayList<String>();
			if (ColumnsToReturn == null || ColumnsToReturn.length < 1) {
				int tble = builder.TableId(table);
				ColumnsToReturn = builder.Column(tble).clone();
			} 			
			whereStatement = parseWhereStmt(whereStatement);		
			synchronized (db) {
				Cursor cursor = db.query(table, ColumnsToReturn, whereStatement, whereArgs, null, null, null, limit+"");
				if (cursor.moveToFirst()) {
					int length = cursor.getColumnCount();
					do {
						smallist.clear();
						for (int i = 0; i < length; i++) {
							smallist.add(cursor.getString(i));
						}
						biglist.add(new ArrayList<String>(smallist));
					
					} while (cursor.moveToNext());
				}
			}				
			return biglist;
		}
		throw new RuntimeException("SimpleSQL received a query call while in BatchWrite mode!");
	}
	
	/**
	 * For more advanced users. This allows you to pass arguments to the db just
	 * like the "regular" android sqlite wants them. Basically, if you need to
	 * use a really complicated where statement, this is how you'll have to
	 * do it.
	 * 
	 * @param table The table to search
	 * @param columns The columns you'd like returned
	 * @param selection This is the where statement. Use ? for all values here ( ex: name=? AND number=? AND longitude>? )
	 * @param selectionArgs This is a string array of the values. So, from the above example ( new String[] { "John", "5555555555", "43" } )
	 * @param groupBy more options for the cursor, usually just null
	 * @param having again more options for the cursor, usually null
	 * @param orderBy yet again, same as above, usually null
	 * @return An ArrayList<ArrayList<String>> of the results. This is a list of a list. (Each inner list contains a row of data)
	 */
	public synchronized ArrayList<ArrayList<String>> Query3(String table, String[] columns, String selection, 
			String[] selectionArgs, String groupBy, String having, String orderBy) {
		dbCheck();
		if (!batch) {
			final ArrayList<ArrayList<String>> biglist = new ArrayList<ArrayList<String>>();
			final ArrayList<String> list = new ArrayList<String>();
			synchronized (db) {				
				Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
				int cols = cursor.getColumnCount();			
				if (cursor.moveToFirst()) {
					int tableid = builder.TableId(table);
					do {				
						list.clear();
						for (int i = 0; i < cols; i++) {
							if (columns == null) {
								if (i==0) {
									
								} else {								
									list.add(cursor.getString(i));							
								}
							} else {
								
								list.add(cursor.getString(i));
							}
						}
						biglist.add(list);
					} while (cursor.moveToNext());
				}
			}		
			return biglist;
		}
		throw new RuntimeException("SimpleSQL received a query call while in BatchWrite mode!");
	}
	
	/**
	 * For more advanced users. This allows you to pass arguments to the db just
	 * like the "regular" android sqlite wants them. Basically, if you need to
	 * use a really complicated where statement, this is how you'll have to
	 * do it.
	 * 
	 * @param table The table to search
	 * @param columns The columns you'd like returned
	 * @param selection This is the where statement. Use ? for all values here ( ex: name=? AND number=? AND longitude>? )
	 * @param selectionArgs This is a string array of the values. So, from the above example ( new String[] { "John", "5555555555", "43" } )
	 * @param groupBy more options for the cursor, usually just null
	 * @param having again more options for the cursor, usually null
	 * @param orderBy yet again, same as above, usually null
	 * @return An ArrayList<ArrayList<String>> of the results. This is a list of a list. (Each inner list contains a row of data)
	 */
	public synchronized ArrayList<ArrayList<String>> Query3(String table, String[] columns, String selection, 
			String[] selectionArgs, int limit, String groupBy, String having, String orderBy) {
		dbCheck();
		if (!batch) {
			final ArrayList<ArrayList<String>> biglist = new ArrayList<ArrayList<String>>();
			final ArrayList<String> list = new ArrayList<String>();
			synchronized (db) {				
				
				Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, String.valueOf(limit));				
				int cols = cursor.getColumnCount();			
				if (cursor.moveToFirst()) {
					int tableid = builder.TableId(table);
					do {				
						list.clear();
						for (int i = 0; i < cols; i++) {
							if (columns == null) {
								if (i==0) {
									
								} else {									
										list.add(cursor.getString(i));									
								}
							} else {								
									list.add(cursor.getString(i));								
							}
						}
						biglist.add(list);
					} while (cursor.moveToNext());
				}
			}		
			return biglist;
		}
		throw new RuntimeException("SimpleSQL received a query call while in BatchWrite mode!");
	}
	
	private String parseWhereStmt(String whereStatement) {
		// parse the where statement, and pass back the modified
		// where statement
		String firstcol="";
		String secondcol="";
		if (whereStatement.contains("AND")) {			
			String andFirst = whereStatement.split("AND")[0];
			String andSecond = whereStatement.split("AND")[1];
			whereArgs = new String[2];
			firstcol = processFirstSecond(andFirst, andSecond)[0];
			secondcol = processFirstSecond(andFirst, andSecond)[1];			
			whereStatement = firstcol + " AND " + secondcol;
		} else if (whereStatement.contains("OR")) {
			String andFirst = whereStatement.split("OR")[0];
			String andSecond = whereStatement.split("OR")[1];
			whereArgs = new String[2];
			firstcol = processFirstSecond(andFirst, andSecond)[0];
			secondcol = processFirstSecond(andFirst, andSecond)[1];			
			whereStatement = firstcol + " OR " + secondcol;
		} else {
			whereStatement = processOne(whereStatement);
		}
		return whereStatement;
	}
	
	private String processOne(String stmt) {
		String firstcol="";
		whereArgs = new String[1];
		if (stmt.contains("=")) {
			firstcol = stmt.split("=")[0]+"=?";
			whereArgs[0] = stmt.split("=")[1];				
		}
		if (stmt.contains("!=")) {
			firstcol = stmt.split("!=")[0]+"!=?";
			whereArgs[0] = stmt.split("!=")[1];				
		}
		if (stmt.contains("<")) {
			firstcol = stmt.split("\\<")[0]+"<?";
			whereArgs[0] = stmt.split("\\<")[1];				
		}
		if (stmt.contains("<=")) {
			firstcol = stmt.split("\\<=")[0]+"<=?";
			whereArgs[0] = stmt.split("\\<=")[1];				
		}
		if (stmt.contains(">")) {
			firstcol = stmt.split("\\>")[0]+">?";
			whereArgs[0] = stmt.split("\\>")[1];				
		}
		if (stmt.contains(">=")) {
			firstcol = stmt.split("\\>=")[0]+">=?";
			whereArgs[0] = stmt.split("\\>=")[1];				
		}
		if (stmt.contains("<>")) {
			firstcol = stmt.split("\\<\\>")[0]+"<>?";
			whereArgs[0] = stmt.split("\\<\\>")[1];				
		}
		return firstcol;
	}
	
	private String[] processFirstSecond(String andFirst, String andSecond) {
		String firstcol="";
		String secondcol="";
		if (andFirst.contains("=")) {
			firstcol = andFirst.split("=")[0]+"=?";
			whereArgs[0] = andFirst.split("=")[1];				
		}
		if (andSecond.contains("=")) {
			secondcol = andSecond.split("=")[0]+"=?";
			whereArgs[1] = andSecond.split("=")[1];
		}
		if (andFirst.contains("!=")) {
			firstcol = andFirst.split("!=")[0]+"!=?";
			whereArgs[0] = andFirst.split("!=")[1];				
		}
		if (andSecond.contains("!=")) {
			secondcol = andSecond.split("!=")[0]+"!=?";
			whereArgs[1] = andSecond.split("!=")[1];
		}
		if (andFirst.contains("<")) {
			firstcol = andFirst.split("\\<")[0]+"<?";
			whereArgs[0] = andFirst.split("\\<")[1];				
		}
		if (andSecond.contains("<")) {
			secondcol = andSecond.split("\\<")[0]+"<?";
			whereArgs[1] = andSecond.split("\\<")[1];
		}
		if (andFirst.contains("<=")) {
			firstcol = andFirst.split("\\<=")[0]+"<=?";
			whereArgs[0] = andFirst.split("\\<=")[1];				
		}
		if (andSecond.contains("<=")) {
			secondcol = andSecond.split("\\<=")[0]+"<=?";
			whereArgs[1] = andSecond.split("\\<=")[1];
		}
		if (andFirst.contains(">")) {
			firstcol = andFirst.split("\\>")[0]+">?";
			whereArgs[0] = andFirst.split("\\>")[1];				
		}
		if (andSecond.contains(">")) {
			secondcol = andSecond.split("\\>")[0]+">?";
			whereArgs[1] = andSecond.split("\\>")[1];
		}
		if (andFirst.contains(">=")) {
			firstcol = andFirst.split("\\>=")[0]+">=?";
			whereArgs[0] = andFirst.split("\\>=")[1];				
		}
		if (andSecond.contains(">=")) {
			secondcol = andSecond.split("\\>=")[0]+">=?";
			whereArgs[1] = andSecond.split("\\>=")[1];
		}
		if (andFirst.contains("<>")) {
			firstcol = andFirst.split("\\<\\>")[0]+"<>?";
			whereArgs[0] = andFirst.split("\\<\\>")[1];				
		}
		if (andSecond.contains("<>")) {
			secondcol = andSecond.split("\\<\\>")[0]+"<>?";
			whereArgs[1] = andSecond.split("\\<\\>")[1];
		}
		return new String[] { firstcol, secondcol };
	}

		
		
	/**
	 * 
	 * Removes a row from the specified table
	 * 
	 * @param table The table to remove the row from
	 * @param id The rowid to remove
	 */
	public synchronized void RemoveRow(String table, long id) {
		dbCheck();
		db.delete(table, "_id=?", new String[] { String.valueOf(id) });
	}
	
	/**
	 * 
	 * @return If the db is locked by a thread, either current, or other threads.
	 */
	public synchronized boolean isLocked() {
		dbCheck();
		if (db.isDbLockedByCurrentThread() || db.isDbLockedByOtherThreads()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Helper method to see if some data is actually in the
	 * table provided.
	 * Note: This is a heavy operation if you have a large database, as it
	 * scans the entire table. You're probably better off trying to get the
	 * rowId of the data, and if it returns -1, it's not there. However, this
	 * is a more broad search (searches the whole table rather than a column)
	 * 
	 * @param table Table to check data in
	 * @param data The data to check
	 * @return true if the data is in the database, false if not
	 */	
	public synchronized boolean InTable(String table, Object data) {	
		dbCheck();
		if (!batch) {
			String querystring = "select * from " + table;		
			synchronized (db) {
				Cursor cursor = db.rawQuery(querystring, null);
				int length = cursor.getColumnCount();			
				if (cursor.moveToFirst()) {		
					int tableid = builder.TableId(table);
					do {				
						for (int i = 0; i < length; i++) {
							if (i==0) {
							
							} else {
								/*if (builder.DataTypes(tableid)[i-1].equalsIgnoreCase("integer")) {
									if (((Integer) cursor.getInt(i)).equals(data)) {
										return true;
									}
								} else if (builder.DataTypes(tableid)[i-1].equalsIgnoreCase("real")) {
									if (((Long) cursor.getLong(i)).equals(data)) {
										return true;
									}
								} else {*/
									if (cursor.getString(i).equals(data)) {
										return true;
									}	
								}
							}
						//}				
					} while (cursor.moveToNext());
				}
			}				
			return false;
		}
		throw new RuntimeException("SimpleSQL received a query call while in BatchWrite mode!");
	}
	
	/**
	 * 
	 * @param table The table to search
	 * @return an int of the amount of rows in the table. This may change
	 * to a long at some point.
	 */
	public synchronized int GetRowCount(String table) {
		dbCheck();
		if (!batch) {
			return db.rawQuery("select * from "+ table, null).getCount();
		}
		throw new RuntimeException("SimpleSQL received a query call while in BatchWrite mode!");
	}
	
	/**
	 * Grab a whole row of data from a table, of the specified rowid
	 * 
	 * @param table The table where the data resides
	 * @param rowId The rowid of the row you'd like to grab
	 * @return An ArrayList<String> of all the data in the specified table, and rowid
	 */
	public synchronized ArrayList<String> GetRow(String table, long rowId) {
		dbCheck();
		if (!batch) {
			ArrayList<String> list = new ArrayList<String>();
			synchronized (db) {
				Cursor cursor = db.query(table, null, COLUMN_ID + "=" + rowId, null, null, null, null);			
				if (cursor.moveToFirst()) {
					int tableid = builder.TableId(table);
					int count = cursor.getColumnCount();
					for (int i = 0; i < count; i++) {
						if (i==0) {						
						} else {
							/*if (builder.DataTypes(tableid)[i-1].equalsIgnoreCase("integer")) {
								list.add(String.valueOf(cursor.getInt(i)));
							} else if (builder.DataTypes(tableid)[i-1].equalsIgnoreCase("real")) {
								list.add(String.valueOf(cursor.getLong(i)));
							} else {*/
								list.add(cursor.getString(i));
							//}
						}
					}			
				}
			}
		
			return list;
		}
		throw new RuntimeException("SimpleSQL received a query call while in BatchWrite mode!");
	}
	
	/**
	 * Use this method to get the rowid of a piece of data.
	 * If the data is not found, -1 will be returned.
	 * 
	 * @param table The table to search
	 * @param column The column to search in
	 * @param data The data to look for. This is case sensitive
	 * @return the rowid of the data (long)
	 */
	
	public synchronized long GetRowID(String table, String column, String data) {
		dbCheck();
		if (!batch) {
			synchronized (db) {
				Cursor cursor = db.query(table, new String[] { "_id" }, column+"=?", new String[] { data }, null, null, null);
				if (cursor.moveToFirst()) {
					return cursor.getLong(0);
				}					
			}
			return -1;
		}
		throw new RuntimeException("SimpleSQL received a query call while in BatchWrite mode!");
	}
	
		
	/**
	 * Use this method to get a single item from the database. This requires
	 * the rowid of the item. Use GetRowID to obtain the rowid.
	 * 
	 * @param table The table the value resides in
	 * @param columnName The column the value resides in
	 * @param id the rowid of the data
	 * @return a String of the data stored in the specified position
	 */
	public synchronized String GetValue(String table, String columnName, long id) {
		dbCheck();
		if (!batch) {
			String rtn;
			synchronized (db) {
				String[] column = { COLUMN_ID, columnName };
				Cursor cursor = db.query(table, column, COLUMN_ID + "=" + id, null, null, null, null);
				if (cursor.moveToFirst()) {		
					int colid = builder.ColumnPosition(table, columnName);
					int tableid = builder.TableId(table);
			
					/*if (builder.DataTypes(tableid)[colid].equalsIgnoreCase("integer")) {
						rtn = String.valueOf(cursor.getInt(1));
					} else if (builder.DataTypes(tableid)[colid].equalsIgnoreCase("real")) {
						rtn = String.valueOf(cursor.getLong(1));
					} else {*/
						rtn = cursor.getString(1);
					//}
				} else {
					Log.e("SimpleSQL", "List is empty. Are you sure you have a valid rowid?");
					rtn = "";
				}
			}		
			return rtn;
		}
		throw new RuntimeException("SimpleSQL received a query call while in BatchWrite mode!");
	}
	
	/**
	 * Get all data in a column. This will return an empty list
	 * if there is no data in the column. An error will also be
	 * sent into logcat.
	 * 
	 * @param table The table of the column you want returned
	 * @param column The column you'd like returned
	 * @return an ArrayList<String> of all the values in the column.
	 */
	public synchronized ArrayList<String> GetColumn(String table, String column) {
		dbCheck();
		if (!batch) {
			ArrayList<String> list = new ArrayList<String>();
			Cursor cursor = db.rawQuery("select "+column+" from "+table, null);
			int size = cursor.getCount();
			if (size==0) {
				Log.e("SimpleSQL", "Can't get column, as it's empty!");			
			} else {
				int clm = builder.ColumnPosition(table, column);
				int tblid = builder.TableId(table);
				//boolean nmbr = (builder.DataTypes(tblid)[clm].equalsIgnoreCase("integer"));
				//boolean flote = (builder.DataTypes(tblid)[clm].equalsIgnoreCase("real"));		
				cursor.moveToFirst();
				for ( int i = 0; i < size ; i++) {
					/*if (nmbr) {
						list.add(String.valueOf(cursor.getInt(0)));
					} else if (flote) {
						list.add(String.valueOf(cursor.getLong(0)));
					} else {*/
						list.add(cursor.getString(0));
					//}			
					cursor.moveToNext();
				}
			}
			return list;
		}
		throw new RuntimeException("SimpleSQL received a query call while in BatchWrite mode!");
	}
	
	private void genDatabaseCreateStmt(int table, int cnt, DBBuilder builder) {		
		if (cnt==1) {
			DATABASE_CREATE = " create table if not exists " + TABLE_NAME + " (" + COLUMN_ID + 
				" integer primary key autoincrement, " + builder.Column(table)[0] +
				// "+builder.DataTypes(table)[0];
				" text";
			int cols = builder.ColumnCount(TABLE_NAME);
			for (int i = 1; i < cols; i++) {
				//DATABASE_CREATE = DATABASE_CREATE + ", " + builder.Column(table)[i] + " "+builder.DataTypes(table)[i];
				DATABASE_CREATE = DATABASE_CREATE + ", " + builder.Column(table)[i] + " text";
			}
			DATABASE_CREATE = DATABASE_CREATE + ")";
		} else {
			DATABASE_CREATE = " create table if not exists " + TABLE_NAME + " (" + COLUMN_ID + 
					" integer primary key autoincrement, " + builder.Column(table)[0] +
					// "+builder.DataTypes(table)[0];
					" text";
			int cols = builder.ColumnCount(TABLE_NAME);
			for (int i = 1; i < cols; i++) {
				//DATABASE_CREATE = DATABASE_CREATE + ", " + builder.Column(table)[i] + " "+builder.DataTypes(table)[i];
				DATABASE_CREATE = DATABASE_CREATE + ", " + builder.Column(table)[i] + " text";			
			}
			DATABASE_CREATE = DATABASE_CREATE + ")";			
		}
	}
	
	public boolean isInBatchMode() {
		return batch;
	}
	
				
	private class BigDBSqlOpenHelper extends SQLiteOpenHelper {		
		
		public BigDBSqlOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			if (builder == null) {
				throw new RuntimeException("Table name list is empty! Can't create database.");
			}			
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {			
			int cnt = builder.TableCount();
			for (int i = 0; i < cnt; i ++) {
				TABLE_NAME = builder.Table(i);
				COLUMN_NAME = builder.Column(i)[0];
				genDatabaseCreateStmt(i, cnt, builder);
				db.execSQL(DATABASE_CREATE);
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(BigDBSqlOpenHelper.class.getName(),
					"Upgrading database from version " + oldVersion + " to "
							+ newVersion + ".");
			int tblcnt = builder.TableCount();
			// Create a list of string lists. Each string list represents the column
			// names for that table.
			// Then copy current db to a temp db, create the new db
			// and copy the old db data to the new.
			List<List<String>> curColumns = new ArrayList<List<String>>();
			for (int i = 0; i < tblcnt; i++) {
				db.execSQL("DROP TABLE IF EXISTS " + builder.Table(i));
				//curColumns.add(DBUtil.GetColumns(db, builder.Table(i)));
					
			}
			onCreate(db);
			
		}		
	}


	@Override
	public void onDestroy() {
		// Hopefully this should never be true, but it's there as a convenience
		// so it doesn't throw an error.
		if (batch) {
			db.setTransactionSuccessful();
			db.endTransaction();
		}
		db.close();		
	}
	
}
