package com.wjh.checkbox.animation.widget;

import com.wjh.checkbox.animation.R;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class BottomBarBuilder implements BottomBar {

	private Context mContext;
	
	private ViewGroup mContentContainer;
	
	private LinearLayout mBottomBarLayout;
	
    private int mBottomBarHeight;
    
    private LayoutInflater mLayoutInflater;
    
    public static final int MAX_ACTION_BUTTON_COUNT = 4;
    
    private MenuItem[] mMenuItems = new MenuItem[MAX_ACTION_BUTTON_COUNT];
    
    private AnimatorSet mShowAnimatorSet = null;
    
    private AnimatorSet mHideAnimatorSet = null;
    
    private OnMenuItemClickListener mOnMenuItemClickListener;
    
    private Menu mMenu;
	
	public BottomBarBuilder(Context context, ViewGroup container) {
		this.mContext = context;
		this.mContentContainer = container;
		this.mLayoutInflater = LayoutInflater.from(mContext);
		mLayoutInflater.inflate(R.layout.bottom_view, mContentContainer);
		this.mBottomBarLayout = (LinearLayout) mContentContainer.findViewById(R.id.bottombar);
		this.mBottomBarHeight = mContext.getResources().getDimensionPixelSize(R.dimen.activity_bottom_bar_height);
		mMenu = new Menu();
	}
	
	public void updateView() {
		int size = mMenu.getMenuSize();
		Log.d("wujiaohai", "size = " + size);
		// 没有菜单项则底部栏隐藏
		if (size == 0) {
			Log.d("wujiaohai", "BottomBar hide because no menu");
			hide(true);
			return;
		} else {
			show(true);
		}
		mBottomBarLayout.removeAllViews();
		int itemWidth = (mContext.getResources().getDisplayMetrics().widthPixels - mContext.getResources()
				.getDimensionPixelSize(R.dimen.activity_horizontal_margin) - mContext.getResources()
				.getDimensionPixelSize(R.dimen.activity_vertical_margin)) / size; 
        for (int i = 0; i < size; i++) {
            final MenuItem item = mMenu.getItem(i);
            MenuItemView itemView = null;
            if (item != null) {
            	itemView = new MenuItemView(mContext);
            	itemView.setIcon(item.getIconRes());
            	itemView.setTitle(item.getTitleRes());
				LayoutParams params = new LayoutParams(itemWidth, LayoutParams.WRAP_CONTENT);
				params.gravity = Gravity.CENTER;
                itemView.setLayoutParams(params);
                itemView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(mOnMenuItemClickListener != null) {
							mOnMenuItemClickListener.onMenuItemClick(item);
						}
					}
				});
            	mBottomBarLayout.addView(itemView);
            }
        }
	}

	@Override
	public int getHeight() {
		return mBottomBarHeight;
	}

	@Override
	public Menu getMenu() {
		return mMenu;
	}

	@Override
	public void updateBottomBar() {
		
	}

	@Override
	public void hide() {
		hide(false);
	}

	@Override
	public void show() {
		show(false);
	}

	@Override
	public void show(boolean animation) {

        Log.d("wujiaohai", "BottomBar show: animation="+animation);
        
        if (mMenu.getMenuSize() <= 0) {
            Log.d("wujiaohai", "BottomBar show return because no menu");
            return;
        }
        
        // 强制结束隐藏动画，不然两个动画会引起混乱
        if (mHideAnimatorSet != null && mHideAnimatorSet.isRunning()) {
            mHideAnimatorSet.end();
        }   
        
//        if (mContentContainer.getVisibility() == View.VISIBLE) {
//            Log.d("wujiaohai", "BottomBar show return because mContainer visible");
//            return;
//        }

        if (animation) {
            if(mShowAnimatorSet == null){
                mShowAnimatorSet = new AnimatorSet();
                float distance = mBottomBarHeight / 0.618f;
                long duration = 450L;
                ObjectAnimator translateAnimator = ObjectAnimator.ofFloat(mBottomBarLayout, "translationY", distance, 0.0f);
                translateAnimator.setDuration(duration);
                ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(mContentContainer, "alpha", 0.0f, 1.0f);
                alphaAnimator.setDuration(duration);
                mShowAnimatorSet.play(translateAnimator).with(alphaAnimator);
                mShowAnimatorSet.addListener(new Animator.AnimatorListener() {                  
                    @Override
                    public void onAnimationStart(Animator animator) {
                    	mContentContainer.setVisibility(View.VISIBLE);
                    }
                    
                    @Override
                    public void onAnimationRepeat(Animator animator) {
                        
                    }
                    
                    @Override
                    public void onAnimationEnd(Animator animator) {
                    	mContentContainer.setVisibility(View.VISIBLE);                     
                    }
                    
                    @Override
                    public void onAnimationCancel(Animator animator) {
                        
                    }
                });
            }            
            if (mShowAnimatorSet.isRunning()) {
                Log.d("wujiaohai", "BottomBar show return because Animation running");
                return;
            }
            mShowAnimatorSet.start();
        } else {
        	mContentContainer.setVisibility(View.VISIBLE);
        }
    
	}

	@Override
	public void hide(boolean animation) {

        Log.d("wujiaohai", "BottomBar hide: animation=" + animation);
        
        // 强制结束显示动画，不然两个动画会引起混乱
        if (mShowAnimatorSet != null && mShowAnimatorSet.isRunning()) {
            mShowAnimatorSet.end();
        }
        
        if (animation) {
            if(mHideAnimatorSet == null){
                mHideAnimatorSet = new AnimatorSet();
                float distance = mBottomBarHeight / 0.618f;
                long duration = 200L;
                ObjectAnimator translateAnimator = ObjectAnimator.ofFloat(mBottomBarLayout, "translationY", 0.0f, distance);
                translateAnimator.setDuration(duration);
                ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(mContentContainer, "alpha", 1.0f, 0.0f);
                alphaAnimator.setDuration(duration);
                mHideAnimatorSet.play(translateAnimator).with(alphaAnimator);
                mHideAnimatorSet.addListener(new Animator.AnimatorListener() {                  
                    @Override
                    public void onAnimationStart(Animator animator) {
                        
                    }
                    
                    @Override
                    public void onAnimationRepeat(Animator animator) {
                        
                    }
                    
                    @Override
                    public void onAnimationEnd(Animator animator) {
                    	mContentContainer.setVisibility(View.GONE);
                        // 动画结束要恢复属性
                    	mBottomBarLayout.setTranslationY(0.0f);
                        mContentContainer.setAlpha(1.0f);
                    }
                    
                    @Override
                    public void onAnimationCancel(Animator animator) {
                        
                    }
                });
            }
            if(mHideAnimatorSet.isRunning()){
                return;
            }
            mHideAnimatorSet.start();
        } else {
        	mBottomBarLayout.setVisibility(View.GONE);
        }
    
	}

	@Override
	public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
		mOnMenuItemClickListener = listener;
	}
	
	
}
