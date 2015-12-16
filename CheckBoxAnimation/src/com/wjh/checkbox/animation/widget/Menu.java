package com.wjh.checkbox.animation.widget;

import java.util.ArrayList;
import java.util.List;

public class Menu {
	
	private List<MenuItem> menuList;
	
	public Menu() {
		menuList = new ArrayList<MenuItem>();
	}

	public MenuItem add(int group, int id, int titleRes, int iconRes) {
		MenuItem item = new MenuItem(group, id, titleRes, iconRes);
		menuList.add(item);
		return item;
	}

	public void clear() {
		menuList.clear();
	}
	
	public int getMenuSize() {
		return menuList.size();
	}
	
	public MenuItem getItem(int position) {
		if(menuList != null) {
			return menuList.get(position);
		}
		return null;
	}
	
}
