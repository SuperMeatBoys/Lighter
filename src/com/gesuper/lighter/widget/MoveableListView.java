package com.gesuper.lighter.widget;

import com.gesuper.lighter.R;
<<<<<<< HEAD
=======
import com.gesuper.lighter.ui.EventItemView;
import com.gesuper.lighter.ui.MainActivity;
>>>>>>> 5126d5b19bf38ebd3276b5e1d48551ca54a782c1
import com.gesuper.lighter.widget.MultiGestureDetector.MultiMotionEvent;
import com.gesuper.lighter.widget.MultiGestureDetector.OnMultiGestureListener;

import android.annotation.SuppressLint;
import android.content.Context;
<<<<<<< HEAD
=======
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
>>>>>>> 5126d5b19bf38ebd3276b5e1d48551ca54a782c1
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View.OnTouchListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

public class MoveableListView extends ListView implements OnTouchListener, OnMultiGestureListener{
	public static final String TAG = "MoveableListView";
	
	//status code for head view
	public final static int HEAD_PULL = 0;
	public final static int HEAD_RELEASE = 1;
	public final static int HEAD_CREATING = 2;
	public final static int DONE = 3;
	
	private LayoutInflater mInflater;
<<<<<<< HEAD
	private Context mContext;
	private MultiGestureDetector mGesture;
	
=======
	private Context context;
	private MultiGestureDetector mGesture;

>>>>>>> 5126d5b19bf38ebd3276b5e1d48551ca54a782c1
	//
    private RotateAnimation animation;  
    private RotateAnimation reverseAnimation;
	private LinearLayout mHeadView;
	private ImageView mHeadImage;
	private TextView mHeadText;
	private int mHeadWidth;
	private int mHeadHeight;
	
<<<<<<< HEAD
	private int mStartX;
	private int mStartY;
	
	
	public MoveableListView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    // TODO Auto-generated constructor stub

=======
	private View mFootText;
	private int mStartX;
	private int mStartY;
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
>>>>>>> 5126d5b19bf38ebd3276b5e1d48551ca54a782c1
	}
	
	public void initResource() {
		// TODO Auto-generated method stub
<<<<<<< HEAD
		this.mInflater = LayoutInflater.from(this.mContext);
		this.mGesture = new MultiGestureDetector(this.mContext, this);
=======
		this.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS );
		this.mInflater = LayoutInflater.from(this.context);
		this.mGesture = new MultiGestureDetector(this.context, this);
