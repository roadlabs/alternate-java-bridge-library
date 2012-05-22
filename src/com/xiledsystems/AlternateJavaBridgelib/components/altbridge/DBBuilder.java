package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.util.ArrayList;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.collect.DoubleList;

public class DBBuilder {

	private ArrayList<String> tables;
	private ArrayList<String> columns;
	private ArrayList<String> datatypes;
	private ArrayList<DoubleList> bigColumns;
	private int dbVersion = 1;
	private String dbName;
	
	/**
	 * 
	 * Helper class used to setup the table/column arrangement of a
	 * SimpleSQL database. This must be defined before instantiating
	 * SimpleSQL.
	 * 
	 */
	public DBBuilder() {
		tables = new ArrayList<String>();
		columns = new ArrayList<String>();
		datatypes = new ArrayList<String>();
		bigColumns = new ArrayList<DoubleList>();
	}
	
	/**
	 * 
	 * Adds table information for the SimpleSQL database.
	 * 
	 * @param tableName The name of the table. Spaces are not allowed.
	 * @param columnNames A string array of the column names in this table
	 * @param dataTypes A string array of the data type of each column in the table
	 */
	public void addTable(String tableName, String[] columnNames, String[] dataTypes) {
		tables.add(tableName);
		columns.clear();
		datatypes.clear();
		int cols = columnNames.length;
		for (int i = 0; i < cols; i++) {
			columns.add(columnNames[i]);
		}
		cols = dataTypes.length;
		for (int i = 0; i < cols; i++) {
			String type;
			String s = dataTypes[i];
			if (s.equals("float") || s.equals("double")) {
				type = "REAL";
			} else if (s.equals("int") || s.equals("byte") || s.equals("short") || s.equals("long")) {
				type = "INTEGER";
			} else if (s.equalsIgnoreCase("text") || s.equalsIgnoreCase("real") || 
					s.equalsIgnoreCase("integer")) {
				type = s;
			} else {
				type = "TEXT";
			}			
			datatypes.add(type);
		}
		bigColumns.add(new DoubleList(new ArrayList<Object>(columns), new ArrayList<Object>(datatypes)));
		
	}
	
	/**
	 * This is used internally. If you call this, you will have to re-instantiate
	 * SimpleSQL. 
	 * 
	 * @param table The table to delete.
	 * @return False if the table is not found.
	 */
	public boolean removeTable(String table) {
		if (tables.contains(table)) {
			int id = TableId(table);
			tables.remove(id);
			bigColumns.remove(id);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean containsTable(String table) {
		if (tables.contains(table)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @return the database version number
	 */
	public int Version() {
		return dbVersion;
	}
	
	/**
	 * 
	 * Returns the name of the database file, if specified earlier.
	 * 
	 * @return
	 */
	public String DBName() {
		return dbName;
	}
	
	/**
	 * 
	 * Set the name of the database file.
	 * 
	 * @param name The name of the database file.
	 */
	public void DBName(String name) {
		dbName = name;
	}
	
	/**
	 * 
	 * Set the version number of the database. Default is 1.
	 * Note that right now, this is destructive! Which means if you
	 * set the version higher than what it is, you will lose all data
	 * in the database! (This includes all tables). This will eventually
	 * allow you to upgrade the database, but not right now.
	 * 
	 * @param version the version number of the database.
	 */
	public void Version(int version) {
		dbVersion= version;
	}
	
	/**
	 * 
	 * @return the table count
	 */
	public int TableCount() {
		return tables.size();
	}
	
	/**
	 * 
	 * Internal use. 
	 * 
	 * @param table The table
	 * @return the id of the table
	 */
	public int TableId(String table) {
		return tables.indexOf(table);
	}
	
	/**
	 * 
	 * 
	 * @param table The table to check
	 * @return the amount of columns in the table
	 */
	public int ColumnCount(String table) {
		int position = tables.indexOf(table);
		return bigColumns.get(position).size();
	}
	
	/**
	 * 
	 * @param position The table id to get
	 * @return the table name
	 */
	public String Table(int position) {
		return tables.get(position);
	}
	
	/**
	 * 
	 * @param table The table to check
	 * @param column The column to check
	 * @return the position of the column
	 */
	public int ColumnPosition(String table, String column) {
		int tbl = tables.indexOf(table);
		return bigColumns.get(tbl).getList(1).indexOf(column);
	}
	
	/**
	 * 
	 * 
	 * @param position the tableid
	 * @return the column names
	 */
	public String[] Column(int position) {
		int size = bigColumns.get(position).size();
		String[] tmp = new String[size];
		for (int i = 0; i < size; i++) {
			tmp[i] = bigColumns.get(position).get(i)[0].toString();
		}
		return tmp;
	}
	
	/**
	 * 
	 * @param position table id
	 * @return the data types of each column
	 */
	public String[] DataTypes(int position) {
		int size = bigColumns.get(position).size();
		String[] tmp = new String[size];
		for (int i = 0; i < size; i++) {
			tmp[i] = bigColumns.get(position).get(i)[1].toString();
		}
		return tmp;
	}
	
}
