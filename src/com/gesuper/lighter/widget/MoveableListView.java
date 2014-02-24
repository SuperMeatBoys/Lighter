package com.gesuper.lighter.widget;

import com.gesuper.lighter.R;
import com.gesuper.lighter.tools.ActivityHelper;
import com.gesuper.lighter.tools.Rotate3DAnimation;
import com.gesuper.lighter.tools.SmoothScrollAnimation;
import com.gesuper.lighter.tools.SwitchAnimation;
import com.gesuper.lighter.tools.Utils;
import com.gesuper.lighter.ui.CaseActivity;
import com.gesuper.lighter.widget.MultiGestureDetector.EventInfo;
import com.gesuper.lighter.widget.MultiGestureDetector.MultiMotionEvent;
import com.gesuper.lighter.widget.MultiGestureDetector.OnMultiGestureListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
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
	public final int HEAD_SWITCH = 4;
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
	private int belowTouch;
	private int upTouch;
	private ItemViewBase belowItem;
	private ItemViewBase upItem;
	private int initPaddingTop;
	private int dBelow;
	private int dUp;
	private boolean isNeedCreateMiddleItem;
	private int status;
	
	
	//field for create new 
	private LinearLayout mHeadView;
	private LinearLayout mHeadLinear;
	private EditText mHeadText;
	private LinearLayout mHeadSwitch;
	private int mSwitchHeight;
	private ImageView mSwitchImage;
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
	private OnDeleteItemListener deleteItemListener;
	private Handler startEditItemHandler = new Handler(){
		public void handleMessage(Message message){
			MoveableListView.this.currentItem = (ItemViewBase) MoveableListView.this.getChildAt(message.what);
			MoveableListView.this.startEditItem(message.what);
		}
	};

	private boolean isEvent;

	private int scrollStart;

	private int scrollEnd;
	
	public MoveableListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.initResource();
	}
	 
	public MoveableListView(Context context, AttributeSet attrs, int headLayout, int footLayout, boolean isEvent){
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.headLayout = headLayout;
		this.footLayout = footLayout;
		this.isEvent = isEvent;
		this.initResource();
	}
	
	public void initResource() {
		// TODO Auto-generated method stub
		this.newItemListener = null;
		this.itemClickedListener = null;
		this.setSmoothScrollbarEnabled(true);
		WindowManager wm = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
		Point p = new Point();
	    wm.getDefaultDisplay().getSize(p);
	    screenWidth = p.x;
	    screenHeight = p.y; 
	    Log.d(TAG, "width: " + screenWidth + " height: " + screenHeight);
		//this.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS );
		this.mInflater = LayoutInflater.from(this.context);
		this.mGesture = new MultiGestureDetector(this.context, this);
		this.mGesture.setIsLongpressEnabled(true);
		this.belowTouch = 0; this.upTouch = 1;this.dBelow = 0;this.dUp = 0;
		this.isNeedCreateMiddleItem = false;
        this.status = HANDLE_NOTHING;
        
		//init for head view
		//this.mHeadView = new HeadViewBase(this.context);
		this.mHeadView = (LinearLayout) mInflater.inflate(this.headLayout, null);
		this.measureView(mHeadView);
		this.mSwitchImage = (ImageView) this.mHeadView.findViewById(R.id.activity_switch_view);
		this.mHeadLinear = (LinearLayout) this.mHeadView.findViewById(R.id.item_head_linear);
		this.mHeadSwitch= (LinearLayout) this.mHeadView.findViewById(R.id.item_head_switch); 
		this.mHeadText = (EditText) this.mHeadView.findViewById(R.id.item_content_et);
		this.mHeadImage = (ImageView) this.mHeadView.findViewById(R.id.item_head_bitmap);
		this.mHeadHeight = (int) this.context.getResources().getDimension(R.dimen.item_height);
		this.mHeadLinear.setBackgroundColor(Utils.getThemeColor(context, this.isEvent, 3, 0));
		this.mHeadBitmap = Utils.getBitmapofView(this.mHeadView);
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
		this.mFootBitmap = Utils.getBitmapofView(this.mFootView);
		this.mFootImage.setImageBitmap(this.mFootBitmap);
		this.addFooterView(this.mFootView);
		this.mFootLinear.setVisibility(View.GONE);
		this.mFootImage.setVisibility(View.GONE);
		this.mFootPlaceHolder.setVisibility(View.GONE);
		
		this.translateAnimation = new TranslateAnimation(0, -screenWidth, 0, 0);
		this.translateAnimation.setDuration(300L);
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
				} else if(MoveableListView.this.status == HANDLE_ITEM_EDITING){
					if(deleteItemListener != null){
						deleteItemListener.deleteItem(currentItem);
					}
					MoveableListView.this.status = HANDLE_NOTHING;
				}
			}
			public void onAnimationRepeat(Animation arg0) {}
			public void onAnimationStart(Animation arg0) {}
		});
		this.rotate3dAnimation = new Rotate3DAnimation(-90,0, this.mFootBitmap.getWidth()/2);
		this.rotate3dAnimation.setDuration(300L);
		this.rotate3dAnimation.setAnimationListener(new AnimationListener(){
			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				mFootImage.setVisibility(View.GONE);
				mFootLinear.setVisibility(View.GONE);

				MoveableListView.this.newItemListener.createNewItem(MoveableListView.this.getChildCount()-1, "");
				MoveableListView.this.startEditItemHandler.sendEmptyMessageDelayed(MoveableListView.this.getChildCount()-1, 200);
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

	public void setOnDeleteItemListener(
			OnDeleteItemListener listener) {
		// TODO Auto-generated method stub
		this.deleteItemListener = listener;
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
			} else if( y > 2*this.mHeadHeight){
				if(!this.isEvent){
					this.headStatus = HEAD_SWITCH;
					Bitmap bm = Utils.getBitmapofView(ActivityHelper.getInstance().getMain().getLayoutView());
					this.mSwitchHeight = this.mHeadHeight * Utils.getEventCount(context);
					Log.v(TAG, "event count " + mSwitchHeight);
					bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), this.mSwitchHeight);
					this.mSwitchImage.setImageBitmap(bm);
					this.mSwitchImage.setPadding(0, 0, 0, this.mHeadHeight);
					AnimationListener listener = new AnimationListener(){
						@Override
						public void onAnimationStart(Animation animation) {}
						@Override
						public void onAnimationEnd(Animation animation) {
							// TODO Auto-generated method stub
							mHeadLinear.setVisibility(View.GONE);
							mHeadSwitch.setVisibility(VISIBLE);
							mHeadSwitch.startAnimation(Utils.createFadeInAnimation(null));
						}@Override
						public void onAnimationRepeat(Animation animation) {}
						
					};
					this.mHeadLinear.startAnimation(Utils.createFadeOutAnimation(listener));
				}
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
		case HEAD_SWITCH:
			if(y <= 0){
				this.headStatus = HEAD_DONE;
				this.mSwitchImage.setImageBitmap(null);
				this.mSwitchImage.setPadding(0, 0, 0, 0);
				this.mHeadSwitch.setVisibility(View.GONE);
				this.mHeadLinear.setVisibility(View.GONE);
				this.mHeadImage.setVisibility(View.VISIBLE);
			}else if(y < this.mHeadHeight){
				this.headStatus = HEAD_PULL;
				this.mSwitchImage.setImageBitmap(null);
				this.mSwitchImage.setPadding(0, 0, 0, 0);
				this.mHeadSwitch.setVisibility(View.GONE);
				this.mHeadLinear.setVisibility(View.GONE);
				this.mHeadImage.setVisibility(View.VISIBLE);
			}else if(y < 2*this.mHeadHeight){
				this.headStatus = HEAD_RELEASE;
				this.mSwitchImage.setImageBitmap(null);
				this.mSwitchImage.setPadding(0, 0, 0, 0);
				this.mHeadSwitch.setVisibility(View.GONE);
				this.mHeadLinear.setVisibility(View.VISIBLE);
			} else if(y < 3*this.mHeadHeight){
				if(!this.isEvent){
					//this.mSwitchImage.setPadding(0, (-this.mSwitchHeight) + y - 2*this.mHeadHeight , 0, 0);
				}
			}
		}
		
		if(this.headStatus == HEAD_PULL){
			Bitmap map = this.roateImageView(this.mHeadBitmap, 0, y, 0);
			if(map != null)
				this.mHeadImage.setImageBitmap(map);
		}
		this.updateHeadText();
		int paddingTop = y - this.mHeadHeight;
		if(this.headStatus == HEAD_SWITCH ){
			paddingTop -= this.mSwitchHeight + this.mHeadHeight;
		}
		this.mHeadView.setPadding(0, paddingTop, 0, 0);
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
	
	public void startEditItem(int position) {
		// TODO Auto-generated method stub
		this.hideBelowItems(position);
		scrollItemToTop(position);
//		InputMethodManager inputManager = 
//			(InputMethodManager)this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//		if(position == 0){		//edit head view
//			this.mHeadText.requestFocus();
//			inputManager.showSoftInput(this.mHeadText, 0);
//		} else if(position == this.getChildCount()){
//			this.mFootText.requestFocus();
//			inputManager.showSoftInput(this.mFootText, 0);
//		} else {
//		}
	}
	
	private void endEditItem(int index) {
		// TODO Auto-generated method stub
		InputMethodManager inputManager = 
			(InputMethodManager)this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(this.mHeadText.getWindowToken(), 0);
		this.showBelowItems(index);
		scrollToOrigin();
//		if(index == 0){		//end edit head view
//			String content = this.mHeadText.getText().toString();
//			if(content.length() == 0){
//				//start ani
//				this.mHeadView.startAnimation(translateAnimation);
//			} else {
//				this.updateHeadStatus(0);
//				this.status = HANDLE_NOTHING;
//				if(this.newItemListener != null)
//					this.newItemListener.createNewItem(OnCreateNewItemListener.CREATE_TOP, content);
//			}
//		} else if(index == this.getChildCount()){
//			String content = this.mFootText.getText().toString();
//			if(content.length() == 0){
//				//start ani
//				this.mFootView.startAnimation(translateAnimation);
//			} else {
//				if(this.newItemListener != null)
//					this.newItemListener.createNewItem(OnCreateNewItemListener.CREATE_BOTTOM, content);
//				this.status = HANDLE_NOTHING;
//				this.mFootText.setText(null);
//				this.mFootLinear.setVisibility(View.GONE);
//				this.mFootPlaceHolder.setVisibility(View.VISIBLE);
//			}
//		} else {
//		}
	}
	
	private void scrollItemToTop(int position){
		ViewGroup.LayoutParams p = this.mFootPlaceHolder.getLayoutParams();
		p.height = this.screenHeight;
		this.mFootPlaceHolder.setLayoutParams(p);
		this.scrollStart = this.getScrollY();
		this.scrollEnd = - this.currentItem.getTop();
		if(Math.abs(this.scrollStart - this.scrollEnd) > 10){
			SmoothScrollAnimation smoothScroll = new SmoothScrollAnimation(this, this.scrollStart, this.scrollEnd, SmoothScrollAnimation.SCROLL_TOP);
			smoothScroll.setDuration(300);
			smoothScroll.setAnimationListener(new AnimationListener(){
				public void onAnimationEnd(Animation arg0) {
					// TODO Auto-generated method stub
					MoveableListView.this.currentItem.startEdit();
				}
				public void onAnimationRepeat(Animation arg0) {}
				public void onAnimationStart(Animation arg0) {}
				
			});
			this.startAnimation(smoothScroll);
		} else {
			this.currentItem.startEdit();
		}
	}
	
	private void scrollToOrigin(){
		ViewGroup.LayoutParams p = this.mFootPlaceHolder.getLayoutParams();
		p.height = 0;
		this.mFootPlaceHolder.setLayoutParams(p);
		SmoothScrollAnimation smoothScroll = new SmoothScrollAnimation(this, this.scrollEnd, this.scrollStart, SmoothScrollAnimation.SCROLL_TOP);
		smoothScroll.setDuration(300);
		smoothScroll.setAnimationListener(new AnimationListener(){
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				if(!MoveableListView.this.currentItem.endEdit(isEvent)){
					MoveableListView.this.currentItem.startAnimation(translateAnimation);
				} else {
					MoveableListView.this.status = HANDLE_NOTHING;
				}
				
			}
			public void onAnimationRepeat(Animation arg0) {}
			public void onAnimationStart(Animation arg0) {}
			
		});
		this.startAnimation(smoothScroll);
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
	public boolean onDown(MultiMotionEvent e) {
		// TODO Auto-generated method stub
		Log.v(TAG, "onDown " + e.getX() + " " + e.getY() + " " + e.getId());
		//Multi touch
		if(this.mGesture.getFingerCount() > 1 ){
			//clear other motions
			//if(this.status == HANDLE_NOTHING){
				this.status = HANDLE_MULTI;
			//}
			MultiMotionEvent m1 = this.mGesture.getEventInfoAt(0).getCurrentDownEvent();
			
			int p1 = this.pointToPosition(
					(int)m1.getX(), 
					(int)m1.getY());
			Log.v(TAG, "First touch Position: " + p1);
			int p2 = this.pointToPosition((int)e.getX(), (int)e.getY());
			if(p1 == INVALID_POSITION){
				return false;
			} else {
				if(m1.getY() < e.getY()){
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
				this.mFootBitmap = this.createItemViewImage(p2-1);
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
		Log.v(TAG, "onSingleTapUp " + this.status);
		switch(this.status){
		case HANDLE_NOTHING:
			int position = this.pointToPosition((int)e.getOffsetX(), (int)e.getOffsetY());
			if(position == INVALID_POSITION){
				//create new item on bottom
				this.mFootBitmap = this.createItemViewImage(this.getChildCount()-2);
				Log.v(TAG, "footbitmap " + this.mFootBitmap.getWidth() + " " + this.mFootBitmap.getHeight());
				this.mFootImage.setImageBitmap(this.mFootBitmap);
				this.mFootPlaceHolder.setVisibility(View.GONE);
				this.mFootImage.setVisibility(View.VISIBLE);
				this.mFootImage.startAnimation(this.rotate3dAnimation);
				this.status = HANDLE_ITEM_EDITING;
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
				this.headStatus = HEAD_DONE;
				this.updateHeadStatus(0);
				this.status = HANDLE_ITEM_EDITING;
				this.newItemListener.createNewItem(1, "");
				this.startEditItemHandler.sendEmptyMessageDelayed(1, 200);
			}else if(this.headStatus == HEAD_SWITCH){
					Log.v(TAG, "" + this.getHeight());
					SwitchAnimation animation = new SwitchAnimation(this.mSwitchImage, this.mHeadView,
							this.mHeadView.getPaddingTop(), -this.mHeadView.getPaddingTop(),
							this.mHeadHeight, this.getHeight() - this.mSwitchHeight);
					animation.setDuration(500);
					animation.setAnimationListener(new AnimationListener(){
						@Override
						public void onAnimationStart(Animation animation) {}
						@Override
						public void onAnimationEnd(Animation animation) {
							// TODO Auto-generated method stub
							((Activity)context).onBackPressed();
						}
						@Override
						public void onAnimationRepeat(Animation animation) {}
					});
					this.startAnimation(animation);
			} else {
				this.updateHeadStatus(0);
				this.status = HANDLE_NOTHING;
			}
			break;
		case HANDLE_ITEM_MOVE:
			if(this.itemStatus == ITEM_FINISH){
				this.currentItem.finishItem(this.isEvent, this.getChildCount()-2, this.currentItem.getModel().getSequence());
			} else if(this.itemStatus == ITEM_DELETE){
				new AlertDialog.Builder(this.context)   
				.setTitle(R.string.alert_delete_event_title)
				.setMessage(R.string.alert_delete_event_message)  
				.setPositiveButton(R.string.alert_yes, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if(deleteItemListener != null){
							deleteItemListener.deleteItem(currentItem);
						}
					}
				})  
				.setNegativeButton(R.string.alert_no, null)  
				.show();  
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
			break;
		case HANDLE_FOOT:
			this.endEditItem(this.getChildCount());
			break;
		case HANDLE_MULTI:
			if(this.mGesture.getFingerCount() == 0){
				this.setPadding(0, initPaddingTop, 0, 0);
				Log.v(TAG, "no finger on screen");
				this.dBelow = 0; this.dUp = 0;
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
			if(this.mGesture.getFingerCount() == 0){
				this.setPadding(0, initPaddingTop, 0, 0);
				this.createImage.setImageBitmap(null);
				this.createImage.setPadding(0, 0, 0, 0);
				if(this.isNeedCreateMiddleItem){
					int p = this.getPositionForView(belowItem);
					this.newItemListener.createNewItem(p, "");
					this.startEditItemHandler.sendEmptyMessageDelayed(p, 300);
					this.status = HANDLE_ITEM_EDITING;
				} else {
					this.status = HANDLE_NOTHING;
				}
				this.dBelow = 0; this.dUp = 0;
			}
			break;
		}
		return false;
	}
	
	private Bitmap createItemViewImage(int index) {
		// TODO Auto-generated method stub
		this.removeFooterView(this.mFootView);
		
		this.mFootImage.setVisibility(View.GONE);
		this.mFootPlaceHolder.setVisibility(View.GONE);
		this.mFootLinear.setVisibility(View.VISIBLE);
		
		this.mFootLinear.setBackgroundColor(Utils.getThemeColor(context, isEvent, this.getChildCount()-2, index));
		
		this.measureView(this.mFootView);
		Bitmap bitmap =  Utils.getBitmapofView(this.mFootView, this.screenWidth);
		
		this.mFootLinear.setVisibility(View.GONE);
		this.addFooterView(this.mFootView);
		return bitmap;
	}

	@Override
	public boolean onFling(MultiMotionEvent e1, MultiMotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		Log.v(TAG, "onFling " + e2.getX() + " " + e2.getY());
		return this.onSingleTapUp(e2);
//		switch(this.status){
//		case HANDLE_NOTHING:
//			break;
//		case HANDLE_HEAD:
//			if(this.headStatus == HEAD_CREATING){
//				this.headStatus = HEAD_DONE;
//				this.endEditItem(0);
//			}else if(this.headStatus == HEAD_RELEASE){
//				this.headStatus = HEAD_CREATING;
//				this.updateHeadStatus(0);
//				this.startEditItem(0);
//			}else {
//				this.updateHeadStatus(0);
//				this.status = HANDLE_NOTHING;
//			}
//			break;
//		case HANDLE_ITEM_MOVE:
//			if(this.itemStatus == ITEM_FINISH){
//				this.currentItem.finishItem();
//			}
//			this.updateItemStatus(0);
//			this.status = HANDLE_NOTHING;
//			break;
//		case HANDLE_LONGPRESS:
//			this.status = HANDLE_NOTHING;
//			stopDrag();
//			break;
//		case HANDLE_MULTI:
//			if(this.mGesture.getFingerCount() == 0){
//				this.setPadding(0, initPaddingTop, 0, 0);
//				Log.v(TAG, "no finger on screen");
//				this.dBelow = 0; this.dUp = 0;
//				this.status = HANDLE_NOTHING;
//			}
//			break;
//		case HANDLE_MULTI_USE:
//			if(e1.getId() == this.upTouch){
//				this.setPadding(0, 0, 0, 0);
//				this.upItem.setPadding(0, 0, 0, 0);
//				this.dUp = (int) (e2.getY() - e1.getY()) * (-1);
//			} else if(e1.getId() == this.belowTouch){
//				this.belowItem.setPadding(0, 0, 0, 0);
//				this.dBelow = (int) (e2.getY() - e1.getY());
//			}
//			if(this.mGesture.getFingerCount() == 0){
//				this.setPadding(0, initPaddingTop, 0, 0);
//				this.createImage.setImageBitmap(null);
//				this.createImage.setPadding(0, 0, 0, 0);
//				if(this.isNeedCreateMiddleItem){
//					int p = this.getPositionForView(belowItem);
//					this.newItemListener.createNewItem(p, "");
//					this.startEditItemHandler.sendEmptyMessageDelayed(p, 300);
//				}
//				this.dBelow = 0; this.dUp = 0;
//				this.status = HANDLE_NOTHING;
//			}
//			break;
//		}
//		return false;
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
			return true;
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
				if(dY + this.dUp < this.mHeadHeight){
					this.isNeedCreateMiddleItem = false;
					this.dBelow = dY;
					Bitmap map = this.roateImageView(this.mFootBitmap, 0,this.dBelow + this.dUp,0);
					if(map != null){
						this.createImage.setImageBitmap(map);
						this.createImage.setPadding(0, 0, 0, 0);
					}
				}
				else {
					this.isNeedCreateMiddleItem = true;
					this.createImage.setImageBitmap(this.mFootBitmap);
					this.createImage.setPadding(0, 0, 0, dY - this.dBelow);
				}
			}
			else if(this.upTouch == e1.getId()){
				this.setPadding(0, this.initPaddingTop + dY, 0, 0);
				if(this.dBelow - dY < this.mHeadHeight){
					this.isNeedCreateMiddleItem = false;
					this.dUp = - dY;
					Bitmap map = this.roateImageView(this.mFootBitmap, 0, this.dBelow + this.dUp,0);
					if(map != null){
						this.createImage.setImageBitmap(map);
						this.createImage.setPadding(0, 0, 0, 0);
					}
				}else {
					this.isNeedCreateMiddleItem = true;
					this.upItem.setPadding(0, 0, 0, -(this.initPaddingTop + dY + this.dUp));
				}
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
		EventInfo event1 = this.mGesture.getEventInfoAt(0);
		if(event1 == null){
			return ;
		}
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

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return this.mGesture.onTouchEvent(event);
	}

    private Bitmap roateImageView(Bitmap bitmap, float x, float y, float z){
    	float angle = (float) (Math.acos(y / bitmap.getHeight())*(180/Math.PI));
    	Camera camera = new Camera();
        camera.save();
        Matrix matrix = new Matrix();
        Bitmap upBit = null;
        Bitmap belowBit = null;
        Bitmap backBit = bitmap.copy(Config.ARGB_8888, false);
        try {
            // rotate
            camera = new Camera();
            camera.save();
            camera.rotate(angle,0,0);
            // translate
            camera.translate(0, 0, 0);
            matrix = new Matrix();
            camera.getMatrix(matrix);
            // 恢复到之前的初始状态。
            camera.restore();
            // 设置图像处理的中心点
            int w = bitmap.getWidth()/2;
            matrix.preTranslate(-w, 0);
            matrix.postTranslate(w, 0);
            belowBit = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
        
        return belowBit;
    }
    
    private void measureView(View child) {  
	    int screenWidth = this.screenWidth;
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
	
	public interface OnDeleteItemListener{
		public void deleteItem(ItemViewBase item);
	}
	
	public interface onItemClickedListener {
		public void onItemClicked(int position);
	}
}

