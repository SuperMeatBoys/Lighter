package com.gesuper.lighter.widget;

import java.util.ArrayList;
import java.util.List;

import com.gesuper.lightclock.view.AlertItemView;
import com.gesuper.lighter.R;
import com.gesuper.lighter.widget.MultiGestureDetector.MultiMotionEvent;
import com.gesuper.lighter.widget.MultiGestureDetector.OnMultiGestureListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.view.inputmethod.InputMethodManager;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;

public class MoveableListView extends ListView implements OnTouchListener, OnMultiGestureListener{
	public static final String TAG = "MoveableListView";

	//status code for head view
	public final int HEAD_PULL = 0;
	public final int HEAD_RELEASE = 1;
	public final int HEAD_CREATING = 2;
	public final int HEAD_DONE = 3;
	public final int ITEM_NORMAL = 0;
	public final int ITEM_FINISH = 1;
	public final int ITEM_DELETE = 2;
	public final int HANDLE_NOTHING = 0;
	public final int HANDLE_HEAD = 1;
	public final int HANDLE_ITEM = 2;
	public final int HANDLE_LONGPRESS = 3;
	public final int HANDLE_EDITING = 4;
	
	private int screenWidth;
	private int screenHeight;
	private LayoutInflater mInflater;
	private Context context;
	private MultiGestureDetector mGesture;
	private List<TouchEvent> touchEvents;
	private int status;
	
	//field for create new 
	private LinearLayout mHeadView;
	private EditText mHeadText;
	//private int mHeadWidth;
	private int mHeadHeight;
	private int headStatus;
	private TranslateAnimation translateAnimation;
	
	// field for item 
	private int itemStatus;
	private ItemViewBase currentItem;
	private int scrollY;
	private View mFootText;
	
	//field for long press
	private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;
	private Bitmap mDragBitmap;
	private ImageView mDragView;
    private ItemViewBase mDragItemView;
	private int mDragCurrentPostion;
	private int mDragOffSetY;
	private int mDragPointX;
	private int mDragPointY;
	private int mUpperBound;
	private int mLowerBound;
	