>>>>>>> 5126d5b19bf38ebd3276b5e1d48551ca54a782c1
		this.mGesture.setIsLongpressEnabled(false);
		
		animation = new RotateAnimation(0, -180,  
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,  
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);  
        animation.setInterpolator(new LinearInterpolator());  
        animation.setDuration(100);  
        animation.setFillAfter(true);  
  
        reverseAnimation = new RotateAnimation(-180, 0,  
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,  
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);  
        reverseAnimation.setInterpolator(new LinearInterpolator());  
        reverseAnimation.setDuration(100);  
        reverseAnimation.setFillAfter(true);
        
		//init for head view
		this.mHeadView = (LinearLayout) this.mInflater.inflate(R.layout.event_head, null);
		this.mHeadImage = (ImageView) this.mHeadView.findViewById(R.id.head_image);
		this.mHeadText = (TextView) this.mHeadView.findViewById(R.id.head_text);
		this.measureView(this.mHeadView);
		this.mHeadWidth = this.mHeadView.getMeasuredWidth();
		this.mHeadHeight = this.mHeadView.getMeasuredHeight();
		this.mHeadView.setPadding(this.mHeadView.getPaddingLeft(), -1 * this.mHeadHeight, this.mHeadView.getPaddingRight(), this.mHeadView.getPaddingBottom());
		this.mHeadView.invalidate();
		this.addHeaderView(this.mHeadView);
		
		View v = this.mInflater.inflate(R.layout.list_footer, null);
		mFootText = (TextView) v.findViewById(R.id.list_foot_text);
		this.addFooterView(v);
	}
	
	private void changeHeadViewByStatus(int status){
		switch(status){
		case HEAD_PULL:
			this.mHeadImage.setVisibility(View.VISIBLE);
			this.mHeadImage.clearAnimation();
			this.mHeadImage.startAnimation(animation);
			this.mHeadText.setText(R.string.event_pull_create);
			break;
		case HEAD_RELEASE:
			this.mHeadImage.setVisibility(View.VISIBLE);
			this.mHeadImage.clearAnimation();
			this.mHeadImage.startAnimation(this.reverseAnimation);
			this.mHeadText.setText(R.string.event_pull_create);
			break;
		case HEAD_CREATING:
			break;
		case DONE:
			mHeadView.setPadding(mHeadView.getPaddingLeft(), -1 * mHeadHeight, mHeadView.getPaddingRight(), mHeadView.getPaddingBottom());  
			mHeadView.invalidate();  
            this.mHeadImage.clearAnimation();  
            this.mHeadImage.setImageResource(R.drawable.arrow);  
            this.mHeadText.setText(R.string.event_pull_create);
		}
	}
	
	@SuppressLint("NewApi")
	private void scrollItemToTop(int position){
		//ViewGroup.LayoutParams p = this.mFootText.getLayoutParams();
		//p.width = ((MainActivity)this.context).screenWidth;
		//p.height = ((MainActivity)this.context).screenHeight;
		//this.mFootText.setLayoutParams(p);
		this.smoothScrollToPositionFromTop(position, 0);
	}
	
	@Override
	public boolean onDown(MultiMotionEvent e) {
		// TODO Auto-generated method stub
<<<<<<< HEAD
		Log.v(TAG, "onDown");
=======
		Log.v(TAG, "onDown " + e.getX() + " " + e.getY());
>>>>>>> 5126d5b19bf38ebd3276b5e1d48551ca54a782c1
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
<<<<<<< HEAD
		Log.v(TAG, "onSingleTapUp");
=======
		int position = this.pointToPosition((int)e.getOffsetX(), (int)e.getOffsetY());
		Log.v(TAG, "onSingleTapUp " + position);
		EventItemView view = (EventItemView) this.getChildAt(position - this.getFirstVisiblePosition());
		
		if(view instanceof OnItemFocusListener){
			if(view.isNeedFocus((int)e.getOffsetX(), (int)e.getOffsetY() - view.getTop())){
				scrollItemToTop(position);
			}
		}
		if(position < 0){
			//create new item at bottom
			
		} 
>>>>>>> 5126d5b19bf38ebd3276b5e1d48551ca54a782c1
		return false;
	}
	
	@Override
	public boolean onScroll(MultiMotionEvent e1, MultiMotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
<<<<<<< HEAD
		Log.v(TAG, "onScroll");
=======
		// Log.v(TAG, "onScroll " + e2.getX() + " " + e2.getY());
>>>>>>> 5126d5b19bf38ebd3276b5e1d48551ca54a782c1
		return false;
	}
	
	@Override
	public void onLongPress(MultiMotionEvent e) {
		// TODO Auto-generated method stub
		Log.v(TAG, "onLongPress");
	}
	
	@Override
	public boolean onFling(MultiMotionEvent e1, MultiMotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
<<<<<<< HEAD
		Log.v(TAG, "onFling");
=======
		Log.v(TAG, "onFling " + e2.getX() + " " + e2.getY());
>>>>>>> 5126d5b19bf38ebd3276b5e1d48551ca54a782c1
		return false;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		this.mStartX = (int) event.getX();
		this.mStartY = (int) event.getY();
		return this.mGesture.onTouchEvent(event);
	}
	
	private void measureView(View child) {  
        ViewGroup.LayoutParams p = child.getLayoutParams();  
        if (p == null) {  
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,  
                    ViewGroup.LayoutParams.WRAP_CONTENT);  
        }  
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);  
        int lpHeight = p.height;  
        int childHeightSpec;  
        if (lpHeight > 0) {  
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,  
                    MeasureSpec.EXACTLY);  
        } else {  
            childHeightSpec = MeasureSpec.makeMeasureSpec(0,  
                    MeasureSpec.UNSPECIFIED);  
        }  
        child.measure(childWidthSpec, childHeightSpec);  
    }
	
	public interface OnItemFocusListener{
		Rect focusRect = new Rect();
		public void calcFocusRect();
		public boolean isNeedFocus(int x, int y);
		public void onFocus();
		public void outFocus();
	}
}


