package com.wjh.checkbox.animation.widget;

import com.wjh.checkbox.animation.R;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class TopBarBuilder implements TopBar {
	
	private Activity mContext;
	
	private TopBarStyle mTopBarStyle;
	
	private LayoutInflater mLayoutInflater;
	
	private ViewGroup mContentContainer;
	
	/**正常模式的返回*/
	private ImageView mBackImage;
	
	/**正常模式主标题*/
	private TextView mTitleView;
	
	/**正常模式副标题*/
	private TextView mSubTitleView;
	
	/**批量模式的取消*/
	private TextView mCancelView;
	
	/**批量模式的全选*/
	private TextView mChooseView;
	
	private String mTitle;
	
	private String mSubTitle;
	
	private int mTopBarHeight = 0;
	
	private OnClickListener mBackClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(!mContext.isFinishing())
				mContext.onBackPressed();
		}
	};
	
	private OnClickListener mCancelListener;
	
	private BatchCallBack mBatchCallBack;
	
	public TopBarBuilder(Activity context, ViewGroup group) {
		mContext = context;
		mContentContainer = group;
		mLayoutInflater = LayoutInflater.from(mContext);
	}

	@Override
	public void setTopBarStyle(TopBarStyle style) {
    	if (style == mTopBarStyle) {
			return;
		}
        mTopBarStyle = style;
        mContentContainer.removeAllViews();
        
        switch (style) {
		case TOP_BAR_NOTMAL_STYLE:
			View normalView = mLayoutInflater.inflate(R.layout.top_view_normal, mContentContainer);
			mBackImage = (ImageView) normalView.findViewById(R.id.back);
			mTitleView = (TextView) normalView.findViewById(R.id.title);
			mSubTitleView = (TextView) normalView.findViewById(R.id.subtitle);
			
			mBackImage.setOnClickListener(mBackClickListener);
			break;
		case TOP_BAR_BATCH_EDIT_STYLE:
			View batchView = mLayoutInflater.inflate(R.layout.top_view_batch, mContentContainer);
			mCancelView = (TextView) batchView.findViewById(R.id.cancel);
			mTitleView = (TextView) batchView.findViewById(R.id.title);
			mSubTitleView = (TextView) batchView.findViewById(R.id.subtitle);
			mChooseView = (TextView) batchView.findViewById(R.id.all);
			
			mCancelView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(mCancelListener != null) {
						mCancelListener.onClick(mCancelView);
					}
				}
			});
			
			mChooseView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (mBatchCallBack != null) {
						int checkedCount = mBatchCallBack.getCheckedItemCount();
						int totalCount = mBatchCallBack.getTotalItemCount();
						if (checkedCount != totalCount) {
							mBatchCallBack.onSelectAllItems();
						} else {
							mBatchCallBack.onClearAllItems();
						}
					}
				}
			});
			break;
		default:
			break;
		}
        
	}

	@Override
	public TopBarStyle getTopBarStyle() {
		return mTopBarStyle;
	}

	@Override
	public void setTopBarTtitle(String title) {
		mTitle = title;
		if (!TextUtils.isEmpty(title)) {
			mTitleView.setText(title);
		}
	}

	@Override
	public String getTopBarTtitle() {
		return mTitle;
	}

	@Override
	public void setTopBarSubTtitle(String subTitle) {
		mSubTitle = subTitle;
		mSubTitle = subTitle;
		if (!TextUtils.isEmpty(subTitle)) {
			mSubTitleView.setVisibility(View.VISIBLE);
			mSubTitleView.setText(subTitle);
		} else {
			mSubTitleView.setVisibility(View.GONE);
		}
	}
	
	@Override
	public int getTopBarHeight() {
		if(mTopBarHeight == 0) {
			mTopBarHeight = mContext.getResources().getDimensionPixelSize(R.dimen.activity_top_bar_height);
		}
		return mTopBarHeight;
	}
	
	@Override
	public String getTopBarSubTtitle() {
		return mSubTitle;
	}

	@Override
	public void setBackOnClickListener(OnClickListener listener) {
		if (mBackImage != null) {
			if (listener == null) {
				mBackImage.setOnClickListener(mBackClickListener);
			} else {
				mBackImage.setOnClickListener(listener);
			}
		}
	}

	@Override
	public void setCancelOnClickListener(OnClickListener listener) {
		if(mTopBarStyle != TopBarStyle.TOP_BAR_BATCH_EDIT_STYLE) {
			throw new IllegalStateException("[wujiaohai set TOP_BAR_BATCH_EDIT_STYLE first");
		}
		mCancelListener = listener;
	}

	@Override
	public void setOnBatchCallBack(BatchCallBack callBack) {
		if(mTopBarStyle != TopBarStyle.TOP_BAR_BATCH_EDIT_STYLE) {
			throw new IllegalStateException("[wujiaohai set TOP_BAR_BATCH_EDIT_STYLE first");
		}
		mBatchCallBack = callBack;
		notifyBatchStateChanged();
	}
	
	@Override
	public void notifyBatchStateChanged() {
        if (mTopBarStyle != TopBarStyle.TOP_BAR_BATCH_EDIT_STYLE) {
            return;
        }
        int checkedCount = 0;
        if (mBatchCallBack != null) {
            checkedCount = mBatchCallBack.getCheckedItemCount();
        }
        setTopBarTtitle(checkedCount + "项已选中");
        
        int totalCount = 0;
        if (mBatchCallBack != null) {
            totalCount = mBatchCallBack.getTotalItemCount();
        }
        setTopBarSubTtitle("总共" + totalCount + "项");
        
        if (totalCount != checkedCount) {
        	mChooseView.setText("全选");
        } else {
        	mChooseView.setText("全不选");
        }
	}
	
}
