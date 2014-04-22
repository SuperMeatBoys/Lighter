package com.gesuper.lighter.widget;

import com.gesuper.lighter.R;
import com.gesuper.lighter.model.ItemModelBase;
import com.gesuper.lighter.tools.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ItemViewBase extends LinearLayout {
	public static final String TAG = "ItemViewBase";
	private static final int NORMAL = 0;
	private static final int FINISHED = 1;
	
	protected Context context;
	protected ItemModelBase model;
	protected EditText mContentEt;
	protected TextView mContentTv;
	protected LinearLayout mContentLinear;

	protected Rect focusRect;
	private AlphaAnimation alphaAnimation;
	private int status;
	private LinearLayout createImageLinear;
	private ImageView createImageUp;
	private ImageView createImageDown;
	
	public ItemViewBase(Context context){
		super(context);
		this.initResource();
	}
	
	public ItemViewBase(Context context, int layoutId) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		inflate(context, layoutId, this);
		this.initResource();
	}

	private void initResource() {
		// TODO Auto-generated method stub
		this.createImageLinear = (LinearLayout) this.findViewById(R.id.item_create_image);
		this.createImageUp = (ImageView) this.findViewById(R.id.item_create_image_up);
		this.createImageDown = (ImageView) this.findViewById(R.id.item_create_image_down);
		
		this.mContentLinear = (LinearLayout)findViewById(R.id.item_linear);
		this.mContentEt = (EditText)findViewById(R.id.item_content_et);
		this.mContentTv = (TextView)findViewById(R.id.item_content_tv);
//		ViewGroup.LayoutParams p = this.mContentLinear.getLayoutParams();
//		p.width = 480;
//		this.mContentLinear.setLayoutParams(p);
		
		this.focusRect = new Rect();
		this.alphaAnimation = new AlphaAnimation(1.0F, 0.5F);
		this.alphaAnimation.setDuration(100L);
		this.alphaAnimation.setFillAfter(true);
		this.status = NORMAL;
	}

	public void setModel(ItemModelBase mItemModel) {
		// TODO Auto-generated method stub
		this.model = mItemModel;
		this.mContentEt.setText(mItemModel.getContent());
		this.mContentTv.setText(mItemModel.getContent());
		
		this.calcFocusRect();
	}

	public ItemModelBase getModel() {
		// TODO Auto-generated method stub
		return this.model;
	}
	
	public String getContent(){
		return this.mContentTv.getText().toString();
	}
	
	public void startEdit() {
		// TODO Auto-generated method stub
		Log.v(TAG, "startEdit");
		this.mContentEt.setVisibility(View.VISIBLE);
		this.mContentTv.setVisibility(View.GONE);
		this.mContentEt.setSelection(this.mContentEt.getEditableText().length());
		this.mContentEt.requestFocus();

		InputMethodManager inputManager = 
			(InputMethodManager)this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.showSoftInput(this.mContentEt, 0);
	}
	
	public boolean endEdit(boolean isEvent){
		// TODO Auto-generated method stub
		this.mContentEt.setVisibility(View.GONE);
		this.mContentTv.setVisibility(View.VISIBLE);
		Log.v(TAG, "endedit " + this.mContentEt.getText() + " " + model.getId() + " " + isEvent);
		if(this.mContentEt.getText().toString().length() == 0){
			if(this.mContentTv.getText().toString().length() == 0){
				return false;
			} else {
				new AlertDialog.Builder(this.context)   
				.setTitle(R.string.alert_warning)
				.setMessage(R.string.alert_warning_message)  
				.setPositiveButton(R.string.alert_yes, null) 
				.show();
				this.mContentEt.setText(this.mContentTv.getText());
			}
		} 
		this.mContentTv.setText(this.mContentEt.getText());
		model.setContent(this.mContentEt.getText().toString());
		Utils.saveItemContent(context, isEvent, model.getId(), this.mContentEt.getText().toString());
		
		return true;
	}
	
	public void createSplitImage(Bitmap map, float y){
		if(Utils.getItemHeight(this.context) == (int)Math.abs(y)){
			this.createImageUp.setImageBitmap(map);
    		this.createImageDown.setImageBitmap(null);
    		return ;
		}
		
    	Bitmap upImage = Utils.roateImageView(map, 0, -y, 0);
    	Bitmap downImage = Utils.roateImageView(map, 0, y, 0);
	    if(upImage != null && downImage != null){
	        try{
	    		upImage = Bitmap.createBitmap(upImage, 0, 0, upImage.getWidth(), upImage.getHeight()/2, null, false);
	    		downImage = Bitmap.createBitmap(downImage, 0, downImage.getHeight()/2, downImage.getWidth(), downImage.getHeight()/2, null, false);
	    		this.createImageUp.setImageBitmap(upImage);
	    		this.createImageDown.setImageBitmap(downImage);

	    	} catch (IllegalArgumentException e) {
	    		e.printStackTrace();
	    	}
	    }
    }
	
	public void setCreateLinearPadding(int left, int top, int right, int bottom){
		this.createImageLinear.setPadding(left, top, right, bottom);
	}
	
	public void startCreateState(){
		this.createImageLinear.setVisibility(View.VISIBLE);
	}
	
	public void endCreateState(){
		this.createImageLinear.setVisibility(View.GONE);
		this.createImageUp.setImageBitmap(null);
		this.createImageDown.setImageBitmap(null);
		this.createImageLinear.setPadding(0, 0, 0, 0);
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
	
	public LinearLayout getContentLearLayout(){
		// TODO Auto-generated method stub
		return this.mContentLinear;
	}

	public void noneAlpha() {
		// TODO Auto-generated method stub
		this.clearAnimation();
	}

	public void halfAlpha() {
		// TODO Auto-generated method stub
		this.startAnimation(this.alphaAnimation);
	}
	
	public void setBgColor(int color) {
		// TODO Auto-generated method stub
		this.mContentLinear.setBackgroundColor( color );
	}
	
	public void finishItem(boolean isEvent, int n, int o) {
		// TODO Auto-generated method stub
		if(this.status == NORMAL){
			this.mContentTv.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
			this.mContentLinear.setBackgroundResource(R.color.activity_bg);
			this.status = FINISHED;
		} else {
			this.mContentTv.getPaint().setFlags(Paint.LINEAR_TEXT_FLAG);
			this.mContentLinear.setBackgroundColor(Utils.getThemeColor(context, isEvent, n, o));
			this.status = NORMAL;
		}
	}
}
