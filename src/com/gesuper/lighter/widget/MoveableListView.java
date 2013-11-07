package com.gesuper.lighter.widget;

import java.util.ArrayList;
import java.util.List;

import com.gesuper.lighter.R;
import com.gesuper.lighter.tools.Rotate3DAnimation;
import com.gesuper.lighter.widget.MultiGestureDetector.MultiMotionEvent;
import com.gesuper.lighter.widget.MultiGestureDetector.OnMultiGestureListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
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
import android.view.animation.AccelerateInterpolator;
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
	public final int HANDLE_ITEM_MOVE = 2;
	public final int HANDLE_LONGPRESS = 3;
	public final int HANDLE_ITEM_EDITING = 4;
	public final int HANDLE_FOOT = 5;
	public final int HANDLE_MULTI = 6;
	public final int HANDLE_MULTI_USE = 7;
	
	private int headLayout;
	private int footLayout;
	private int screenWidth;
	private int screenHeight;
	private LayoutInflater mInflater;
	protected Context context;
	private MultiGestureDetector mGesture;
	private List<TouchEvent> touchEvents;
	private int belowTouch;
	private int upTouch;
	private ItemViewBase belowItem;
	private ItemViewBase upItem;
	private int initPaddingTop;
	private int status;
	
	//field for create new 
	private LinearLayout mHeadView;
	private LinearLayout mHeadLinear;
	private EditText mHeadText;
	//private int mHeadWidth;
	private int mHeadHeight;
	private int headStatus;
	private ImageView mHeadImage;
	private Bitmap mHeadBitmap;
	private TranslateAnimation translateAnimation;
	private Rotate3DAnimation rotate3dAnimation;
	private LinearLayout mFootView;
	private LinearLayout mFootLinear;
	private TextView mFootText;
	private ImageView mFootImage;
	private View mFootPlaceHolder;
	private Bitmap mFootBitmap;
	// field for item 
	private int itemStatus;
	private ItemViewBase currentItem;
	private ImageView createImage;
	private int scrollY;
	
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
	private int mTouchSlop;

	private OnCreateNewItemListener newItemListener;
	private onItemClickedListener itemClickedListener;
	public MoveableListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.initResource();
	}
	
	public MoveableListView(Context context, AttributeSet attrs, int headLayout, int footLayout){
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.headLayout = headLayout;
		this.footLayout = footLayout;
		this.initResource();
	}
	
	@SuppressWarnings("deprecation")
	public void initResource() {
		// TODO Auto-generated method stub
		this.newItemListener = null;
		this.itemClickedListener = null;
		this.setSmoothScrollbarEnabled(true);
		WindowManager wm = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
	    screenWidth = wm.getDefaultDisplay().getWidth();
	    screenHeight = wm.getDefaultDisplay().getHeight();
	    Log.d(TAG, "width: " + screenWidth + " height: " + screenHeight);
		//this.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS );
		this.mInflater = LayoutInflater.from(this.context);
		this.mGesture = new MultiGestureDetector(this.context, this);
		this.mGesture.setIsLongpressEnabled(true);
		this.touchEvents = new ArrayList<TouchEvent>();
		this.belowTouch = 0; this.upTouch = 1;
        this.status = HANDLE_NOTHING;
        
		//init for head view
		//this.mHeadView = new HeadViewBase(this.context);
		this.mHeadView = (LinearLayout) mInflater.inflate(this.headLayout, null);
		this.measureView(mHeadView);
		this.mHeadLinear = (LinearLayout) this.mHeadView.findViewById(R.id.item_head_linear);
		this.mHeadText = (EditText) this.mHeadView.findViewById(R.id.item_content_et);
		this.mHeadImage = (ImageView) this.mHeadView.findViewById(R.id.item_head_bitmap);
		this.mHeadHeight = (int) this.context.getResources().getDimension(R.dimen.item_height);
		this.mHeadBitmap = this.getBitmapofView(this.mHeadView);
		this.addHeaderView(this.mHeadView);
		this.mHeadView.setPadding(this.mHeadView.getPaddingLeft(), -1 * this.mHeadHeight, this.mHeadView.getPaddingRight(), this.mHeadView.getPaddingBottom());
		this.mHeadImage.setImageBitmap(this.mHeadBitmap);
		this.mHeadLinear.setVisibility(View.GONE);
		this.mHeadImage.setVisibility(View.VISIBLE);
		this.headStatus = HEAD_DONE;
		this.itemStatus = ITEM_NORMAL;
		
		this.mFootView = (LinearLayout) this.mInflater.inflate(this.footLayout, null);
		this.measureView(this.mFootView);
		this.mFootLinear = (LinearLayout) this.mFootView.findViewById(R.id.item_foot_linear);
		this.mFootText = (TextView) this.mFootView.findViewById(R.id.item_content_et);
		this.mFootImage = (ImageView) this.mFootView.findViewById(R.id.item_foot_image);
		this.mFootPlaceHolder = this.mFootView.findViewById(R.id.item_foot_view);
		this.mFootBitmap = this.getBitmapofView(this.mFootView);
		this.mFootImage.setImageBitmap(this.mFootBitmap);
		this.addFooterView(this.mFootView);
		this.mFootLinear.setVisibility(View.GONE);
		this.mFootImage.setVisibility(View.GONE);
		this.mFootPlaceHolder.setVisibility(View.VISIBLE);
		
		this.translateAnimation = new TranslateAnimation(0, -screenWidth, 0, 0);
		this.translateAnimation.setDuration(500L);
		this.translateAnimation.setInterpolator(new AccelerateInterpolator());
		this.translateAnimation.setAnimationListener(new AnimationListener(){
			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				if(MoveableListView.this.status == HANDLE_HEAD){
					MoveableListView.this.updateHeadStatus(0);
					MoveableListView.this.status = HANDLE_NOTHING;
				} else if(MoveableListView.this.status == HANDLE_FOOT){
					MoveableListView.this.status = HANDLE_NOTHING;
					MoveableListView.this.mFootText.setText(null);
					MoveableListView.this.mFootLinear.setVisibility(View.GONE);
					MoveableListView.this.mFootPlaceHolder.setVisibility(View.VISIBLE);
				}
			}
			public void onAnimationRepeat(Animation arg0) {}
			public void onAnimationStart(Animation arg0) {}
		});
		this.rotate3dAnimation = new Rotate3DAnimation(-90,0, this.mFootBitmap.getWidth()/2, this.mFootBitmap.getHeight()/2, 0, false);
		this.rotate3dAnimation.setDuration(500L);
		this.rotate3dAnimation.setAnimationListener(new AnimationListener(){
			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				mFootImage.setVisibility(View.GONE);
				mFootLinear.setVisibility(View.VISIBLE);
				startEditItem(MoveableListView.this.getChildCount());
			}
			public void onAnimationRepeat(Animation arg0) {}
			public void onAnimationStart(Animation arg0) {}
		});
		this.mTouchSlop = ViewConfiguration.get(this.getContext()).getScaledTouchSlop();
		this.createImage = null;
	}
	
	public void setOnCreateNewItem(OnCreateNewItemListener listener){
		this.newItemListener = listener;
	}
	
	
	public void setOnItemClickedListener(onItemClickedListener listener){
		this.itemClickedListener = listener;
	}
	private void updateHeadText(){
		switch(this.headStatus){
		case HEAD_PULL:
			//this.mHeadText.setText(R.string.event_pull_create);
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
				this.mHeadImage.setVisibility(View.GONE);
				this.mHeadLinear.setVisibility(View.VISIBLE);
			}else if(y <= 0){
				this.headStatus = HEAD_DONE;
			}
			break;
		case HEAD_RELEASE:
			if( y <= 0){
				this.headStatus = HEAD_DONE;
			} else if(y < this.mHeadHeight){
				this.headStatus = HEAD_PULL;
				this.mHeadLinear.setVisibility(View.GONE);
				this.mHeadImage.setVisibility(View.VISIBLE);
			} else if(y > 3*this.mHeadHeight){
				return ;
			}
			break;
		case HEAD_CREATING:
			y = this.mHeadHeight;
			break;
		case HEAD_DONE:
			if(y>0){
				this.headStatus = HEAD_PULL;	
			} else {
			y = 0;
				this.mHeadLinear.setVisibility(View.GONE);
				this.mHeadImage.setVisibility(View.VISIBLE);
				break;
			}
		}
		
		if(this.headStatus == HEAD_PULL){
			Bitmap map = this.roateImageView(this.mHeadBitmap, 0, y, 0);
			this.mHeadImage.setImageBitmap(map);
		}
		this.updateHeadText();
		this.mHeadView.setPadding(0, y - this.mHeadHeight, 0, 0);
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
		this.hideBelowItems(position);
		if(position == 0){		//edit head view
			this.mHeadText.requestFocus();
		} else if(position == this.getChildCount()){
			this.mFootText.requestFocus();
		} else {
			scrollItemToTop(position);
			//this.currentItem.startEdit();
		}
	}
	
	private void endEditItem(int index) {
		// TODO Auto-generated method stub
		this.showBelowItems(index);
		InputMethodManager inputManager = 
			(InputMethodManager)this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(this.mHeadText.getWindowToken(), 0);
		if(index == 0){		//end edit head view
			String content = this.mHeadText.getText().toString();
			if(content.length() == 0){
				//start ani
				this.mHeadView.startAnimation(translateAnimation);
			} else {
				this.updateHeadStatus(0);
				this.status = HANDLE_NOTHING;
				if(this.newItemListener != null)
					this.newItemListener.createNewItem(OnCreateNewItemListener.CREATE_TOP, content);
			}
		} else if(index == this.getChildCount()){
			String content = this.mFootText.getText().toString();
			if(content.length() == 0){
				//start ani
				this.mFootView.startAnimation(translateAnimation);
			} else {
				if(this.newItemListener != null)
					this.newItemListener.createNewItem(OnCreateNewItemListener.CREATE_BOTTOM, content);
				this.status = HANDLE_NOTHING;
				this.mFootText.setText(null);
				this.mFootLinear.setVisibility(View.GONE);
				this.mFootPlaceHolder.setVisibility(View.VISIBLE);
			}
		} else {
			scrollToOrigin();
			this.currentItem.endEdit();
		}
	}
	
	@SuppressLint("NewApi")
	private void scrollItemToTop(final int position){
		ViewGroup.LayoutParams p = this.mFootPlaceHolder.getLayoutParams();
		p.height = this.screenHeight;
		this.mFootPlaceHolder.setLayoutParams(p);
		this.scrollY = this.getScrollY();
		this.smoothScrollToPositionFromTop(position, this.currentItem.getTop());
		Log.v(TAG, "this.scrollY: " + this.scrollY);
	}
	
	private void scrollToOrigin(){
		this.scrollTo(0, this.scrollY);
		ViewGroup.LayoutParams p = this.mFootPlaceHolder.getLayoutParams();
		p.height = 0;
		this.mFootPlaceHolder.setLayoutParams(p);
	}

	private void hideBelowItems(int index) {
		// TODO Auto-generated method stub
		int total = this.getAdapter().getCount() - this.getFooterViewsCount();
		for(int i = index +1 ;i < total ; i++){
			ItemViewBase v = (ItemViewBase) this.getChildAt(i - this.getFirstVisiblePosition());
			if(v != null){
				v.halfAlpha();
			}
		}
	}
	private void showBelowItems(int index){
		int total = this.getAdapter().getCount() - this.getFooterViewsCount();;
		for(int i = index +1 ;i < total ; i++){
			ItemViewBase v = (ItemViewBase) this.getChildAt(i - this.getFirstVisiblePosition());
			if(v != null){
				v.noneAlpha();
			}
		}
	}
	
	@Override
	public boolean onDown(MultiMotionEvent e) {
		// TODO Auto-generated method stub
		TouchEvent touch = new TouchEvent(e);
		this.addEventtoList(touch);
		Log.v(TAG, "onDown " + e.getX() + " " + e.getY() + " " + e.getId());
		//Multi touch
		if(this.getFingerCount() > 1 ){
			//clear other motions
			//if(this.status == HANDLE_NOTHING){
				this.status = HANDLE_MULTI;
			//}
			TouchEvent t1 = this.touchEvents.get(0);
			int p1 = this.pointToPosition(
					(int)t1.mCurrentDownEvent.getX(), 
					(int)t1.mCurrentDownEvent.getY());
			Log.v(TAG, "First touch Position: " + p1);
			int p2 = this.pointToPosition((int)e.getX(), (int)e.getY());
			if(p1 == INVALID_POSITION){
				return false;
			} else {
				if(t1.mCurrentDownEvent.getY() < e.getY()){
					p2 = p1 + 1;
					this.belowTouch=1;
					this.upTouch = 0;
				} else {
					p2 = p1;
					p1 = p2 - 1;
					this.belowTouch=0;
					this.upTouch = 1;
				}
			}
			try{
				this.belowItem = (ItemViewBase) this.getChildAt(p2 - this.getFirstVisiblePosition());
				this.createImage = (ImageView) this.belowItem.findViewById(R.id.item_create_image);
				this.createImage.setVisibility(View.VISIBLE);
				this.upItem = (ItemViewBase) this.getChildAt(p1 - this.getFirstVisiblePosition());
			} catch(ClassCastException error){
				Log.v(TAG, "ClassCastException");
				return false;
			}
			this.status = HANDLE_MULTI_USE;
			this.initPaddingTop = this.getPaddingTop();
		}
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
				//create new item on bottom
				this.mFootPlaceHolder.setVisibility(View.GONE);
				this.mFootImage.setVisibility(View.VISIBLE);
				this.mFootImage.startAnimation(this.rotate3dAnimation);
				this.status = HANDLE_FOOT;
				break;
			}
			View v = this.getChildAt(position - this.getFirstVisiblePosition());
			
			if(v instanceof ItemViewBase){
				this.currentItem = (ItemViewBase) v;
				if(this.currentItem.isNeedFocus((int)e.getOffsetX(), (int)e.getOffsetY() - this.currentItem.getTop())){
					this.startEditItem(position);
					this.status = HANDLE_ITEM_EDITING;
				}else if(this.itemClickedListener != null){
					this.itemClickedListener.onItemClicked(position);
				}
			}
			break;
		case HANDLE_HEAD:
			if(this.headStatus == HEAD_CREATING){
				this.headStatus = HEAD_DONE;
				this.endEditItem(0);
			}else if(this.headStatus == HEAD_RELEASE){
				this.headStatus = HEAD_CREATING;
				this.updateHeadStatus(0);
				this.startEditItem(0);
			}else {
				this.updateHeadStatus(0);
				this.status = HANDLE_NOTHING;
			}
			break;
		case HANDLE_ITEM_MOVE:
			if(this.itemStatus == ITEM_FINISH){
				this.currentItem.finishItem();
			}
			this.updateItemStatus(0);
			this.status = HANDLE_NOTHING;
			break;
		case HANDLE_LONGPRESS:
			this.status = HANDLE_NOTHING;
			stopDrag();
			break;
		case HANDLE_ITEM_EDITING:
			if(this.currentItem instanceof ItemViewBase){
				if(this.currentItem.isNeedFocus((int)e.getOffsetX(), (int)e.getOffsetY() - this.currentItem.getTop())){
					break;
				}
			}
			this.endEditItem(1);
			this.status = HANDLE_NOTHING;
			break;
		case HANDLE_FOOT:
			this.endEditItem(this.getChildCount());
			break;
		case HANDLE_MULTI:
			if(this.getFingerCount() == 0){
				this.setPadding(0, initPaddingTop, 0, 0);
				Log.v(TAG, "no finger on screen");
				this.status = HANDLE_NOTHING;
			}
			break;
		case HANDLE_MULTI_USE:
			if(e.getId() == this.upTouch){
				this.setPadding(0, 0, 0, 0);
				this.upItem.setPadding(0, 0, 0, 0);
			} else if(e.getId() == this.belowTouch){
				this.belowItem.setPadding(0, 0, 0, 0);
			}
			if(this.getFingerCount() == 0){
				this.setPadding(0, initPaddingTop, 0, 0);
				this.createImage.setImageBitmap(null);
				this.createImage.setPadding(0, 0, 0, 0);
				this.status = HANDLE_NOTHING;
			}
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
				this.updateHeadStatus(0);
				this.startEditItem(0);
			}else {
				this.updateHeadStatus(0);
				this.status = HANDLE_NOTHING;
			}
			break;
		case HANDLE_ITEM_MOVE:
			if(this.itemStatus == ITEM_FINISH){
				this.currentItem.finishItem();
			}
			this.updateItemStatus(0);
			this.status = HANDLE_NOTHING;
			break;
		case HANDLE_LONGPRESS:
			this.status = HANDLE_NOTHING;
			stopDrag();
			break;
		case HANDLE_MULTI:
			if(this.getFingerCount() == 0){
				this.setPadding(0, initPaddingTop, 0, 0);
				Log.v(TAG, "no finger on screen");
				this.status = HANDLE_NOTHING;
			}
			break;
		case HANDLE_MULTI_USE:
			if(e1.getId() == this.upTouch){
				this.setPadding(0, 0, 0, 0);
				this.upItem.setPadding(0, 0, 0, 0);
			} else if(e1.getId() == this.belowTouch){
				this.belowItem.setPadding(0, 0, 0, 0);
			}
			if(this.getFingerCount() == 0){
				this.setPadding(0, initPaddingTop, 0, 0);
				this.createImage.setImageBitmap(null);
				this.createImage.setPadding(0, 0, 0, 0);
				this.status = HANDLE_NOTHING;
			}
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
		switch(this.status){
		case HANDLE_NOTHING:{
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
				this.status = HANDLE_ITEM_MOVE;
				this.updateItemStatus(dX);
			} else if(Math.abs(dY) > 2*Math.abs(dX)){
				//竖直滚动
				if(this.getFirstVisiblePosition() == 0 && this.status != HANDLE_ITEM_MOVE){
					this.status = HANDLE_HEAD;
					this.headStatus = HEAD_PULL;
					this.updateHeadStatus(dY);
				}
			}
		}
		case HANDLE_LONGPRESS:{
			dragView((int) e2.getOffsetY());
 			//adjustScrollBounds(this.mCurrentY);
			break;
		}
		case HANDLE_HEAD:{
			this.updateHeadStatus(dY);
			break;
		}
		case HANDLE_ITEM_MOVE:{
			this.updateItemStatus(dX);
			break;
		}
		case HANDLE_MULTI_USE:
			if(this.belowTouch == e1.getId()){
				if(dY<this.mHeadHeight){
					Bitmap map = this.roateImageView(this.mFootBitmap, 0,dY,0);
					this.createImage.setImageBitmap(map);
					this.createImage.setPadding(0, 0, 0, 0);
				}
				else {
					Bitmap map = this.roateImageView(this.mFootBitmap, 0,this.mHeadHeight,0);
					this.createImage.setImageBitmap(map);
					this.createImage.setPadding(0, 0, 0, dY - this.mHeadHeight);
				}
			}
			else if(this.upTouch == e1.getId() && dY <0){
				this.setPadding(0, this.initPaddingTop + dY, 0, 0);
				this.upItem.setPadding(0, 0, 0, -dY);
			}
		}
		return false;
	}

	private void dragView(int y) {
		// TODO Auto-generated method stub
		if(mDragView != null){
	        mWindowParams.y = y - mDragPointY + mDragOffSetY;
	        mWindowManager.updateViewLayout(mDragView, mWindowParams);
		}
		int tempPosition = this.pointToPosition(20, y);
		if(tempPosition == INVALID_POSITION){
			return ;
		}
//		if(mDragCurrentPostion != tempPosition){
//			this.exchangeAdapterItem(mDragCurrentPostion, tempPosition);
//			mDragCurrentPostion = tempPosition;
//		}
		
		//滚动
//		int scrollY = 0;
//		if(this.mCurrentY < mUpperBound){
//			scrollY = 8;
//		}else if(this.mCurrentY > mLowerBound){
//			scrollY = -8;
//		}
//		
//		if(scrollY != 0){
//			int top = this.getChildAt(mDragCurrentPostion - this.getFirstVisiblePosition()).getTop();
//			this.setSelectionFromTop(mDragCurrentPostion, top + scrollY);
//		}
	}

	@Override
	public void onLongPress(MultiMotionEvent e) {
		// TODO Auto-generated method stub
		TouchEvent touch = touchEvents.get(e.getId());
		if(touch == null){
			return ;
		}
		touch.isLongPress = true;
		if(this.status != HANDLE_NOTHING){
			return ;
		}
		this.status = HANDLE_LONGPRESS;
		Log.v(TAG, "onLongPress");
		//prepare for drag currentItem
		int position = this.pointToPosition((int)e.getOffsetX(), (int)e.getOffsetY());
		if(position == INVALID_POSITION){
			return ;
		}
		this.mDragItemView = (EventItemView) this.getChildAt(position - this.getFirstVisiblePosition());
		
 		this.mDragPointX = 9;
 		this.mDragPointY = (int) (e.getOffsetY() - mDragItemView.getTop());
 		this.mDragOffSetY = (int) (e.getY() - e.getOffsetY());
			
 		int height = getHeight();
 		mUpperBound = (int) Math.min(e.getOffsetY() - mTouchSlop, height / 3);
 		mLowerBound = (int) Math.max(e.getOffsetY() + mTouchSlop, height * 2 / 3);
 		mDragCurrentPostion = position;
		
		Log.d(TAG, "drag item:" + this.mDragItemView.getContent());
		this.mDragItemView.setDrawingCacheEnabled(true);
		Bitmap bitmap = Bitmap.createBitmap(this.mDragItemView.getDrawingCache(true));
		this.mDragItemView.setDrawingCacheEnabled(false);
		startDrag(bitmap, this.mDragPointY + this.mDragItemView.getTop());
	}

	private void startDrag(Bitmap bitmap, int y) {
		// TODO Auto-generated method stub
		this.stopDrag();
		this.mWindowParams = new WindowManager.LayoutParams();
		this.mWindowParams.gravity = Gravity.TOP | Gravity.LEFT;
		
		this.mWindowParams.alpha = (float) 0.5;
		this.mWindowParams.x = mDragPointX;
		this.mWindowParams.y = y - mDragPointY + mDragOffSetY;

		this.mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		this.mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		this.mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
		this.mWindowParams.format = PixelFormat.TRANSLUCENT;
		this.mWindowParams.windowAnimations = 0;
        
        Context context = this.getContext();
        ImageView v = new ImageView(context);
        
        v.setImageBitmap(bitmap);
        mDragBitmap = bitmap;

        mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(v, mWindowParams);
        mDragView = v;
	}

	private void stopDrag() {
		// TODO Auto-generated method stub
		if (mDragView != null) {
            mWindowManager.removeView(mDragView);
            mDragView.setImageDrawable(null);
            mDragView = null;
        }
        if (mDragBitmap != null) {
            mDragBitmap.recycle();
            mDragBitmap = null;
        }
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return this.mGesture.onTouchEvent(event);
	}
	
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
    
    private int getFingerCount(){
    	int count = 0;
    	for(int i=0;i<this.touchEvents.size();i++){
    		if(this.touchEvents.get(i) != null){
    			count +=1;
    		}
    	}
    	return count;
    }
    
    private Bitmap roateImageView(Bitmap bitmap, float x, float y, float z){
    	float angle = (float) (Math.acos(y / bitmap.getHeight())*(180/Math.PI));
    	Camera camera = new Camera();
        camera.save();
        Matrix matrix = new Matrix();
        // rotate
        camera.rotateX(angle);
        camera.rotateY(0);
        camera.rotateZ(0);
        // translate
        camera.translate(0, 0, 0);
        camera.getMatrix(matrix);
        // 恢复到之前的初始状态。
        camera.restore();
        // 设置图像处理的中心点
        int w = bitmap.getWidth()/2;
        int h = bitmap.getHeight()/2;
        matrix.preTranslate(-w, 0);
        matrix.postTranslate(w, 0); 
        Bitmap newBit = null;
        try {
            // 经过矩阵转换后的图像宽高有可能不大于0，此时会抛出IllegalArgumentException
            newBit = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
        }
        return newBit;
    }
    
    private Bitmap getBitmapofView(ViewGroup view) {
		// TODO Auto-generated method stub
    	//EventItemView mHeadView = new EventItemView(this.context);
    	//this.measureView(mHeadView);
    	view.measure(
				MeasureSpec.makeMeasureSpec(view.getMeasuredWidth(), MeasureSpec.EXACTLY),
		        MeasureSpec.makeMeasureSpec(view.getMeasuredHeight(), MeasureSpec.EXACTLY));
    	view.layout(0, 0, view.getMeasuredWidth(),view.getMeasuredHeight());
		Bitmap b = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.RGB_565);
		Canvas c = new Canvas(b);
		view.draw(c);
		return b;
	}
    
    private void measureView(View child) {  
		WindowManager wm = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
	    int screenWidth = wm.getDefaultDisplay().getWidth();
	    ViewGroup.LayoutParams p = child.getLayoutParams();  
		if (p == null) {  
		    p = new ViewGroup.LayoutParams(screenWidth,  
		    		ViewGroup.LayoutParams.WRAP_CONTENT);  
		}  
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);  
		int lpHeight = p.height;  
		int childHeightSpec;  
		if (lpHeight > 0) {  
		    childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);  
		} else {  
		    childHeightSpec = MeasureSpec.makeMeasureSpec(0,  MeasureSpec.UNSPECIFIED);  
		}  
		child.measure(childWidthSpec, childHeightSpec);  
	}
    
	public interface OnCreateNewItemListener {
		public static final int CREATE_TOP = -1;
		public static final int CREATE_BOTTOM = -2;
		public void createNewItem(int position, String content);
	}
	
	public interface onItemClickedListener {
		public void onItemClicked(int position);
	}
}

