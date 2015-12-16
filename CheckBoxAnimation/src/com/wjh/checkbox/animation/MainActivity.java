package com.wjh.checkbox.animation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.wjh.checkbox.animation.widget.BottomBar;
import com.wjh.checkbox.animation.widget.BottomBarBuilder;
import com.wjh.checkbox.animation.widget.Menu;
import com.wjh.checkbox.animation.widget.MenuItem;
import com.wjh.checkbox.animation.widget.OnMenuItemClickListener;
import com.wjh.checkbox.animation.widget.TopBar.BatchCallBack;
import com.wjh.checkbox.animation.widget.TopBarBuilder;
import com.wjh.checkbox.animation.widget.TopBar.TopBarStyle;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 2015-12-15 13:52:22
 * @author wujiaohai
 *
 */
public class MainActivity extends Activity {
	
	private static final String TAG = "wujiaohai";
	
	private static final int INITIALIZE = 0;
	
	private static final int ADD = 1;
	
	private LinearLayout mTopLayout;//顶部布局
	
	private TopBarBuilder mTopBarBuilder;
	
	private ListView mListView;//列表
	
	private ListAdapter mListAdapter;//adapter
	
	private List<DataBean> mListData = new ArrayList<DataBean>();//数据
	
	private boolean isBatchModel;//是否批量模式
	
    private Animation mAnimationShow;//顶部栏显示动画
    
	private Animation mAnimationHide;//顶部栏隐藏动画
	
	private LinearLayout mBottomLayout;//底部菜单布局
	
	private BottomBarBuilder mBottomBarBuilder;
	
	private MenuItemClickListener mMenuItemClickListener;

    /** 批量模式下，用来记录当前选中状态 */
    private SparseArray<Boolean> mSelectState = new SparseArray<Boolean>();
    
