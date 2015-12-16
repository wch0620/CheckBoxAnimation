package com.wjh.checkbox.animation.widget;

public interface BottomBar {

    /**
     * 
     * 获取底部栏的高度
     */
	public int getHeight();
	
    /**
     * 
     * 获取顶部栏菜单项
     */
	public Menu getMenu();
	
    /**
     * 
     * 刷新底部栏
     */
    public void updateBottomBar();
    
    /**
     * 设置监听器
     * @param listener
     */
    public void setOnMenuItemClickListener(OnMenuItemClickListener listener);
	
    /**
     * 隐藏底部栏 ，默认不带动画
     */
    public void hide();

    /**
     * 显示底部栏 ，默认不带动画
     */
    public void show();
    
    /**
     * 
     * 显示底部栏
     * 是否有动画
     */
    public void show(boolean animation);

    /**
     * 
     * 隐藏底部栏
     * 是否有动画
     */
    public void hide(boolean animation);
}
