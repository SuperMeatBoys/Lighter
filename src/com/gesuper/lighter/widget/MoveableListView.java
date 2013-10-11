package com.gesuper.lighter.widget;

import com.gesuper.lighter.R;
import com.gesuper.lighter.widget.MultiGestureDetector.MultiMotionEvent;
import com.gesuper.lighter.widget.MultiGestureDetector.OnMultiGestureListener;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
	private Context mContext;
	private MultiGestureDetector mGesture;
	
	//
    private RotateAnimation animation;  
    private RotateAnimation reverseAnimation;
	private LinearLayout mHeadView;
	private ImageView mHeadImage;
	private TextView mHeadText;
	private int mHeadWidth;
	private int mHeadHeight;
	
	private int mStartX;
	private int mStartY;
	
	
	public MoveableListView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    // TODO Auto-generated constructor stub

	}
	
	private void initResourse() {
		// TODO Auto-generated method stub
		this.mInflater = LayoutInflater.from(this.mContext);
		this.mGesture = new MultiGestureDetector(this.mContext, this);
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
		this.mHeadView = (LinearLayout) this.mInflater.inflate(R.layout.listview_head, null);
		this.mHeadImage = (ImageView) this.mHeadView.findViewById(R.id.head_image);
		this.mHeadText = (TextView) this.mHeadView.findViewById(R.id.head_text);
		this.measureView(this.mHeadView);
		this.mHeadWidth = this.mHeadView.getMeasuredWidth();
		this.mHeadHeight = this.mHeadView.getMeasuredHeight();
		this.mHeadView.setPadding(this.mHeadView.getPaddingLeft(), -1 * this.mHeadHeight, this.mHeadView.getPaddingRight(), this.mHeadView.getPaddingBottom());
		this.mHeadView.invalidate();
		this.addHeaderView(this.mHeadView);
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
	
	@Override
	public boolean onDown(MultiMotionEvent e) {
		// TODO Auto-generated method stub
		Log.v(TAG, "onDown");
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
		Log.v(TAG, "onSingleTapUp");
		return false;
	}
	
	@Override
	public boolean onScroll(MultiMotionEvent e1, MultiMotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		Log.v(TAG, "onScroll");
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
		Log.v(TAG, "onFling");
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

}
