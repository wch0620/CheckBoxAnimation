# 欢迎使用Markdown编辑器写博客

##一、背景
很多时候，对于ListView需要切换批量操作模式，通常进入批量模式的方式有：长按列表和菜单方式。于是封装了一个ListView的批量操作的Demo。

##二、效果
 1. CheckBox显示和隐藏动画
 2. 封装一个顶部栏TopBar，并且实现批量操作的切换动画
 3. 封装一个底部菜单，实现显示和隐藏动画。

##三、实现

定义接口TopBar与BottomBar。
定义TopBar的类型：
```
    public enum TopBarStyle {

        /** 常规顶部栏样式，支持主副标题 */
        TOP_BAR_NOTMAL_STYLE,

        /** 标题区为批量模式 */
        TOP_BAR_BATCH_EDIT_STYLE,

        /** 使用自定义的View作为顶部栏的样式 */
        TOP_BAR_CUSTOM_STYLE,
        
    }
```
</br>
|  常量      |    说明 | 
| :-------- | --------:| 
| TOP_BAR_NOTMAL_STYLE      |   普通模式    | 
| TOP_BAR_BATCH_EDIT_STYLE  |   批量模式    |
| TOP_BAR_CUSTOM_STYLE      |   自定义模式  |
</br>
TopBar的回调接口
```
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
```
通过设置SetTopBarStyle的时候切换TopBar。
```
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
```

TopBar动画：

```
mAnimationHide = new AlphaAnimation(1.0f, 0.0f);
mAnimationHide.setInterpolator(new DecelerateInterpolator());
mAnimationHide.setDuration(200);
mAnimationShow = new AlphaAnimation(0.0f, 1.0f);
mAnimationShow.setInterpolator(new AccelerateInterpolator());
mAnimationShow.setDuration(600);
```

BottomBar动画：

```
	public void show(boolean animation) {
        
        if (mMenu.getMenuSize() <= 0) {
            return;
        }
        
        // 强制结束隐藏动画，不然两个动画会引起混乱
        if (mHideAnimatorSet != null && mHideAnimatorSet.isRunning()) {
            mHideAnimatorSet.end();
        } 
        
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
                return;
            }
            mShowAnimatorSet.start();
        } else {
        	mContentContainer.setVisibility(View.VISIBLE);
        }
    
	}
```

CheckBox动画：
```
public static void animateShowing(final ViewHolder holder,
				final ListAdapter adapter, boolean isAnimate) {
			final CheckBox checkBox = holder.checkBox;
			if (checkBox.getVisibility() == View.VISIBLE) {
				return;
			}
			checkBox.setVisibility(View.VISIBLE);
			checkBox.setAlpha(0.0f);
			final int widthSpec = View.MeasureSpec.makeMeasureSpec(0,
					View.MeasureSpec.UNSPECIFIED);
			final int heightSpec = View.MeasureSpec.makeMeasureSpec(0,
					View.MeasureSpec.UNSPECIFIED);
			checkBox.measure(widthSpec, heightSpec);
			ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) checkBox
					.getLayoutParams();
			final long transValue = checkBox.getMeasuredWidth() + lp.leftMargin
					+ lp.rightMargin;

			if (!isAnimate) {
				checkBox.setAlpha(1.0f);
				holder.contentLayout.setTranslationX(transValue);
				return;
			}

			final ObjectAnimator transBodyAnimator = new ObjectAnimator();
			final PropertyValuesHolder trans = PropertyValuesHolder.ofFloat(
					"TranslationX", 0.0f, transValue);
			transBodyAnimator.setTarget(holder.contentLayout);
			transBodyAnimator.setValues(trans);
			transBodyAnimator.setDuration(DURATION);

			ObjectAnimator checkBoxAnim = new ObjectAnimator();
			final PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(
					"ScaleX", 0.0f, 1.0f);
			final PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(
					"ScaleY", 0.0f, 1.0f);
			final PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(
					"Alpha", 0.0f, 1.0f);
			checkBoxAnim.setValues(scaleX, scaleY, alpha);
			checkBoxAnim.setTarget(holder.checkBox);
			checkBoxAnim.setDuration(DURATION);
			checkBoxAnim.setInterpolator(new DecelerateInterpolator());
			checkBoxAnim.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationStart(Animator animation) {
					checkBox.setTag("animating");
				}

				@Override
				public void onAnimationEnd(Animator animation) {
					// adapter.setCheckBoxAnimator(false);
					checkBox.setTag("animated");
				}
			});
			if (!(checkBox.getTag() != null && "animating".equals(checkBox
					.getTag()))) {
				// 若正在播放动画，则不继续播放动画
				transBodyAnimator.start();
				checkBoxAnim.start();
			}
		}
```

批量模式下，用来记录当前选中状态
```
private SparseArray<Boolean> mSelectState = new SparseArray<Boolean>();
```
##四、效果图：
![普通模式](http://xiangce.baidu.com/picture/detail/c6d0419927f2bf78492cef68db1dca4037e1b72a?from=dialog)
![批量模式i](http://xiangce.baidu.com/picture/detail/208482f2c6212ac7b31d34d4e0d3b3126a192029?from=dialog)



---------

[1]: http://math.stackexchange.com/
[2]: https://github.com/jmcmanus/pagedown-extra "Pagedown Extra"
[3]: http://meta.math.stackexchange.com/questions/5020/mathjax-basic-tutorial-and-quick-reference
[4]: http://bramp.github.io/js-sequence-diagrams/
[5]: http://adrai.github.io/flowchart.js/
[6]: https://github.com/benweet/stackedit