	/**头像Id*/
	private int[] headIds = new int[] {
			R.drawable.personal_center_avatar_female1,
			R.drawable.personal_center_avatar_female2,
			R.drawable.personal_center_avatar_male1,
			R.drawable.personal_center_avatar_male2 };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		initAnimation();
		initView();
		initTopBar();
		initBottomBar();
		loadData();
	}
	
	private void initAnimation() {
        mAnimationHide = new AlphaAnimation(1.0f, 0.0f);
        mAnimationHide.setInterpolator(new DecelerateInterpolator());
        mAnimationHide.setDuration(200);
        mAnimationShow = new AlphaAnimation(0.0f, 1.0f);
        mAnimationShow.setInterpolator(new AccelerateInterpolator());
        mAnimationShow.setDuration(600);
	}
	
	private void initTopBar() {
		mTopBarBuilder = new TopBarBuilder(this, mTopLayout);
		updateTopBar();
	}
	
	private void updateTopBar() {
		if(isBatchModel) {
			mTopBarBuilder.setTopBarStyle(TopBarStyle.TOP_BAR_BATCH_EDIT_STYLE);
			mTopBarBuilder.setCancelOnClickListener(new onCancelClickListener());
			mTopBarBuilder.setOnBatchCallBack(mBatchCallBack);
		} else {
			mTopBarBuilder.setTopBarStyle(TopBarStyle.TOP_BAR_NOTMAL_STYLE);
			mTopBarBuilder.setTopBarTtitle("普通模式");
		}
	}
	
	/**批量模式的取消监听*/
	private class onCancelClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			cancelBatchModel();
		}
		
	}
	
	/**全选按钮*/
	private BatchCallBack mBatchCallBack = new BatchCallBack() {

		@Override
		public void onSelectAllItems() {
			Log.d(TAG, "onSelectAllItems");
            if (mListData != null) {
                mSelectState.clear();
                int size = mListData.size();
                if (size == 0) {
                    return;
                }
                for (int i = 0; i < size; i++) {
                    int _id = (int) mListData.get(i).getId();
                    mSelectState.put(_id, true);
                }
                refreshListView();
                mTopBarBuilder.notifyBatchStateChanged();
            }
		}

		@Override
		public void onClearAllItems() {
			Log.d(TAG, "onClearAllItems");
            if (mListAdapter != null) {
                mSelectState.clear();
                refreshListView();
                mTopBarBuilder.notifyBatchStateChanged();
            }
		}

		@Override
		public int getTotalItemCount() {
			if(mListData != null) {
				return mListData.size();
			}
			return 0;
		}

		@Override
		public int getCheckedItemCount() {
			return mSelectState.size();
		}
		
	};
	
	private void initBottomBar() {
		if(mBottomBarBuilder == null) {
			mBottomBarBuilder = new BottomBarBuilder(this, mBottomLayout);
		}
		onPrepareBottomBar(mBottomBarBuilder);
		if(mMenuItemClickListener == null) {
			mMenuItemClickListener = new MenuItemClickListener();
		}
		mBottomBarBuilder.setOnMenuItemClickListener(mMenuItemClickListener);
		mBottomBarBuilder.updateView();
	}
	
	private void onPrepareBottomBar(BottomBar bottomBar) {
	    if (bottomBar == null) {
	    	Log.w("wujiaohai", "the bottombar is null in onPrepareBottomBar");
	        return;
	    }
	    Menu menu = bottomBar.getMenu();
	    if (menu != null) {
	        menu.clear();
	        onPrepareBottomMenu(menu);
	        int menuItemSize = menu.getMenuSize();
	        Log.d("wujiaohai", "the menuItemSize is " + menuItemSize + " after call onPrepareMenu");
	    } else {
	    	Log.w("wujiaohai", "the menu is null in onPrepareBottomBar");
	    }
	}
	
	private class MenuItemClickListener implements OnMenuItemClickListener {

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			int itemId = item.getId();
			switch (itemId) {
			case R.string.menu_delete:
				List<Integer> ids = getSelectedIds();
				doDelete(ids);
				break;
			case R.string.menu_add:
				new LoadDataTask().execute(new Params(ADD));
				break;
			case R.string.menu_batch:
				isBatchModel = true;
	            rmTopBarView();
	            initBottomBar();
				refreshListView();
				break;
			}
			return true;
		}
		
	}
	
	private void doDelete(List<Integer> ids) {
		for (int i = 0; i < mListData.size(); i++) {
			long dataId = mListData.get(i).getId();
			for (int j = 0; j < ids.size(); j++) {
				int deleteId = ids.get(j);
				if(dataId == deleteId) {
					mListData.remove(i);
					i--;
					ids.remove(j);
					j--;
				}
			}
		}
        cancelBatchModel();
	}
	
    private final List<Integer> getSelectedIds() {
        ArrayList<Integer> selectedIds = new ArrayList<Integer>();
        for (int index = 0; index < mSelectState.size(); index++) {
            if (mSelectState.valueAt(index)) {
                selectedIds.add(mSelectState.keyAt(index));
            }
        }
        return selectedIds;
    }
	
	private void onPrepareBottomMenu(Menu menu) {
		if(isBatchModel) {
			menu.add(0, R.string.menu_delete, R.string.menu_delete,
					R.drawable.bottombar_icon_delete);
		} else {
			menu.add(0, R.string.menu_batch, R.string.menu_batch,
					R.drawable.bottombar_icon_copy_number);
			menu.add(0, R.string.menu_add, R.string.menu_add,
					R.drawable.bottombar_icon_add_contact);
		}
	}
	
	private void initView() {
		mTopLayout = (LinearLayout) findViewById(R.id.top_bar);
		mBottomLayout = (LinearLayout) findViewById(R.id.bottom_bar);
		mListView = (ListView) findViewById(R.id.listview);
		mListView.setSelector(R.drawable.list_selector);
	}
	
	private void loadData() {
		new LoadDataTask().execute(new Params(INITIALIZE));
	}
	
	private void refreshListView() {
		if(mListAdapter == null) {
			mListAdapter = new ListAdapter();
			mListView.setAdapter(mListAdapter);
			mListView.setOnItemClickListener(mListAdapter);
			mListView.setOnItemLongClickListener(mListAdapter);
		} else {
			mListAdapter.notifyDataSetChanged();
		}
	}
	
	private List<DataBean> getData() {
		int maxId = 0;
		if(mListData != null && mListData.size() > 0)
			maxId = mListData.get(mListData.size() - 1).getId();
		List<DataBean> result = new ArrayList<DataBean>();
		DataBean data = null;
		for (int i = 0; i < 20; i++) {
			data = new DataBean();
			data.setId(maxId + i + 1);//从最大Id的下一个开始
			data.setTitle("Data-" + (maxId + i + 1));
			data.setContent("创建时间 " + getTime());
			data.setHeadId(getHeadId());
			result.add(data);
		}
		return result;
	}
	
	private String getTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        return format.format(new Date());
	}
	
	private int getHeadId() {
		int random = (int) (Math.random() * 100);
		int headId = random % 4;
		return headIds[headId];
	}
	
	class Params {
		int op;

		public Params(int op) {
			this.op = op;
		}
		
	}
	
	class Result {
		int op;
		List<DataBean> list;
	}
	
	private class LoadDataTask extends AsyncTask<Params, Void, Result> {
		@Override
		protected Result doInBackground(Params... params) {
			Params p = params[0];
			Result result = new Result();
			result.op = p.op;
			try {//模拟耗时
				Thread.sleep(500L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			result.list = getData();
			return result;
		}

		@Override
		protected void onPostExecute(Result result) {
			super.onPostExecute(result);
			if(result.op == INITIALIZE) {
				mListData = result.list;
			} else {
				mListData.addAll(result.list);
				Toast.makeText(getApplicationContext(), "添加成功！", Toast.LENGTH_SHORT).show();
			}
			
			refreshListView();
		}
		
	}
	
	private class ListAdapter extends BaseAdapter implements OnItemClickListener, OnItemLongClickListener {

		@Override
		public int getCount() {
			return mListData.size();
		}

		@Override
		public Object getItem(int position) {
			return mListData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			View view = convertView;
			if(view == null) {
				view = LayoutInflater.from(MainActivity.this).inflate(R.layout.list_item, null);
				holder = new ViewHolder(view);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			DataBean data = mListData.get(position);
			bindListItem(holder, data);
			switchBatchModel(holder);
			return view;
		}
		
		private void bindListItem(ViewHolder holder, DataBean data) {
			holder.image.setImageResource(data.getHeadId());
			holder.title.setText(data.getTitle());
			holder.content.setText(data.getContent());
			
			int _id = data.getId();
            if(isBatchModel) {
                boolean selected = mSelectState.get(_id, false);
                holder.checkBox.setChecked(selected);
            }
		}
		
        /**批量模式与正常模式的切换*/
        private void switchBatchModel(ViewHolder holder) {
            if (isBatchModel) {
                CheckBoxAnimatorHelper.animateShowing(holder, this, true);
            } else {
                CheckBoxAnimatorHelper.animateHiding(holder, this, true);
            }
        }

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			isBatchModel = true;
			int _id = (int) mListData.get(position).getId();
            mSelectState.put(_id, true);
            rmTopBarView();
            initBottomBar();
			refreshListView();
			return true;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
            DataBean bean = mListData.get(position);
            if(isBatchModel) {
                ViewHolder holder = (ViewHolder) view.getTag();
                int _id = (int) bean.getId();
                boolean selected = !mSelectState.get(_id, false);
                holder.checkBox.toggle();
                if(selected) {
                	mSelectState.put(_id, true);
                } else {
                	mSelectState.delete(_id);
                }
                
                mTopBarBuilder.notifyBatchStateChanged();
            }
		}
		
	}
	
	private void rmTopBarView() {
		mTopLayout.startAnimation(mAnimationHide);
		mAnimationHide.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {

			}

			@Override
			public void onAnimationRepeat(Animation arg0) {

			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				addTopBarView();
			}
		});
		mTopLayout.setVisibility(View.GONE);
	}
	
	private void addTopBarView() {
		initTopBar();
    	mTopLayout.setVisibility(View.VISIBLE);
    	mTopLayout.startAnimation(mAnimationShow);
	}
	
	class ViewHolder {
		CheckBox checkBox;
		View contentLayout;
		ImageView image;
		TextView title;
		TextView content;

		public ViewHolder(View view) {
			checkBox = (CheckBox) view.findViewById(R.id.check_box);
			contentLayout = (View) view.findViewById(R.id.content_Layout);
			image = (ImageView) view.findViewById(R.id.image);
			title = (TextView) view.findViewById(R.id.title);
			content = (TextView) view.findViewById(R.id.content);
		}
	}
	
    /**退出批量模式*/
    private void cancelBatchModel() {
        isBatchModel = false;
        mSelectState.clear();
        refreshListView();
        initBottomBar();
        rmTopBarView();
    }
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(KeyEvent.KEYCODE_BACK == keyCode) {
            if(isBatchModel) {
                cancelBatchModel();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
	
	public static final class CheckBoxAnimatorHelper {

		public static final int DURATION = 400;

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

		public static void animateHiding(final ViewHolder holder,
				final ListAdapter adapter, boolean isAnimate) {
			final CheckBox checkBox = holder.checkBox;
			final View tansBody = holder.contentLayout;

			if (checkBox.getVisibility() == View.GONE) {
				return;
			}
			ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) checkBox
					.getLayoutParams();
			final float transValue = checkBox.getMeasuredWidth()
					+ lp.leftMargin + lp.rightMargin;

			if (!isAnimate) {
				checkBox.setVisibility(View.GONE);
				holder.contentLayout.setTranslationX(0.0f);
				return;
			}
			final ObjectAnimator transBodyAnimator = new ObjectAnimator();
			final PropertyValuesHolder trans = PropertyValuesHolder.ofFloat(
					"TranslationX", transValue, 0.0f);
			transBodyAnimator.setTarget(tansBody);
			transBodyAnimator.setValues(trans);
			transBodyAnimator.setDuration(DURATION);

			ObjectAnimator checkBoxAnim = new ObjectAnimator();
			final PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(
					"ScaleX", 1.0f, 0.0f);
			final PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(
					"ScaleY", 1.0f, 0.0f);
			final PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(
					"Alpha", 1.0f, 0.0f);
			checkBoxAnim.setValues(scaleX, scaleY, alpha);
			checkBoxAnim.setTarget(checkBox);
			checkBoxAnim.setDuration(DURATION);
			checkBoxAnim.setInterpolator(new AccelerateInterpolator());
			checkBoxAnim.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationStart(Animator animation) {
					checkBox.setTag("animating");
				}

				@Override
				public void onAnimationEnd(Animator animation) {
					// adapter.setCheckBoxAnimator(false);
					checkBox.setScaleX(1.0f);
					checkBox.setScaleY(1.0f);
					checkBox.setAlpha(1.0f);
					checkBox.setVisibility(View.GONE);
					checkBox.setTag("animated");
				}
			});
			if (!(checkBox.getTag() != null && "animating".equals(checkBox
					.getTag()))) {
				transBodyAnimator.start();
				checkBoxAnim.start();
			}
		}
	}

}
