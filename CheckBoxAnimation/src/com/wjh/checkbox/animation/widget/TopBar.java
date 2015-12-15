package com.wjh.checkbox.animation.widget;

import android.view.View.OnClickListener;

public interface TopBar {
	
    public enum TopBarStyle {

        /** 常规顶部栏样式，支持主副标题 */
        TOP_BAR_NOTMAL_STYLE,

        /** 标题区为批量模式 */
        TOP_BAR_BATCH_EDIT_STYLE,

        /** 使用自定义的View作为顶部栏的样式 */
        TOP_BAR_CUSTOM_STYLE,
        
    }
    
    public void setTopBarStyle(TopBarStyle style);
    
    public TopBarStyle getTopBarStyle();

	public void setTopBarTtitle(String title);
	
	public String getTopBarTtitle();
	
	public void setTopBarSubTtitle(String subTitle);
	
	public String getTopBarSubTtitle();
	
	public int getTopBarHeight();
	
	public void setBackOnClickListener(OnClickListener listener);
	
	public void setCancelOnClickListener(OnClickListener listener);
	
    public interface BatchCallBack {
        /**
         * 全选之回调
         */
        public void onSelectAllItems();
        
        /**
         * 全清之回调
         */
        public void onClearAllItems();
        
        /**
         * 获取列表项数目
         */
        public int getTotalItemCount();
        
        /**
         * 获取当前选中数目
         */
        public int getCheckedItemCount();
    }
    
    public void setOnBatchCallBack(BatchCallBack callBack);
    
    public void notifyBatchStateChanged(); 
	
}
