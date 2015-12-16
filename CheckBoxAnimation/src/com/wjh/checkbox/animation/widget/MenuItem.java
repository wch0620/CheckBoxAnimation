package com.wjh.checkbox.animation.widget;

public class MenuItem {

	int group;
	
	int id;
	
	int titleRes;
	
	int iconRes;

	public MenuItem(int group, int id, int titleRes, int iconRes) {
		this.group = group;
		this.id = id;
		this.titleRes = titleRes;
		this.iconRes = iconRes;
	}

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTitleRes() {
		return titleRes;
	}

	public void setTitleRes(int titleRes) {
		this.titleRes = titleRes;
	}

	public int getIconRes() {
		return iconRes;
	}

	public void setIconRes(int iconRes) {
		this.iconRes = iconRes;
	}
	
	
	
}
