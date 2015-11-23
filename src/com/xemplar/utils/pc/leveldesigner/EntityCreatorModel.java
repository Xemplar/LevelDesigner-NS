package com.xemplar.utils.pc.leveldesigner;

import java.util.ArrayList;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

public class EntityCreatorModel<T> implements ListModel<T>{
	private ArrayList<ListDataListener> listeners = new ArrayList<ListDataListener>();
	private ArrayList<T> items = new ArrayList<T>();
	
	public int getSize() {
		return items.size();
	}
	
	public void addItem(T t){
		this.items.add(t);
	}
	
	public T getElementAt(int index) {
		return items.get(index);
	}

	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
	}

	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}

}