	public MoveableListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.initResource();
	}
	
	public MoveableListView(Context context, AttributeSet attrs){
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.initResource();
	}
	
	@SuppressWarnings("deprecation")
	public void initResource() {
		// TODO Auto-generated method stub
		WindowManager wm = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
	    screenWidth = wm.getDefaultDisplay().getWidth();
	    screenHeight = wm.getDefaultDisplay().getHeight();
		this.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS );
		this.mInflater = LayoutInflater.from(this.context);
		this.mGesture = new MultiGestureDetector(this.context, this);
		this.mGesture.setIsLongpressEnabled(true);
		this.touchEvents = new ArrayList<TouchEvent>();
        this.status = HANDLE_NOTHING;
        
		//init for head view
		this.mHeadView = new ListHeadView(this.context);
		this.mHeadText = (EditText) this.mHeadView.findViewById(R.id.event_content_et);
		this.mHeadHeight = (int) this.context.getResources().getDimension(R.dimen.item_height);
		this.addHeaderView(this.mHeadView);
		this.mHeadView.setPadding(this.mHeadView.getPaddingLeft(), -1 * this.mHeadHeight, this.mHeadView.getPaddingRight(), this.mHeadView.getPaddingBottom());
		this.headStatus = HEAD_DONE;
		this.itemStatus = ITEM_NORMAL;
		
		View v = this.mInflater.inflate(R.layout.list_footer, null);
		mFootText = (TextView) v.findViewById(R.id.list_foot_text);
		this.addFooterView(v);
		
		this.translateAnimation = new TranslateAnimation(0, -480, 0, 0);
		this.translateAnimation.setDuration(200L);
		this.translateAnimation.setFillEnabled(true);
		this.translateAnimation.setFillBefore(true);
		this.translateAnimation.setAnimationListener(new AnimationListener(){
			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				MoveableListView.this.updateHeadStatus(-1);
				MoveableListView.this.status = HANDLE_NOTHING;
			}
			public void onAnimationRepeat(Animation arg0) {}
			public void onAnimationStart(Animation arg0) {}
		});
	}
	
	private void updateHeadText(){
		switch(this.headStatus){
		case HEAD_PULL:
			this.mHeadText.setText(R.string.event_pull_create);
			break;
		case HEAD_RELEASE:
			this.mHeadText.setText(R.string.event_release_create);
			break;
		case HEAD_CREATING:
			this.mHeadText.setText("");
			break;
		case HEAD_DONE:  
            this.mHeadText.setText(R.string.event_pull_create);
		}
	}

	private void updateHeadStatus(int y) {
		// TODO Auto-generated method stub
		if(this.status != HANDLE_HEAD)  return ;
		
		switch(this.headStatus){
		case HEAD_PULL:
			if(y > this.mHeadHeight){
				this.headStatus = HEAD_RELEASE;
			}else if(y < 0){
				this.headStatus = HEAD_DONE;
			}
			break;
		case HEAD_RELEASE:
			if( y < 0){
				this.headStatus = HEAD_DONE;
			} else if(y < this.mHeadHeight){
				this.headStatus = HEAD_PULL;
			} else if(y > 3*this.mHeadHeight){
				return ;
			}
			break;
		case HEAD_CREATING:
			y = this.mHeadHeight;
			break;
		case HEAD_DONE:
			y = 0;
			break;
		}
		this.updateHeadText();
		mHeadView.setPadding(0, y - this.mHeadHeight, 0, 0);
	}

	private void updateItemStatus(int x) {
		// TODO Auto-generated method stub
		switch(this.itemStatus){
		case ITEM_NORMAL:
			if(x > this.mHeadHeight){
				this.itemStatus = ITEM_FINISH;
			} else if((-1 * x) > this.mHeadHeight){
				this.itemStatus = ITEM_DELETE;
			}
			break;
		case ITEM_FINISH:
			if(x < this.mHeadHeight){
				this.itemStatus = ITEM_NORMAL;
			}
			break;
		case ITEM_DELETE:
			if((-1 * x) < this.mHeadHeight){
				this.itemStatus = ITEM_NORMAL;
			}
			break;
		}
		float radio = (float)Math.abs(x)/this.screenWidth + 1;
		LinearLayout v = this.currentItem.getContentLearLayout();
		FrameLayout.LayoutParams p = (FrameLayout.LayoutParams) v.getLayoutParams();
		p.setMargins((int) (x/radio), 0, 0, 0);
		v.setLayoutParams(p);
	}
	
	private void startEditItem(int position) {
		// TODO Auto-generated method stub
		if(position == 0){		//edit head view
			this.mHeadText.requestFocus();
		} else {
			scrollItemToTop(position);
			this.currentItem.startEdit();
		}
		this.hideBelowItems(position);
		InputMethodManager inputManager = 
			(InputMethodManager)this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.showSoftInput(this.mHeadText, 0);
	}
	
	private void endEditItem(int index) {
		// TODO Auto-generated method stub
		if(index == 0){		//end edit head view
			String content = this.mHeadText.getText().toString();
			if(content.length() == 0){
				//start ani
				this.mHeadView.startAnimation(translateAnimation);
			}
		} else {
			scrollToOrigin();
			this.currentItem.startEdit();
		}
		this.showBelowItems(index);
		InputMethodManager inputManager = 
			(InputMethodManager)this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(this.mHeadText.getWindowToken(), 0);
	}
	
	@SuppressLint("NewApi")
	private void scrollItemToTop(int position){
		ViewGroup.LayoutParams p = this.mFootText.getLayoutParams();
		p.height = this.screenHeight;
		this.mFootText.setLayoutParams(p);
		this.scrollY = this.getScrollY();
		this.smoothScrollToPositionFromTop(position, 0);
		Log.v(TAG, "Foot View Top: " + mFootText.getTop());
	}
	
	private void scrollToOrigin(){
		ViewGroup.LayoutParams p = this.mFootText.getLayoutParams();
		p.height = 0;
		this.mFootText.setLayoutParams(p);
		this.scrollTo(0, this.scrollY);
	}

	private void hideBelowItems(int index) {
		// TODO Auto-generated method stub
		int total = this.getAdapter().getCount();
		for(int i = index +1 ;i < total - 1;i++){
			ItemViewBase v = (ItemViewBase) this.getChildAt(i - this.getFirstVisiblePosition());
			if(v != null){
				v.halfAlpha();
			}
		}
	}
	private void showBelowItems(int index){
		int total = this.getAdapter().getCount();
		for(int i = index +1 ;i < total - 1;i++){
			ItemViewBase v = (ItemViewBase) this.getChildAt(i - this.getFirstVisiblePosition());
			if(v != null){
				v.noneAlpha();
			}
		}
	}
	
	@Override
	public boolean onDown(MultiMotionEvent e) {
		// TODO Auto-generated method stub
		Log.v(TAG, "onDown " + e.getX() + " " + e.getY());
		TouchEvent touch = new TouchEvent(e);
		this.addEventtoList(touch);
		return false;
	}
	
	@Override
	public void onShowPress(MultiMotionEvent e) {
		// TODO Auto-generated method stub
		Log.v(TAG, "onShowPress");
	}
	
	@Override
	public boolean onSingleTapUp(MultiMotionEvent e) {
		// TODO Auto-generated method stub
		TouchEvent touch = touchEvents.get(e.getId());
		if(touch == null){
			return false;
		}
		touch.isLongPress = false;
		this.removeEventFromList(e.getId());
		Log.v(TAG, "onSingleTapUp ");
		switch(this.status){
		case HANDLE_NOTHING:
			int position = this.pointToPosition((int)e.getOffsetX(), (int)e.getOffsetY());
			if(position == INVALID_POSITION){
				break;
			}
			this.currentItem = (EventItemView) this.getChildAt(position - this.getFirstVisiblePosition());
			
			if(this.currentItem instanceof ItemViewBase){
				if(this.currentItem.isNeedFocus((int)e.getOffsetX(), (int)e.getOffsetY() - this.currentItem.getTop())){
					this.startEditItem(position);
					this.status = HANDLE_EDITING;
				}
			}
			if(position < 0){
				//create new item at bottom
				
			}
			break;
		case HANDLE_HEAD:
			if(this.headStatus == HEAD_CREATING){
				this.headStatus = HEAD_DONE;
				this.endEditItem(0);
			}else if(this.headStatus == HEAD_RELEASE){
				this.headStatus = HEAD_CREATING;
				this.updateHeadStatus(-1);
				this.startEditItem(0);
			}else {
				this.updateHeadStatus(-1);
			}
			break;
		case HANDLE_ITEM:
			if(this.itemStatus == ITEM_FINISH){
				this.currentItem.finishItem();
			}
			this.updateItemStatus(0);
			this.status = HANDLE_NOTHING;
			break;
		case HANDLE_LONGPRESS:
			this.status = HANDLE_NOTHING;
			break;
		case HANDLE_EDITING:
			if(this.currentItem instanceof ItemViewBase){
				if(this.currentItem.isNeedFocus((int)e.getOffsetX(), (int)e.getOffsetY() - this.currentItem.getTop())){
					break;
				}
			}
			this.endEditItem(0);
			this.status = HANDLE_NOTHING;
			break;
		}
		return false;
	}
	
	@Override
	public boolean onFling(MultiMotionEvent e1, MultiMotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		Log.v(TAG, "onFling " + e2.getX() + " " + e2.getY());
		TouchEvent touch = touchEvents.get(e1.getId());
		if(touch == null){
			return false;
		}
		touch.isLongPress = false;
		this.removeEventFromList(e1.getId());
		switch(this.status){
		case HANDLE_NOTHING:
			break;
		case HANDLE_HEAD:
			if(this.headStatus == HEAD_CREATING){
				this.headStatus = HEAD_DONE;
				this.endEditItem(0);
			}else if(this.headStatus == HEAD_RELEASE){
				this.headStatus = HEAD_CREATING;
				this.updateHeadStatus(-1);
				this.startEditItem(0);
			}else {
				this.updateHeadStatus(-1);
			}
			break;
		case HANDLE_ITEM:
			if(this.itemStatus == ITEM_FINISH){
				this.currentItem.finishItem();
			}
			this.updateItemStatus(0);
			this.status = HANDLE_NOTHING;
			break;
		case HANDLE_LONGPRESS:
			this.status = HANDLE_NOTHING;
			break;
		}
		return false;
	}
	
	@Override
	public boolean onScroll(MultiMotionEvent e1, MultiMotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		//Log.v(TAG, "onScroll " + e2.getX() + " " + e2.getY());
		int dX = (int) (e2.getX() - e1.getX());
		int dY = (int) (e2.getY() - e1.getY());
		//Log.v(TAG, "onScroll " + dX + " " + dY);
		if(Math.abs(dX) > 2*Math.abs(dY)){
			//水平滚动
			//如果竖直滑动距离大于一个item的长度
			if(Math.abs(distanceY) > this.mHeadHeight) return false;
			int position = this.pointToPosition((int)e2.getOffsetX(), (int)e2.getOffsetY());
			if(position == INVALID_POSITION){
				return false;
			}
			if(this.itemStatus == ITEM_NORMAL)
				this.currentItem = (ItemViewBase) this.getChildAt(position - this.getFirstVisiblePosition());
			this.status = HANDLE_ITEM;
			this.updateItemStatus(dX);
		} else if(Math.abs(dY) > 2*Math.abs(dX)){
			//竖直滚动
			if(this.getFirstVisiblePosition() == 0 && this.status != HANDLE_ITEM){
				this.status = HANDLE_HEAD;
				this.headStatus = HEAD_PULL;
				this.updateHeadStatus(dY);
			}
		}
		return false;
	}

	@Override
	public void onLongPress(MultiMotionEvent e) {
		// TODO Auto-generated method stub
		TouchEvent touch = touchEvents.get(e.getId());
		if(touch == null){
			return ;
		}
		touch.isLongPress = false;
		this.status = HANDLE_LONGPRESS;
		Log.v(TAG, "onLongPress");
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return this.mGesture.onTouchEvent(event);
	}
	
