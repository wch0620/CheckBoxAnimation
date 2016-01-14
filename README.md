# Android中ListView切换批量模式动画效果

##一、背景
很多时候，对于ListView需要切换批量操作模式，通常进入批量模式的方式有：长按列表和菜单方式。于是封装了一个ListView的批量操作的Demo。

##二、效果图：
![Gif](https://github.com/wch0620/CheckBoxAnimation/raw/master/gif/screen.gif)

##三、微信公众号：
**关注微信公众号，获取密码，了解更多。**

**微信公众号：jike_android**

![公众号](https://github.com/wch0620/StatusBar/raw/master/WeiXin/qrcode.jpg)

##四、功能
 1. CheckBox显示和隐藏动画
 2. 封装一个顶部栏TopBar，并且实现批量操作的切换动画
 3. 封装一个底部菜单，实现显示和隐藏动画。

##五、实现

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


| 常量             | 说明                           |
| -------------- | ---------------------------- |
| TOP_BAR_NOTMAL_STYLE | 普通模式         |
| TOP_BAR_BATCH_EDIT_STYLE  | 批量模式 |
| TOP_BAR_CUSTOM_STYLE  | 自定义模式              |


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
        mShowAnimatorSet = new AnimatorSet();
        float distance = mBottomBarHeight / 0.618f;
        long duration = 450L;
        ObjectAnimator translateAnimator = ObjectAnimator.ofFloat(mBottomBarLayout, "translationY", distance, 0.0f);
        translateAnimator.setDuration(duration);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(mContentContainer, "alpha", 0.0f, 1.0f);
        alphaAnimator.setDuration(duration);
        mShowAnimatorSet.play(translateAnimator).with(alphaAnimator);	
```

CheckBox动画：
```
	final ObjectAnimator transBodyAnimator = new ObjectAnimator();
	final PropertyValuesHolder trans = PropertyValuesHolder.ofFloat("TranslationX", 0.0f, transValue);
	transBodyAnimator.setTarget(holder.contentLayout);
	transBodyAnimator.setValues(trans);
	transBodyAnimator.setDuration(DURATION);

	ObjectAnimator checkBoxAnim = new ObjectAnimator();
	final PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("ScaleX", 0.0f, 1.0f);
	final PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("ScaleY", 0.0f, 1.0f);
	final PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("Alpha", 0.0f, 1.0f);
	checkBoxAnim.setValues(scaleX, scaleY, alpha);
	checkBoxAnim.setTarget(holder.checkBox);
	checkBoxAnim.setDuration(DURATION);
	checkBoxAnim.setInterpolator(new DecelerateInterpolator());
```

批量模式下，用来记录当前选中状态
```
private SparseArray<Boolean> mSelectState = new SparseArray<Boolean>();
```






