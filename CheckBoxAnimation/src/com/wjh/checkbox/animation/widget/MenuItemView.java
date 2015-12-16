package com.wjh.checkbox.animation.widget;

import com.wjh.checkbox.animation.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 自定义View
 * @author wujiaohai
 * 2015-12-16 14:18:11
 */
public class MenuItemView extends FrameLayout {

	private ImageView mIconView;
	private TextView mTitleView;
	private LinearLayout mActionView;

	public MenuItemView(Context context) {
		this(context, null);
	}

	public MenuItemView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MenuItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setClickable(true);
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.bottombar_item, this);
		mActionView = (LinearLayout) findViewById(R.id.bottombar_layout);
		mIconView = (ImageView) findViewById(R.id.bottombar_item_icon);
		mTitleView = (TextView) findViewById(R.id.bottombar_item_title);
	}
	
	public void setIcon(int iconRes) {
		mIconView.setImageResource(iconRes);
	}

	public void setIcon(Drawable icon) {
		mIconView.setImageDrawable(icon);
	}
	
	public void setTitle(int titleRes) {
		mTitleView.setText(titleRes);
	}

	public void setTitle(CharSequence title) {
		mTitleView.setText(title);
	}

	public void setTitleColor(int color) {
		mTitleView.setTextColor(color);
	}

	public void setOnClickListener(OnClickListener listener) {
		mActionView.setOnClickListener(listener);
	}

	public View getIconView() {
		return mIconView;
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		mActionView.setEnabled(enabled);
		mIconView.setEnabled(enabled);
		mTitleView.setEnabled(enabled);
		// disable项透明70%
		if (enabled) {
			setAlpha(1.0f);
		} else {
			setAlpha(0.3f);
		}
	}
}
