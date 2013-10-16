package com.gesuper.lighter.widget;

import com.gesuper.lighter.R;
import com.gesuper.lighter.model.EventModel;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ItemViewBase extends FrameLayout {
	public static final String TAG = "ItemViewBase";
	private static final int NORMAL = 0;
	private static final int FINISHED = 1;
	protected Context context;
	protected EventModel model;
	protected EditText mContentEt;
	protected TextView mContentTv;
	protected LinearLayout mContentLinear;

	protected Rect focusRect;
	private AlphaAnimation alphaAnimation;
	private int status;
	public ItemViewBase(Context context, int layoutId) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		inflate(context, layoutId, this);
		this.initResource();
	}

	private void initResource() {
		// TODO Auto-generated method stub
		this.mContentLinear = (LinearLayout)findViewById(R.id.event_linear);
		this.mContentEt = (EditText)findViewById(R.id.event_content_et);
		this.mContentTv = (TextView)findViewById(R.id.event_content_tv);
		ViewGroup.LayoutParams p = this.mContentLinear.getLayoutParams();
		p.width = 480;
		this.mContentLinear.setLayoutParams(p);
		
		this.focusRect = new Rect();
		this.alphaAnimation = new AlphaAnimation(1.0F, 0.5F);
		this.alphaAnimation.setDuration(100L);
		this.alphaAnimation.setFillAfter(true);
		this.status = NORMAL;
	}

	public void setModel(EventModel mItemModel) {
		// TODO Auto-generated method stub
		this.model = mItemModel;
		this.mContentEt.setText(mItemModel.getContent());
		this.mContentTv.setText(mItemModel.getContent());
		
		this.calcFocusRect();
	}

	public EventModel getModel() {
		// TODO Auto-generated method stub
		return this.model;
	}
	
	public String getContent(){
		return this.mContentTv.getText().toString();
	}
	
	public void startEdit() {
		// TODO Auto-generated method stub
		this.mContentTv.setVisibility(View.GONE);
		this.mContentEt.setVisibility(View.VISIBLE);
		this.mContentEt.setSelection(this.mContentEt.getEditableText().length());
		this.mContentEt.requestFocus();
	}
	
	public void endEdit(){
		// TODO Auto-generated method stub
		this.mContentEt.setVisibility(View.GONE);
		this.mContentTv.setVisibility(View.VISIBLE);
	}
	
	public void calcFocusRect(){
		focusRect.left = this.mContentTv.getLeft();
		focusRect.top = this.mContentTv.getTop();
		focusRect.right = this.mContentTv.getRight();
		focusRect.bottom = this.mContentTv.getBottom();
	}
	
	public boolean isNeedFocus(int x, int y) {
		// TODO Auto-generated method stub
		this.calcFocusRect();
		if(focusRect.contains(x, y)){
			return true;
		}
		return false;
	}
	
	public void onFocus() {
		// TODO Auto-generated method stub
		this.mContentTv.setVisibility(View.GONE);
		this.mContentEt.setVisibility(View.VISIBLE);
	}

	public void outFocus() {
		// TODO Auto-generated method stub
		this.mContentTv.setText(this.mContentEt.getText());
		this.mContentEt.setVisibility(View.GONE);
		this.mContentTv.setVisibility(View.VISIBLE);
		
		this.calcFocusRect();
	}
	
	public LinearLayout getContentLearLayout(){
		// TODO Auto-generated method stub
		return this.mContentLinear;
	}
	
	public void finishItem() {
		// TODO Auto-generated method stub
		if(this.status == NORMAL){
			this.mContentTv.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
			this.mContentLinear.setBackgroundResource(R.color.activity_bg);
			this.status = FINISHED;
		} else {
			this.mContentTv.getPaint().setFlags(Paint.LINEAR_TEXT_FLAG);
			this.mContentLinear.setBackgroundResource(R.drawable.item_bg);
			this.status = NORMAL;
		}
	}

	public void noneAlpha() {
		// TODO Auto-generated method stub
		this.clearAnimation();
	}

	public void halfAlpha() {
		// TODO Auto-generated method stub
		this.startAnimation(this.alphaAnimation);
	}

}