//	private void measureView(View child) {  
//        ViewGroup.LayoutParams p = child.getLayoutParams();  
//        if (p == null) {  
//            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,  
//                    ViewGroup.LayoutParams.WRAP_CONTENT);  
//        }  
//        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);  
//        int lpHeight = p.height;  
//        int childHeightSpec;  
//        if (lpHeight > 0) {  
//            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);  
//        } else {  
//            childHeightSpec = MeasureSpec.makeMeasureSpec(0,  MeasureSpec.UNSPECIFIED);  
//        }  
//        child.measure(childWidthSpec, childHeightSpec);  
//    }
	
	private class TouchEvent{
        private MultiMotionEvent mCurrentDownEvent;
        private boolean isLongPress;
        
        private TouchEvent(MultiMotionEvent me) {  
            mCurrentDownEvent = me;
            isLongPress = false;
        }
	}
	
	/** 
     * 从事件信息队列中移除指定序号的事件 
     *  
     * @param idx 
     */  
    private void removeEventFromList(int id) {  
        if (id > touchEvents.size() || id < 0) {  
            // Log.e(MYTAG, CLASS_NAME + ".removeEventFromList(), id=" + id + ", while sEventInfos.size() =" +  
            // sEventInfos.size());  
            return;  
        }  
        touchEvents.set(id, null);  
    }  
  
    /** 
     * 向事件队列中添加新信息 
     *  
     * @param e 
     */  
    private void addEventtoList(TouchEvent info) {  
        int id = info.mCurrentDownEvent.getId();  
        if (id < touchEvents.size()) {  
            // if (sEventInfos.get(id) != null)  
            // Log.e(MYTAG, CLASS_NAME + ".addEventIntoList, info(" + id + ") has not set to null !");  
        	touchEvents.set(info.mCurrentDownEvent.getId(), info);  
        } else if (id == touchEvents.size()) {  
        	touchEvents.add(info);  
        } else {  
            // Log.e(MYTAG, CLASS_NAME + ".addEventIntoList, invalidata id !");  
        }  
    } 
}

