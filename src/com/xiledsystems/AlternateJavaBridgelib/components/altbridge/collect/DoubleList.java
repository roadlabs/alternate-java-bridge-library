package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.collect;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;


public class DoubleList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 340146960159650274L;
	private ArrayList<Object> list1;
	private ArrayList<Object> list2;
	
	/**
	 *  This constructor creates a blank doublelist.
	 */
	public DoubleList() {
		list1 = new ArrayList<Object>();
		list2 = new ArrayList<Object>();
	}
	
	/**
	 * Use this constructor to prepopulate the double list
	 * with two lists. This will throw a RuntimeException
	 * if the two lists aren't the same size.
	 * 
	 * @param list1
	 * @param list2
	 */
	public DoubleList(ArrayList<Object> list1, ArrayList<Object> list2) {
		if (list1.size() != list2.size()) {
			throw new RuntimeException("The two lists are not the same size. DoubleList requires both lists to have the same items.");
		}
		this.list1 = list1;
		this.list2 = list2;
	}
	
	/**
	 *  Add data to the double list.
	 *  
	 * @param firstItem
	 * @param secondItem
	 */
	public void add(Object firstItem, Object secondItem) {
		list1.add(firstItem);
		list2.add(secondItem);
	}
	
	/**
	 *  Add data to the double list at the specified location. The item in
	 *  that location gets moved back (along with everything afterwards). If
	 *  the index is the size of the list, it gets added to the end.
	 *  
	 * @param index
	 * @param firstItem
	 * @param secondItem
	 */
	public void add(int index, Object firstItem, Object secondItem) {
		list1.add(index, firstItem);
		list2.add(index, secondItem);
	}
	
	/**
	 * 
	 *  Remove an item from the double list. (This removes an item from both lists
	 *  at the specified index position)
	 * @param index
	 */
	public void remove(int index) {
		list1.remove(index);
		list2.remove(index);		
	}
	
	/**
	 * Use this to replace the lists contained in the DoubleList
	 * @param firstList
	 * @param secondList
	 */
	public void replaceLists(ArrayList<Object> firstList, ArrayList<Object> secondList) {
		list1 = firstList;
		list2 = secondList;
	}
	
	/**
	 * Get the two values stored in the double list at the specified index
	 * 
	 * @param index
	 * @return and Object array of the two values.
	 */
	public Object[] get(int index) {
		Object things[] = new Object[2];
		things[0] = list1.get(index);
		things[1] = list2.get(index);
		return things;
	}
			
	/**
	 * Output one of the two lists stored in the Double List.
	 * 
	 * @param Which list to output. Valid numbers to use are 1, and 2.
	 * @return 
	 */
	public ArrayList<Object> getList(int oneortwo) {
		if (oneortwo==1) {
			return list1;
		}
		if (oneortwo==2) {
			return list2;			
		}
		return null;
	}
	
	public ArrayList<String> getStringList(int oneortwo) {
		ArrayList<String> tmp = new ArrayList<String>();
		if (oneortwo ==1) {
			int size = list1.size();			
			for (int i = 0; i < size; i++) {
				tmp.add(list1.get(i).toString());
			}
			return tmp;
		}
		if (oneortwo ==2) {
			int size = list2.size();			
			for (int i = 0; i < size; i++) {
				tmp.add(list2.get(i).toString());
			}
			return tmp;
		}
		return null;
	}
	
	/**
	 * 
	 * @return The size in indexes of this doublelist.
	 */
	public int size() {
		int one = list1.size();
		int two = list2.size();
		if (one != two) {
			return -1;
		}
		return one;
	}
	
	/**
	 * 
	 * @param object Object to check for in the doublelist
	 * @return the index of the object in the double list. If it's not found,
	 * -1 will be returned.
	 */
	public int index(Object object) {
		int idx = list1.indexOf(object);
		if (idx == -1) {
			idx = list2.indexOf(object); 
		} 
		return idx;		
	}
	
	/**
	 * 
	 *  Clears the double list of all data.
	 *  
	 */
	public void clearDoubleList() {
		list1.clear();
		list2.clear();
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof DoubleList) {
			DoubleList listcheck = (DoubleList) object;
			if (listcheck.getList(1).equals(list1) && listcheck.getList(2).equals(list2)) {
				return true;
			}
		} 
		return false;		
	}
	
	// Serializable implementation
	private void writeObject(ObjectOutputStream out) throws IOException {
		int size = size();
		out.writeInt(size);
		
		for (int i = 0; i < size; i++) {
			out.writeObject(list1.get(i));
			out.writeObject(list2.get(i));
		}
		
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		
		int size = in.readInt();
		list1 = new ArrayList<Object>();
		list2 = new ArrayList<Object>();
		for (int i = 0; i < size; i++) {
			list1.add(in.readObject());
			list2.add(in.readObject());
		}
		
	}
	
}
