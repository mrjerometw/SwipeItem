package com.example.jerome.swipeitem;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ListView;

public class SwipeListView extends ListView {

	public interface OnClickListener {
		public void OnClick(View view, int position);
	}
	private OnClickListener mOnClickListener = null;
	private static final int mToutchStateNone = 0;
	private static final int mTouchStateX = 1;
	private static final int mTouchStateY = 2;

	private int mMaxY = 5;
	private int mMaxX = 3;
	private float mDownX;
	private float mDownY;
	private int mTouchState;
	private int mTouchPosition;
	private SwipeItemLayout mTouchView;

//	private OnSwipeListener mOnSwipeListener;

//	private OnMenuItemClickListener mOnMenuItemClickListener;
	private Interpolator mCloseInterpolator;
	private Interpolator mOpenInterpolator;

	public SwipeListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	public SwipeListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public SwipeListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}
	public void setOnClickListener(OnClickListener listener){
		mOnClickListener = listener;
	}
	private void init() {
		mMaxX = dp2px(mMaxX);
		mMaxY = dp2px(mMaxY);
		mTouchState = mToutchStateNone;
	}

//	@Override
//	public void setAdapter(ListAdapter adapter) {
//		super.setAdapter(new SwipeMenuAdapter(getContext(), adapter) {
//			@Override
//			public void createMenu(SwipeMenu menu) {
//				if (mMenuCreator != null) {
//					mMenuCreator.create(menu);
//				}
//			}
//
//			@Override
//			public void onItemClick(SwipeMenuView view, SwipeMenu menu,
//					int index) {
//				if (mOnMenuItemClickListener != null) {
//					mOnMenuItemClickListener.onMenuItemClick(
//							view.getPosition(), menu, index);
//				}
//				if (mTouchView != null) {
//					mTouchView.smoothCloseMenu();
//				}
//			}
//		});
//	}

	public void setCloseInterpolator(Interpolator interpolator) {
		mCloseInterpolator = interpolator;
	}

	public void setOpenInterpolator(Interpolator interpolator) {
		mOpenInterpolator = interpolator;
	}

	public Interpolator getOpenInterpolator() {
		return mOpenInterpolator;
	}

	public Interpolator getCloseInterpolator() {
		return mCloseInterpolator;
	}

//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent ev) {
//		return super.onInterceptTouchEvent(ev);
//	}
	private long mTouchDownTime = 0;
	private long mTouchUpTime = 0;
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction() != MotionEvent.ACTION_DOWN && mTouchView == null)
			return super.onTouchEvent(ev);
		int action = MotionEventCompat.getActionMasked(ev);
		action = ev.getAction();
		switch (action) {
			case MotionEvent.ACTION_CANCEL:
				getParent().requestDisallowInterceptTouchEvent(false);
				break;
			case MotionEvent.ACTION_DOWN:
				mTouchDownTime = System.currentTimeMillis();
				int oldPos = mTouchPosition;
				mDownX = ev.getX();
				mDownY = ev.getY();
				mTouchState = mToutchStateNone;

				mTouchPosition = pointToPosition((int) ev.getX(), (int) ev.getY());

				if (mTouchPosition == oldPos && mTouchView != null
						&& mTouchView.isOpen()) {
					mTouchState = mTouchStateX;
					mTouchView.onSwipe(ev);
					return true;
				}

				View view = getChildAt(mTouchPosition - getFirstVisiblePosition());

				if (mTouchView != null && mTouchView.isOpen()) {
					mTouchView.smoothCloseMenu();
					mTouchView = null;
					return super.onTouchEvent(ev);
				}
				if (view instanceof SwipeItemLayout) {
					mTouchView = (SwipeItemLayout) view;
				}
				if (mTouchView != null) {
					mTouchView.onSwipe(ev);
				}
				break;
			case MotionEvent.ACTION_MOVE:
				getParent().requestDisallowInterceptTouchEvent(true);
				float dy = Math.abs((ev.getY() - mDownY));
				float dx = Math.abs((ev.getX() - mDownX));
				if (mTouchState == mTouchStateX) {
					if (mTouchView != null) {
						mTouchView.onSwipe(ev);
					}
					getSelector().setState(new int[] { 0 });
					ev.setAction(MotionEvent.ACTION_CANCEL);
					super.onTouchEvent(ev);
					return true;
				} else if (mTouchState == mToutchStateNone) {
					if (Math.abs(dy) > mMaxY) {
						mTouchState = mTouchStateY;
					} else if (dx > mMaxX) {
						mTouchState = mTouchStateX;
	//					if (mOnSwipeListener != null) {
	//						mOnSwipeListener.onSwipeStart(mTouchPosition);
	//					}
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				getParent().requestDisallowInterceptTouchEvent(false);
				mTouchUpTime = System.currentTimeMillis();
				float offsetX = Math.abs((ev.getY() - mDownY));
				float offsetY = Math.abs((ev.getX() - mDownX));
				float clickTolerance = 5;
				if (offsetX < clickTolerance && offsetY < clickTolerance){
					triggerClickEvennt(mTouchView, mTouchPosition);
				}
				if (mTouchState == mTouchStateX) {
					if (mTouchView != null) {
						mTouchView.onSwipe(ev);
						if (!mTouchView.isOpen()) {
							mTouchPosition = -1;
							mTouchView = null;
						}
					}
	//				if (mOnSwipeListener != null) {
	//					mOnSwipeListener.onSwipeEnd(mTouchPosition);
	//				}
					ev.setAction(MotionEvent.ACTION_CANCEL);
					super.onTouchEvent(ev);
					return true;
				}
				break;
		}
		return super.onTouchEvent(ev);
	}
	private void triggerClickEvennt(View view, int position){
		if (mOnClickListener != null)
			mOnClickListener.OnClick(view, position);
	}
	public void smoothOpenMenu(int position) {
		if (position >= getFirstVisiblePosition()
				&& position <= getLastVisiblePosition()) {
			View view = getChildAt(position - getFirstVisiblePosition());
			if (view instanceof SwipeItemLayout) {
				mTouchPosition = position;
				if (mTouchView != null && mTouchView.isOpen()) {
					mTouchView.smoothCloseMenu();
				}
				mTouchView = (SwipeItemLayout) view;
				mTouchView.smoothOpenMenu();
			}
		}
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getContext().getResources().getDisplayMetrics());
	}
}
